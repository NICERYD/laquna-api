package kr.co.seedit.domain.hr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class PayStubMailHistDto {

	private String baseYyyymm; // 조회 기준 년월
	private int companyId; // 회사아이디
	private String employeeNumber;// 사원번호
	private String lastRequestEmailId;// last request email_id of fnd_user
	private String lastStatus;// 진행상태
	private String lastUpdatedDate;// last update time
	private String lastSuccessDate;// 최종 전송 성공 time

}
