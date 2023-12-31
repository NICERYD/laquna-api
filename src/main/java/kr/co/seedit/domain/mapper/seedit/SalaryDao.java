package kr.co.seedit.domain.mapper.seedit;


import kr.co.seedit.domain.hr.dto.*;
import kr.co.seedit.global.common.dto.RequestDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
@Mapper
public interface SalaryDao {

    List<SelectListDto> findCompanyList(Integer companyId);
    List<SelectListDto> findBusinessList(Integer companyId);
    List<EmployeeInformationDto> findAdtList(EmployeeInformationDto employeeInformationDto);
    void deleteADTData(ErpIUDto.RequestDto erpIUDto);
    List<BasicSalaryDto> getCalcSalaryList(ReportParamsDto reportParamsDto);
    List<BasicSalaryDto> getCalcBonusList(ReportParamsDto reportParamsDto);
    
    void updateSalaryList (BasicSalaryDto basicSalaryDto);
    
    void upadateOtherAllowance(OtherAllowanceDto otherAllowanceDto);

    void insetADTData(List<ADTExcelDto> adtExcelDtos);

    void upadateOutAdTData(ADTExcelDto outAdtExcelDto);

    void deleteDepartment(ErpIUDto.RequestDto erpIUDto);

    void deleteEmployee(ErpIUDto.RequestDto erpIUDto);

    void insertDepartment(List<ErpIUDto.DepartmentDto> departmentDtos);

    void insertEmployee(List<ErpIUDto.EmployemeeDto> employemeeDtos);

    void deleteSalaryItem(ErpIUDto.RequestDto erpIUDto);

    void deleteSalaryBasic(ErpIUDto.RequestDto erpIUDto);

    void insertSalaryItem(List<ErpIUDto.SalaryItemDto> salaryItemDtos);

    void insertSalaryBasic(List<ErpIUDto.SalaryBasicDto> salaryBasicDtos);

    void deleteBonus(ErpIUDto.RequestDto erpIUDto);

    void insertBonus(List<ErpIUDto.BonusDtos> bonusDtos);

    List<BasicSalaryDto> selectBasicSalary(RequestDto requestDto);
    List<ADTDataDto> selectADTData(@Param("companyId") Integer companyId, @Param("yyyymm") String yyyymm, @Param("employeeID") Integer employeeId);
    void insertCalcSalary(List<BasicSalaryDto> calcSalaryDtos);
    void deleteCalcSalary(RequestDto requestDto);
    void deleteCalcSalary(ErpIUDto.RequestDto erpIUDto);
    List<SalaryCodeValuesDto> selectCodeValues(RequestDto requestDto);

    void deleteCalendar(ErpIUDto.RequestDto erpIUDto);
    void insertCalendar(List<ErpIUDto.CalendarDto> calendarDtos);
    boolean selectLastWeek(@Param("yyyymmdd") String yyyymmdd);

    void insertNonPayDay(@Param("paidHolidayindex") Integer paidHolidayindex,@Param("companyId") Integer companyId,@Param("yyyymm") String yyyymm,@Param("employeeId") Integer employeeId);
    void insertnightTeamDay(@Param("nightTeamDay") Integer nightTeamDay,@Param("companyId") Integer companyId,@Param("yyyymm") String yyyymm,@Param("employeeId") Integer employeeId);
    

    Integer selectpaidHolidayindex(@Param("companyId") Integer companyId,@Param("yyyymm") String yyyymm,@Param("employeeNumber")  String employeeNumber);

    Integer selectNightdayindex(@Param("companyId") Integer companyId,@Param("yyyymm") String yyyymm,@Param("employeeNumber")  String employeeNumber);

    void deleteMonthlyKeunTae(RequestDto requestDto);
    void insertMonthlyKeunTae(List<MonthlyKeunTaeDto> monthlyKeunTaeDtos);

    void deleteNightEeamDay(RequestDto requestDto);
    void deletePaidLeave(RequestDto requestDto);

    void deletedhTax(ErpIUDto.RequestDto erpIUDto);
    void insertdhTax(List<ErpIUDto.CalcTaxDto> calcTaxDtos);

}
