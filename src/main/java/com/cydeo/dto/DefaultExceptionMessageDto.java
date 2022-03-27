package com.cydeo.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder    // we can write our code with "." and without "=" sign
// to be able to carry exception message to json body
public class DefaultExceptionMessageDto {

    private String message;

}
