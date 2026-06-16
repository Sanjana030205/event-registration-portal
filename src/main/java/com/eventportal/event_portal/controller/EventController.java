package com.eventportal.event_portal.controller;

import com.eventportal.event_portal.model.Event;
import com.eventportal.event_portal.model.User;
import com.eventportal.event_portal.service.EventService;
import com.eventportal.event_portal.service.RegistrationService;
import com.eventportal.event_portal.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final UserService userService;
    private final RegistrationService registrationService;

    @GetMapping("/events")
    public String listEvents(Model model, Authentication auth) {
        User currentUser = userService.findByEmail(auth.getName()).orElseThrow();
        List<Event> events = eventService.getAllEvents();
        model.addAttribute("events", events);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("registrationService", registrationService);
        return "events";
    }

    @GetMapping("/organizer/events/new")
    public String newEventForm() {
        return "event-form";
    }

    @PostMapping("/organizer/events/new")
    public String createEvent(@RequestParam String title,
                              @RequestParam String description,
                              @RequestParam String venue,
                              @RequestParam String eventDate,
                              @RequestParam int capacity,
                              Authentication auth) {
        User organizer = userService.findByEmail(auth.getName()).orElseThrow();
        Event event = new Event();
        event.setTitle(title);
        event.setDescription(description);
        event.setVenue(venue);
        event.setEventDate(LocalDateTime.parse(eventDate));
        event.setCapacity(capacity);
        event.setSeatsfilled(0);
        event.setOrganizer(organizer);
        eventService.createEvent(event);
        return "redirect:/organizer/dashboard";
    }

    @GetMapping("/organizer/dashboard")
    public String organizerDashboard(Model model, Authentication auth) {
        User organizer = userService.findByEmail(auth.getName()).orElseThrow();
        List<Event> events = eventService.getEventsByOrganizer(organizer);
        model.addAttribute("events", events);
        model.addAttribute("currentUser", organizer);
        return "organizer-dashboard";
    }

    @GetMapping("/organizer/events/{id}/registrations")
    public String viewRegistrations(@PathVariable Long id, Model model) {
        Event event = eventService.getEventById(id);
        model.addAttribute("event", event);
        model.addAttribute("registrations",
                registrationService.getRegistrationsByEvent(event));
        return "registrations";
    }

    @PostMapping("/student/register/{eventId}")
    public String registerForEvent(@PathVariable Long eventId, Authentication auth) {
        User user = userService.findByEmail(auth.getName()).orElseThrow();
        Event event = eventService.getEventById(eventId);
        try {
            registrationService.registerForEvent(user, event);
        } catch (RuntimeException e) {
            return "redirect:/events?error=" + e.getMessage();
        }
        return "redirect:/events?success";
    }

    @PostMapping("/student/cancel/{eventId}")
    public String cancelRegistration(@PathVariable Long eventId, Authentication auth) {
        User user = userService.findByEmail(auth.getName()).orElseThrow();
        Event event = eventService.getEventById(eventId);
        registrationService.cancelRegistration(user, event);
        return "redirect:/events?cancelled";
    }
}