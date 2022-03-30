package com.cydeo.service;

import com.cydeo.dto.UserDTO;
import com.cydeo.entity.User;
import com.cydeo.exception.TicketingProjectException;

import java.nio.file.AccessDeniedException;
import java.util.List;


public interface UserService {

    List<UserDTO> listAllUsers();
    UserDTO findByUserName(String username) throws AccessDeniedException;
    UserDTO save(UserDTO dto) throws TicketingProjectException;
    UserDTO update(UserDTO dto) throws TicketingProjectException, AccessDeniedException;

    void delete(String username) throws TicketingProjectException, AccessDeniedException;
    void deleteByUsername(String username) throws TicketingProjectException, AccessDeniedException;

    List<UserDTO> listAllByRole(String role);

    Boolean checkIfUserCanBeDeleted(User user);

    UserDTO confirm(User user);

}
