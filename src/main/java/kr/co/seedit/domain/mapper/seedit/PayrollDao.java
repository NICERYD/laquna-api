package kr.co.seedit.domain.mapper.seedit;


import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import kr.co.seedit.domain.hr.dto.PayrollDto;
import kr.co.seedit.domain.hr.dto.ReportParamsDto;

@Repository
@Mapper
public interface PayrollDao {

	List<PayrollDto> getFulltimePayrollList(ReportParamsDto requestParamsDto);
	List<PayrollDto> getParttimePayrollList(ReportParamsDto requestParamsDto);
	
}
