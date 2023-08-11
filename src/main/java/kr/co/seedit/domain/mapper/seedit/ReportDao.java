package kr.co.seedit.domain.mapper.seedit;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import kr.co.seedit.domain.hr.dto.BasicSalaryDto;
import kr.co.seedit.domain.hr.dto.MonthlyKeunTaeDto;
import kr.co.seedit.domain.hr.dto.ReportMonthlyKeunTaeDto;
import kr.co.seedit.domain.hr.dto.ReportPayrollDto;
import kr.co.seedit.domain.hr.dto.SalaryExcelDto;



@Repository
@Mapper
public interface ReportDao {
	
	List<SalaryExcelDto> findSalaryExcel(BasicSalaryDto basicSalaryDto);
	
	List<ReportMonthlyKeunTaeDto> findPayroll(MonthlyKeunTaeDto monthlyKeunTaeDto);
	
	List<ReportPayrollDto> findPayrollReport(ReportPayrollDto reportPayrollDto);
}
