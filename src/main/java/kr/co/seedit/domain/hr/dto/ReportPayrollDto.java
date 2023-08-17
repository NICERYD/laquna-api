package kr.co.seedit.domain.hr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class ReportPayrollDto {
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
    private String dutyType;
    private Integer basicSalary;
    private Integer hourlyPay;
    private Integer annualAllowance;
    private Integer overtimeAllowance01;
    private Integer overtimeAllowance02;
    private Integer nightAllowance01;
    private Integer nightAllowance02;
    private Integer holidayAllowance01;
    private Integer holidayAllowance02;
    private Integer positionAllowance;
    private Integer otherAllowances;
    private Integer subsidies;
    private Integer transportationExpenses;
    private Integer mealsExpenses;
    private Integer bonusAmount;
    private Integer loginUserId;
    private String employeeType;
    private String hireDate;
    private String retireDate;
    private String definedName;
    private Integer salarySum;
}
