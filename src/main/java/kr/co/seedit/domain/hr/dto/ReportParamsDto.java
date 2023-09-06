package kr.co.seedit.domain.hr.dto;

import java.util.List;

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
    private Integer employeeId;
    private String employeeNumber;
    private Integer employeeType;
    
    private String reportType;
    private String sort;
    
    private List<String> employeeNumberList;
}
