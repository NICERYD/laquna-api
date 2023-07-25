package kr.co.seedit.domain.hr.dto;

import java.io.Serializable;

import kr.co.seedit.domain.commoncd.dto.CommonModuleDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class SelectListDto implements Serializable {
	
	private Integer value;
	private String name;
	
}
