package ru.kata.spring.boot_security.demo.util;

public class UserNotUpdatedException extends RuntimeException {
    UserNotUpdatedException(String message) {
        super(message);
    }
}
