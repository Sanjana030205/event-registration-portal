package com.eventportal.event_portal.controller;

import com.eventportal.event_portal.model.Event;
import com.eventportal.event_portal.model.User;
import com.eventportal.event_portal.repository.UserRepository;
import com.eventportal.event_portal.service.EventService;
import com.eventportal.event_portal.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final EventService eventService;
    private final RegistrationService registrationService;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model, Authentication auth) {
        List<User> users = userRepository.findAll();
        List<Event> events = eventService.getAllEvents();
        model.addAttribute("users", users);
        model.addAttribute("events", events);
        model.addAttribute("totalUsers", users.size());
        model.addAttribute("totalEvents", events.size());
        long totalRegistrations = events.stream()
                .mapToLong(e -> registrationService
                        .getRegistrationsByEvent(e).size())
                .sum();
        model.addAttribute("totalRegistrations", totalRegistrations);
        return "admin-dashboard";
    }

    @PostMapping("/users/{id}/ban")
    public String banUser(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow();
        user.setBanned(true);
        userRepository.save(user);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/users/{id}/unban")
    public String unbanUser(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow();
        user.setBanned(false);
        userRepository.save(user);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/events/{id}/delete")
    public String deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return "redirect:/admin/dashboard";
    }
}