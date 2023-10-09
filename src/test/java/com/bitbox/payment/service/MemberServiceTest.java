package com.bitbox.payment.service;

import com.bitbox.payment.domain.Payment;
import com.bitbox.payment.domain.Subscription;
import com.bitbox.payment.exception.NotFoundException;
import com.bitbox.payment.repository.PaymentRepository;
import com.bitbox.payment.repository.SubscriptionRepository;
import com.bitbox.payment.service.response.PaymentPageCountResponse;
import io.github.bitbox.bitbox.enums.PaymentType;
import io.github.bitbox.bitbox.enums.SubscriptionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class MemberServiceTest {
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private MemberService memberService;
    private final String memberId="csh";

    @BeforeEach
    public void before(){
        subscriptionRepository.save(Subscription.builder()
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .isValid(false)
                .memberId(memberId)
                .subscriptionType(SubscriptionType.ONE_DAY)
                .build());
        paymentRepository.save(Payment.builder()
                .memberId(memberId)
                .paymentDate(LocalDateTime.now())
                .paymentAmount(100L)
                .taxFreeAmount(100L)
                .productName("test")
                .paymentSerial("test")
                .paymentType(PaymentType.KAKAOPAY)
                .build());
    }

    @Test
    public void 유효한_구독권_정보가_존재하지않으므로_isValid가_false로_나와야한다(){
        assertEquals(memberService.getMemberSubscription(memberId).isValid(),false);
    }

    @Test
    public void 구독권_정보가_존재하므로_isValid가_true로_나와야한다(){
        subscriptionRepository.save(Subscription.builder()
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .isValid(true)
                .memberId(memberId)
                .subscriptionType(SubscriptionType.ONE_DAY)
                .build());
        List<Subscription> all = (List<Subscription>) subscriptionRepository.findAll();

        assertEquals(all.size(),2);
        assertEquals(memberService.getMemberSubscription(memberId).isValid(),true);
    }

    @Test
    public void 존재하지_않는_페이지를_요청시_NotFoundException_예외가_발생한다(){
        PageRequest pageable = PageRequest.of(1, 5);
        assertThrows(NotFoundException.class, () -> memberService.getPaymentsByMemberId(memberId, pageable));
    }

    @Test
    public void 존재하는_페이지인경우_정상처리된다(){
        PageRequest pageable = PageRequest.of(0, 5);
        memberService.getPaymentsByMemberId(memberId, pageable);
    }

    @Test
    public void 페이지사이즈가_5이고_데이터가_한개면_총개수는_1이고_페이지개수도_1이다(){
        PaymentPageCountResponse paymentCountByMemberId = memberService.getPaymentCountByMemberId(memberId, 5);
        assertEquals(paymentCountByMemberId.getPageCount(), 1);
        assertEquals(paymentCountByMemberId.getTotalCount(), 1);
    }

    @Test
    public void 페이지사이즈가_2이고_데이터가_세개면_총개수는_3이고_페이지개수는_2이다(){
        for(int i=0;i<2;i++) {
            paymentRepository.save(Payment.builder()
                    .memberId(memberId)
                    .paymentDate(LocalDateTime.now())
                    .paymentAmount(100L)
                    .taxFreeAmount(100L)
                    .productName("test")
                    .paymentSerial("test")
                    .paymentType(PaymentType.KAKAOPAY)
                    .build());
        }

        PaymentPageCountResponse paymentCountByMemberId = memberService.getPaymentCountByMemberId(memberId, 2);
        assertEquals(paymentCountByMemberId.getPageCount(), 2);
        assertEquals(paymentCountByMemberId.getTotalCount(), 3);
    }

}