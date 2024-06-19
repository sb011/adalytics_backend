package com.adalytics.adalytics_backend.exceptions;

import com.adalytics.adalytics_backend.enums.ErrorCodes;
import com.adalytics.adalytics_backend.models.ErrorModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalErrorHandler {
    @ExceptionHandler
    public ResponseEntity<ErrorModel> handleException(BadRequestException exception) {
        ErrorModel errorModel = new ErrorModel(exception.getMessage(), exception.getErrorCode());
        return new ResponseEntity<>(errorModel, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorModel> handleException(Exception exception) {
        ErrorModel errorModel = new ErrorModel(exception.getMessage(), ErrorCodes.Internal_Server_Error.getErrorCode());
        return new ResponseEntity<>(errorModel, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
