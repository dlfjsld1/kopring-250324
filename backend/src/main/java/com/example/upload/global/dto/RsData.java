package com.example.upload.global.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.lang.NonNull;

@AllArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsData<T> {

    public static final RsData<Empty> OK = new RsData<>("200-1", "OK", new Empty());

    @NonNull
    private String code;
    @NonNull
    private String msg;
    @NonNull
    private T data;

    public RsData(String code, String msg) {
        this(code, msg, (T) new Empty());
    }

    @JsonIgnore
    public int getStatusCode() {
        String statusCodeStr = code.split("-")[0];
        return Integer.parseInt(statusCodeStr);
    }

    @JsonIgnore
    public boolean isSuccess() {
        return getStatusCode() < 400;
    }

    @JsonIgnore
    public boolean isFail() {
        return !isSuccess();
    }

    public <T> RsData<T> newDataOf(T data) {
        return new RsData<>(code, msg, data);
    }
}
