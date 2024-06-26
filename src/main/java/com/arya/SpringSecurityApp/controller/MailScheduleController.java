package com.arya.SpringSecurityApp.controller;

import com.arya.SpringSecurityApp.request.ScheduleMailRequest;
import com.arya.SpringSecurityApp.response.GenericResponse;
import com.arya.SpringSecurityApp.service.EmailService;
import com.arya.SpringSecurityApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class MailScheduleController {

    @Autowired
    private EmailService emailService;

    private UserService userService;


    @PostMapping("/schedule-mail")
    public ResponseEntity<?> scheduleEmail(@RequestBody ScheduleMailRequest request) {
        GenericResponse<String> response = new GenericResponse<>();
        try {
            List<String> toList = request.getTo();
            String subject = request.getSubject();
            String text = request.getText();
            for (String to : toList) {
                emailService.scheduleEmail(to, subject, text);
            }
            response.setStatus("success");
            response.setMessage("Email scheduled to be sent to: " + toList.size() + " recipients.");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            response.setStatus("failure");
            response.setMessage("Failed to schedule emails: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
