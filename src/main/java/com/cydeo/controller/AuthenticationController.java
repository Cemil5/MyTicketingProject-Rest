package com.cydeo.controller;



import com.cydeo.entity.ResponseWrapper;
import com.cydeo.entity.User;
import com.cydeo.entity.common.AuthenticationRequest;
import com.cydeo.service.UserService;
import com.cydeo.util.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.hibernate.service.spi.ServiceException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Tag(name = "Authentication controller", description = "Authenticate API")
public class AuthenticationController {

    private UserService userService;
    private AuthenticationManager authenticationManager;
    private JWTUtil jwtUtil;
/*

    @PostMapping("/authenticate")
    @DefaultExceptionMessage(defaultMessage = "Bad Credentials")
    @Operation(summary = "Login to application")
    public ResponseEntity<ResponseWrapper> doLogin(@RequestBody AuthenticationRequest authenticationRequest){
        String username = authenticationRequest.getUsername();
        String password = authenticationRequest.getPassword();

        User foundUser = userService.readByUsername(username);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        authenticationManager.authenticate(authenticationToken);    // just authenticated with database

        String jwtToken = jwtUtil.generateToken(foundUser);     // token is generated
        return ResponseEntity.ok(new ResponseWrapper("Login successfull!", jwtToken));
    }

    @PostMapping("/create-user")
    @DefaultExceptionMessage(defaultMessage = "Failed to create, please, try again later")
    @Operation(summary = "Create a new user")
    public ResponseEntity<ResponseWrapper> createAccount(@RequestBody User user) throws ServiceException {
        User createdUser = userService.createUser(user);
        return ResponseEntity.ok(new ResponseWrapper("User has been created successfully", createdUser));
    }
*/

}
