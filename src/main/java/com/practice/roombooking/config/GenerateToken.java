package com.practice.roombooking.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class GenerateToken extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        String key=ConfigConstants.secretKey;
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        if(authentication!=null) {
            SecretKey secretKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
            String jwt= Jwts.builder().setIssuedAt(new Date())
                    .setIssuer("mostafa")
                    .setSubject("booking token")
                    .claim("username",authentication.getName())
                    .claim("authorities",getAuthorities(authentication.getAuthorities()))
                    .setExpiration(new Date(new Date().getTime()+300000))
                    .signWith(secretKey).compact();
            System.out.println("check authorities "+getAuthorities(authentication.getAuthorities()));
            httpServletResponse.setHeader("Authorization",jwt);
        }
      filterChain.doFilter(httpServletRequest,httpServletResponse);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        System.out.println("display "+request.getServletPath());
        return !request.getServletPath().equals("/api/basicAuth/validate");
    }
    public String getAuthorities(Collection<? extends GrantedAuthority> authority){

        List<String> authorities=authority.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        return String.join(",",authorities);

    }
}
