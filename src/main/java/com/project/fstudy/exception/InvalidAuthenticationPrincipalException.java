package com.project.fstudy.exception;

public class InvalidAuthenticationPrincipalException extends OutOfMemoryError {
    public InvalidAuthenticationPrincipalException() {
        super("Invalid authentication principal");
    }
}
