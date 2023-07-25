package kr.co.seedit.domain.company.dto;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class CompanyDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 3344544142821306849L;

    private String emailId;
    private String password;
    private String salt;
    private String nickName;
    private String companyName;
    private String businessRegistrationNumber;
    private int employeeId;
    private int userId;
    private int passwordId;
    private int authenticationId;
    private int companyId;
    private int representativeId;
    private int createdBy;
    private int lastUpdatedBy;
    private String role;
    private String cnt;
    private String approveStatus;

}