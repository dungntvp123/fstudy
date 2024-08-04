package com.project.fstudy.filter;

import com.project.fstudy.repository.AccountRepository;
import com.project.fstudy.utils.JwtUtils;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Slf4j
@Component
public class JwtAuthenticationFilter implements Filter {
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private AccountRepository accountRepository;
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String jwt = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (jwt == null || !jwt.startsWith("Bearer")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = jwt.split("Bearer")[1].trim();

        try {
            String username = jwtUtils.extractUsername(token);
            UserDetails user = accountRepository.findByUsername(username).get();
            if (!jwtUtils.validateToken(token, user) || !user.isEnabled()) {
                filterChain.doFilter(request, response);
                return;
            }
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            log.info(exception.getMessage());

            filterChain.doFilter(request, response);
        }
    }
}
