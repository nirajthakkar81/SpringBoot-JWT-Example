package com.mycodebuffer.jwt.filter;

import com.mycodebuffer.jwt.service.UserService;
import com.mycodebuffer.jwt.utility.JWTUtilty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JWTFilter extends OncePerRequestFilter {


    @Autowired
    private JWTUtilty jwtUtilty;

    @Autowired
    private UserService userService;

    /*
    For Every API Request this Filter will be invoked to Authenticate Token
     */
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authorization = httpServletRequest.getHeader("Authorization");
        String token = null;
        String userName = null;

        if (null != authorization && authorization.startsWith("Bearer")) {
            token = authorization.substring(7);
            userName = jwtUtilty.getUserNameFromToken(token);
        }

        if (null != userName && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userService.loadUserByUsername(userName);

            if (jwtUtilty.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                        = new UsernamePasswordAuthenticationToken(userDetails, null,
                        userDetails.getAuthorities());

                usernamePasswordAuthenticationToken.setDetails(
                        new WebAuthenticationDetailsSource()
                        .buildDetails(httpServletRequest)
                );
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
            filterChain.doFilter(httpServletRequest, response);
        }
    }
}
