package com.eventportal.event_portal.repository;

import com.eventportal.event_portal.model.Event;
import com.eventportal.event_portal.model.Registration;
import com.eventportal.event_portal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    List<Registration> findByEvent(Event event);
    List<Registration> findByUser(User user);
    Optional<Registration> findByUserAndEvent(User user, Event event);
    boolean existsByUserAndEvent(User user, Event event);
    boolean existsByUserAndEventAndStatus(User user, Event event, Registration.Status status);
}