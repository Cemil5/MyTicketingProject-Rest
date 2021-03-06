package com.cydeo.exception;

import com.cydeo.dto.DefaultExceptionMessageDto;
import com.cydeo.entity.ResponseWrapper;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;
import java.nio.file.AccessDeniedException;
import java.util.Optional;

@RestControllerAdvice // we don't have create instance from this class. It will run globally
@Order(Ordered.HIGHEST_PRECEDENCE) // we give high priority to custom exception handling than spring exception handling
public class ExceptionMessageHandler {

    @ExceptionHandler(TicketingProjectException.class)   // we use our ServiceException not Spring's same class
    public ResponseEntity<ResponseWrapper> ticketingException(TicketingProjectException exception){
        String message = exception.getMessage();
        return new ResponseEntity<>(ResponseWrapper.builder()
                .success(false)
                .code(HttpStatus.CONFLICT.value())
                .message(message)
                .build(), HttpStatus.CONFLICT);
    }

    // if any user is not authorized for endpoint (role is wrong), throws this exception instead of Spring's same class
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseWrapper> accessDeniedException(AccessDeniedException se){
        String message = se.getMessage();   // if we override spring's message, it shows ours message.
        return new ResponseEntity<>(ResponseWrapper.builder()
                .success(false)
                .code(HttpStatus.FORBIDDEN.value())
                .message(message)
                .build(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({Exception.class, RuntimeException.class, Throwable.class, BadCredentialsException.class})
    public ResponseEntity<ResponseWrapper> genericException(Throwable e, HandlerMethod handlerMethod) {

        // if we have related message at our annotation package, send this, if not then throws Spring's message
        Optional<DefaultExceptionMessageDto> defaultMessage = getMessageFromAnnotation(handlerMethod.getMethod());
        if (defaultMessage.isPresent() && !ObjectUtils.isEmpty(defaultMessage.get().getMessage())) {
            ResponseWrapper response = ResponseWrapper
                    .builder()
                    .success(false)
                    .message(defaultMessage.get().getMessage())
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(ResponseWrapper.builder()
                .success(false).message("Action failed: An error occurred!")
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Optional<DefaultExceptionMessageDto> getMessageFromAnnotation(Method method) {
        com.cydeo.annotation.DefaultExceptionMessage defaultExceptionMessage = method
                .getAnnotation(com.cydeo.annotation.DefaultExceptionMessage.class);
        if (defaultExceptionMessage != null) {
            DefaultExceptionMessageDto defaultExceptionMessageDto = DefaultExceptionMessageDto
                    .builder()
                    .message(defaultExceptionMessage.defaultMessage())
                    .build();
            return Optional.of(defaultExceptionMessageDto);
        }
        return Optional.empty();
    }
}
