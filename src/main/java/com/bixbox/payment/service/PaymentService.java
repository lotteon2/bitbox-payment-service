package com.bixbox.payment.service;

import com.bixbox.payment.domain.Payment;
import com.bixbox.payment.domain.Subscription;
import com.bixbox.payment.dto.KakaoPayDto;
import com.bixbox.payment.exception.SubscriptionExistException;
import com.bixbox.payment.repository.PaymentRepository;
import com.bixbox.payment.repository.SubscriptionRepository;
import com.bixbox.payment.util.KafkaUtil;
import com.bixbox.payment.util.KakaoPayUtil;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final SubscriptionRepository subscriptionRepository;
    private final PaymentRepository paymentRepository;
    private final KakaoPayUtil kakaoPayUtil;
    private final KafkaUtil kafkaUtil;

    @Transactional
    public void createPayment(KakaoPayDto kakaoPayDto){
        // 결제 테이블(payment)에 결제 정보를 insert 한다
        paymentRepository.save(Payment.createKakaoPayDtoToPayment(kakaoPayDto));
        
        if(kakaoPayDto.getCredit() == null){ // 구독권 결제인 경우 구독권 관련 테이블에 insert 후 early return
            subscriptionRepository.findByMemberIdAndIsValidTrue(kakaoPayDto.getPartnerUserId()).ifPresent(subscription -> {
                throw new SubscriptionExistException("구독권 정보가 존재합니다");
            });
            subscriptionRepository.save(Subscription.createKakaoPayDtoToSubscription(kakaoPayDto));
            return;
        }
        
        // 유저 크레딧에 카프카를 전달하고 그 후 카카오페이 승인 API를 호출한다. 여기서 예외 발생시 다시 카프카에 빼라는 요청을 보냄
        kafkaUtil.callKafkaWithKakaoPayDto(kakaoPayDto, kakaoPayDto.getCredit());
        if(kakaoPayUtil.callKakaoApproveApi(kakaoPayDto) != HttpStatus.SC_OK){ // 정상적으로 처리되지 않았으면 크레딧 빼라는 카프카 보냄
            kafkaUtil.callKafkaWithKakaoPayDto(kakaoPayDto, -kakaoPayDto.getCredit());
        }

        /*
            어차피 정밀도에는 어디선가 삑이날 수 밖에 없음
            
            비동기로 하면 아마 로직이 (1) [결제 -> 카프카 비동기 호출 -> 카카오 승인 API 호출] 이런식으로 진행이 됨
                                   (2) [카프카 Listen -> 크레딧 더하기] 이런식으로 두 갈래로 진행이 됨
            이러면 결제는 처리되었는데 크레딧이 없거나 혹은 반대로 크레딧은 있는데 결제는 처리안된 케이스가 존재할 수 있음
            거기다가 여기서 예외를 처리하는게 조금 복잡할 수 있음 예를 들면 (1)하고 (2)에서 예외가 발생할 수 있는데 각각의 해결법은 아래와 같음
            (1)에서 예외 발생시 카프카에 다시 크레딧을 차감하라는 요청을 보냄
            (2)에서 예외 발생시 카프카에 카카오페이 취소 요청을 보냄
            -> 비동기로하면 속도에 대한 이점을 얻을 수 있으나 처리속도에 (네트워크 지연 or 카프카 죽음) 이런 케이스에 대해서는 문제가 발생함
            근데 솔직히 너무 극적인 상황(괄호친 부분)만 아니면 매우 이점을 얻을 수 있을 것이라 생각이 된다. 극적인 상황만 아니면
            결과적으로 정밀도는 정확하기 때문이다
            
            동기로하면 아마 로직이 [결제 -> 크레딧 결제 확인 -> API 승인 호출]인데 근데 만약에 API 승인 호출에서 예외가 터지면
            보상패턴으로 크레딧을 차감하라는 요청을 보낼것임 근데, 이거 조차도 동기로 던져서 해결하려 한다면 정밀도에서는 완벽함
            만약 보상패턴을 비동기로 하면 그럼 이거는 사실 모든걸 비동기로 처리했던것과 크게 다르지 않음 아무튼 모든걸 동기로하면
            응답속도 부분에서는 최악임. 트랜잭션 독립성에 관한 이론도 보면 2단계(커밋된 읽기)를 권장하고 있음

            즉, 나는 비동기로 모든 사항을 처리해도 좋을 것 같음 모든걸 비동기로 하되, 아래와 같은 사항을 항상 인지하고 있어야한다

            1. 카프카에 객체를 던졌는데 받아주는 쪽이 죽은 상황(이건 모니터링을 적극 활용하여 수동 개입을 해야함) -> 쿠버네티스 쓰던가
            2. 모놀리식 코드 보다 좀 더 코드리뷰를 많이 해야할듯?
            3. 응답속도 문제로 정밀도가 안맞는 케이스를 어떻게 할 것인지?( 비동기로 처리 했을 때 (2)에서 예외가 발생해서 카카오 페이 취소
            API가 호출이 되었는데 그 시점이 결제 모듈에서 카카오 승인 API 호출하는 시점보다 앞당겨졌으면 ? )
            4. 확실한 조건이 있어야함(각각의 모듈은 카프카에서 요청이 왔을때 특정 동작에 대해서 확실하게 처리를 한다는 조건임) -> 즉, 각각의 모듈 담당자가
            확실하게 구현을 제대로 했을 것이라는 가정을 해야한다. ( 테스팅 및 통합 테스팅을 깐깐하게 해야함)
         */
    }
}