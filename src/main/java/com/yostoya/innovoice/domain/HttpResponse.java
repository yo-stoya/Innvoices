package com.yostoya.innovoice.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

@Data
@SuperBuilder
@JsonInclude(NON_DEFAULT)
public class HttpResponse {

//    @JsonFormat(shape = STRING, pattern = "MM-dd-yyyy HH:mm:ss")
    protected String timestamp;
    protected int statusCode;
    protected HttpStatus status;
    protected String reason;
    protected String message;
    protected String developerMessage;
    protected Map<?, ?> data;
}
