package kr.co.seedit.domain.mapper.seedit;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import kr.co.seedit.domain.hr.dto.BasicSalaryDto;
import kr.co.seedit.domain.hr.dto.EmployeeInformationDto;
import kr.co.seedit.domain.hr.dto.MonthlyKeunTaeDto;
import kr.co.seedit.domain.hr.dto.PersonalPayrollParamsDto;
import kr.co.seedit.domain.hr.dto.ReportMonthlyKeunTaeDto;
import kr.co.seedit.domain.hr.dto.ReportParamsDto;
import kr.co.seedit.domain.hr.dto.ReportPayrollDto;
import kr.co.seedit.domain.hr.dto.SalaryExcelDto;
import kr.co.seedit.domain.hr.dto.SelectListDto;



@Repository
@Mapper
public interface ReportDao {
	
	List<SalaryExcelDto> findERPIUData(ReportParamsDto reportParamsDto);
	
	List<ReportPayrollDto> findPayrollData(ReportParamsDto reportParamsDto);
	
	List<ReportPayrollDto> findEstPayrollData(ReportParamsDto reportParamsDto);
	
	List<EmployeeInformationDto> findPersonalPayroll(ReportParamsDto reportParamsDto);
	
	List<PersonalPayrollParamsDto> findEmployees(ReportParamsDto reportParamsDto);
	
//	List<ReportMonthlyKeunTaeDto> findPayroll(MonthlyKeunTaeDto monthlyKeunTaeDto);
	
	
}
