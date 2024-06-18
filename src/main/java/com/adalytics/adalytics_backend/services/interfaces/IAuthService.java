package com.adalytics.adalytics_backend.services.interfaces;

import com.adalytics.adalytics_backend.models.requestModels.SignupRequestModel;

public interface IAuthService {
    void signUp(SignupRequestModel signupRequestModel);
}
