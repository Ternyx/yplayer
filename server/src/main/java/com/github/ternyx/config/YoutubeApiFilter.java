package com.github.ternyx.config;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.github.ternyx.services.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * YoutubeApiFilter
 */
@Component
public class YoutubeApiFilter implements Filter {

    @Autowired
    UserManager userManager;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest servReq = (HttpServletRequest) req;

        Integer userId = (Integer) servReq.getSession().getAttribute(UserManager.SESSION_USER_ATTRIBUTE);
        boolean foundAndAttached = userManager.attachUserToContext(userId);

        if (!foundAndAttached) {
            ((HttpServletResponse) res).sendError(401);
            return;
        }

        chain.doFilter(req, res);
    }

    
}
