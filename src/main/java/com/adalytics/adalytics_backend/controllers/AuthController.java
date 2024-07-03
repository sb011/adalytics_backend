package com.adalytics.adalytics_backend.controllers;

import com.adalytics.adalytics_backend.enums.Role;
import com.adalytics.adalytics_backend.models.requestModels.LoginRequestDTO;
import com.adalytics.adalytics_backend.models.requestModels.SignupRequestDTO;
import com.adalytics.adalytics_backend.models.responseModels.LoginResponseDTO;
import com.adalytics.adalytics_backend.services.interfaces.IAuthService;
import com.adalytics.adalytics_backend.utils.ContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    private IAuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(@RequestBody SignupRequestDTO signupRequestDTO) {
        signupRequestDTO.setRole(Role.USER.name());
        authService.signUp(signupRequestDTO, ContextUtil.getCurrentOrgId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        return new ResponseEntity<>(authService.login(loginRequestDTO), HttpStatus.OK);
    }
}
