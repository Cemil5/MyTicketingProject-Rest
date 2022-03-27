package com.cydeo.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MailDTO {

    private String  emailTo;
    private String emailFrom;
    private String message;
    private String token;   // confirmation must be unique for each email and must be deleted just after first confirmation
    private String subject;
    private String url;

}
