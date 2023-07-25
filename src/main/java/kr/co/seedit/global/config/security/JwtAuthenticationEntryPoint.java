package kr.co.seedit.global.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.co.seedit.global.common.dto.ExceptionResponseDto;
import kr.co.seedit.global.error.exception.CustomException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

  private static final long serialVersionUID = -7858869558953243875L;

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
                       AuthenticationException authException) throws IOException {
    OutputStream out = response.getOutputStream();
    ExceptionResponseDto exceptionResponseDto = ExceptionResponseDto.builder().build();
    ObjectMapper objectMapper = new ObjectMapper();
    String[] err = CustomException.ERR_9789.split(",");

    if (authException instanceof BadCredentialsException) {
      err = CustomException.ERR_8999.split(",");
    }

    exceptionResponseDto.setError(Map.of("code", err[0], "message", err[1]));
    exceptionResponseDto.setData(Map.of("code", err[0], "message", err[1]));
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setHeader("Content-Type", "application/json");
    response.setHeader("Access-Control-Allow-Origin", "*");
    response.setHeader("Access-Control-Allow-Methods",
      "Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS");

    objectMapper.writeValue(out, exceptionResponseDto);
    out.flush();
    out.close();
  }
}
