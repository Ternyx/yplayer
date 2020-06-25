package com.github.ternyx.services;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import com.github.ternyx.models.OAuthToken;
import com.github.ternyx.models.User;
import com.github.ternyx.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * UserManager
 */
@Service
public class UserManager {

    private static InheritableThreadLocal<User> userContext = new InheritableThreadLocal<>();
    public static final String SESSION_USER_ATTRIBUTE = "user_id";

    @Autowired
    AuthService authService;

    @Autowired
    UserRepo userRepo;

    public User authUser(HttpServletRequest req, String code) {
        if (req == null || code == null) {
            return null;
        }

        OAuthToken token = authService.retrieveToken(code);
        String channelId = authService.getUserId(token);

        Optional<User> optUser = userRepo.findByChannelId(channelId);

        User user;

        if (optUser.isEmpty()) {
            user = new User(channelId, token);
        } else {
            user = optUser.get();
            user.setToken(token);
        }

        user = userRepo.save(user);
        req.getSession().setAttribute(SESSION_USER_ATTRIBUTE, user.getUserId());

        // live updates might f this up

        return user;
    }

    public String refreshToken() {
        User user = getUserFromContext();
        OAuthToken token = authService.refreshToken(user.getToken());
        token.setRefreshToken(user.getToken().getRefreshToken());
        user.setToken(token);
        updateUser(user);
        
        return token.getAccessToken();
    }

    public boolean attachUserToContext(Integer userId) {
        if (userId == null) {
            return false;
        }

        Optional<User> optUser = userRepo.findById(userId);

        if (!optUser.isPresent()) {
            return false;
        }

        userContext.set(optUser.get());

        return true;
    }

    public boolean updateUser(User user) {
        if (user == null || !userRepo.existsById(user.getUserId())) {
            return false;
        }

        userContext.set(user);
        userRepo.save(user);

        return true;
    }

    public static User getUserFromContext() {
        return userContext.get();
    }

}
