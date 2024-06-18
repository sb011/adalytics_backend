package com.adalytics.adalytics_backend.services;

import com.adalytics.adalytics_backend.enums.ErrorCodes;
import com.adalytics.adalytics_backend.exceptions.BadRequestException;
import com.adalytics.adalytics_backend.models.entities.User;
import com.adalytics.adalytics_backend.models.requestModels.SignupRequestModel;
import com.adalytics.adalytics_backend.repositories.interfaces.IUserRepository;
import com.adalytics.adalytics_backend.services.interfaces.IAuthService;
import com.adalytics.adalytics_backend.utils.FieldValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements IAuthService {
    @Autowired
    private IUserRepository userRepository;

    public void signUp(SignupRequestModel signupRequestModel) {
        if (signupRequestModel.getEmail().isBlank()) {
            throw new BadRequestException("Email is empty.", ErrorCodes.Signup_Email_Invalid.getErrorCode());
        }
        if (signupRequestModel.getPassword().isBlank()) {
            throw new BadRequestException("Password is empty.", ErrorCodes.Signup_Password_Invalid.getErrorCode());
        }
        if (!FieldValidator.isValidEmail(signupRequestModel.getEmail())) {
            throw new BadRequestException("Email is not in right format.", ErrorCodes.Signup_Email_Invalid.getErrorCode());
        }
        if (FieldValidator.isValidPassword(signupRequestModel.getPassword())) {
            throw new BadRequestException("Password is not following rules.", ErrorCodes.Signup_Password_Invalid.getErrorCode());
        }

        User user = userRepository.findByEmail(signupRequestModel.getEmail())
                .orElseThrow(() -> new BadRequestException("Email already exist.", ErrorCodes.Signup_Email_Invalid.getErrorCode()));

        String encodedPassword = new BCryptPasswordEncoder().encode(signupRequestModel.getPassword());
        user.setPassword(encodedPassword);

        userRepository.save(user);
    }
}
