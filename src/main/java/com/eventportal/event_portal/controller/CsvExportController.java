package com.eventportal.event_portal.controller;

import com.eventportal.event_portal.model.Registration;
import com.eventportal.event_portal.service.EventService;
import com.eventportal.event_portal.service.RegistrationService;
import com.opencsv.CSVWriter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CsvExportController {

    private final EventService eventService;
    private final RegistrationService registrationService;

    @GetMapping("/organizer/events/{id}/export")
    public void exportRegistrations(@PathVariable Long id,
                                    HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition",
                "attachment; filename=registrations.csv");

        var event = eventService.getEventById(id);
        List<Registration> registrations =
                registrationService.getRegistrationsByEvent(event);

        try (CSVWriter writer = new CSVWriter(response.getWriter())) {
            writer.writeNext(new String[]{"Name", "Email", "Status", "Registered At"});
            for (Registration r : registrations) {
                writer.writeNext(new String[]{
                        r.getUser().getName(),
                        r.getUser().getEmail(),
                        r.getStatus().name(),
                        r.getRegisteredAt().toString()
                });
            }
        }
    }
}