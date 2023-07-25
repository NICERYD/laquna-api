package kr.co.seedit.global.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.co.seedit.global.common.application.JwtUserDetailsService;
import kr.co.seedit.global.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@SuppressWarnings("unused")
@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

  private final JwtUserDetailsService jwtUserDetailsService;

  private final JwtTokenUtil jwtTokenUtil;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain chain)
    throws ServletException, IOException {

    final String requestTokenHeader = request.getHeader("Authorization");
    String username = null;
    String role = null;
    String jwtToken = null;

    // JWT Token is in the form "Bearer token". when you Test with Postman use
    // Bearer Token
    // No using Bearer in prod
    if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer")) {
      jwtToken = requestTokenHeader.substring(7);
      // jwtToken = requestTokenHeader;
      try {
        username = jwtTokenUtil.getUsernameFromToken(jwtToken);
      } catch (IllegalArgumentException e) {
        String url = request.getRequestURL().toString();
//        if (!url.contains("insertPartnerStore") && !url.contains("businessRegistration")) {
//          username = null;
//        }

      } catch (ExpiredJwtException e) {
        // request.getRequestDispatcher("/chkJWT").forward(request, response);
      } catch (Exception e) {
        //e.printStackTrace();
      }
    }

    // Once you get the token validate it.
    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);
      // if token is valid configure Spring Security to manually set authentication
      if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
          userDetails, null, userDetails.getAuthorities());
        usernamePasswordAuthenticationToken.setDetails(
          new WebAuthenticationDetailsSource().buildDetails(request));
        // After setting the Authentication in the context, we specify
        // that the current user is authenticated. So it passes the Spring Security
        // Configurations successfully.
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
      }
    }

    try {
      // Claims claims2 = jwtTokenUtil.getAllClaimsFromToken(jwtToken);
      chain.doFilter(request, response);
    } catch (IllegalArgumentException e) {
      chain.doFilter(request, response);
    } catch (ExpiredJwtException e) {

    } catch (Exception e) {
      chain.doFilter(request, response);
    }

  }

}
