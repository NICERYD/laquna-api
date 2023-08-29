package kr.co.seedit.domain.mapper.seedit;


import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import kr.co.seedit.domain.hr.dto.Payroll6InPageDto;
import kr.co.seedit.domain.hr.dto.ReportParamsDto;

@Repository
@Mapper
public interface Payroll6InPageDao {

	List<Payroll6InPageDto> getPayroll6InPageList(ReportParamsDto requestParamsDto);	
}
