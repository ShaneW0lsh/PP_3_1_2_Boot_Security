package ru.kata.spring.boot_security.demo.controller;

import org.attoparser.util.TextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.TextUtils;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;
import java.util.List;

@Controller
public class UsersController {
    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    private final Logger logger = LoggerFactory.getLogger(UsersController.class);

    @Autowired
    public UsersController(UserService userService, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/user")
    public String showUserWithId(ModelMap model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("user", user);
        return "user-profile";
    }

    @GetMapping("/admin/show")
    public String showUserWithId(@RequestParam("id") int id, ModelMap model) {
        model.addAttribute("user", userService.getUserById(id));
        return "user-profile";
    }

    @GetMapping("/admin")
    public String showUsers(ModelMap model, Principal principal) {
        List<User> userList = userService.listUsers();
        model.addAttribute("users", userList);
        model.addAttribute("user", new User());
        model.addAttribute("roles", roleService.getRoles());
        for (Role role : roleService.getRoles()) {
            logger.info(role.getName());
        }
        model.addAttribute("admin", userService.findByUsername(principal.getName()));
        return "admin-page";
    }

    @GetMapping("/admin/new")
    public String newUser(ModelMap model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", roleService.getRoles());
        return "new-user";
    }

    @PostMapping("/admin")
    public String createUser(@ModelAttribute("user") User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.add(user);
        return "redirect:/admin";
    }

    @GetMapping("/admin/edit")
    public String editUser(ModelMap model, @RequestParam("id") int id) {
        model.addAttribute("user", userService.getUserById(id));
        model.addAttribute("roles", roleService.getRoles());
        return "edit-user";
    }

    @PatchMapping("/admin")
    public String updateUser(@ModelAttribute("user") User user, @RequestParam("id") int id) {
        User oldUser = userService.getUserById(id);
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(oldUser.getPassword());
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(oldUser.getRoles());
        }
        userService.updateUser(id, user);

        return "redirect:/admin";
    }

    @DeleteMapping("/admin")
    public String deleteUser(@RequestParam("id") int id) {
        userService.deleteUserById(id);
        return "redirect:/admin";
    }
}
