package com.bixbox.payment.util;

import com.bixbox.payment.exception.KakaoPayFailException;
import com.bixbox.payment.temp.PaymentDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoPayUtil {
    @Value("${cid}")
    private String cid;

    @Value("${approvalUrl}")
    private String approvalUrl;

    @Value("${cancelUrl}")
    private String cancelUrl;

    @Value("${failUrl}")
    private String failUrl;

    @Value("${kakaoPayKey}")
    private String kakaoPayKey;

    @Value("${requestUrl}")
    private String requestUrl;

    private final RedisTemplate<String, String> redisTemplate;

    private final ObjectMapper objectMapper;


    public ResponseEntity<String> getKakaoTemplate(String partnerUserId, PaymentDto paymentDto) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.set("Authorization", "KakaoAK "+kakaoPayKey);

        HttpEntity<String> requestEntity = new HttpEntity<>(getKakaoPayloadData(partnerUserId,paymentDto), httpHeaders);

        try {
            ResponseEntity<String> exchange = new RestTemplate().exchange(
                    requestUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );
            // 이 시점에 레디스에 값을 저장한다
            savePaymentInfoToRedis(exchange, paymentDto, partnerUserId);
            return exchange;
        }catch(Exception e){
            throw new KakaoPayFailException("카카오페이 결제 중 문제가 발생했습니다",e);
        }
    }

    private String getKakaoPayloadData(String partnerUserId, PaymentDto paymentDto){
        return "cid=" + cid
                + "&partner_order_id=" + paymentDto.getPartnerOrderId() // UUID
                + "&partner_user_id=" + partnerUserId
                + "&item_name=" + paymentDto.getItemName()
                + "&quantity=" + paymentDto.getQuantity()
                + "&total_amount=" + paymentDto.getTotalAmount()
                + "&tax_free_amount=" + paymentDto.getTaxFreeAmount()
                + "&approval_url=" + approvalUrl + "?partnerOrderId=" + paymentDto.getPartnerOrderId()
                + "&cancel_url=" + cancelUrl
                + "&fail_url=" + failUrl;
    }

    private String getTid(ResponseEntity<String> jsonResponse){
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonResponse.getBody());
            return jsonNode.get("tid").asText();
        } catch (Exception e) {
            throw new RuntimeException("파싱 실패");
        }
    }

    private void savePaymentInfoToRedis(ResponseEntity<String> jsonResponse, PaymentDto paymentDto, String partnerUserId){
        ValueOperations<String, String> vop = redisTemplate.opsForValue();
        StringBuilder value = new StringBuilder();
        value.append("tid=");
        value.append(getTid(jsonResponse));
        value.append("&partner_user_id=");
        value.append(partnerUserId);
        value.append("&credit=");
        value.append(paymentDto.getChargeCredit());
        value.append("&subscription=");
        value.append(paymentDto.getSubscriptionType());

        vop.set(paymentDto.getPartnerOrderId(), value.toString());
    }
}
