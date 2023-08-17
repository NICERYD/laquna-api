package kr.co.seedit.domain.mapper.seedit;


import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface PayrollDao {

	Map<String, String> getPayrollList(Map<String, String> input);
	
}
