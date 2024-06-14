package ru.kata.spring.boot_security.demo.dao;

import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;

public interface UserDao {
    void add(User user);
    List<User> listUsers();

    User getUserById(int id);
    void deleteUserById(int id);

    void updateUser(int id, User user);
}
