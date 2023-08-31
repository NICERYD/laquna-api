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
	private Integer companyId;
	private Integer loginUserId;
	private Double otherAllowance02;
	private String employeeNumber;
	private String yyyymm;
}
