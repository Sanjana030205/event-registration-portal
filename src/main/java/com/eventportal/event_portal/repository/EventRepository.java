package com.eventportal.event_portal.repository;

import com.eventportal.event_portal.model.Event;
import com.eventportal.event_portal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByOrganizer(User organizer);
}