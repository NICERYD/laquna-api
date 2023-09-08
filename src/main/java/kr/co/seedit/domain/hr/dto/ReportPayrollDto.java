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
	private int companyId;
    private int employeeId;
    private int estId;
    private String yyyymm;
    private String employeeNumber;
    private String koreanName;
    private String estName;
    private String departmentName;
    private String positionCode;
    private String positionType;
    private String dutyType;
    private int basicSalary;
    private int hourlyPay;
    private int annualAllowance;
    private int overtimeAllowance01;
    private int overtimeAllowance02;
    private int nightAllowance01;
    private int nightAllowance02;
    private int holidayAllowance01;
    private int holidayAllowance02;
    private int positionAllowance;
    private int otherAllowances;
    private int subsidies;
    private int transportationExpenses;
    private int mealsExpenses;
    private int bonusAmount;
    private int loginUserId;
    private String employeeType;
    private String hireDate;
    private String retireDate;
    private String definedName;
    private int salarySum;
    private int nationalPension;
    private int healthInsurance;
    private int careInsurance;
    private int employmentInsurance;
    private int advance;
    private int otherTax;
    private int gyeongjobi;
    private int yearendIncomtax;
    private int yearendResidtax;
    private int yearend;
    private int healthInsuranceSettlement;
    private int careInsuranceSettlement;
    private int holidayTax;
    private int incomtax;
    private int residtax;
    private int taxSum;
    private String dtPay;
    private int cnt;
    private String emailAddress;
}
