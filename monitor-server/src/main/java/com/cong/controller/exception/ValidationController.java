package com.cong.controller.exception;

import com.cong.entity.RestBean;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ValidationController {
  //validate the request parameters
  @ExceptionHandler(ValidationException.class)
  public RestBean<?> validationException(ValidationException e) {
    log.warn("Resolve [{} : {}]" ,e.getClass().getName(), e.getMessage());
    return RestBean.fail(400, "Request Parameters Error");
  }
}
