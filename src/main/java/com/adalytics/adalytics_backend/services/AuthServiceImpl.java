package com.adalytics.adalytics_backend.services;

import com.adalytics.adalytics_backend.enums.ErrorCodes;
import com.adalytics.adalytics_backend.enums.Role;
import com.adalytics.adalytics_backend.exceptions.BadRequestException;
import com.adalytics.adalytics_backend.exceptions.NotFoundException;
import com.adalytics.adalytics_backend.models.entities.Token;
import com.adalytics.adalytics_backend.models.entities.User;
import com.adalytics.adalytics_backend.repositories.interfaces.ITokenRepository;
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
import java.util.UUID;

import static com.adalytics.adalytics_backend.constants.CommonConstants.EMAIL_TOKEN_EXPIRY;

@Service
public class AuthServiceImpl implements IAuthService {
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private JWTUtil jwtUtil;
    @Autowired
    private ITokenRepository tokenRepository;

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
        if(Role.USER.name().equals(newUser.getRole())) {
            newUser.setEnabled(true);
        }
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
        if(!user.isEnabled()) {
            throw new BadRequestException("Email not verified", ErrorCodes.Email_Not_Verified.getErrorCode());
        }
        boolean isPasswordMatching = new BCryptPasswordEncoder().matches(loginRequestDTO.getPassword(), user.getPassword());
        if (!isPasswordMatching) {
            throw new BadRequestException("Password not matching.", ErrorCodes.Password_Not_Matching.getErrorCode());
        }

        String token = jwtUtil.generateToken(user.getId());
        LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
        loginResponseDTO.setToken(token);
        return loginResponseDTO;
    }

    @Override
    public void verifyEmail(String token) {
        Optional<Token> existingToken = tokenRepository.findByToken(token);
        if(existingToken.isEmpty() || existingToken.get().isExpired(System.currentTimeMillis())) {
            throw new BadRequestException("Token Expired!", ErrorCodes.Token_Expired.getErrorCode());
        }
        User user = userRepository.findByEmail(existingToken.get().getEmail())
                .orElseThrow(() -> new NotFoundException("User not found.", ErrorCodes.User_Not_Found.getErrorCode()));
        user.setEnabled(true);
        userRepository.save(user);
    }
}
