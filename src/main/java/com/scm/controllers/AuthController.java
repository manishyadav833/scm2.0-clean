package com.scm.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.scm.entities.User;
import com.scm.helpers.Message;
import com.scm.helpers.MessageType;
import com.scm.repsitories.UserRepo;

import jakarta.servlet.http.HttpSession;
@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepo userRepo;

    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam("token") String token, HttpSession session) {

        // Debug (optional but useful)
        System.out.println("Token from URL: " + token);

        User user = userRepo.findByEmailToken(token).orElse(null);

        // Proper null + token check
        if (user != null && user.getEmailToken() != null
                && user.getEmailToken().equals(token)) {

            // ✅ MAIN FIX (ENABLE USER)
            user.setEmailVerified(true);
            user.setEnabled(true);

            // Optional: token remove kar do security ke liye
            user.setEmailToken(null);

            userRepo.save(user);

            session.setAttribute("message", Message.builder()
                    .type(MessageType.green)
                    .content("Your email is verified. Now you can login.")
                    .build());

            return "success_page";
        }

        // ❌ Invalid token case
        session.setAttribute("message", Message.builder()
                .type(MessageType.red)
                .content("Invalid or expired token!")
                .build());

        return "error_page";
    }
}