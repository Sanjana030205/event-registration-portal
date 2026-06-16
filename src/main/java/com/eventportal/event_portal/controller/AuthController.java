package com.eventportal.event_portal.controller;

import com.eventportal.event_portal.model.User;
import com.eventportal.event_portal.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("roles", User.Role.values());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String name,
                               @RequestParam String email,
                               @RequestParam String password,
                               @RequestParam User.Role role,
                               Model model) {
        try {
            userService.registerUser(name, email, password, role);
            return "redirect:/login?registered";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("roles", User.Role.values());
            return "register";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth) {
        User user = userService.findByEmail(auth.getName()).orElseThrow();
        return switch (user.getRole()) {
            case ADMIN -> "redirect:/admin/dashboard";
            case ORGANIZER -> "redirect:/organizer/dashboard";
            default -> "redirect:/events";
        };
    }
}