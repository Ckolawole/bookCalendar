package com.example.demo.controller;


import com.example.demo.service.GoogleCalendarService;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/calendar")
public class CalendarController {

    @Autowired
    private GoogleCalendarService calendarService;




    // Use @ResponseBody to return plain text or JSON directly.
    @RequestMapping(value = "/book", method = RequestMethod.POST)
    @ResponseBody
    public String bookEvent(@RequestBody Map<String, String> request, HttpServletResponse response) throws IOException {
        String summary = request.get("summary");
        String startTime = request.get("start_time");
        String endTime = request.get("end_time");

        String result = calendarService.createEvent(summary, startTime, endTime);
        return result;
    }

    @GetMapping("/events")
    public ResponseEntity<List<Event>> getAllEvents() {
        List<Event> events = calendarService.getAllEvents();
        return ResponseEntity.ok(events);
    }
}
