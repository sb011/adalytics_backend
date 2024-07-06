package com.adalytics.adalytics_backend.services.interfaces;

public interface IEmailSender {
    void send (String to, String email);
}