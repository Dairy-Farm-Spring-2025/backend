package com.capstone.dfms.components.exceptions;

import com.capstone.dfms.components.apis.CoreApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BindException.class)
    public CoreApiResponse<?> handleBindException(BindException e) {
        List<String> errorMessages = new ArrayList<>();
        BindingResult bindingResult = e.getBindingResult();
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                String errorMessage = fieldError.getDefaultMessage();
                errorMessages.add(errorMessage);
            }
        }
        return CoreApiResponse.error(HttpStatus.BAD_REQUEST, "Request không hợp lệ", errorMessages);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CoreApiResponse<?> handleBindException(MethodArgumentNotValidException e) {
        List<String> errorMessages = new ArrayList<>();
        BindingResult bindingResult = e.getBindingResult();
        if (bindingResult.hasErrors()) {
            List<ObjectError> fieldErrors = bindingResult.getAllErrors();
            for (ObjectError fieldError : fieldErrors) {
                String errorMessage = fieldError.getDefaultMessage();
                errorMessages.add(errorMessage);
            }
        }
        return CoreApiResponse.error(HttpStatus.BAD_REQUEST, "Request không hợp lệ", errorMessages);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public CoreApiResponse<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        String errorMessage = "Invalid request format";
        return CoreApiResponse.error(HttpStatus.BAD_REQUEST,errorMessage);
    }


    @ExceptionHandler(MissingServletRequestParameterException.class)
    public CoreApiResponse<?> handleMissingRequestParam(MissingServletRequestParameterException e) {
        String paramName = e.getParameterName();
        String errorMessage = "Missing or invalid request parameter: " + paramName;
        return CoreApiResponse.error(HttpStatus.BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler({Exception.class,AppException.class, BadCredentialsException.class, DataNotFoundException.class, AccessDeniedException.class, AuthenticationException.class})
    public CoreApiResponse<?> handleCoreException(Exception error) {
        if(error instanceof AppException){
            AppException appException =(AppException) error;
            return CoreApiResponse.error(HttpStatus.valueOf(appException.getCode()), appException.getMessage(),appException.getData());
        }else if(error instanceof BadCredentialsException){
            BadCredentialsException badCredentialsException = (BadCredentialsException) error;
            return CoreApiResponse.success("Email or password is incorrect");
        }else if(error instanceof DataNotFoundException){
            DataNotFoundException resourceException = (DataNotFoundException) error;
            return CoreApiResponse.error(HttpStatus.NOT_FOUND, resourceException.getMessage());
        }else if(error instanceof AccessDeniedException){
            AccessDeniedException accessDeniedException = (AccessDeniedException) error;
            return CoreApiResponse.error(HttpStatus.FORBIDDEN, accessDeniedException.getMessage());
        }else if(error instanceof AuthenticationException){
            AuthenticationException authenticationException = (AuthenticationException) error;
            return CoreApiResponse.error(HttpStatus.UNAUTHORIZED, authenticationException.getMessage());
        }
        log.error("An error occurred: " + error.getMessage(), error);
        return CoreApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR,"Unknown error");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public CoreApiResponse<?> handleException(DataIntegrityViolationException ex, WebRequest request) {
        log.error("DataIntegrity Violation Exception ::: {}", ex);

        String message = ex.getMostSpecificCause().getMessage();

        if (message != null) {
            message = message.split("for")[0];
        }
        return CoreApiResponse.error(HttpStatus.BAD_REQUEST,"Lỗi: Thao tác không thể hoàn thành vì liên quan đến dữ liệu khác.");
    }

}

