package com.bitbox.payment.exception.advice;

import com.bitbox.payment.exception.*;
import com.bitbox.payment.exception.response.ErrorResponse;
import com.bitbox.payment.util.KakaoPayUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.kafka.KafkaException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class ApiControllerAdvice {
    //KafkaSubmitException, UrgentMailException
    private final KakaoPayUtil kakaoPayUtil;

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse notFoundException(NotFoundException e) {
        return ErrorResponse.builder()
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(SubscriptionExistException.class)
    public String handleSubscriptionExistException(SubscriptionExistException e) {
        return kakaoPayUtil.generatePageCloseCodeWithAlert(e.getMessage());
    }

    @ExceptionHandler(KakaoPayFailException.class)
    public String handleKakaoPayFailException(KakaoPayFailException e) {
        return kakaoPayUtil.generatePageCloseCodeWithAlert(e.getMessage());
    }

    @ExceptionHandler(KakaoPayReadyException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorResponse handleKakaoPayReadyException(KakaoPayFailException e){
        return ErrorResponse.builder()
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class) // 아규먼트 존재 안하는 케이스
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationError(MethodArgumentNotValidException e) {
        //log.error();

        return ErrorResponse.builder()
                .message(e.getBindingResult()
                        .getAllErrors()
                        .get(0)
                        .getDefaultMessage())
                .build();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class) // 아규먼트 변환이 불가한 경우
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        //log.error();
        return ErrorResponse.builder()
                .message("잘못된 형식의 파라미터를 넘겼습니다.")
                .build();
    }

    @ExceptionHandler(KakaoPayArgumentException.class) // 커스텀적인 카카오페이 아규먼트 예외
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleHttpMessageNotReadableException(KakaoPayArgumentException e) {
        //log.error();
        return ErrorResponse.builder()
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(KafkaException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorResponse handleKafkaException(KafkaException e){
        //TODO 카프카 예외 발생시 어떻게 처리할까?(카프카에 보내는것이 불가능한 케이스임)
        return ErrorResponse.builder()
                .message("카프카 서버에 접속할 수 없습니다.")
                .build();
    }

}

// TODO 시간남으면 logback 설정(error만 저장하게끔)