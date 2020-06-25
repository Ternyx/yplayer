package com.github.ternyx.controllers;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.github.ternyx.models.User;
import com.github.ternyx.services.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AuthController
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    UserManager userManager;

    @PostMapping("/code")
    public Map<String, Object> exchangeCodeForTokens(@RequestBody Map<String,Object> payload, HttpServletRequest req, HttpServletResponse res) {
        User user = userManager.authUser(req, (String) payload.get("code"));
        if (user == null) {
            res.setStatus(400);
            return null;
        } 

        return Map.of("userId", user.getUserId(),
                "channelId", user.getChannelId());
    }

    @GetMapping("/logout")
    public void logout(HttpServletRequest req) {
        req.getSession().invalidate();
    }
} 
