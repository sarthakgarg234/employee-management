package com.sarthak.employee_management.controller;

import com.sarthak.employee_management.entity.User;
import com.sarthak.employee_management.repository.UserRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Locale;

@Controller
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/users")
    public String listUsers(
            @RequestParam(required = false) String keyword,
            Model model) {

        List<User> users = userRepository.findAll();

        if (keyword != null && !keyword.trim().isEmpty()) {
            String search = keyword.trim().toLowerCase(Locale.ROOT);

            users = users.stream()
                    .filter(user ->
                            user.getName().toLowerCase(Locale.ROOT)
                                    .contains(search)
                            || user.getEmail().toLowerCase(Locale.ROOT)
                                    .contains(search))
                    .toList();
        }

        model.addAttribute("users", users);
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        model.addAttribute("totalUsers", userRepository.count());

        return "users";
    }
}