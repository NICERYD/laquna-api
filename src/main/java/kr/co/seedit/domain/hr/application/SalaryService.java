package kr.co.seedit.domain.hr.application;


import kr.co.seedit.domain.company.application.CompanyService;
import kr.co.seedit.domain.company.dto.CompanyDto;
import kr.co.seedit.domain.hr.dto.*;
import kr.co.seedit.domain.mapper.neoe.ErpIUDao;
import kr.co.seedit.domain.mapper.seedit.CompanyDao;
import kr.co.seedit.domain.mapper.seedit.SalaryDao;
import kr.co.seedit.global.common.dto.RequestDto;
import kr.co.seedit.global.common.dto.ResponseDto;
import kr.co.seedit.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SalaryService {
    private static final Logger logger = LoggerFactory.getLogger(CompanyService.class);
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String YYYY_MM_DD_TT = "yyyy-MM-dd HH:mm:ss";
    public static final String HH_MM = "HH:mm";

    private final SalaryDao salaryDao;
    private final ErpIUDao erpIUDao;
    private final CompanyDao companyDao;


    @Transactional
    public List<SelectListDto> getCompanyList() throws Exception {
        CompanyDto companyDto = new CompanyDto();
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userName = ((UserDetails) principal).getUsername();
        companyDto.setEmailId(userName);
        CompanyDto info = companyDao.selectTokenInfo(companyDto);
        Integer companyId = info.getCompanyId();

        return salaryDao.findCompanyList(companyId);
    }

    @Transactional
    public List<SelectListDto> getBusinessList() throws Exception {

        CompanyDto companyDto = new CompanyDto();
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userName = ((UserDetails) principal).getUsername();
        companyDto.setEmailId(userName);
        CompanyDto info = companyDao.selectTokenInfo(companyDto);
        Integer companyId = info.getCompanyId();
        return salaryDao.findBusinessList(companyId);
    }

    @Transactional
    public List<EmployeeInformationDto> getAdtList(EmployeeInformationDto employeeInformationDto) throws Exception {
        return salaryDao.findAdtList(employeeInformationDto);
    }

    @Transactional
    public List<BasicSalaryDto> getCalcSalaryList(BasicSalaryDto basicSalaryDto) throws Exception {

        return salaryDao.getCalcSalaryList(basicSalaryDto);
    }

    @Transactional
    public ResponseDto uploadADTExcel(MultipartFile adtExcel01, MultipartFile adtExcel02, String attribute1, ErpIUDto.RequestDto erpIUDto, HttpServletRequest request) throws CustomException, IOException, InvalidFormatException {
        ResponseDto responseDto = ResponseDto.builder().build();

        CompanyDto companyDto = new CompanyDto();
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userName = ((UserDetails) principal).getUsername();
        companyDto.setEmailId(userName);
        CompanyDto info = companyDao.selectTokenInfo(companyDto);
        erpIUDto.setCompanyId(info.getCompanyId());
        erpIUDto.setLoginUserId(info.getUserId());
        erpIUDto.setYyyymm(attribute1);

        List<ADTExcelDto> listADT = new ArrayList<>();
        List<ADTExcelDto> OutlistADT = new ArrayList<>();

        try {
            OPCPackage opcPackage = OPCPackage.open(adtExcel01.getInputStream());
            XSSFWorkbook workbook = new XSSFWorkbook(opcPackage);
            XSSFSheet sheet = workbook.getSheetAt(0);

            int rows = sheet.getPhysicalNumberOfRows();
            int rowindex = 0;
            int cellindex = 0;

            for (rowindex = 1; rowindex < rows; rowindex++) {
                ADTExcelDto adtExcelDto = new ADTExcelDto();
                adtExcelDto.setCompanyId(info.getCompanyId());
                adtExcelDto.setLoginUserId(info.getUserId());
                adtExcelDto.setYyyymm(attribute1);
                XSSFRow row = sheet.getRow(rowindex);
                int cells = row.getPhysicalNumberOfCells();

                for (cellindex = 0; cellindex <= cells; cellindex++) {
                    XSSFCell cell = row.getCell(cellindex);
                    String value = "";
                    if (cell == null || cell.getCellType() == CellType.BLANK) {
                        value = "";
                    } else {
                        value = determineDate(cell, cell.getCellType());
                    }
                    switch (cellindex) {
                        case 0:
                            adtExcelDto.setWorkDate(value);
                            break;
                        case 3:
                            adtExcelDto.setEmployeeNumber(value);
                            break;
                        case 7:
                            adtExcelDto.setWorkStatus(value);
                            break;
                        case 8:
                            adtExcelDto.setWorkStartDate(value);
                            break;
                        case 9:
                            adtExcelDto.setWorkEndDate(value);
                            break;
                        case 10:
                            adtExcelDto.setInStatus(value);
                            break;
                        case 11:
                            adtExcelDto.setOutStatus(value);
                            break;
                        case 12:
                            adtExcelDto.setLateTime(value);
                            break;
                        case 13:
                            adtExcelDto.setOverTime(value);
                            break;
                        case 14:
                            adtExcelDto.setNightTime(value);
                            break;
                        case 15:
                            adtExcelDto.setHolidayTime(value);
                            break;
                        case 16:
                            adtExcelDto.setRealWorkTime(value);
                            break;
                        case 17:
                            adtExcelDto.setTotalTime(value);
                            break;
                        case 18:
                            adtExcelDto.setDefaultTime(value);
                            break;
                    }
                    if (cellindex == 18) break;
                }
                listADT.add(adtExcelDto);
            }
            opcPackage.close();
            salaryDao.deleteADTData(erpIUDto);
            salaryDao.deleteCalcSalary(erpIUDto);
            salaryDao.insetADTData(listADT);

            if (adtExcel02 != null && !adtExcel02.isEmpty()) {
                //외출 리스트
                OPCPackage outOpcPackage = OPCPackage.open(adtExcel02.getInputStream());
                XSSFWorkbook outWorkbook = new XSSFWorkbook(outOpcPackage);
                XSSFSheet outSheet = outWorkbook.getSheetAt(0);

                int outRows = outSheet.getPhysicalNumberOfRows();
                int outRowindex = 0;
                int outCellIndex = 0;

                for (outRowindex = 1; outRowindex < outRows; outRowindex++) {
                    ADTExcelDto outAdtExcelDto = new ADTExcelDto();
                    outAdtExcelDto.setLoginUserId(info.getUserId());
                    XSSFRow outRow = outSheet.getRow(outRowindex);
                    int outCells = outRow.getPhysicalNumberOfCells();

                    for (outCellIndex = 0; outCellIndex <= outCells; outCellIndex++) {
                        XSSFCell outCell = outRow.getCell(outCellIndex);

                        if (outCell == null || outCell.getCellType() == CellType.BLANK) {
                            ;
                        }
                        switch (outCellIndex) {
                            case 0:
                                outAdtExcelDto.setEventTime(new SimpleDateFormat(YYYY_MM_DD_TT).format(outCell.getDateCellValue()));
                                outAdtExcelDto.setWorkDate(new SimpleDateFormat(YYYY_MM_DD).format(outCell.getDateCellValue()));
                                break;
                            case 6:
                                outAdtExcelDto.setEmployeeNumber(outCell.getStringCellValue());
                                break;
                            case 10:
                                outAdtExcelDto.setEventType(outCell.getStringCellValue());
                                break;
                        }
                        if (outCellIndex == 10) break;
                    }
                    logger.info(outAdtExcelDto.getEventType());
                    if ((outAdtExcelDto.getEventType()).equals("외출") || (outAdtExcelDto.getEventType()).equals("복귀")) {
                        OutlistADT.add(outAdtExcelDto);
                    }
                }
                logger.info("outAdtExcelDto : {}", OutlistADT.toString());
                outOpcPackage.close();
                outWorkbook.close();

                for (ADTExcelDto item : OutlistADT) {
                    salaryDao.upadateOutAdTData(item);
                }
            }


        } catch (Exception e) {
            logger.error("Exception", e);
            throw e;
        }
        return responseDto;
    }

    private static String determineDate(Cell cell, CellType cellType) {
        String value = "";
        switch (cellType) {
            case NUMERIC:
                short dataFormat = cell.getCellStyle().getDataFormat();
                if (14 == dataFormat || 176 == dataFormat) {
                    Date dateCellValue = cell.getDateCellValue();
                    value = new SimpleDateFormat(YYYY_MM_DD).format(dateCellValue);
                } else if (20 == dataFormat) {
                    Date dateCellValue = cell.getDateCellValue();
                    value = new SimpleDateFormat(HH_MM).format(dateCellValue);
                } else if (22 == dataFormat || 177 == dataFormat) {
                    Date dateCellValue = cell.getDateCellValue();
                    value = new SimpleDateFormat(YYYY_MM_DD_TT).format(dateCellValue);
                }
                ;
                break;
            case STRING:
                value = cell.getStringCellValue();
                break;
        }
        return value;
    }

    @Transactional
    public ResponseDto calcSalary(RequestDto requestDto) throws CustomException, IOException, InvalidFormatException {
        ResponseDto responseDto = ResponseDto.builder().build();

        List<BasicSalaryDto> resultData = new ArrayList<>();

        CompanyDto companyDto = new CompanyDto();
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userName = ((UserDetails) principal).getUsername();
        companyDto.setEmailId(userName);
        CompanyDto info = companyDao.selectTokenInfo(companyDto);
        requestDto.setCompanyId(info.getCompanyId());
        requestDto.setLoginUserId(info.getUserId());

        // 기준코드값
        LocalTime overtimeBaseTime = LocalTime.MIN;
        BigDecimal overtimeBaseAmount = BigDecimal.ZERO;
        LocalTime nightBaseTime = LocalTime.MIN;
        BigDecimal nightBaseAmount = BigDecimal.ZERO;
        Integer holidayBaseTime = 0;
        BigDecimal holidayBaseAmount = BigDecimal.ZERO;

        List<SalaryCodeValuesDto> salaryCodeValuesDto = salaryDao.selectCodeValues(requestDto);

        for (SalaryCodeValuesDto codeValues : salaryCodeValuesDto) {
            if (codeValues.getCodeField().equals("HR_Z0001")) {
                overtimeBaseTime = LocalTime.parse(codeValues.getValue01(), DateTimeFormatter.ofPattern("HH:mm"));
                overtimeBaseAmount = new BigDecimal(codeValues.getValue02());
            }
            if (codeValues.getCodeField().equals("HR_Z0002")) {
                nightBaseTime = LocalTime.parse(codeValues.getValue01(), DateTimeFormatter.ofPattern("HH:mm"));
                nightBaseAmount = new BigDecimal(codeValues.getValue02());
            }
            if (codeValues.getCodeField().equals("HR_Z0003")) {
                holidayBaseTime = Integer.parseInt(codeValues.getValue01());
                holidayBaseAmount = new BigDecimal(codeValues.getValue02());
            }
        }


        List<BasicSalaryDto> basicSalaryDtos = salaryDao.selectBasicSalary(requestDto);
        for (BasicSalaryDto basicSalaryDto : basicSalaryDtos) {
            // 기본급, 시급, 연차수당
            BigDecimal basicAmount = BigDecimal.ZERO;
            BigDecimal hourlyPay = BigDecimal.ZERO;
            BigDecimal annualAllowance = BigDecimal.ZERO;
            // 수당1 저장 변수
            BigDecimal overtimeAmount01 = BigDecimal.ZERO;
            BigDecimal nightAmount01 = BigDecimal.ZERO;
            BigDecimal holidayAmount01 = BigDecimal.ZERO;
            // 수당2 저장 변수
            BigDecimal overtimeAmount02 = BigDecimal.ZERO;
            BigDecimal nightAmount02 = BigDecimal.ZERO;
            BigDecimal holidayAmount02 = BigDecimal.ZERO;
            // 기타, 교통비, 식비
            BigDecimal otherAmount = BigDecimal.ZERO;
            BigDecimal transportationAmount = BigDecimal.ZERO;
            BigDecimal mealsAmount = BigDecimal.ZERO;

            LocalDate hireDate = LocalDate.parse(basicSalaryDto.getHireDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate retireDate = LocalDate.parse(basicSalaryDto.getRetireDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            YearMonth yearMonth = YearMonth.parse(requestDto.getYyyymm(), DateTimeFormatter.ofPattern("yyyyMM"));
            String midStatus = "000";
            // 무급 count 변수
            Integer nonPaycnt = 0;
            // 출산휴가 count 변수
            Integer maternityLeavecnt = 0;

            // 해당 월(YYYYMM) 기준 중도 입사/퇴사 날짜계산
            long diff;
            // 중도 입사/퇴사 체크
            if (basicSalaryDto.getHireDate().substring(0, 7).replace("-", "").equals(requestDto.getYyyymm())) {
                diff = ChronoUnit.DAYS.between(hireDate, yearMonth.atEndOfMonth());
                midStatus = "001";
            } else if (basicSalaryDto.getRetireDate().substring(0, 7).replace("-", "").equals(requestDto.getYyyymm())) {
                diff = ChronoUnit.DAYS.between(yearMonth.atDay(1), retireDate);
                midStatus = "002";
            } else {
                diff = 0;
            }
            // 중도 입사/퇴사
            // 각 항목 금액(기본급/연장수당2/야간수당2/휴일수당2) / 30일 * 근무일 로 각 항목 금액으로 표기
            if ((basicSalaryDto.getEmployeeType().equals("100") || (basicSalaryDto.getEmployeeType().equals("200") && basicSalaryDto.getDutyType().equals("201")))
                    && !midStatus.equals("000")) {
                basicAmount = new BigDecimal(basicSalaryDto.getBasicSalary()).multiply(BigDecimal.valueOf(diff).divide(BigDecimal.valueOf(30), 2, BigDecimal.ROUND_UP)).setScale(0, BigDecimal.ROUND_UP);
                overtimeAmount02 = Optional.ofNullable(basicSalaryDto.getOvertimeAllowance02())
                        .map(amount -> new BigDecimal(amount)
                                .multiply(BigDecimal.valueOf(diff).divide(BigDecimal.valueOf(30), 4, BigDecimal.ROUND_UP)).setScale(0, BigDecimal.ROUND_UP))
                        .orElse(BigDecimal.ZERO);

                nightAmount02 = Optional.ofNullable(basicSalaryDto.getNightAllowance02())
                        .map(amount -> new BigDecimal(amount)
                                .multiply(BigDecimal.valueOf(diff).divide(BigDecimal.valueOf(30), 4, BigDecimal.ROUND_UP)).setScale(0, BigDecimal.ROUND_UP))
                        .orElse(BigDecimal.ZERO);

                holidayAmount02 = Optional.ofNullable(basicSalaryDto.getHolidayAllowance02())
                        .map(amount -> new BigDecimal(amount)
                                .multiply(BigDecimal.valueOf(diff).divide(BigDecimal.valueOf(30), 4, BigDecimal.ROUND_UP)).setScale(0, BigDecimal.ROUND_UP))
                        .orElse(BigDecimal.ZERO);
                //시급제 && 별정직 .  경비
                // 시급제지만 기본금만 지급, 추가 근무시간 이외 연장, 휴일의 경우 연봉제에 동일하게 수당 적용
            } else if (basicSalaryDto.getEmployeeType().equals("200")) {

            }

            // 01. 연봉제
            if ((basicSalaryDto.getEmployeeType().equals("100")
                    && !Arrays.asList("120", "130", "140", "150").contains(basicSalaryDto.getPositionCode()) //사장, 부사장, 전무, 상무
                    && !Arrays.asList("100", "910").contains(basicSalaryDto.getPositionType()))  // 대표이사, 고문
                    || basicSalaryDto.getEmployeeType().equals("200") && basicSalaryDto.getDutyType().equals("201")) {  // 시급제, 별정직
                List<ADTDataDto> adtDataDtos = salaryDao.selectADTData(requestDto.getCompanyId(), requestDto.getYyyymm(), basicSalaryDto.getEmployeeId());
                for (ADTDataDto adtDataDto : adtDataDtos) {
                    // 연장수당1
                    DayOfWeek dayOfWeek = LocalDate.parse(adtDataDto.getWorkDate(), DateTimeFormatter.ISO_LOCAL_DATE).getDayOfWeek();
                    if (!Arrays.asList("SATURDAY", "SUNDAY").contains(dayOfWeek.name())
                            && !Arrays.asList("결근", "휴일", "휴일출근").contains(adtDataDto.getInStatus())
                            && !adtDataDto.getOvertime().equals("00:00")) {
                        LocalDate workStartDate = LocalDate.parse(adtDataDto.getWorkStartDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        LocalDate workEndDate = LocalDate.parse(adtDataDto.getWorkEndDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        Period period = Period.between(workStartDate, workEndDate);
                        LocalTime workEndTime = LocalTime.parse(adtDataDto.getWorkEndDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        if (workEndTime.equals(overtimeBaseTime) || workEndTime.isAfter(overtimeBaseTime) || period.getDays() >= 1) {
                            overtimeAmount01 = overtimeAmount01.add(overtimeBaseAmount);
                        }
                    }
                    // 야간수당1
                    if (!adtDataDto.getNightTime().equals("00:00")) {
                        LocalDate workStartDate = LocalDate.parse(adtDataDto.getWorkStartDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        LocalDate workEndDate = LocalDate.parse(adtDataDto.getWorkEndDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        LocalDateTime workEndTime = LocalDateTime.parse(adtDataDto.getWorkEndDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        if (workEndDate.toEpochDay() - workStartDate.toEpochDay() >= 1 && workEndTime.getHour() >= nightBaseTime.getHour() && workEndTime.getMinute() >= nightBaseTime.getMinute()) {
                            nightAmount01 = nightAmount01.add(nightBaseAmount);
                            System.out.println(adtDataDto.getWorkDate() + " " + adtDataDto.getEmployeeId() + " " + nightAmount01);
                        }
                    }
                    // 휴일수당1
                    if (Arrays.asList("휴일", "휴일출근").contains(adtDataDto.getInStatus())
                            && !adtDataDto.getHolidayTime().equals("00:00")) {
                        LocalDateTime workStartTime = LocalDateTime.parse(adtDataDto.getWorkStartDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        LocalDateTime workEndTime = LocalDateTime.parse(adtDataDto.getWorkEndDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        // 10:30분 이전 출근
                        boolean startyn = (workStartTime.toLocalTime().isBefore(LocalTime.of(10, 30)) || workStartTime.toLocalTime().equals(LocalTime.of(10, 30)));
                        // 17:30분 이후 퇴근
                        boolean endyn = (workEndTime.toLocalTime().isAfter(LocalTime.of(17, 30)) || workEndTime.toLocalTime().equals(LocalTime.of(17, 30)));
                        // 출근시간과 퇴근시간 사이에 점심시간이 포함되는지 체크
                        boolean isLunchTimeIncluded = checkLunchTime(workStartTime, workEndTime, LocalTime.of(12, 30), LocalTime.of(13, 30));
                        Duration duration = Duration.between(workStartTime, workEndTime);
                        int yy = duration.toHoursPart() - (isLunchTimeIncluded ? 1 : 0);
                        if (startyn && endyn && yy >= 8) {
                            holidayAmount01 = holidayAmount01.add(holidayBaseAmount.multiply(BigDecimal.valueOf(2)));
                        } else if ((startyn || endyn) && yy >= 4) {
                            holidayAmount01 = holidayAmount01.add(holidayBaseAmount);
                        }
                    }
                    // 무급처리 count
                    if (adtDataDto.getWorkStatus().equals("무급")) {
                        nonPaycnt++;
                    }
                    // 출산휴가 count
                    if (adtDataDto.getWorkStatus().equals("출산휴가")) {
                        maternityLeavecnt++;
                    }
                }
                // 무급처리
                // 책정임금등록의 (기본급/연장수당2/야간수당2/휴일수당2) / 30일 * 무급휴가일 (소숫점 첫째자리 ROUNDUP)
                if (!(nonPaycnt == 0)) {
                    annualAllowance = calcNonPay(nonPaycnt, basicSalaryDto.getBasicSalary(), basicSalaryDto.getOvertimeAllowance02(), basicSalaryDto.getNightAllowance02(), basicSalaryDto.getHolidayAllowance02());
                }
                // 02. 시급제
                // 별정직 제외
            } else if (basicSalaryDto.getEmployeeType().equals("200") && !basicSalaryDto.getDutyType().equals("201") && !(basicSalaryDto.getHourlyPay() == null)) {
                // 야간조 근무일수
                Integer nightCnt = 0;
                hourlyPay = new BigDecimal(basicSalaryDto.getHourlyPay());
                // 유급휴가 count 변수
                Integer paidHolidayindex = 0;
                Integer paidHoliday = 0;
                // 평일 실근무일수
                Integer workcnt = 0;
                // 연차/반차 사용 count, 시간 변수
                Integer annualLeave = 0;
                Integer halfDayLeave = 0;
                double halfDayLeaveTime = 0;
                // 기타수당. 지각, 조퇴, 외출
                double lateTime = 0;
                double earlyLeaveTime = 0;
                double outingTime = 0;

                List<ADTDataDto> adtDataDtos = salaryDao.selectADTData(requestDto.getCompanyId(), requestDto.getYyyymm(), basicSalaryDto.getEmployeeId());
                for (ADTDataDto adtDataDto : adtDataDtos) {
                    LocalDate workStartDate = null;
                    LocalDate workEndDate = null;
                    LocalDateTime workStartTime = null;
                    LocalDateTime workEndTime = null;
                    System.out.println(adtDataDto.getWorkStartDate());
                    if (!(adtDataDto.getWorkStartDate() == null) && !(adtDataDto.getWorkStartDate().equals(""))
                            && !(adtDataDto.getWorkEndDate() == null) && !(adtDataDto.getWorkEndDate().equals(""))) {
                        workStartDate = LocalDate.parse(adtDataDto.getWorkStartDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        workEndDate = LocalDate.parse(adtDataDto.getWorkEndDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        workStartTime = LocalDateTime.parse(adtDataDto.getWorkStartDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        workEndTime = LocalDateTime.parse(adtDataDto.getWorkEndDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    }
                    // 유급휴가 지난달 index값 있으면 해당값으로 초기화
                    Integer paidLeaveCnt = salaryDao.selectpaidHolidayindex(info.getCompanyId(), String.valueOf(Integer.parseInt(requestDto.getYyyymm()) - 1), adtDataDto.getEmployeeNumber());
                    paidHolidayindex = Optional.ofNullable(paidLeaveCnt).orElse(0);

                    // 연차/반차 count
                    if (adtDataDto.getWorkStatus().equals("연차")) {
                        annualLeave++;
                    }
                    if (adtDataDto.getWorkStatus().contains("오전반차") && !workStartTime.isBefore(workStartTime.with(LocalTime.of(8, 30)))) {
                        Duration duration = Duration.between(workStartTime.with(LocalTime.of(8, 30)), workStartTime);
                        halfDayLeaveTime = halfDayLeaveTime + duration.toHours() + (duration.toMinutes() % 60 >= 30 ? 1 : 0.5);
                        halfDayLeave++;
                    }
                    if (adtDataDto.getWorkStatus().contains("오후반차") && !workEndTime.isAfter(workEndTime.with(LocalTime.of(13, 00)))) {
                        Duration duration = Duration.between(workEndTime.with(LocalTime.of(13, 00)), workEndTime);
                        halfDayLeaveTime = halfDayLeaveTime + duration.toHours() + (duration.toMinutes() % 60 >= 30 ? 1 : 0.5);
                        halfDayLeave++;
                    }

                    // 연장수당1
                    if (!adtDataDto.getOvertime().equals("00:00") && adtDataDto.getDateType().equals("1")) {
                        LocalTime overTime = LocalTime.parse(adtDataDto.getOvertime(), DateTimeFormatter.ofPattern("HH:mm"));
                        if (overTime.getHour() >= 1)
                            overtimeAmount01 = overtimeAmount01.add(new BigDecimal(overTime.getHour()).multiply(hourlyPay).multiply(BigDecimal.valueOf(1.5)));
                    }

                    // 야간수당1
                    if (adtDataDto.getWorkStatus().equals("야간")) {
                        nightCnt++;
                    }
                    if (!adtDataDto.getNightTime().equals("00:00")) {
                        LocalTime nightTime = LocalTime.parse(adtDataDto.getNightTime(), DateTimeFormatter.ofPattern("HH:mm"));
                        if (nightTime.getHour() >= 0 && nightTime.getMinute() >= 30) {
                            nightAmount01 = nightAmount01.add(new BigDecimal(nightTime.getHour()).multiply(hourlyPay).multiply(BigDecimal.valueOf(2)));
                        }
                    }

                    // 휴일수당1
                    if (!adtDataDto.getHolidayTime().equals("00:00")) {
                        LocalTime holidayTime = LocalTime.parse(adtDataDto.getHolidayTime(), DateTimeFormatter.ofPattern("HH:mm"));
                        holidayAmount01 = holidayAmount01.add(new BigDecimal(holidayTime.getHour()).multiply(hourlyPay).multiply(BigDecimal.valueOf(1.5)));
                    }

                    // 기타수당
                    if (adtDataDto.getInStatus().equals("지각")) {
                        String[] timeParts = adtDataDto.getLateTime().split(":");
                        int hours = Integer.parseInt(timeParts[0]);
                        int minutes = Integer.parseInt(timeParts[1]);
                        lateTime = lateTime + hours + (minutes % 60 >= 30 ? 1 : 0.5);
                    }
                    if (adtDataDto.getOutStatus().equals("조퇴")) {
                        Duration duration = Duration.between(workEndTime, workEndTime.with(LocalTime.of(17, 30)));
                        earlyLeaveTime = earlyLeaveTime + duration.toHours() + (duration.toMinutes() % 60 >= 30 ? 1 : 0.5);
                    }
                    if (adtDataDto.getOutTime() != null && !adtDataDto.getOutTime().equals("")) {
//                        String[] timeParts = adtDataDto.getOutTime().split(":");
//                        int hours = Integer.parseInt(timeParts[0]);
//                        int minutes = Integer.parseInt(timeParts[1]);
//                        outingTime = outingTime + hours + (minutes % 60 >= 30 ? 1 : 0.5);
                    }

                    // 유급휴일 count
                    if (adtDataDto.getDateType().equals("1")) {
                        paidHolidayindex++;
                        workcnt++;
                    } else {
                        paidHolidayindex = 0;
                    }
                    if (paidHolidayindex == 5) {
                        paidHoliday++;
                        paidHolidayindex = 0;
                    }
                    // 마지막근무일이면서 마지막주이면서 금요일이 아니면 다음달에 연속하여 유급휴일 count
                    if ((adtDataDto == adtDataDtos.get(adtDataDtos.size() - 1)) && salaryDao.selectLastWeek(adtDataDto.getWorkDate())
                            && !adtDataDto.getDateWeek().equals("5")) {
                        salaryDao.insertNonPayDay(paidHolidayindex, requestDto.getCompanyId(), requestDto.getYyyymm(), basicSalaryDto.getEmployeeId());
                    }
                }

                // 기본급 퇴직여부
                // Y "000": 계약서 상의 시급 × 209H
                // N "001", "002": 일할 계산 시 "(실근무일+유급휴일)×8H" 로 계산
                if (midStatus.equals("000")) {
                    basicAmount = basicAmount.add(hourlyPay.multiply(BigDecimal.valueOf(209)));
                } else {
                    basicAmount = hourlyPay.multiply(BigDecimal.valueOf((workcnt + paidHoliday) * 8L));
                }
                // 연차수당
                annualAllowance = annualAllowance.add(hourlyPay.multiply(BigDecimal.valueOf(8))).setScale(0, RoundingMode.CEILING);
                if (!(annualLeave == 0)) {
                    annualAllowance = annualAllowance.subtract(hourlyPay.multiply(BigDecimal.valueOf(annualLeave))).setScale(0, RoundingMode.CEILING);
                }
                if (!(halfDayLeave == 0)) {
                    annualAllowance = annualAllowance.subtract(hourlyPay.multiply(BigDecimal.valueOf(halfDayLeaveTime))).setScale(0, RoundingMode.CEILING);
                }
                if (!basicSalaryDto.getHireDate().substring(0, 7).replace("-", "").equals(requestDto.getYyyymm())) {
                    annualAllowance = BigDecimal.ZERO;
                }

                // 야간조 야간수당, 교통비, 식대
                if (!(nightCnt == 0)) {
                    nightAmount01 = nightAmount01.add(BigDecimal.valueOf(nightCnt).multiply(hourlyPay).multiply(BigDecimal.valueOf(6.5)).multiply(BigDecimal.valueOf(0.5)));
                    transportationAmount = BigDecimal.valueOf(20000).multiply(BigDecimal.valueOf(nightCnt)).setScale(0, RoundingMode.CEILING);
                    mealsAmount = BigDecimal.valueOf(14000).multiply(BigDecimal.valueOf(nightCnt)).setScale(0, RoundingMode.CEILING);
                }
                // 기타수당
//                    double lateTime = 0;
//                    double earlyLeaveTime = 0;
//                    double outingTime = 0;
                if (lateTime != 0) {
                    otherAmount = otherAmount.add(hourlyPay.multiply(BigDecimal.valueOf(lateTime * -1))).setScale(0, RoundingMode.CEILING);
                }
                if (earlyLeaveTime != 0) {
                    otherAmount = otherAmount.add(hourlyPay.multiply(BigDecimal.valueOf(earlyLeaveTime * -1))).setScale(0, RoundingMode.CEILING);
                }
                if (outingTime != 0) {
                    otherAmount = otherAmount.add(hourlyPay.multiply(BigDecimal.valueOf(outingTime * -1))).setScale(0, RoundingMode.CEILING);
                }

            }

            if (!basicAmount.equals(BigDecimal.ZERO))
                basicSalaryDto.setBasicSalary(basicAmount.toString());
            if (!annualAllowance.equals(BigDecimal.ZERO))
                basicSalaryDto.setAnnualAllowance(annualAllowance.toString());

            if (!overtimeAmount01.equals(BigDecimal.ZERO))
                basicSalaryDto.setOvertimeAllowance01(overtimeAmount01.toString());
            if (!nightAmount01.equals(BigDecimal.ZERO))
                basicSalaryDto.setNightAllowance01(nightAmount01.toString());
            if (!holidayAmount01.equals(BigDecimal.ZERO))
                basicSalaryDto.setHolidayAllowance01(holidayAmount01.toString());

            if (!overtimeAmount02.equals(BigDecimal.ZERO))
                basicSalaryDto.setOvertimeAllowance02(overtimeAmount02.toString());
            if (!nightAmount02.equals(BigDecimal.ZERO))
                basicSalaryDto.setNightAllowance02(nightAmount02.toString());
            if (!holidayAmount02.equals(BigDecimal.ZERO))
                basicSalaryDto.setHolidayAllowance02(holidayAmount02.toString());

            if (!otherAmount.equals(BigDecimal.ZERO))
                basicSalaryDto.setOtherAllowance(otherAmount.toString());
            if (!transportationAmount.equals(BigDecimal.ZERO))
                basicSalaryDto.setTransportationExpenses(transportationAmount.toString());
            if (!mealsAmount.equals(BigDecimal.ZERO))
                basicSalaryDto.setMealsExpenses(mealsAmount.toString());

            resultData.add(basicSalaryDto);
        }
        salaryDao.deleteCalcSalary(requestDto);
        if (!basicSalaryDtos.isEmpty()) salaryDao.insertCalcSalary(resultData);
        return responseDto;
    }

    public static boolean checkLunchTime(LocalDateTime workStartTime, LocalDateTime workEndTime, LocalTime lunchStartTime, LocalTime lunchEndTime) {
        LocalDateTime startTimeWithLunch = workStartTime.with(lunchStartTime);
        LocalDateTime endTimeWithLunch = workStartTime.with(lunchEndTime);

        boolean isLunchTimeIncluded = false;
        if ((startTimeWithLunch.isAfter(workStartTime) || startTimeWithLunch.equals(workStartTime))
                && (startTimeWithLunch.isBefore(workEndTime) || startTimeWithLunch.equals(workEndTime))
                && (endTimeWithLunch.isAfter(workStartTime) || endTimeWithLunch.equals(workStartTime))
                && (endTimeWithLunch.isBefore(workEndTime) || endTimeWithLunch.equals(workEndTime))) {
            isLunchTimeIncluded = true;
        }
        return isLunchTimeIncluded;
    }

    public static BigDecimal calcNonPay(Integer nonPaycnt, String basicSalary, String overtimeAllowance02, String nightAllowance02, String holidayAllowance02) {
        BigDecimal nonPayBasicSalary = BigDecimal.ZERO;
        BigDecimal nonPayOvertimeAllowance02 = BigDecimal.ZERO;
        BigDecimal nonPayNightAllowance02 = BigDecimal.ZERO;
        BigDecimal nonPayHolidayAllowance02 = BigDecimal.ZERO;
        if (basicSalary != null) {
            nonPayBasicSalary = new BigDecimal(basicSalary).multiply(BigDecimal.valueOf(nonPaycnt).divide(BigDecimal.valueOf(30), 3, RoundingMode.HALF_UP)).setScale(0, RoundingMode.HALF_UP);
        }
        if (overtimeAllowance02 != null) {
            nonPayOvertimeAllowance02 = new BigDecimal(overtimeAllowance02).multiply(BigDecimal.valueOf(nonPaycnt).divide(BigDecimal.valueOf(30), 3, BigDecimal.ROUND_HALF_UP)).setScale(0, RoundingMode.HALF_UP);
        }
        if (nightAllowance02 != null) {
            nonPayNightAllowance02 = new BigDecimal(nightAllowance02).multiply(BigDecimal.valueOf(nonPaycnt).divide(BigDecimal.valueOf(30), 3, BigDecimal.ROUND_HALF_UP)).setScale(0, RoundingMode.HALF_UP);
        }
        if (holidayAllowance02 != null) {
            nonPayHolidayAllowance02 = new BigDecimal(holidayAllowance02).multiply(BigDecimal.valueOf(nonPaycnt).divide(BigDecimal.valueOf(30), 3, BigDecimal.ROUND_HALF_UP)).setScale(0, RoundingMode.HALF_UP);
        }

        return (nonPayBasicSalary.add(nonPayOvertimeAllowance02).add(nonPayNightAllowance02).add(nonPayHolidayAllowance02)).multiply(BigDecimal.valueOf(-1));
    }

    @Transactional
    public ResponseDto downloadSalaryExcel(BasicSalaryDto basicSalaryDto, HttpServletResponse response) throws Exception {
    	ResponseDto responseDto = ResponseDto.builder().build();

    	//Excel Data Select
    	List<SalaryExcelDto> salaryExcelDtoList = new ArrayList<>();
    	salaryExcelDtoList = salaryDao.findSalaryExcel(basicSalaryDto);

    	XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(basicSalaryDto.getYyyymm()+"SalaryUpload");
        int rowindex = 0;
        int cellindex = 0;

        //Excel Header Setting
        XSSFRow headerRow = sheet.createRow(rowindex++);
        headerRow.createCell(cellindex++).setCellValue("CD_COMPANY");
        headerRow.createCell(cellindex++).setCellValue("CD_BIZAREA");
        headerRow.createCell(cellindex++).setCellValue("YM");
        headerRow.createCell(cellindex++).setCellValue("CD_EMP");
        headerRow.createCell(cellindex++).setCellValue("TP_EMP");
        headerRow.createCell(cellindex++).setCellValue("TP_PAY");
        headerRow.createCell(cellindex++).setCellValue("NO_SEQ");
        headerRow.createCell(cellindex++).setCellValue("NO_EMP");
        headerRow.createCell(cellindex++).setCellValue("AM_PAY01");
        headerRow.createCell(cellindex++).setCellValue("AM_PAY02");
        headerRow.createCell(cellindex++).setCellValue("AM_PAY03");
        headerRow.createCell(cellindex++).setCellValue("AM_PAY04");
        headerRow.createCell(cellindex++).setCellValue("AM_PAY05");
        headerRow.createCell(cellindex++).setCellValue("AM_PAY06");
        headerRow.createCell(cellindex++).setCellValue("AM_PAY07");
        headerRow.createCell(cellindex++).setCellValue("AM_PAY08");
        headerRow.createCell(cellindex++).setCellValue("AM_PAY09");
        headerRow.createCell(cellindex++).setCellValue("AM_PAY10");
        headerRow.createCell(cellindex++).setCellValue("AM_PAY11");
        headerRow.createCell(cellindex++).setCellValue("AM_PAY12");
        headerRow.createCell(cellindex).setCellValue("AM_PAY13");

        //Empty Row Create
//        XSSFRow emptyRow = sheet.createRow(rowindex++);
//        for(cellindex = 0; cellindex <= 20; cellindex++) {
//        	emptyRow.createCell(cellindex).setCellValue("");
//        }

        //Data Insert
        for(SalaryExcelDto s: salaryExcelDtoList) {
        	cellindex = 0;
        	XSSFRow row = sheet.createRow(rowindex++);
        	row.createCell(cellindex++).setCellValue(s.getCdCompany());
        	row.createCell(cellindex++).setCellValue(s.getCdBizarea());
        	row.createCell(cellindex++).setCellValue(s.getYm());
        	row.createCell(cellindex++).setCellValue(s.getCdEmp());
        	row.createCell(cellindex++).setCellValue(s.getTpEmp());
        	row.createCell(cellindex++).setCellValue(s.getTpPay());
        	row.createCell(cellindex++).setCellValue(s.getNoSeq());
        	row.createCell(cellindex++).setCellValue(s.getNoEmp());
        	row.createCell(cellindex++).setCellValue(s.getAmPay01());
        	row.createCell(cellindex++).setCellValue(s.getAmPay02());
        	row.createCell(cellindex++).setCellValue(s.getAmPay03());
        	row.createCell(cellindex++).setCellValue(s.getAmPay04());
        	row.createCell(cellindex++).setCellValue(s.getAmPay05());
        	row.createCell(cellindex++).setCellValue(s.getAmPay06());
        	row.createCell(cellindex++).setCellValue(s.getAmPay07());
        	row.createCell(cellindex++).setCellValue(s.getAmPay08());
        	row.createCell(cellindex++).setCellValue(s.getAmPay09());
        	row.createCell(cellindex++).setCellValue(s.getAmPay10());
        	row.createCell(cellindex++).setCellValue(s.getAmPay11());
        	row.createCell(cellindex++).setCellValue(s.getAmPay12());
        	row.createCell(cellindex++).setCellValue(s.getAmPay13());
        }

        try {
        	File xlsxFile = new File("C:/Users/admin/Downloads/"+ basicSalaryDto.getYyyymm()+"SalaryUpload" + ".xlsx");
        	FileOutputStream fileOut = new FileOutputStream(xlsxFile);
        	workbook.write(fileOut);
        	workbook.close();
		}catch (Exception e) {
			logger.error("Exception", e);
	        throw e;
    	}


        return responseDto;
    }

}
