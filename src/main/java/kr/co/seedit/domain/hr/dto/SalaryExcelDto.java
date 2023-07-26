package kr.co.seedit.domain.hr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class SalaryExcelDto {
	
	private String cdCompany;
	private String cdBizarea;
	private String ym;
	private String cdEmp;
	private String tpEmp;
	private Integer tpPay;
	private Integer noSeq;
	private String noEmp;
	private String amPay01;
	private String amPay02;
	private String amPay03;
	private String amPay04;
	private String amPay05;
	private String amPay06;
	private String amPay07;
	private String amPay08;
	private String amPay09;
	private String amPay10;
	private String amPay11;
	private String amPay12;
	private String amPay13;
	private String amDeduct01;
	private String amDeduct02;
	private String amDeduct03;
	private String amDeduct04;
	private String amDeduct05;
	private String amDeduct06;
	private String amDeduct07;
	private String amDeduct08;
	private String amDeduct09;
	private String amDeduct10;
	private String amDeduct11;
	private String amDeduct12;
	private String amIncomtax;
	private String amResidtax;

}
