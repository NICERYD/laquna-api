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
    private String residentRegistrationNumber;
    private int totalTime;

    private Double overtimeDaytime01;
    private Double ntDaytime01;
    private Double ntNighttime01;
    private Double ntNighttime02;
    private Double holidaySatTime01;
    private Double holidaySunTime01;
    private Double holidayTime02;
	private int overtimeDaytime;
	private int nightDaytime;
	private int holidaySaturday;
	private int holidaySunday;
	private int attribute20;
    private int overtime01;
	private int holiday01;
	private int annualLeaveUsed;
	private int transportation;
	private int meals;
	private int attribute01;
	private int attribute09;
	private int lateTime;
	private int outerTime;
	private int earlyLeaveTime;

}
