package com.adalytics.adalytics_backend.controllers;

import com.adalytics.adalytics_backend.models.requestModels.LoginRequestModel;
import com.adalytics.adalytics_backend.models.requestModels.SignupRequestModel;
import com.adalytics.adalytics_backend.models.responseModels.LoginResponseModel;
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
    public ResponseEntity<Void> signUp(@RequestBody SignupRequestModel signupRequestModel) {
        authService.signUp(signupRequestModel, ContextUtil.getCurrentOrgId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseModel> login(@RequestBody LoginRequestModel loginRequestModel) {
        return new ResponseEntity<>(authService.login(loginRequestModel), HttpStatus.OK);
    }
}
