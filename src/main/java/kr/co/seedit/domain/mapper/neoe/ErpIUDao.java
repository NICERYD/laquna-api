package kr.co.seedit.domain.mapper.neoe;

import kr.co.seedit.domain.hr.dto.ErpIUDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface ErpIUDao {
    List<ErpIUDto.DepartmentDto> getDepartment(ErpIUDto.RequestDto erpIUDto);
    List<ErpIUDto.EmployemeeDto> getEmployee(ErpIUDto.RequestDto erpIUDto);
    List<ErpIUDto.SalaryItemDto> getSalaryItem(ErpIUDto.RequestDto erpIUDto);
    List<ErpIUDto.SalaryBasicDto> getSalaryBasic(ErpIUDto.RequestDto erpIUDto);
    List<ErpIUDto.BonusDtos> getBonus(ErpIUDto.RequestDto erpIUDto);
    List<ErpIUDto.CalendarDto> getCalendar(ErpIUDto.RequestDto erpIUDto);
}
