package com.cydeo.service;

import com.cydeo.entity.ConfirmationToken;
import com.cydeo.exception.TicketingProjectException;
import lombok.Setter;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

public interface ConfirmationTokenService {

    ConfirmationToken save(ConfirmationToken confirmationToken);
    void sendEmail(SimpleMailMessage email);
    ConfirmationToken readByToken(String token) throws TicketingProjectException;
    void delete(ConfirmationToken confirmationToken);

}
