package com.example.upload.global.exception;

import com.example.upload.global.app.AppConfig;
import com.example.upload.global.dto.Empty;
import com.example.upload.global.dto.RsData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RsData<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        String message = e.getBindingResult().getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + " : " + fe.getCode() + " : "  + fe.getDefaultMessage())
                .sorted()
                .collect(Collectors.joining("\n"));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        new RsData<>(
                                "400-1",
                                message
                        )
                );
    }


    @ResponseStatus
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<RsData<Void>> ServiceExceptionHandle(ServiceException ex) {

        // 개발 모드에서만 작동되도록.
        if(AppConfig.isNotProd()) ex.printStackTrace();

        return ResponseEntity
                .status(ex.getStatusCode())
                .body(
                        new RsData<>(
                                ex.getCode(),
                                ex.getMsg()
                        )
                );
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<RsData<Void>> MethodArgumentTypeMismatchExceptionHandle(MethodArgumentTypeMismatchException ex) {
        
        if(AppConfig.isNotProd()) ex.printStackTrace();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        new RsData<>(
                                "400-1",
                                "잘못된 요청입니다."
                        )
                );
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<RsData<Empty>> handle(MaxUploadSizeExceededException ex) {

        if (AppConfig.isNotProd()) ex.printStackTrace();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new RsData<>(
                        "413-1",
                        "업로드되는 개별 파일의 용량은 %s(을)를 초과할 수 없습니다.".formatted(AppConfig.getSpringServletMultipartMaxFileSize())
                ));
    }

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<RsData<Void>> RuntimeException(RuntimeException ex) {

        if(AppConfig.isNotProd()) ex.printStackTrace();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        new RsData<>(
                                "500",
                                "에러 발생"
                        )
                );
    }
}
