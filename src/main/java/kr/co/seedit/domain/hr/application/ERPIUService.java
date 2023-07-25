package kr.co.seedit.domain.hr.application;

import kr.co.seedit.domain.company.dto.CompanyDto;
import kr.co.seedit.domain.hr.dto.ErpIUDto;
import kr.co.seedit.domain.mapper.neoe.ErpIUDao;
import kr.co.seedit.domain.mapper.seedit.CompanyDao;
import kr.co.seedit.domain.mapper.seedit.SalaryDao;
import kr.co.seedit.global.common.dto.ResponseDto;
import kr.co.seedit.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ERPIUService {
    private final ErpIUDao erpIUDao;
    private final CompanyDao companyDao;
    private final SalaryDao salaryDao;

    @Transactional
    public ResponseDto getBasicSalaryData(ErpIUDto.RequestDto erpIUDto) throws CustomException, IOException, InvalidFormatException {
        ResponseDto responseDto = ResponseDto.builder().build();
        CompanyDto companyDto = new CompanyDto();
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userName = ((UserDetails) principal).getUsername();
        companyDto.setEmailId(userName);
        CompanyDto info = companyDao.selectTokenInfo(companyDto);
        erpIUDto.setLoginUserId(info.getUserId());

        // 급여코드항목
        // DELETE
        salaryDao.deleteSalaryItem(erpIUDto);
        // SELECT
        List<ErpIUDto.SalaryItemDto> salaryItemDtos = erpIUDao.getSalaryItem(erpIUDto);
        // INSERT
        salaryDao.insertSalaryItem(salaryItemDtos);

        return responseDto;
    }

    @Transactional
    public ResponseDto getDataErpIU(ErpIUDto.RequestDto erpIUDto, HttpServletRequest request) throws CustomException, IOException, InvalidFormatException {
        ResponseDto responseDto = ResponseDto.builder().build();

        CompanyDto companyDto = new CompanyDto();
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userName = ((UserDetails) principal).getUsername();
        companyDto.setEmailId(userName);
        CompanyDto info = companyDao.selectTokenInfo(companyDto);
        erpIUDto.setLoginUserId(info.getUserId());

        // 부서, 사원, 책정임금, 상여금, 급여계산, 캘린더
        // DELETE
        salaryDao.deleteDepartment(erpIUDto);
        salaryDao.deleteEmployee(erpIUDto);
        salaryDao.deleteSalaryBasic(erpIUDto);
        salaryDao.deleteADTData(erpIUDto);
        salaryDao.deleteBonus(erpIUDto);
        salaryDao.deleteCalcSalary(erpIUDto);
        salaryDao.deleteCalendar(erpIUDto);
        // SELECT
        List<ErpIUDto.DepartmentDto> departmentDtos = erpIUDao.getDepartment(erpIUDto);
        List<ErpIUDto.EmployemeeDto> employemeeDtos = erpIUDao.getEmployee(erpIUDto);
        List<ErpIUDto.SalaryBasicDto> salaryBasicDtos = erpIUDao.getSalaryBasic(erpIUDto);
        List<ErpIUDto.BonusDtos> bonusDtos = erpIUDao.getBonus(erpIUDto);
        List<ErpIUDto.CalendarDto> calendarDtos = erpIUDao.getCalendar(erpIUDto);
        // INSERT
        if (!departmentDtos.isEmpty()) salaryDao.insertDepartment(departmentDtos);
        if (!employemeeDtos.isEmpty()) salaryDao.insertEmployee(employemeeDtos);
        if (!salaryBasicDtos.isEmpty()) salaryDao.insertSalaryBasic(salaryBasicDtos);
        if (!bonusDtos.isEmpty()) salaryDao.insertBonus(bonusDtos);
        if (!calendarDtos.isEmpty()) salaryDao.insertCalendar(calendarDtos);

        return responseDto;
    }
}
