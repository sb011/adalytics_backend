package com.adalytics.adalytics_backend.services;

import com.adalytics.adalytics_backend.enums.ErrorCodes;
import com.adalytics.adalytics_backend.enums.Role;
import com.adalytics.adalytics_backend.exceptions.BadRequestException;
import com.adalytics.adalytics_backend.exceptions.NotFoundException;
import com.adalytics.adalytics_backend.models.entities.User;
import com.adalytics.adalytics_backend.models.requestModels.LoginRequestModel;
import com.adalytics.adalytics_backend.models.requestModels.SignupRequestModel;
import com.adalytics.adalytics_backend.models.responseModels.LoginResponseModel;
import com.adalytics.adalytics_backend.repositories.interfaces.IUserRepository;
import com.adalytics.adalytics_backend.services.interfaces.IAuthService;
import com.adalytics.adalytics_backend.utils.AuthHelper;
import com.adalytics.adalytics_backend.utils.FieldValidator;
import com.adalytics.adalytics_backend.utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements IAuthService {
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private JWTUtil jwtUtil;

    @Override
    public void signUp(SignupRequestModel signupRequestModel, String organizationId) {
        if (signupRequestModel.getEmail().isBlank()) {
            throw new BadRequestException("Email is empty.", ErrorCodes.Signup_Email_Invalid.getErrorCode());
        }
        if (signupRequestModel.getPassword().isBlank()) {
            throw new BadRequestException("Password is empty.", ErrorCodes.Signup_Password_Invalid.getErrorCode());
        }
        if (!FieldValidator.isValidEmail(signupRequestModel.getEmail())) {
            throw new BadRequestException("Email is not in right format.", ErrorCodes.Signup_Email_Invalid.getErrorCode());
        }
        if (!FieldValidator.isValidPassword(signupRequestModel.getPassword())) {
            throw new BadRequestException("Password is not strong.", ErrorCodes.Signup_Password_Invalid.getErrorCode());
        }

        Optional<User> existingUser = userRepository.findByEmail(signupRequestModel.getEmail());
        if (existingUser.isPresent()) {
            throw new BadRequestException("Email already exist.", ErrorCodes.Signup_Email_Invalid.getErrorCode());
        }

        String encodedPassword = new BCryptPasswordEncoder().encode(signupRequestModel.getPassword());
        User newUser = User.builder()
                .email(signupRequestModel.getEmail())
                .username(AuthHelper.getUsernameFromEmail(signupRequestModel.getEmail()))
                .password(encodedPassword)
                .organizationId(organizationId)
                .role(Role.USER.name()).build();

        userRepository.save(newUser);
    }

    @Override
    public LoginResponseModel login(LoginRequestModel loginRequestModel) {
        if (loginRequestModel.getEmail().isBlank()) {
            throw new BadRequestException("Email is empty.", ErrorCodes.Login_Email_Invalid.getErrorCode());
        }
        if (loginRequestModel.getPassword().isBlank()) {
            throw new BadRequestException("Password is empty.", ErrorCodes.Login_Password_Invalid.getErrorCode());
        }
        if (!FieldValidator.isValidEmail(loginRequestModel.getEmail())) {
            throw new BadRequestException("Email is not in right format.", ErrorCodes.Signup_Email_Invalid.getErrorCode());
        }

        User user = userRepository.findByEmail(loginRequestModel.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found.", ErrorCodes.User_Not_Found.getErrorCode()));

        boolean isPasswordMatching = new BCryptPasswordEncoder().matches(loginRequestModel.getPassword(), user.getPassword());
        if (!isPasswordMatching) {
            throw new BadRequestException("Password not matching.", ErrorCodes.Password_Not_Matching.getErrorCode());
        }

        String token = jwtUtil.generateToken(user.getId());
        LoginResponseModel loginResponseModel = new LoginResponseModel();
        loginResponseModel.setToken(token);
        return loginResponseModel;
    }
}
