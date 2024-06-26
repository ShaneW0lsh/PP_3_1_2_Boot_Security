package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;
import ru.kata.spring.boot_security.demo.util.*;

import javax.validation.Valid;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
public class RESTUsersController {
    UserService userService;
    PasswordEncoder passwordEncoder;
    private ZoneId zoneId = ZoneId.of("Europe/Moscow");

    @Autowired
    public RESTUsersController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    // read
    @GetMapping
    public List<User> getUsers() {
        return userService.listUsers();
    }

    @GetMapping("/user")
    public User getOneUser(@RequestParam("id") int id) {
        return userService.getUserById(id);
    }

    @ExceptionHandler
    public ResponseEntity<UserErrorResponse> handleUserException(UserNotFoundException userNotFoundException) {
        UserErrorResponse userErrorResponse = new UserErrorResponse(
                userNotFoundException.getMessage(),
                ZonedDateTime.now(zoneId).format(DateTimeFormatterUtil.getDateTimeFormatter(3))
        );
        return new ResponseEntity<>(userErrorResponse, HttpStatus.NOT_FOUND);
    }

    // create
    @PostMapping
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid User user, BindingResult bindingResult)
            throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        if (bindingResult.hasErrors()) {
            reactToBindingError(bindingResult, UserNotCreatedException.class);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.add(user);

        return ResponseEntity.ok(HttpStatus.OK);
    }


    @ExceptionHandler
    private ResponseEntity<UserErrorResponse> handleUserException(UserNotCreatedException userNotCreatedException) {
        UserErrorResponse userErrorResponse = new UserErrorResponse(
                userNotCreatedException.getMessage(),
                ZonedDateTime.now(zoneId).format(DateTimeFormatterUtil.getDateTimeFormatter(3))
        );

        return new ResponseEntity<>(userErrorResponse, HttpStatus.BAD_REQUEST);
    }

    // update
    @PatchMapping
    private ResponseEntity<HttpStatus> update(@RequestBody User user, BindingResult bindingResult)
            throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (bindingResult.hasErrors()) {
            reactToBindingError(bindingResult, UserNotUpdatedException.class);
        }

        User oldUser = userService.getUserById(user.getId());
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(oldUser.getPassword());
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(oldUser.getRoles());
        }

        userService.updateUser(user.getId(), user);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @ExceptionHandler
    private ResponseEntity<UserErrorResponse> handleUserException(UserNotUpdatedException userNotUpdatedException) {
        UserErrorResponse userErrorResponse = new UserErrorResponse(
                userNotUpdatedException.getMessage(),
                ZonedDateTime.now(zoneId).format(DateTimeFormatterUtil.getDateTimeFormatter(3))
        );

        return new ResponseEntity<>(userErrorResponse, HttpStatus.BAD_REQUEST);
    }

    // delete
    @DeleteMapping
    private ResponseEntity<HttpStatus> delete(@RequestParam("id") int id) {
        userService.deleteUserById(id);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    private void reactToBindingError(BindingResult bindingResult, Class<? extends RuntimeException> exceptionClass)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        StringBuilder errorMessage = new StringBuilder();

        for (FieldError error : bindingResult.getFieldErrors()) {
            errorMessage
                    .append(error.getField())
                    .append(" - ")
                    .append(error.getDefaultMessage())
                    .append(";");
        }

        throw exceptionClass.getDeclaredConstructor(String.class).newInstance(errorMessage.toString());
    }
}
