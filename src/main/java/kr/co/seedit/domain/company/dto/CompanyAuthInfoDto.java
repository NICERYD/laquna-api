package kr.co.seedit.domain.company.dto;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CompanyAuthInfoDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1001615286174360005L;

    private String emailId;
    private int companyId;
    private int userId;
    private int respGroupId;
}
