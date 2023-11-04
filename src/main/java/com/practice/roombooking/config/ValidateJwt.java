package com.practice.roombooking.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
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
import java.util.List;
import java.util.stream.Collectors;

public class ValidateJwt extends OncePerRequestFilter {
    @Value("secret.key")
    private  String key;
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
    String authorization=httpServletRequest.getHeader("Authorization");SecretKey secretKey= Keys.hmacShaKeyFor(ConfigConstants.secretKey.getBytes(StandardCharsets.UTF_8));
    if(authorization!=null) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build().parseClaimsJws(authorization).getBody();
            String username = String.valueOf(claims.get("username"));
            String authorities = String.valueOf(claims.get("authorities"));
            Authentication authentication = new UsernamePasswordAuthenticationToken(username, null,
                    AuthorityUtils.commaSeparatedStringToAuthorityList(authorities));
            SecurityContextHolder.getContext().setAuthentication(authentication);


        } catch (Exception e) {
            throw new BadCredentialsException("no header found ", e);
        }
    }
    filterChain.doFilter(httpServletRequest,httpServletResponse);
    }
    public String getAuthorities(Collection<? extends GrantedAuthority> authority){

        List<String> authorities=authority.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        return String.join(",",authorities);

    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getServletPath().equals("/api/basicAuth/validate");
    }
}
