package kr.co.seedit.domain.mapper.seedit;


import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import kr.co.seedit.domain.hr.dto.PayrollDto;

@Repository
@Mapper
public interface PayrollDao {

	List<PayrollDto> getFulltimePayrollList(Map<String, Object> input);
	List<PayrollDto> getParttimePayrollList(Map<String, Object> input);
	
}
