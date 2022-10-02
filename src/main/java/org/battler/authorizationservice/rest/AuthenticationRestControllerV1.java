package org.battler.authorizationservice.rest;

import com.sun.xml.bind.v2.TODO;
import lombok.extern.slf4j.Slf4j;
import org.battler.authorizationservice.dto.AuthenticationRequestDto;
import org.battler.authorizationservice.dto.AuthenticationResponseDto;
import org.battler.authorizationservice.model.User;
import org.battler.authorizationservice.security.jwt.JwtTokenProvider;
import org.battler.authorizationservice.security.jwt.JwtUser;
import org.battler.authorizationservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping(value = "/api/v1/auth/")
@Slf4j
public class AuthenticationRestControllerV1 {

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    private final UserService userService;

    @Autowired
    public AuthenticationRestControllerV1(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @PostMapping("check")
    public String check (@CurrentSecurityContext(expression = "authentication")Authentication authentication) {
        return ((JwtUser)authentication.getPrincipal()).getUsername();
    }

    @PostMapping("login")
    public ResponseEntity login(@RequestBody AuthenticationRequestDto requestDto){
        try {
            String username = requestDto.getUsername();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, requestDto.getPassword()));
            User user = userService.findByUsername(username);

            if (user == null){
                throw new UsernameNotFoundException("User with username: " + username + " not found");
            }

            String token = jwtTokenProvider.createToken(username, user.getRoles());
            log.info("IN login - token: {} successfully created", token);

            AuthenticationResponseDto responseDto = new AuthenticationResponseDto();
            responseDto.setUsername(username);
            responseDto.setToken(token);

            /*Map<Object, Object> response = new HashMap<>();
            response.put("username", username);
            response.put("token", token);*/

            return ResponseEntity.ok(responseDto);
        }catch (AuthenticationException e){
            throw new BadCredentialsException("Invalid username or password");
        }
    }
}
