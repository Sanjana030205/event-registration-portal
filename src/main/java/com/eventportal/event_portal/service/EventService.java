package com.eventportal.event_portal.service;

import com.eventportal.event_portal.model.Event;
import com.eventportal.event_portal.model.User;
import com.eventportal.event_portal.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
    }

    public List<Event> getEventsByOrganizer(User organizer) {
        return eventRepository.findByOrganizer(organizer);
    }

    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    public Event updateEvent(Event event) {
        return eventRepository.save(event);
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    public boolean hasAvailableSeats(Event event) {
        return event.getSeatsfilled() < event.getCapacity();
    }
}