package com.adalytics.adalytics_backend.services;

import com.adalytics.adalytics_backend.enums.ErrorCodes;
import com.adalytics.adalytics_backend.exceptions.BadRequestException;
import com.adalytics.adalytics_backend.models.requestModels.SignupRequestModel;
import com.adalytics.adalytics_backend.repositories.interfaces.IUserRepository;
import com.adalytics.adalytics_backend.services.interfaces.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements IAuthService {
    @Autowired
    private IUserRepository userRepository;

    public void signUp(SignupRequestModel signupRequestModel) {

    }
}
