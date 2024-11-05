package org.url_shortener.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class ExceptionDto {
    private final String error;
    private final String message;
    private final String path;
    private final int status;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, String> errors;
}
