package com.cydeo.converter;

import com.cydeo.dto.UserDTO;
import com.cydeo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.nio.file.AccessDeniedException;

@Component
public class UserDtoConverter implements Converter<String, UserDTO>
{

    @Autowired
    UserService userService;

    @Override
    public UserDTO convert(String source) {
        try {
            return userService.findByUserName(source);
        } catch (AccessDeniedException e) {
            e.printStackTrace();
        }
        return null;
    }


}
