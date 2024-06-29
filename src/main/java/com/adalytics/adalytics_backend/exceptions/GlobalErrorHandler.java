package com.adalytics.adalytics_backend.exceptions;

import com.adalytics.adalytics_backend.enums.ErrorCodes;
import com.adalytics.adalytics_backend.models.ErrorModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalErrorHandler {
    // Bad request exception - 400
    @ExceptionHandler
    public ResponseEntity<ErrorModel> handleException(BadRequestException exception) {
        ErrorModel errorModel = new ErrorModel(exception.getMessage(), exception.getErrorCode());
        return new ResponseEntity<>(errorModel, HttpStatus.BAD_REQUEST);
    }

    // Not found exception - 404
    @ExceptionHandler
    public ResponseEntity<ErrorModel> handleException(NotFoundException exception) {
        ErrorModel errorModel = new ErrorModel(exception.getMessage(), exception.getErrorCode());
        return new ResponseEntity<>(errorModel, HttpStatus.NOT_FOUND);
    }

    // Bad gateway - 502
    @ExceptionHandler
    public ResponseEntity<ErrorModel> handleException(BadGatewayException exception) {
        ErrorModel errorModel = new ErrorModel(exception.getMessage(), exception.getErrorCode());
        return new ResponseEntity<>(errorModel, HttpStatus.BAD_GATEWAY);
    }

    // General exception - 500
    @ExceptionHandler
    public ResponseEntity<ErrorModel> handleException(Exception exception) {
        ErrorModel errorModel = new ErrorModel(exception.getMessage(), ErrorCodes.Internal_Server_Error.getErrorCode());
        return new ResponseEntity<>(errorModel, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
