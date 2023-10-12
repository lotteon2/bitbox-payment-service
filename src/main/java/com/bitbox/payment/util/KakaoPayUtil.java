package com.bitbox.payment.util;

import com.bitbox.payment.dto.KakaoPayCancelDto;
import com.bitbox.payment.dto.KakaoPayDto;
import com.bitbox.payment.exception.KakaoPayFailException;
import com.bitbox.payment.dto.PaymentDto;
import com.bitbox.payment.exception.KakaoPayReadyException;
import com.bitbox.payment.exception.UrgentMailException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.bitbox.bitbox.enums.SubscriptionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;

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

    @Value("${kakao-readyUrl}") // 준비시 호출하는 url
    private String kakaoReadyUrl;

    @Value("${kakao-approveUrl}") // 승인시 호출하는 url
    private String kakaoApproveUrl;

    @Value("${kakao-cancelUrl}") // 취소시 호출하는 url
    private String kakaoCancelUrl;

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public ResponseEntity<String> callKakaoReadyApi(PaymentDto paymentDto) {
        ResponseEntity<String> exchange;
        try{
            exchange = callKakaoApi(kakaoReadyUrl, getKakaoPayReadyPayloadData(paymentDto));
            savePaymentInfoToRedis(exchange, paymentDto);
        }catch(Exception e){
            // 사실 레디스에서 터질 수 있긴한데 그렇다고 예외를 구분하기는 좀 그래서 하나로 퉁침
            throw new KakaoPayReadyException("카카오 페이 결제 서버 관련 문제가 발생했습니다");
        }

        return exchange;
    }

    public int callKakaoApproveApi(KakaoPayDto kakaoPayDto) {
        try{
            return callKakaoApi(kakaoApproveUrl, getKakaoPayApprovePayLoadData(kakaoPayDto)).getStatusCode().value();
        }catch(KakaoPayFailException e){
            throw new KakaoPayFailException("카카오 페이 결제 실패");
        }
    }

    public int callKakaoCancelApi(KakaoPayCancelDto kakaoPayCancelDto) {
        try {
            return callKakaoApi(kakaoCancelUrl, getKakaoPayCancelPayLoadData(kakaoPayCancelDto)).getStatusCode().value();
        }catch(KakaoPayFailException e){
            log.error("에러 발생"); // TODO 이런거 로그파일로 관리해야함
            throw new UrgentMailException("카카오페이 취소하는 과정에서 에러발생",e);
        }
    }

    private ResponseEntity<String> callKakaoApi(String url, String payload) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.set("Authorization", "KakaoAK "+kakaoPayKey);

        HttpEntity<String> requestEntity = new HttpEntity<>(payload, httpHeaders);

        try {
            return new RestTemplate().exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );
        }catch(Exception e){
            throw new KakaoPayFailException("카카오페이 결제 중 문제가 발생했습니다",e);
        }
    }


    public KakaoPayDto getKakaoPayDto(String inputString, String pgToken) {
        try {
            KakaoPayDto kakaoPayDto = objectMapper.readValue(inputString, KakaoPayDto.class);
            kakaoPayDto.setPgToken(pgToken);
            return kakaoPayDto;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("카카오페이 관련 값을 만드는데 실패했습니다.");
        }
    }

    private String getKakaoPayReadyPayloadData(PaymentDto paymentDto){
        return "cid=" + cid
                + "&partner_order_id=" + paymentDto.getPartnerOrderId() // UUID
                + "&partner_user_id=" + paymentDto.getPartnerUserId()
                + "&item_name=" + URLEncoder.encode(paymentDto.getItemName())
                + "&quantity=" + paymentDto.getQuantity()
                + "&total_amount=" + paymentDto.getTotalAmount()
                + "&tax_free_amount=" + paymentDto.getTaxFreeAmount()
                + "&approval_url=" + approvalUrl + "?partnerOrderId=" + paymentDto.getPartnerOrderId()
                + "&cancel_url=" + cancelUrl
                + "&fail_url=" + failUrl;
    }

    private String getKakaoPayApprovePayLoadData(KakaoPayDto kakaoPayDto){
        return "cid=" + kakaoPayDto.getCid()
                + "&tid=" + kakaoPayDto.getTid()
                + "&partner_order_id=" + kakaoPayDto.getPartnerOrderId()
                + "&partner_user_id=" + kakaoPayDto.getPartnerUserId()
                + "&pg_token=" + kakaoPayDto.getPgToken();
    }

    private String getKakaoPayCancelPayLoadData(KakaoPayCancelDto kakaoPayCancelDto){
        return "cid=" + cid
                + "&tid=" + kakaoPayCancelDto.getTid()
                + "&cancel_amount=" + kakaoPayCancelDto.getCancelAmount()
                + "&cancel_tax_free_amount=" + kakaoPayCancelDto.getCancelTaxFreeAmount();
    }

    private String getTid(ResponseEntity<String> jsonResponse){
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonResponse.getBody());
            return jsonNode.get("tid").asText();
        } catch (Exception e) {
            throw new RuntimeException("파싱 실패");
        }
    }

    private void savePaymentInfoToRedis(ResponseEntity<String> jsonResponse, PaymentDto paymentDto){
        ValueOperations<String, String> vop = redisTemplate.opsForValue();

        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put("cid",cid);
        jsonNode.put("tid", getTid(jsonResponse));
        jsonNode.put("amount", paymentDto.getTotalAmount());
        jsonNode.put("itemName", paymentDto.getItemName());
        jsonNode.put("taxFreeAmount", paymentDto.getTaxFreeAmount());
        jsonNode.put("partnerUserId", paymentDto.getPartnerUserId());
        jsonNode.put("partnerOrderId",paymentDto.getPartnerOrderId());
        Long credit = paymentDto.getChargeCredit();
        if (credit != null) {
            jsonNode.put("credit", credit);
        }
        SubscriptionType subscriptionType = paymentDto.getSubscriptionType();
        if (subscriptionType != null) {
            jsonNode.put("subscription", subscriptionType.toString());
        }

        try {
            vop.set(paymentDto.getPartnerOrderId(), objectMapper.writeValueAsString(jsonNode));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("레디스에 들어갈 데이터를 만드는 과정에서 문제가 발생했습니다.");
        }
    }

//    public String generatePageCloseCodeWithAlert(String alertMessage) {
//        String htmlCode = "<!DOCTYPE html><html><head></head><body>";
//        htmlCode += "<script>";
//        htmlCode += "window.onload = function() {";
//        htmlCode += "  alert('" + alertMessage + "');";
//        htmlCode += "  window.close();";
//        htmlCode += "};";
//        htmlCode += "</script>";
//        htmlCode += "</body></html>";
//
//        return htmlCode;
//    }
}