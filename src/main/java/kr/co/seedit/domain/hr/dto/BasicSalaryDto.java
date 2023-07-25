package kr.co.seedit.domain.hr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class BasicSalaryDto {
    private Integer companyId;
    private Integer employeeId;
    private Integer estId;
    private String yyyymm;
    private String employeeNumber;
    private String koreanName;
    private String estName;
    private String departmentName;
    private String positionCode;
    private String positionType;
    private String basicSalary;
    private String hourlyPay;
    private String annualAllowance;
    private String overtimeAllowance01;
    private String overtimeAllowance02;
    private String nightAllowance01;
    private String nightAllowance02;
    private String holidayAllowance01;
    private String holidayAllowance02;
    private String positionAllowance;
    private String otherAllowance;
    private String subsidies;
    private String transportationExpenses;
    private String mealsExpenses;
    private String bonusAmount;
    private Integer loginUserId;
    private String employeeType;
    private String hireDate;
    private String retireDate;

}
