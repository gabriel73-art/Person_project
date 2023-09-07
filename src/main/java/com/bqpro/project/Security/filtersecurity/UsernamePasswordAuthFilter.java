package com.bqpro.project.Security.filtersecurity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.bqpro.project.Model.Credentials;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UsernamePasswordAuthFilter extends OncePerRequestFilter {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    protected void doFilterInternal(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            FilterChain filterChain) throws ServletException, IOException {

        if ("/signIn".equals(httpServletRequest.getServletPath())
                && HttpMethod.POST.matches(httpServletRequest.getMethod())) {
            Credentials credentialsDto = MAPPER.readValue(httpServletRequest.getInputStream(), Credentials.class);

            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(credentialsDto.getLogin(), credentialsDto.getPassword()));
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}


