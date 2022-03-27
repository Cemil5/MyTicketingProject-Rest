package com.cydeo.service;

import com.cydeo.dto.UserDTO;
import com.cydeo.entity.User;
import com.cydeo.exception.TicketingProjectException;

import java.util.List;


public interface UserService {

    List<UserDTO> listAllUsers();
    UserDTO findByUserName(String username);
    UserDTO save(UserDTO dto) throws TicketingProjectException;
    UserDTO update(UserDTO dto);

    void delete(String username) throws TicketingProjectException;
    void deleteByUsername(String username) throws TicketingProjectException;

    List<UserDTO> listAllByRole(String role);

    Boolean checkIfUserCanBeDeleted(User user);

}
