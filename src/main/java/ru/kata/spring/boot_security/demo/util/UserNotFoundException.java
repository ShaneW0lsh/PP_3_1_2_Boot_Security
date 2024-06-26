package ru.kata.spring.boot_security.demo.util;

import java.time.ZoneId;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(int id) {
        super(String.format("User with id %s was not found", id));
    }
}
