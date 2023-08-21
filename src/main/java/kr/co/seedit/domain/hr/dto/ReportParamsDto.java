package kr.co.seedit.domain.hr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class ReportParamsDto {

	private Integer companyId;
	private Integer estId;
    private String yyyymm;
    
    private String reportType;
}
