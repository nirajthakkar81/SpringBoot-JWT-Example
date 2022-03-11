package com.mycodebuffer.jwt.controller;

import com.mycodebuffer.jwt.model.JWTRequest;
import com.mycodebuffer.jwt.model.JWTResponse;
import com.mycodebuffer.jwt.service.UserService;
import com.mycodebuffer.jwt.utility.JWTUtilty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

   @Autowired
   private JWTUtilty jwtUtilty;

   @Autowired
   private AuthenticationManager authenticationManager;

   @Autowired
   private UserService userService;

    @GetMapping("/")
    public String home() {
        return "Welcome to my API";
    }

    /**
     * This endpoint is responsible to receive username and password, validating it
     * and generating a JWT token in response
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping("/authenticate")
    public JWTResponse authenticate(@RequestBody JWTRequest request) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUserName(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException exception) {
            throw new Exception("Invalid Credentials", exception);
        }

        final UserDetails userDetails
                = userService.loadUserByUsername(request.getUserName());

        final String token = jwtUtilty.generateToken(userDetails);
        return new JWTResponse(token);

    }
}
