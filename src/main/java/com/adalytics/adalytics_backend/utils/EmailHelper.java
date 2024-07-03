package com.adalytics.adalytics_backend.utils;

import com.adalytics.adalytics_backend.models.entities.Organization;
import com.adalytics.adalytics_backend.models.entities.Token;
import com.adalytics.adalytics_backend.repositories.interfaces.IOrganizationRepository;
import com.adalytics.adalytics_backend.repositories.interfaces.ITokenRepository;
import com.adalytics.adalytics_backend.services.interfaces.IEmailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.adalytics.adalytics_backend.constants.CommonConstants.EMAIL_TOKEN_EXPIRY;

@Component
public class EmailHelper {

    @Autowired
    private ITokenRepository tokenRepository;
    @Autowired
    private IOrganizationRepository organizationRepository;
    @Autowired
    private IEmailSender emailSender;

    @Value("${link.verification}")
    private String verificationLink;
    @Value("${link.invitation}")
    private String invitationLink;

    @Async
    public void createTokenAndSendVerificationMail(String emailId){
        Token token = Token.builder()
                .email(emailId)
                .token(UUID.randomUUID().toString())
                .expirationTime(System.currentTimeMillis() + EMAIL_TOKEN_EXPIRY)
                .build();
        tokenRepository.save(token);
        String link = verificationLink + token.getToken();
        emailSender.send(emailId, link);
    }

    @Async
    public void sendInvitationMail(String emailId, String password) {
        String organizationName = organizationRepository.findById(ContextUtil.getCurrentOrgId()).map(Organization::getName).orElse("");
        emailSender.send(emailId,generateEmailSnippet(organizationName,invitationLink,password));
    }

    private String generateEmailSnippet(String organizationName, String joinUrl, String password) {
        return String.format(
                "<!DOCTYPE html>" +
                        "<html>" +
                        "<head>" +
                        "<style>" +
                        "  .container { font-family: Arial, sans-serif; }" +
                        "  .header { background-color: #0866ff; color: white; padding: 10px 0; text-align: center; }" +
                        "  .content { padding: 20px; }" +
                        "  .button { display: inline-block; padding: 10px 20px; font-size: 16px; color: #ffffff; background-color: #0866ff; text-align: center; text-decoration: none; border-radius: 5px; }" +
                        "  .button:visited{ color:#ffffff; } " +
                        "  .footer { margin-top: 10px; text-align: center; color: #888888; font-size: 12px; }" +
                        "</style>" +
                        "</head>" +
                        "<body>" +
                        "<div class=\"container\">" +
                        "  <div class=\"header\">" +
                        "    <h1>Invitation to Join %s</h1>" +
                        "  </div>" +
                        "  <div class=\"content\">" +
                        "    <p>Hi,</p>" +
                        "    <p>You have been invited to join the organization <strong>%s</strong> on Adalytics.</p>" +
                        "    <p>To accept the invitation and join the organization, please click the button below:</p>" +
                        "    <p><a href=\"%s\" class=\"button\">Join Now</a></p>" +
                        "    <p>Your temporary password is: <strong>%s</strong></p>" +
                        "    <p>We recommend changing your password after your first login.</p>" +
                        "    <p>We look forward to having you as a part of our community!</p>" +
                        "  </div>" +
                        "  <div class=\"footer\">" +
                        "    <p>Best regards,</p>" +
                        "    <p>The Team</p>" +
                        "  </div>" +
                        "</div>" +
                        "</body>" +
                        "</html>",
                organizationName, organizationName, joinUrl, password
        );
    }
}
