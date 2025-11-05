package org.hrithik.documenteditor.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hrithik.documenteditor.config.JwtUtil;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.io.IOException;
import java.util.Collections;

public class JwtCookieFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    public JwtCookieFilter(JwtUtil jwtUtil){ this.jwtUtil = jwtUtil; }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        if(SecurityContextHolder.getContext().getAuthentication() == null){
            Cookie[] cookies = req.getCookies();
            if(cookies != null){
                for(Cookie c : cookies){
                    if("AUTH-TOKEN".equals(c.getName())){
                        String username = jwtUtil.validateAndGetUsername(c.getValue());
                        if(username != null){
                            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
                            SecurityContextHolder.getContext().setAuthentication(auth);
                        }
                    }
                }
            }
        }
        chain.doFilter(req, res);
    }
}
