package kr.co.seedit.domain.jwtAuth.dto;

import java.io.Serial;
import java.io.Serializable;

public class JwtResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 8908091796008474006L;
    private final String jwttoken;

    public JwtResponse(String jwttoken) {
    this.jwttoken = jwttoken;
  }

    public String getToken() {
    return this.jwttoken;
  }
}
