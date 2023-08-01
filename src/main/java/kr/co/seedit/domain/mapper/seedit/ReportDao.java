package kr.co.seedit.domain.mapper.seedit;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import kr.co.seedit.domain.hr.dto.MonthlyKeunTaeDto;
import kr.co.seedit.domain.hr.dto.ReportMonthlyKeunTaeDto;



@Repository
@Mapper
public interface ReportDao {

	List<ReportMonthlyKeunTaeDto> findPayroll(MonthlyKeunTaeDto monthlyKeunTaeDto);
}
