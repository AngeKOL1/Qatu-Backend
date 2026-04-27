package com.example.Qatu.exception;

// import org.apache.hc.client5.http.auth.InvalidCredentialsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;

@RestControllerAdvice
public class ResponseExceptionHandler {
    public static final String ERROR_TYPE_PROPERTY = "errorType";

    @ExceptionHandler(ModelNotFoundException.class)
    public ProblemDetail handleModelNotFoundException(ModelNotFoundException ex, WebRequest request) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setTitle("Model Not Found");
        pd.setType(URI.create(request.getDescription(false)));
        pd.setProperty(ERROR_TYPE_PROPERTY, "ModelNotFound");
        return pd;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        pd.setTitle("Bad Request");
        pd.setType(URI.create(request.getDescription(false)));
        pd.setProperty(ERROR_TYPE_PROPERTY, "InvalidArgument");
        return pd;
    }

    @ExceptionHandler(ArithmeticException.class)
    public ProblemDetail handleArithmeticException(ArithmeticException ex, WebRequest request) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_ACCEPTABLE, ex.getMessage());
        pd.setTitle("Arithmetic Error");
        pd.setType(URI.create(request.getDescription(false)));
        pd.setProperty(ERROR_TYPE_PROPERTY, "MathError");
        return pd;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneralException(Exception ex, WebRequest request) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        pd.setTitle("Internal Server Error");
        pd.setType(URI.create(request.getDescription(false)));
        pd.setProperty(ERROR_TYPE_PROPERTY, "UnexpectedError");
        return pd;
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ProblemDetail handleInvalidCredentialsException(InvalidCredentialsException ex, WebRequest request) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        pd.setTitle("Invalid Credentials");
        pd.setProperty(ERROR_TYPE_PROPERTY, "AuthError");
        return pd;
    }

    @ExceptionHandler(UserDisabledException.class)
    public ProblemDetail handleUserDisabledException(UserDisabledException ex, WebRequest request) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        pd.setTitle("User Disabled");
        pd.setProperty(ERROR_TYPE_PROPERTY, "AuthError");
        return pd;
    }

    @ExceptionHandler(PasswordMismatchException.class)
    public ProblemDetail handlePasswordMismatch(
            PasswordMismatchException ex, WebRequest request) {

        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, ex.getMessage());

        pd.setTitle("Password Mismatch");
        pd.setProperty(ERROR_TYPE_PROPERTY, "ValidationError");
        return pd;
    }

}
