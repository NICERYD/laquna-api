package kr.co.seedit.domain.hr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class OtherAllowanceDto {
	private Integer loginUserId;
	private Integer companyId;
	private String employeeNumber;
	private String yyyymm;
	
	private Double annualLeaveCalc;
	private Double annualLeaveAllowance;
	private Double other02Used;
	private Double otherAllowance02;
}
