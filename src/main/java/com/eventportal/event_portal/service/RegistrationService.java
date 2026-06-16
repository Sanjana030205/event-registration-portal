package com.eventportal.event_portal.service;

import com.eventportal.event_portal.model.Event;
import com.eventportal.event_portal.model.Registration;
import com.eventportal.event_portal.model.User;
import com.eventportal.event_portal.repository.EventRepository;
import com.eventportal.event_portal.repository.RegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final EventRepository eventRepository;

    public Registration registerForEvent(User user, Event event) {
        if (registrationRepository.existsByUserAndEvent(user, event)) {
            throw new RuntimeException("Already registered for this event!");
        }
        if (event.getSeatsfilled() >= event.getCapacity()) {
            throw new RuntimeException("Event is full!");
        }
        Registration registration = new Registration();
        registration.setUser(user);
        registration.setEvent(event);
        registration.setStatus(Registration.Status.CONFIRMED);
        event.setSeatsfilled(event.getSeatsfilled() + 1);
        eventRepository.save(event);
        return registrationRepository.save(registration);
    }

    public void cancelRegistration(User user, Event event) {
        Registration reg = registrationRepository.findByUserAndEvent(user, event)
                .orElseThrow(() -> new RuntimeException("Registration not found"));
        reg.setStatus(Registration.Status.CANCELLED);
        if (event.getSeatsfilled() > 0) {
            event.setSeatsfilled(event.getSeatsfilled() - 1);
        }
        eventRepository.save(event);
        registrationRepository.save(reg);
    }

    public List<Registration> getRegistrationsByEvent(Event event) {
        return registrationRepository.findByEvent(event);
    }

    public List<Registration> getRegistrationsByUser(User user) {
        return registrationRepository.findByUser(user);
    }

    public boolean isUserRegistered(User user, Event event) {
        return registrationRepository.existsByUserAndEventAndStatus(
                user, event, Registration.Status.CONFIRMED);
    }
}