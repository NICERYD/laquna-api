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
	private Integer salaryId;
    private Integer companyId;
    private Integer employeeId;
    private Integer estId;
    private String yyyymm;
    private String employeeNumber;
    private String koreanName;
    private String emailAddress;
    private String residentRegistrationNumber;
    private String estName;
    private String departmentName;
    private String positionCode;
    private String positionType;
    private String dutyType;
    private String basicSalary;
    private String hourlyPay;
    private String annualAllowance;
    private String halfAllowance;
    private String overtimeAllowance01;
    private String lateAmount;
    private String earlyAmount;
    private String outerAmount;
    private String overtimeAllowance02;
    private String nightAllowance01;
    private String nightAllowance01Day;
    private String nightAllowance01Night;
    private String nightAllowance02;

    private String holidayAllowance01;
    private String holidayAllowanceSat;
    private String holidayAllowanceSun;
    private String holidayAllowance02;

    private String positionAllowance;
    private String otherAllowance01;
    private String otherAllowance02;
    private String subsidies;
    private String transportationExpenses;
    private String mealsExpenses;
    private String bonusAmount;
    private Integer loginUserId;
    private String employeeType;
    private String hireDate;
    private String retireDate;
    private String definedName;
    private String salarySum;
    private String sort;
    private String nonPayAmount;

}
