package com.cydeo.controller;

import com.cydeo.annotation.DefaultExceptionMessage;
import com.cydeo.dto.MailDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.entity.ConfirmationToken;
import com.cydeo.entity.ResponseWrapper;
import com.cydeo.entity.User;
import com.cydeo.exception.TicketingProjectException;
import com.cydeo.util.MapperUtil;
import com.cydeo.service.ConfirmationTokenService;
import com.cydeo.service.RoleService;
import com.cydeo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


//@Controller
@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "User Controller", description = "User API")
public class UserController {

    @Value("${app.local-url}")
    private String BASE_URL;

    private UserService userService;
    private MapperUtil mapperUtil;
    private ConfirmationTokenService confirmationTokenService;
    private RoleService roleService;

    public UserController(UserService userService, MapperUtil mapperUtil, ConfirmationTokenService confirmationTokenService, RoleService roleService) {
        this.userService = userService;
        this.mapperUtil = mapperUtil;
        this.confirmationTokenService = confirmationTokenService;
        this.roleService = roleService;
    }

    @PostMapping("/create-user")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
    @Operation(summary = "Create new account")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<ResponseWrapper> doRegister(@RequestBody UserDTO userDTO) throws TicketingProjectException {

        UserDTO createdUser = userService.save(userDTO);
        sendEmail(createEmail(createdUser));

        return ResponseEntity.ok(new ResponseWrapper("User has been created! \n Please confirm your email!", createdUser));
    }

    @GetMapping
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
    @Operation(summary = "Read all user")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<ResponseWrapper> readAll(){
        List<UserDTO> list = userService.listAllUsers();
        return ResponseEntity.ok(new ResponseWrapper("Successfully retrieved users", list));
    }

    @GetMapping("/{username}")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
    @Operation(summary = "Read one user by username")
    //@PreAuthorize("hasAuthority('Admin')")    // only admin should see all users, current user should see his/her profile
    public ResponseEntity<ResponseWrapper> readByUsername(@PathVariable("username") String username){
        UserDTO dto = userService.findByUserName(username);
        return ResponseEntity.ok(new ResponseWrapper("Successfully retrieved the user", dto));
    }

    @PostMapping
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
    @Operation(summary = "Update an user")
    //@PreAuthorize("hasAuthority('Admin')")    // only admin should see all users, current user should see his/her profile
    public ResponseEntity<ResponseWrapper> updateByUsername(@RequestBody UserDTO user) throws TicketingProjectException {
        UserDTO dto = userService.update(user);
        return ResponseEntity.ok(new ResponseWrapper("Successfully updated the user", dto));
    }

    @DeleteMapping("/{username}")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
    @Operation(summary = "Delete one user by username")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<ResponseWrapper> deleteUser(@PathVariable("username") String username) throws TicketingProjectException {
        userService.deleteByUsername(username);
        return ResponseEntity.ok(new ResponseWrapper("Successfully deleted"));
    }

    @GetMapping("/role")
    @DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
    @Operation(summary = "Read all users by role")
    @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
    public ResponseEntity<ResponseWrapper> readByRole(@RequestParam String role){
        List<UserDTO> list = userService.listAllByRole(role);
        return ResponseEntity.ok(new ResponseWrapper("Successfully read users by role", list));
    }




    private MailDTO createEmail(UserDTO userDTO){

        User user = mapperUtil.convert(userDTO, new User());
        ConfirmationToken confirmationToken = new ConfirmationToken(user);
        confirmationToken.setIsDeleted(false);

        ConfirmationToken createdConfirmationToken = confirmationTokenService.save(confirmationToken);
        return MailDTO.builder().emailTo(user.getUserName())
                .token(createdConfirmationToken.getToken())
                .subject("Confirm Registration")
                .message("To confirm your account, please click here: \n")
                .url(BASE_URL + "/confirmation?token=")
                .build();
    }

    private void sendEmail(MailDTO mailDTO){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(mailDTO.getEmailTo());
        mailMessage.setSubject(mailDTO.getSubject());
        mailMessage.setText(mailDTO.getMessage() + mailDTO.getUrl() + mailDTO.getToken());
        confirmationTokenService.sendEmail(mailMessage);
    }







    // from MVC
   /* RoleService roleService;
    UserService userService;

    @GetMapping({"/create", "/add", "/initialize"})
    public String  createUser(Model model){

        model.addAttribute("user", new UserDTO());
        model.addAttribute("roles", roleService.listAllRoles());
     //   System.out.println("controller : " + roleService.listAllRoles().get(0).getDescription());
        model.addAttribute("users", userService.listAllUsers());

        return "/user/create";
    }


    @PostMapping("/create")
    public String insertUser(UserDTO user, Model model) throws TicketingProjectException {

        userService.save(user);

//        model.addAttribute("user", new UserDTO());
//        model.addAttribute("roles", roleService.findAll());
//        model.addAttribute("users", userService.findAll());
//        return "user/create";

        // instead of writing codes above, we use redirect keyword.
        return "redirect:/user/create";
    }

    @GetMapping("/update/{username}")
    public String editUser(@PathVariable String username, Model model){
        model.addAttribute("user", userService.findByUserName(username));
        model.addAttribute("users", userService.listAllUsers());
        model.addAttribute("roles", roleService.listAllRoles());
        return "user/update";
    }

    @PostMapping("/update/{username}")
    public String updateUser(@PathVariable String username, UserDTO user, Model model){

        userService.update(user);
//        model.addAttribute("user", new UserDTO());
//        model.addAttribute("roles", roleService.findAll());
//        model.addAttribute("users", userService.findAll());
//        return "user/create";

        // instead of writing codes above, we use redirect keyword.
        return "redirect:/user/create";
    }

    @GetMapping("/delete/{username}")
    public String prepToDeleteUser (@PathVariable String username) throws TicketingProjectException {
        userService.deleteByUsername(username);
        return "redirect:/user/create";
    }*/
}
