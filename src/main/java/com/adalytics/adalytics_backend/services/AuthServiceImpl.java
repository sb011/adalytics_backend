package com.adalytics.adalytics_backend.services;

import com.adalytics.adalytics_backend.enums.ErrorCodes;
import com.adalytics.adalytics_backend.exceptions.BadRequestException;
import com.adalytics.adalytics_backend.exceptions.NotFoundException;
import com.adalytics.adalytics_backend.models.entities.User;
import com.adalytics.adalytics_backend.models.requestModels.LoginRequestDTO;
import com.adalytics.adalytics_backend.models.requestModels.SignupRequestDTO;
import com.adalytics.adalytics_backend.models.responseModels.LoginResponseDTO;
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
    public void signUp(SignupRequestDTO signupRequestDTO, String organizationId) {
        if (signupRequestDTO.getEmail().isBlank()) {
            throw new BadRequestException("Email is empty.", ErrorCodes.Signup_Email_Invalid.getErrorCode());
        }
        if (signupRequestDTO.getPassword().isBlank()) {
            throw new BadRequestException("Password is empty.", ErrorCodes.Signup_Password_Invalid.getErrorCode());
        }
        if (!FieldValidator.isValidEmail(signupRequestDTO.getEmail())) {
            throw new BadRequestException("Email is not in right format.", ErrorCodes.Signup_Email_Invalid.getErrorCode());
        }
        if (!FieldValidator.isValidPassword(signupRequestDTO.getPassword())) {
            throw new BadRequestException("Password is not strong.", ErrorCodes.Signup_Password_Invalid.getErrorCode());
        }

        Optional<User> existingUser = userRepository.findByEmail(signupRequestDTO.getEmail());
        if (existingUser.isPresent()) {
            throw new BadRequestException("Email already exist.", ErrorCodes.Signup_Email_Invalid.getErrorCode());
        }

        String encodedPassword = new BCryptPasswordEncoder().encode(signupRequestDTO.getPassword());
        User newUser = User.builder()
                .email(signupRequestDTO.getEmail())
                .username(AuthHelper.getUsernameFromEmail(signupRequestDTO.getEmail()))
                .password(encodedPassword)
                .organizationId(organizationId)
                .role(signupRequestDTO.getRole()).build();

        userRepository.save(newUser);
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        if (loginRequestDTO.getEmail().isBlank()) {
            throw new BadRequestException("Email is empty.", ErrorCodes.Login_Email_Invalid.getErrorCode());
        }
        if (loginRequestDTO.getPassword().isBlank()) {
            throw new BadRequestException("Password is empty.", ErrorCodes.Login_Password_Invalid.getErrorCode());
        }
        if (!FieldValidator.isValidEmail(loginRequestDTO.getEmail())) {
            throw new BadRequestException("Email is not in right format.", ErrorCodes.Signup_Email_Invalid.getErrorCode());
        }

        User user = userRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found.", ErrorCodes.User_Not_Found.getErrorCode()));

        boolean isPasswordMatching = new BCryptPasswordEncoder().matches(loginRequestDTO.getPassword(), user.getPassword());
        if (!isPasswordMatching) {
            throw new BadRequestException("Password not matching.", ErrorCodes.Password_Not_Matching.getErrorCode());
        }

        String token = jwtUtil.generateToken(user.getId());
        LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
        loginResponseDTO.setToken(token);
        return loginResponseDTO;
    }
}
