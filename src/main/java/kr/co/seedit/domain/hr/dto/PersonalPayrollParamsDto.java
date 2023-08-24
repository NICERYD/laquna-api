package kr.co.seedit.domain.hr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class PersonalPayrollParamsDto {
	private Integer employeeId;
	private String koreanName;
	private String employeeType;	//100:연봉제, 200:시급제 
}
