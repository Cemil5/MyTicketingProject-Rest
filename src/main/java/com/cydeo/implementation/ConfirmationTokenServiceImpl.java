package com.cydeo.implementation;

import com.cydeo.entity.ConfirmationToken;
import com.cydeo.exception.TicketingProjectException;
import com.cydeo.repository.ConfirmationTokenRepository;
import com.cydeo.service.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ConfirmationTokenServiceImpl implements ConfirmationTokenService {

    private ConfirmationTokenRepository confirmationTokenRepository;
    private JavaMailSender javaMailSender;

    @Override
    public ConfirmationToken save(ConfirmationToken confirmationToken) {
        return confirmationTokenRepository.save(confirmationToken);
    }

    @Override
    @Async  // to be able to send more than one mail at the same time
    public void sendEmail(SimpleMailMessage email) {
        javaMailSender.send(email);
    }

    @Override
    public ConfirmationToken readByToken(String token) throws TicketingProjectException {
        ConfirmationToken confirmationToken = confirmationTokenRepository.findByToken(token).orElse(null);
        if (confirmationToken==null){
            throw new TicketingProjectException("This token does not exits");
        }
        if (!confirmationToken.isTokenValid(confirmationToken.getExpireDate()) || !confirmationToken.getIsDeleted()){
            throw new TicketingProjectException("This token has been expired");
        }
        return confirmationToken;
    }

    @Override
    public void delete(ConfirmationToken confirmationToken) {
        confirmationToken.setIsDeleted(true);
        confirmationTokenRepository.save(confirmationToken);
    }
}
