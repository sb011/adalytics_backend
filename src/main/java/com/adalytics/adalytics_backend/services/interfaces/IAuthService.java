package com.adalytics.adalytics_backend.services.interfaces;

import com.adalytics.adalytics_backend.models.requestModels.LoginRequestDTO;
import com.adalytics.adalytics_backend.models.requestModels.SignupRequestDTO;
import com.adalytics.adalytics_backend.models.responseModels.LoginResponseDTO;

public interface IAuthService {
    void signUp(SignupRequestDTO signupRequestDTO, String organizationId);
    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);
    void verifyEmail(String token);
}
