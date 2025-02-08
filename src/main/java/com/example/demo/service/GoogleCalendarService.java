package com.example.demo.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

@Service
public class GoogleCalendarService {

    private static final String APPLICATION_NAME = "MyCalendarApp";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    // Replace with your calendar ID (often your email address)
    private static final String CALENDAR_ID = "ckolawole@gmail.com";

    private Calendar getCalendarService() throws IOException, GeneralSecurityException {
        // Load the service account key JSON file
        FileInputStream serviceAccountStream = new FileInputStream("src/main/resources/credentials/service-account.json");

        GoogleCredential credential = GoogleCredential.fromStream(serviceAccountStream)
                .createScoped(Collections.singleton("https://www.googleapis.com/auth/calendar"));

        return new Calendar.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Get all events from the calendar.
     */
    public List<Event> getAllEvents() {
        try {
            Calendar service = getCalendarService();
            Events events = service.events().list(CALENDAR_ID)
                    .setTimeMin(new com.google.api.client.util.DateTime(System.currentTimeMillis()))
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            return events.getItems();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Check if a time slot is available before booking.
     */
    public boolean isTimeSlotAvailable(String startDateTime, String endDateTime) {
        try {
            com.google.api.client.util.DateTime newStart = new com.google.api.client.util.DateTime(startDateTime);
            com.google.api.client.util.DateTime newEnd = new com.google.api.client.util.DateTime(endDateTime);

            for (Event event : getAllEvents()) {
                com.google.api.client.util.DateTime eventStart = event.getStart().getDateTime();
                com.google.api.client.util.DateTime eventEnd = event.getEnd().getDateTime();

                if (eventStart != null && eventEnd != null) {
                    // Check if the new event overlaps with an existing one
                    if (!(newEnd.getValue() <= eventStart.getValue() || newStart.getValue() >= eventEnd.getValue())) {
                        return false;  // Time slot is taken
                    }
                }
            }
            return true;  // Time slot is available
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String createEvent(String summary, String startDateTime, String endDateTime) {
        try {
            if (!isTimeSlotAvailable(startDateTime, endDateTime)) {
                return "Time slot is already booked!";
            }

            Calendar service = getCalendarService();
            Event event = new Event().setSummary(summary);

            EventDateTime start = new EventDateTime()
                    .setDateTime(new com.google.api.client.util.DateTime(startDateTime))
                    .setTimeZone(TimeZone.getDefault().getID());

            EventDateTime end = new EventDateTime()
                    .setDateTime(new com.google.api.client.util.DateTime(endDateTime))
                    .setTimeZone(TimeZone.getDefault().getID());

            event.setStart(start);
            event.setEnd(end);

            service.events().insert(CALENDAR_ID, event).execute();
            return "Event created successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error creating event: " + e.getMessage();
        }
    }
}


