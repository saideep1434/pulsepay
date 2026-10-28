package com.pulsepay.payment_service.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.pulsepay.payment_service.service.JwtService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component 
@RequiredArgsConstructor 
public class JwtAuthenticationFilter extends OncePerRequestFilter
{
    private final JwtService jwtService;


    protected void doFilterInternal(HttpServletRequest request , HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException
    {

      // 1. take the header from the request
      String Header = request.getHeader("Authorization");

      // 2. check if the header is null or doesn't start with Bearer

      if(Header == null || Header.isEmpty() || !Header.startsWith("Bearer ")) {
        filterChain.doFilter(request, response);
        return;
      }

      // 3. extract the token from the header
      String token = Header.substring(7); // "Bearer " is 7 characters long


      //4. valid token → tell Spring who the user is
      if(jwtService.isTokenValid(token))
      {
        String email = jwtService.getEmailFromToken(token); // got email from jwt service

        List<String> roles = jwtService.getRolesFromToken(token); // got list of roles from jwt service

        // now the problem is spring understands roles as ROLE_USER,ROLE_ADMIN,etc but we are sending USER,ADMIN, so we need to convert it to ROLE_USER,ROLE_ADMIN
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        for(String role : roles)
        {
          authorities.add(new SimpleGrantedAuthority("ROLE_" + role)); // adding ROLE_ prefix to the role
        }
        
        // 5. create an Authentication object and set it in the SecurityContext
        UsernamePasswordAuthenticationToken Authentication = new UsernamePasswordAuthenticationToken(email,null, authorities);

        // 6. set the Authentication object in the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(Authentication); // intially it is empty by doing this we are telling spring that the user is authenticated and who the user is
      }
        // 7. continue the filter chain
        filterChain.doFilter(request, response); // what this does is it tells spring to continue the filter chain and go to the next filter in the chain which is the Authorization filter which will check if the user has access to the resource or not


    }



  

}
