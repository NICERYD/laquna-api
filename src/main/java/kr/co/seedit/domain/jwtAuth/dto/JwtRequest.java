package kr.co.seedit.domain.jwtAuth.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class JwtRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 7988524644813854421L;

    private String emailId;
    private String password;
}
