package com.adalytics.adalytics_backend.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorModel {
    private String errorMessage;
    private int errorCode;
}
