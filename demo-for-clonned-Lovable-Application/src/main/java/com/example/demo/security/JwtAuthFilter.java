package com.example.demo.security;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter
{

    JwtService jwtService;
    UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException
    {
        final String authHeader=request.getHeader("Authorization");

        if(authHeader==null || !authHeader.startsWith("Bearer "))
        {
            filterChain.doFilter(request,response);
            return;
        }

        final String jwttoken=authHeader.split(" ")[1];
        log.info("Jwt Token:::{}",jwttoken);
        JwtUserPrincipal user=jwtService.validateToken(jwttoken);
        //Long userId=user.userId();
        if(user!=null && SecurityContextHolder.getContext().getAuthentication()==null)
        {
            //User u1=userRepository.findById(userId).get();
            UsernamePasswordAuthenticationToken userToken=new UsernamePasswordAuthenticationToken(user,null,user.authorityList());
            SecurityContextHolder.getContext().setAuthentication(userToken);
        }
        filterChain.doFilter(request,response);
    }
}
