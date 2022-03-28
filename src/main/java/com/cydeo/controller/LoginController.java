package com.cydeo.controller;

import com.cydeo.annotation.DefaultExceptionMessage;
import com.cydeo.dto.MailDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.entity.ConfirmationToken;
import com.cydeo.entity.ResponseWrapper;
import com.cydeo.entity.User;
import com.cydeo.entity.common.AuthenticationRequest;
import com.cydeo.exception.TicketingProjectException;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.service.ConfirmationTokenService;
import com.cydeo.service.UserService;
import com.cydeo.util.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//@Controller
@RestController
@Tag(name = "Authentication Controller", description = "Authenticate API")
public class LoginController {

	@Value("${app.local-url}")
	private String BASE_URL;

	private AuthenticationManager authenticationManager;
	private UserService userService;
	private MapperUtil mapperUtil;
	private JWTUtil jwtUtil;
	private ConfirmationTokenService confirmationTokenService;

	public LoginController(AuthenticationManager authenticationManager, UserService userService, MapperUtil mapperUtil, JWTUtil jwtUtil, ConfirmationTokenService confirmationTokenService) {
		this.authenticationManager = authenticationManager;
		this.userService = userService;
		this.mapperUtil = mapperUtil;
		this.jwtUtil = jwtUtil;
		this.confirmationTokenService = confirmationTokenService;
	}

	@PostMapping("/authenticate")
	@DefaultExceptionMessage(defaultMessage = "Bad Credentials")
	public ResponseEntity<ResponseWrapper> doLogin(@RequestBody AuthenticationRequest authenticationRequest) throws TicketingProjectException {

		String username = authenticationRequest.getUsername();
		String password = authenticationRequest.getPassword();

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, password);
		authenticationManager.authenticate(authentication);
		UserDTO foundUser = userService.findByUserName(username);
		User convertedUser = mapperUtil.convert(foundUser, new User());

		if (!foundUser.isEnabled()){
			throw new TicketingProjectException("Please verify your user");
		}
		String jwtToken = jwtUtil.generateToken(convertedUser);
		return ResponseEntity.ok(new ResponseWrapper("Login Successfully", jwtToken));
	}

	@PostMapping("/create-user")
	@DefaultExceptionMessage(defaultMessage = "Something went wrong, try again!")
	@Operation(summary = "Create new account")
	private ResponseEntity<ResponseWrapper> doRegister(@RequestBody UserDTO userDTO) throws TicketingProjectException {

		UserDTO createdUser = userService.save(userDTO);
		sendEmail(createEmail(createdUser));

		return ResponseEntity.ok(new ResponseWrapper("User has been created! \n Please confirm your email!", createdUser));
	}

	@PostMapping("/confirmation")
	@DefaultExceptionMessage(defaultMessage = "Failed to confirm email, try again!")
	@Operation(summary = "Confirm account")
	public ResponseEntity<ResponseWrapper> confirmEmail(@RequestParam("token") String token) throws TicketingProjectException {
		ConfirmationToken confirmationToken = confirmationTokenService.readByToken(token);
		UserDTO confirmUser = userService.confirm(confirmationToken.getUser());
		confirmationTokenService.delete(confirmationToken);
		return ResponseEntity.ok(new ResponseWrapper("User has been confirmed!", confirmUser));
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


	// from MVC security
	/*
	@GetMapping(value = {"/login", "/"})
	public String login(){
		return "/login";
	}
	
	@RequestMapping("/welcome")
	public String welcome(){
		return "welcome";
	}
*/
}
