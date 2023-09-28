package com.bixbox.payment.exception.advice;

import com.bixbox.payment.exception.KakaoPayArgumentException;
import com.bixbox.payment.exception.KakaoPayFailException;
import com.bixbox.payment.exception.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiControllerAdvice {
    @ExceptionHandler(KakaoPayFailException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorResponse kakaoExceptionHandler(KakaoPayFailException e){
        //log.error();
        return ErrorResponse.builder()
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class) // 아규먼트 존재 안하는 케이스
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse processValidationError(MethodArgumentNotValidException e) {
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
    @ResponseBody
    public ErrorResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        //log.error();
        return ErrorResponse.builder()
                .message("잘못된 형식의 파라미터를 넘겼습니다.")
                .build();
    }

    @ExceptionHandler(KakaoPayArgumentException.class) // 커스텀적인 카카오페이 아규먼트 예외
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleHttpMessageNotReadableException(KakaoPayArgumentException e) {
        //log.error();
        return ErrorResponse.builder()
                .message(e.getMessage())
                .build();
    }
}

// TODO 시간남으면 logback 설정(error만 저장하게끔)