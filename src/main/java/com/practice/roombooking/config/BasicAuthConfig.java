package com.practice.roombooking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Configuration
@EnableWebSecurity(debug = true)
public class BasicAuthConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().cors().configurationSource(new CorsConfigurationSource() {
          @Override
          public CorsConfiguration getCorsConfiguration(HttpServletRequest httpServletRequest) {
              CorsConfiguration corsConfiguration=new CorsConfiguration();
              corsConfiguration.addAllowedOrigin("http://localhost:4200");
              corsConfiguration.setAllowedMethods(Arrays.asList("*"));
              corsConfiguration.setAllowedHeaders(Arrays.asList("*"));
              corsConfiguration.addExposedHeader("Authorization");
              corsConfiguration.setAllowCredentials(true);
              return corsConfiguration;
          }
      }).and().csrf().disable()
              .addFilterBefore(new ValidateJwt(), BasicAuthenticationFilter.class)
              .addFilterAfter(new GenerateToken(), BasicAuthenticationFilter.class)
              .authorizeRequests()

              .antMatchers(HttpMethod.GET,"/api/**").hasRole("USER")
              .antMatchers("/api/**").hasRole("ADMIN")
              .anyRequest().authenticated()
              .and().httpBasic();

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("matt")
                .password("secret")
                .roles("ADMIN").and().
                withUser("jane")
                .password("secret")
                .roles("USER");
    }
    @Bean
    PasswordEncoder getPasswordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }
}
