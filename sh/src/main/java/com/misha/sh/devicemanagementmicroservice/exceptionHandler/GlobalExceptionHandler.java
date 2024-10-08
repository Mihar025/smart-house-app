package com.misha.sh.devicemanagementmicroservice.exceptionHandler;


import com.misha.sh.devicemanagementmicroservice.exception.BusinessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ExceptionResponse> handleException( BusinessException ex) {
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(BusinessErrorCode.BUSINESS_ERROR_CODE.getCode())
                                .businessErrorDescription(BusinessErrorCode.BUSINESS_ERROR_CODE.getDescription())
                                .error(ex.getMessage())
                                .build()
                );


    }
}
