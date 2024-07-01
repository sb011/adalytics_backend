package com.adalytics.adalytics_backend.services.interfaces;

import com.adalytics.adalytics_backend.models.requestModels.LoginRequestModel;
import com.adalytics.adalytics_backend.models.requestModels.SignupRequestModel;
import com.adalytics.adalytics_backend.models.responseModels.LoginResponseModel;

public interface IAuthService {
    void signUp(SignupRequestModel signupRequestModel, String organizationId);
    LoginResponseModel login(LoginRequestModel loginRequestModel);
    void verifyEmail(String token);
}
