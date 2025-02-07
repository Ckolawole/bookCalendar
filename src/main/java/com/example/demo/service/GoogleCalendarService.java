package com.example.demo.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
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

    public String createEvent(String summary, String startDateTime, String endDateTime) {
        try {
            Calendar service = getCalendarService();

            Event event = new Event().setSummary(summary);

            // Set the start time
            EventDateTime start = new EventDateTime()
                    .setDateTime(new com.google.api.client.util.DateTime(startDateTime))
                    .setTimeZone(TimeZone.getDefault().getID());
            // Set the end time
            EventDateTime end = new EventDateTime()
                    .setDateTime(new com.google.api.client.util.DateTime(endDateTime))
                    .setTimeZone(TimeZone.getDefault().getID());

            event.setStart(start);
            event.setEnd(end);

            // Insert the event into the calendar
            service.events().insert(CALENDAR_ID, event).execute();
            return "Event created successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error creating event: " + e.getMessage();
        }
    }
}
