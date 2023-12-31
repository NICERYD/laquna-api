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
import java.text.DateFormat;
import java.text.ParseException;
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
    public ResponseDto getCompanyList() throws Exception {
        ResponseDto responseDto = ResponseDto.builder().build();
        CompanyDto companyDto = new CompanyDto();
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userName = ((UserDetails) principal).getUsername();
        companyDto.setEmailId(userName);
        CompanyDto info = companyDao.selectTokenInfo(companyDto);
        Integer companyId = info.getCompanyId();
        try {
            List<SelectListDto> result = salaryDao.findCompanyList(companyId);
            responseDto.setData(result);
        } catch (Exception e) {
            logger.error("Exception", e);
            responseDto.setSuccess(false);
            responseDto.setMessage("오류가 발생했습니다. (" + e.getMessage() + ")");
        }

        return responseDto;
    }

    @Transactional
    public ResponseDto getBusinessList() throws Exception {
        ResponseDto responseDto = ResponseDto.builder().build();
        CompanyDto companyDto = new CompanyDto();
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userName = ((UserDetails) principal).getUsername();
        companyDto.setEmailId(userName);
        CompanyDto info = companyDao.selectTokenInfo(companyDto);
        Integer companyId = info.getCompanyId();
        try {
            List<SelectListDto> result = salaryDao.findBusinessList(companyId);
            responseDto.setData(result);
        } catch (Exception e) {
            logger.error("Exception", e);
            responseDto.setSuccess(false);
            responseDto.setMessage("오류가 발생했습니다. (" + e.getMessage() + ")");
        }

        return responseDto;
    }

    @Transactional
    public ResponseDto getAdtList(EmployeeInformationDto employeeInformationDto) throws Exception {
        ResponseDto responseDto = ResponseDto.builder().build();
        try {
            List<EmployeeInformationDto> result = salaryDao.findAdtList(employeeInformationDto);
            responseDto.setData(result);
        } catch (Exception e) {
            logger.error("Exception", e);
            responseDto.setSuccess(false);
            responseDto.setMessage("조회 중 오류가 발생했습니다. (" + e.getMessage() + ")");
        }
        return responseDto;
    }

    @Transactional
    public ResponseDto getCalcSalaryList(ReportParamsDto reportParamsDto) throws Exception {
        ResponseDto responseDto = ResponseDto.builder().build();
        try {
            List<BasicSalaryDto> result = null;
            if (reportParamsDto.getSort().equals("Salary")) {
                result = salaryDao.getCalcSalaryList(reportParamsDto);
            } else if (reportParamsDto.getSort().equals("Bonus")) {
                result = salaryDao.getCalcBonusList(reportParamsDto);
            }
            responseDto.setData(result);
        } catch (Exception e) {
            logger.error("Exception", e);
            responseDto.setSuccess(false);
            responseDto.setMessage("조회 중 오류가 발생했습니다. (" + e.getMessage() + ")");
        }
        return responseDto;
    }

    @Transactional
    public ResponseDto updateSalaryList(List<BasicSalaryDto> basicSalaryDtoList) throws Exception {
        ResponseDto responseDto = ResponseDto.builder().build();

        CompanyDto companyDto = new CompanyDto();
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userName = ((UserDetails) principal).getUsername();
        companyDto.setEmailId(userName);
        CompanyDto info = companyDao.selectTokenInfo(companyDto);

        try {
            for (BasicSalaryDto b : basicSalaryDtoList) {
                b.setCompanyId(info.getCompanyId());
                b.setLoginUserId(info.getUserId());
                salaryDao.updateSalaryList(b);
            }
        } catch (Exception e) {
            logger.error("Exception", e);
            responseDto.setSuccess(false);
            responseDto.setMessage("오류가 발생했습니다. (" + e.getMessage() + ")");
        }
        return responseDto;

    }

    @Transactional
    public ResponseDto uploadOtherAllowance(MultipartFile file, String yyyymm, ErpIUDto.RequestDto erpIUDto) throws CustomException, IOException, InvalidFormatException {
        ResponseDto responseDto = ResponseDto.builder().build();

        CompanyDto companyDto = new CompanyDto();
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userName = ((UserDetails) principal).getUsername();
        companyDto.setEmailId(userName);
        CompanyDto info = companyDao.selectTokenInfo(companyDto);

        OPCPackage opcPackage = null;
        XSSFWorkbook workbook = null;

        List<OtherAllowanceDto> listOtherAllowance = new ArrayList<>();
        try {
            opcPackage = OPCPackage.open(file.getInputStream());
            workbook = new XSSFWorkbook(opcPackage);
            XSSFSheet sheet = workbook.getSheetAt(0);

            int rows = sheet.getPhysicalNumberOfRows();
            int rowindex = 0;
            int cellindex = 0;

            for (rowindex = 1; rowindex < rows; rowindex++) {
                OtherAllowanceDto otherAllowanceDto = new OtherAllowanceDto();
                otherAllowanceDto.setCompanyId(info.getCompanyId());
                otherAllowanceDto.setLoginUserId(info.getUserId());
                otherAllowanceDto.setYyyymm(yyyymm);

                XSSFRow row = sheet.getRow(rowindex);
                int cells = row.getPhysicalNumberOfCells();

                for (cellindex = 0; cellindex <= cells; cellindex++) {
                    XSSFCell cell = row.getCell(cellindex);
                    switch (cellindex) {
                        case 0:
                            otherAllowanceDto.setEmployeeNumber(cell.getStringCellValue());
                            break;
                        case 2:
                            otherAllowanceDto.setAnnualLeaveCalc(cell.getNumericCellValue());
                            break;
                        case 3:
                            otherAllowanceDto.setAnnualLeaveAllowance(cell.getNumericCellValue());
                            break;
                        case 4:
                            otherAllowanceDto.setOther02Used(cell.getNumericCellValue());
                            break;
                        case 5:
                            otherAllowanceDto.setOtherAllowance02(cell.getNumericCellValue());
                            break;
                    }
                    if (cellindex == 5) break;
                }
                if (otherAllowanceDto.getEmployeeNumber() != null && !"".equals(otherAllowanceDto.getEmployeeNumber())) {
                    listOtherAllowance.add(otherAllowanceDto);
                }
            }

            for (OtherAllowanceDto item : listOtherAllowance) {
                salaryDao.upadateOtherAllowance(item);
            }

        } catch (IllegalStateException e) {
            logger.error("IllegalStateException", e.getMessage());
            responseDto.setSuccess(false);
            responseDto.setMessage("엑셀 처리 중 오류가 발생했습니다. (" + e.getMessage() + ")");
        } catch (Exception e) {
            logger.error("Exception", e);
            responseDto.setSuccess(false);
            responseDto.setMessage("엑셀 처리 중 오류가 발생했습니다. (" + e.getMessage() + ")");
        } finally {
            if (opcPackage != null)
                opcPackage.close();
            if (workbook != null)
                workbook.close();
        }

        return responseDto;
    }

    @Transactional
    public ResponseDto uploadADTExcel(MultipartFile adtExcel01, MultipartFile adtExcel02, String yyyymm, ErpIUDto.RequestDto erpIUDto, HttpServletRequest request) throws CustomException, IOException, InvalidFormatException {
        ResponseDto responseDto = ResponseDto.builder().build();

        CompanyDto companyDto = new CompanyDto();
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userName = ((UserDetails) principal).getUsername();
        companyDto.setEmailId(userName);
        CompanyDto info = companyDao.selectTokenInfo(companyDto);
        erpIUDto.setCompanyId(info.getCompanyId());
        erpIUDto.setLoginUserId(info.getUserId());
        erpIUDto.setYyyymm(yyyymm);

        List<ADTExcelDto> listADT = new ArrayList<>();
        List<ADTExcelDto> OutlistADT = new ArrayList<>();

        OPCPackage opcPackage = null;
        XSSFWorkbook workbook = null;
        OPCPackage outOpcPackage = null;
        XSSFWorkbook outWorkbook = null;

        try {
            opcPackage = OPCPackage.open(adtExcel01.getInputStream());
            workbook = new XSSFWorkbook(opcPackage);
            XSSFSheet sheet = workbook.getSheetAt(0);

            int rows = sheet.getPhysicalNumberOfRows();
            int rowindex = 0;
            int cellindex = 0;

            for (rowindex = 1; rowindex < rows; rowindex++) {
                ADTExcelDto adtExcelDto = new ADTExcelDto();
                adtExcelDto.setCompanyId(info.getCompanyId());
                adtExcelDto.setLoginUserId(info.getUserId());
                adtExcelDto.setYyyymm(yyyymm);
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
                            if (cell.getCellType() == CellType.FORMULA) {
                                throw new Exception("사번을 확인해주세요. (수식 포함)");
                            } else if ("".equals(value)) {
                                throw new Exception("사번을 확인해주세요. (빈 값 포함)");
                            } else {
                                adtExcelDto.setEmployeeNumber(value);
                            }
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

            salaryDao.deleteADTData(erpIUDto);
            salaryDao.deleteCalcSalary(erpIUDto);
            salaryDao.insetADTData(listADT);

            if (adtExcel02 != null && !adtExcel02.isEmpty()) {
                //외출 리스트
                outOpcPackage = OPCPackage.open(adtExcel02.getInputStream());
                outWorkbook = new XSSFWorkbook(outOpcPackage);
                XSSFSheet outSheet = outWorkbook.getSheetAt(0);

                int outRows = outSheet.getPhysicalNumberOfRows();
                int outRowindex = 0;
                int outCellIndex = 0;

                for (outRowindex = 1; outRowindex < outRows; outRowindex++) {
                    ADTExcelDto outAdtExcelDto = new ADTExcelDto();
                    outAdtExcelDto.setLoginUserId(info.getUserId());
                    outAdtExcelDto.setCompanyId(info.getCompanyId());
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
                                if (outCell.getCellType() == CellType.FORMULA) {
                                    throw new Exception("사번을 확인해주세요. (수식 포함)");
                                } else if ("".equals(outCell.getStringCellValue())) {
                                    throw new Exception("사번을 확인해주세요. (빈 값 포함)");
                                } else {
                                    outAdtExcelDto.setEmployeeNumber(outCell.getStringCellValue());
                                }
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


                for (ADTExcelDto item : OutlistADT) {
                    salaryDao.upadateOutAdTData(item);
                }
            }
        } catch (IllegalStateException e) {
            logger.error("IllegalStateException", e.getMessage());
            responseDto.setSuccess(false);
            responseDto.setMessage("엑셀 처리 중 오류가 발생했습니다. (" + e.getMessage() + ")");
        } catch (Exception e) {
            logger.error("Exception", e);
            responseDto.setSuccess(false);
            responseDto.setMessage("엑셀 처리 중 오류가 발생했습니다. (" + e.getMessage() + ")");
        } finally {
            if (opcPackage != null)
                opcPackage.close();
            if (workbook != null)
                workbook.close();
            if (outOpcPackage != null)
                outOpcPackage.close();
            if (outWorkbook != null)
                outWorkbook.close();
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

        // 기본 책정임금 변수 선언
        BigDecimal basicAmount;          // 기본금
        BigDecimal hourlyPay;            // 시급(시간제)
        BigDecimal annualAllowance;      // 연차수당
        BigDecimal halfAllowance;        // 반차수당
        BigDecimal overtimeAllowance01;     // 연장1 수당
        BigDecimal overtimeAllowance02;     // 연장2 수당
//        BigDecimal nightAllowance01;        // 야간1 수당
        BigDecimal nightAllowance01Day;     // 야간1 수당(주간조)
        BigDecimal nightAllowance01Night;   // 야간1 수당(야간조)
        BigDecimal nightAllowance02;        // 야간2 수당

        BigDecimal holidayAllowance01;      // 휴일1 수당
        BigDecimal holidayAllowanceSat;      // 휴일1 수당(토요일)
        BigDecimal holidayAllowanceSun;      // 휴일1 수당(일요일)
        BigDecimal holidayAllowance02;      // 휴일2 수당

        BigDecimal transportationAmount; // 교통비
        BigDecimal mealsAmount;          // 식대
        BigDecimal otherAmount;          // 기타
        BigDecimal lateAmount;           // 지각
        BigDecimal earlyLeaverAmount;    // 조퇴
        BigDecimal OuterAmount;    // 조퇴
        BigDecimal observeHolidayAmount;    // 명절수당

        // 리포트 급여항목 항목 변수 선언
        List<MonthlyKeunTaeDto> monthlyKeunTaeDtos = new ArrayList<>();
        Double rtAnnualLeaveUsed;       // 연차사용
        String rtAnnualLeaveUsedDay;     // 연차사용 일자
        Double rtHalfLeaverUsed;               // 반차시간
        String rtHalfLeaveUseDay;                // 반차일자
        Integer rtHalfLeaveCnt;           // 반차사용횟수

        Double rtOverTimeUsed01;            // 연장1 시간
        Integer rtOverTimeDayCnt;            // 연장1 일수
        Integer rtOverTimeDay2HCnt;        //평일 연장2H
        Integer rtOverTimeDay4H3MCnt;        //철야익일 4.5H

//        Double rtOverDayTimeHours;        // 연장1 주간시간
//        Double rtOverNightTimeHours;      // 연장1 야간시간
//        Double rtNightShiftUsed01;      // 야간1 시간
        Double rtNSDayTimeUsed;        // 야간1 시간(주간조)
        Integer rtNS01DayCnt;        // 야간1 일자


        Integer rtHolidaySaturdayDay4HCnt; //토요 4H
        Integer rtHolidaySaturdayDay8HCnt;//토요 8H
        Double rtNSNightTimeUsed;      // 야간1 시간(야간조)
        Integer rtHolidaySunday4HCnt;   //일요 4H
        Integer rtHolidaySunday8HCnt;   //일요 8H
//        Double rtNightShift02;          // 야간2 일수
        Integer rtNSHoliDayCnt = 0;        // 공휴일 야간일자 cnt

        Double rtHolidaySaturdayUsed;     // 휴일1 (토요일)
        Double rtHolidaySundayUsed;       // 휴일1 (일요일)

        Double rtTransportation;        // 교통비
        Double rtMeal;                  // 식대

        Integer rtOther;                  // 기타비용

        String rtEarlyLeaveDay;          // 조퇴일자
        Double rtEarlyLeaveUsed;         // 조퇴시간
        Integer rtEarlyLeaveCnt;         // 조퇴횟수
        String rtLateDay;                // 지각일자
        Double rtLateUsed;               // 지각시간
        Integer rtLatecnt;               // 지각횟수
        String rtOuterDay;                // 외출일자
        Double rtOuterUsed;               // 외출시간
        Integer rtAbsence;               // 결근

        Double rtTotalTime;             //총근무시간
        Integer workcnt = 0;
        Integer paidHoliday = 0;
        Integer paidHolidayindex = 0;
        Integer nightTeamPlus = 0;

        Integer nonPayCnt = 0;
        String nonPayDay;              // 무급일자
        BigDecimal nonPayAmount = BigDecimal.ZERO;


        // 기준코드값
        LocalTime overtimeBaseTime = LocalTime.MIN;
        BigDecimal overtimeBaseAmount = BigDecimal.ZERO;
        LocalTime nightBaseTime = LocalTime.MIN;
        BigDecimal nightBaseAmount = BigDecimal.ZERO;
        Integer holidayBaseTime = 0;
        BigDecimal holidayBaseAmount = BigDecimal.ZERO;

        // 테이블 초기화
        salaryDao.deleteMonthlyKeunTae(requestDto);
        salaryDao.deleteCalcSalary(requestDto);
        salaryDao.deleteNightEeamDay(requestDto);
        salaryDao.deletePaidLeave(requestDto);
        // 연장수당1(포괄제), 야간수당1(포괄제), 휴일수당1(포괄제) 기준 가져오기
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
        // 기본 책정임금 가져오기
        List<BasicSalaryDto> basicSalaryDtos = salaryDao.selectBasicSalary(requestDto);
        for (BasicSalaryDto basicSalaryDto : basicSalaryDtos) {
            // 기본 책정임금 변수 초기화
            basicAmount = BigDecimal.ZERO;          // 기본금
            hourlyPay = BigDecimal.ZERO;            // 시급(시간제)
            annualAllowance = BigDecimal.ZERO;      // 연차수당
            halfAllowance = BigDecimal.ZERO;      // 반차수당
            overtimeAllowance01 = BigDecimal.ZERO;     // 연장1 수당
            overtimeAllowance02 = BigDecimal.ZERO;     // 연장2 수당

//            nightAllowance01 = BigDecimal.ZERO;        // 야간1 시간
            nightAllowance01Day = BigDecimal.ZERO;        // 야간1 수당
            nightAllowance01Night = BigDecimal.ZERO;        // 야간1 수당
            nightAllowance02 = BigDecimal.ZERO;        // 야간2 수당

            holidayAllowance01 = BigDecimal.ZERO;      // 휴일1 수당
            holidayAllowanceSat = BigDecimal.ZERO;    // 휴일1 수당(토요일)
            holidayAllowanceSun = BigDecimal.ZERO;   // 휴일1 수당(일요일)

            holidayAllowance02 = BigDecimal.ZERO;      // 휴일2 수당
            transportationAmount = BigDecimal.ZERO; // 교통비
            mealsAmount = BigDecimal.ZERO;          // 식대
            otherAmount = BigDecimal.ZERO;          // 기타
            lateAmount = BigDecimal.ZERO;          // 지각
            earlyLeaverAmount = BigDecimal.ZERO;    // 조퇴
            OuterAmount = BigDecimal.ZERO;    // 외출
            observeHolidayAmount = BigDecimal.ZERO;    // 명정수당
            // 입사일, 퇴사일
            LocalDate hireDate = LocalDate.parse(basicSalaryDto.getHireDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate retireDate = LocalDate.parse(basicSalaryDto.getRetireDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            // 초기화 : 리포트 급여항목 항목 변수 초기화
            MonthlyKeunTaeDto monthlyKeunTaeDto = new MonthlyKeunTaeDto();
            rtAnnualLeaveUsed = 0.0;       // 연차 사용
            rtAnnualLeaveUsedDay = "";     // 연차 사용 일자
            rtHalfLeaverUsed = 0.0;        // 반차시간
            rtHalfLeaveUseDay = "";        // 반차일자
            rtHalfLeaveCnt = 0;
            rtOverTimeUsed01 = 0.0;            // 연장1 시간
            rtOverTimeDay2HCnt = 0;              //평일 연장2H
            rtOverTimeDay4H3MCnt = 0;           //철야익일 4.5H
//            rtOverDayTimeHours = 0.0;        // 연장1 주간시간
//            rtOverNightTimeHours = 0.0;      // 연장1 야간시간
            rtNS01DayCnt = 0;        // 야간1 일자

//            rtNightShiftUsed01 = 0.0;          // 야간1 일수
            rtNSDayTimeUsed = 0.0;        // 주간조 야간시간
            rtNSNightTimeUsed = 0.0;      // 야간조 야간시간
//            rtNightShift02 = 0.0;          // 야간2 일수
            nightTeamPlus = 0;

            rtHolidaySaturdayUsed = 0.0;     // 휴일1 (토요일)
            rtHolidaySaturdayDay4HCnt = 0; //토요 4H
            rtHolidaySaturdayDay8HCnt = 0; //토요 8H
            rtHolidaySundayUsed = 0.0;       // 휴일1 (일요일)
            rtHolidaySunday4HCnt = 0; //일요 4H
            rtHolidaySunday8HCnt = 0; //일요 8H
            rtNSHoliDayCnt = 0;

            rtTransportation = 0.0;        // 교통비
            rtMeal = 0.0;                  // 식대
            rtOther = 0;                   // 기타비용

            rtEarlyLeaveDay = "";           // 조퇴일자
            rtEarlyLeaveUsed = 0.0;         // 조퇴시간
            rtEarlyLeaveCnt = 0;            // 조퇴횟수
            rtLateDay = "";                 // 지각일자
            rtLateUsed = 0.0;               // 지각시간
            rtLatecnt = 0;                  // 지각횟수
            rtOuterDay = "";                // 외출일자
            rtOuterUsed = 0.0;              // 외출시간

            rtAbsence = 0;

            rtTotalTime = 209.0;

            monthlyKeunTaeDto.setCompanyId(basicSalaryDto.getCompanyId());
            monthlyKeunTaeDto.setEmployeeId(basicSalaryDto.getEmployeeId());
            monthlyKeunTaeDto.setYyyymm(basicSalaryDto.getYyyymm());
            monthlyKeunTaeDto.setLoginUserId(basicSalaryDto.getLoginUserId());
            // 무급 count 변수
            nonPayCnt = 0;
            nonPayDay = "";
            nonPayAmount = BigDecimal.ZERO;

            // 00. 해당월 입사/퇴직자 처리 댜상:연봉제, 시급제 && 별정직, 경비
            YearMonth yearMonth = YearMonth.parse(requestDto.getYyyymm(), DateTimeFormatter.ofPattern("yyyyMM"));
            int lastDay = yearMonth.lengthOfMonth();
            requestDto.getYyyymm();
            String midStatus = "000";

            // 해당 월(YYYYMM) 기준 중도 입사/퇴사 날짜계산
            long diff;
            // 중도 입사/퇴사 체크. 000:해당없음, 001:중도입사, 002:중도퇴사
            if (checkMidHire(requestDto.getYyyymm(), hireDate)) {
                if (!(hireDate.getDayOfMonth() == 1)) {
                    diff = ChronoUnit.DAYS.between(hireDate, yearMonth.atEndOfMonth()) + 1;
                } else {
                    diff = 0;
                }
                midStatus = "001";
            } else if (checkMidRetire(requestDto.getYyyymm(), retireDate)) {
                if (!(retireDate.getDayOfMonth() == lastDay)) {
                    diff = ChronoUnit.DAYS.between(yearMonth.atDay(1), retireDate) + 1;
                } else {
                    diff = 0;
                }
                midStatus = "002";
            } else {
                diff = 0;
            }
            // 각 항목 금액(기본급/연장수당2/야간수당2/휴일수당2) / 30일 * 근무일 로 각 항목 금액으로 표기
            if ((basicSalaryDto.getEmployeeType().equals("100")
                    || (basicSalaryDto.getEmployeeType().equals("200") && basicSalaryDto.getDutyType().equals("201")))
                    && (!midStatus.equals("000") && diff != 0L)) {
                BigDecimal totalAmount;
                BigDecimal basicSalary = basicSalaryDto.getBasicSalary() != null ? new BigDecimal(basicSalaryDto.getBasicSalary()) : BigDecimal.ZERO;
                BigDecimal overtimeAllowance = basicSalaryDto.getOvertimeAllowance02() != null ? new BigDecimal(basicSalaryDto.getOvertimeAllowance02()) : BigDecimal.ZERO;
                BigDecimal nightAllowance = basicSalaryDto.getNightAllowance02() != null ? new BigDecimal(basicSalaryDto.getNightAllowance02()) : BigDecimal.ZERO;
                BigDecimal holidayAllowance = basicSalaryDto.getHolidayAllowance02() != null ? new BigDecimal(basicSalaryDto.getHolidayAllowance02()) : BigDecimal.ZERO;
                totalAmount = (basicSalary.add(overtimeAllowance)
                        .add(nightAllowance)
                        .add(holidayAllowance))
                        .multiply(BigDecimal.valueOf(diff)
                                .divide(BigDecimal.valueOf(30), 10, RoundingMode.FLOOR))
                        .setScale(0, RoundingMode.CEILING);

                basicAmount = new BigDecimal(basicSalaryDto.getBasicSalary()).multiply(BigDecimal.valueOf(diff).divide(BigDecimal.valueOf(30), 10, RoundingMode.FLOOR)).setScale(0, RoundingMode.HALF_UP);
                overtimeAllowance02 = Optional.ofNullable(basicSalaryDto.getOvertimeAllowance02())
                        .map(amount -> new BigDecimal(amount)
                                .multiply(BigDecimal.valueOf(diff).divide(BigDecimal.valueOf(30), 10, RoundingMode.FLOOR)).setScale(0, RoundingMode.HALF_UP))
                        .orElse(BigDecimal.ZERO);
                nightAllowance02 = Optional.ofNullable(basicSalaryDto.getNightAllowance02())
                        .map(amount -> new BigDecimal(amount)
                                .multiply(BigDecimal.valueOf(diff).divide(BigDecimal.valueOf(30), 10, RoundingMode.FLOOR)).setScale(0, RoundingMode.HALF_UP))
                        .orElse(BigDecimal.ZERO);
                holidayAllowance02 = Optional.ofNullable(basicSalaryDto.getHolidayAllowance02())
                        .map(amount -> new BigDecimal(amount)
                                .multiply(BigDecimal.valueOf(diff).divide(BigDecimal.valueOf(30), 10, RoundingMode.FLOOR)).setScale(0, RoundingMode.HALF_UP))
                        .orElse(BigDecimal.ZERO);
                // 연봉제 퇴사자.입사자 급여 계산시 ERP 책정임금등록에 입력한 기본급 , 연장2, 야간2 금액에 /30 * 일한 일 수 로 계산하지만
                // 해당값이 ERP 책정임금등록 탭의 합계액/30 * 일한 일수 보다 작거나 크면(같이않다면) 책정임금등록의 /30 * 일한 일수(해당값은 소수점 첫쨰자리에서 올림으로 한다.) 로 금액을 맞춘다
                if ((totalAmount.compareTo(basicAmount.add(overtimeAllowance02).add(nightAllowance02).add(holidayAllowance02))) != 0) {
                    basicAmount = basicAmount.add(totalAmount.subtract(basicAmount.add(overtimeAllowance02).add(nightAllowance02).add(holidayAllowance02)));
                }
            }

            // 01. 연봉제
            if ((basicSalaryDto.getEmployeeType().equals("100")
                    && !Arrays.asList("120", "130", "140", "150").contains(basicSalaryDto.getPositionCode()) //사장, 부사장, 전무, 상무
                    && !Arrays.asList("100", "910").contains(basicSalaryDto.getPositionType()))  // 대표이사, 고문
                    || basicSalaryDto.getEmployeeType().equals("200") && basicSalaryDto.getDutyType().equals("201")) {  // 시급제, 별정직
                List<ADTDataDto> adtDataDtos = salaryDao.selectADTData(requestDto.getCompanyId(), requestDto.getYyyymm(), basicSalaryDto.getEmployeeId());
                workcnt = 0;
                paidHoliday = 0;
                for (ADTDataDto adtDataDto : adtDataDtos) {
                    // 주휴수당 count
                    if ((adtDataDto.getDateType().equals("1") && !adtDataDto.getInStatus().equals("결근"))
                            || (Arrays.asList("1", "2", "3", "4", "5").contains(adtDataDto.getDateWeek()) && adtDataDto.getWorkStatus().equals("공휴일_생산"))) {
                        paidHolidayindex++;
                        workcnt++;
                    } else {
                        paidHolidayindex = 0;
                    }
                    if (paidHolidayindex == 5) {
                        paidHoliday++;
                        paidHolidayindex = 0;
                    }
                    // 마지막근무일이면서 마지막주이면서 금요일이 아니면 다음달에 연속하여 주휴수당 count
                    if ((adtDataDto == adtDataDtos.get(adtDataDtos.size() - 1)) && salaryDao.selectLastWeek(adtDataDto.getWorkDate())
                            && !adtDataDto.getDateWeek().equals("5")) {
                        salaryDao.insertNonPayDay(paidHolidayindex, requestDto.getCompanyId(), requestDto.getYyyymm(), basicSalaryDto.getEmployeeId());
                    }
                    // 연장수당1
                    DayOfWeek dayOfWeek = LocalDate.parse(adtDataDto.getWorkDate(), DateTimeFormatter.ISO_LOCAL_DATE).getDayOfWeek();
                    if (!Arrays.asList("SATURDAY", "SUNDAY").contains(dayOfWeek.name())
                            && !Arrays.asList("결근", "휴일", "휴일출근").contains(adtDataDto.getInStatus())
                            && !adtDataDto.getOvertime().equals("00:00")) {
                        LocalDate workStartDate = LocalDate.parse(adtDataDto.getWorkStartDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        LocalDate workEndDate = LocalDate.parse(adtDataDto.getWorkEndDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//                        LocalTime workStartTime = LocalTime.parse(adtDataDto.getWorkStartDate(), DateTimeFormatter.ofPattern("HH:mm"));
                        LocalTime workEndTime = LocalTime.parse(adtDataDto.getWorkEndDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        LocalDateTime workStartDateTime = LocalDateTime.parse(adtDataDto.getWorkStartDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        LocalDateTime workEndDateTime = LocalDateTime.parse(adtDataDto.getWorkEndDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                        Period period = Period.between(workStartDate, workEndDate);
                        if (adtDataDto.getWorkStatus().equals("야간")) {
                            if (!workEndTime.isBefore(LocalTime.parse("08:30", DateTimeFormatter.ofPattern("HH:mm")))) {
//                                Duration duration = null;
//                                duration = Duration.between(workEndDateTime.with(LocalTime.of(8, 30)), workEndDateTime);
////                                rtOverTimeUsed01 = (duration.toHours() + (duration.toMinutes() % 60 >= 30 ? 0.5 : 0));
//                                rtOverTimeUsed01 = rtOverTimeUsed01 + (duration.toHours() + (duration.toMinutes() % 60 >= 30 ? 0.5 : 0));
                                overtimeAllowance01 = overtimeAllowance01.add(overtimeBaseAmount);
                                rtOverTimeDay4H3MCnt++;
                            }
                        } else {
                            if (!workEndTime.isBefore(LocalTime.parse("20:30", DateTimeFormatter.ofPattern("HH:mm"))) || period.getDays() >= 1) {
//                                Duration duration = null;
//                                duration = Duration.between(workEndDateTime.with(LocalTime.of(20, 30)), workEndDateTime);
////                                rtOverTimeUsed01 = (duration.toHours() + (duration.toMinutes() % 60 >= 30 ? 0.5 : 0));
//                                rtOverTimeUsed01 = rtOverTimeUsed01 + (duration.toHours() + (duration.toMinutes() % 60 >= 30 ? 0.5 : 0));
                                overtimeAllowance01 = overtimeAllowance01.add(overtimeBaseAmount);
                                rtOverTimeDay2HCnt++;
                            }
                        }
                    }

                    // 야간수당1
                    if (adtDataDto.getWorkStatus().equals("야간")) {
                        LocalDate workStartDate = LocalDate.parse(adtDataDto.getWorkStartDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        LocalDate workEndDate = LocalDate.parse(adtDataDto.getWorkEndDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        LocalDateTime workStartTime = LocalDateTime.parse(adtDataDto.getWorkStartDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        LocalDateTime workEndTime = LocalDateTime.parse(adtDataDto.getWorkEndDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        if (!workEndTime.toLocalTime().isBefore(LocalTime.of(5, 30))) {
                            nightAllowance01Night = nightAllowance01Night.add(nightBaseAmount);
                            rtNS01DayCnt++;
                        }
                    }
                    // 휴일수당1
                    if (Arrays.asList("휴일", "휴일출근").contains(adtDataDto.getInStatus())
                            && (adtDataDto.getWorkStartDate() != null && !adtDataDto.getWorkStartDate().equals("")) && (adtDataDto.getWorkEndDate() != null && !adtDataDto.getWorkEndDate().equals(""))
                            && !adtDataDto.getWorkStatus().equals("연차")) {
                        System.out.println(adtDataDto.getEmployeeNumber());
                        LocalDateTime workStartTime = LocalDateTime.parse(adtDataDto.getWorkStartDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        LocalDateTime workEndTime = LocalDateTime.parse(adtDataDto.getWorkEndDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        String dataWeek = adtDataDto.getDateWeek();

                        // 8시반 이전 출근 제외
                        if (workStartTime.toLocalTime().isBefore(LocalTime.of(8, 30))) {
                            workStartTime = workStartTime.with(LocalTime.of(8, 30));
                        }

                        // 10:30분 이전 출근
                        boolean startyn = (workStartTime.toLocalTime().isBefore(LocalTime.of(10, 30)) || workStartTime.toLocalTime().equals(LocalTime.of(10, 30)));
                        // 17:30분 이후 퇴근
                        boolean endyn = (workEndTime.toLocalTime().isAfter(LocalTime.of(17, 30)) || workEndTime.toLocalTime().equals(LocalTime.of(17, 30)));
                        // 출근시간과 퇴근시간 사이에 점심시간이 포함되는지 체크
                        Duration duration = Duration.between(workStartTime, workEndTime);
                        // 점심시간 제외
                        if (!workStartTime.toLocalTime().isAfter(LocalTime.of(12, 30)) && !workEndTime.toLocalTime().isBefore(LocalTime.of(13, 30))) {
                            duration = duration.minus(Duration.ofHours(1));
                        }
                        // 저녁시간 제외
                        if (!workStartTime.toLocalTime().isBefore(LocalTime.of(17, 30)) && !workEndTime.toLocalTime().isBefore(LocalTime.of(18, 00))) {
                            duration = duration.minus(Duration.ofMinutes(30));
                        }

                        if (Arrays.asList("추석", "설날").contains(adtDataDto.getDescription())) {
                            if (duration.toHours() >= 8) {
                                observeHolidayAmount = observeHolidayAmount.add(BigDecimal.valueOf(150000));
                                holidayAllowanceSun = holidayAllowanceSun.add(BigDecimal.valueOf(150000));
                                rtHolidaySunday8HCnt++;
                            } else if (duration.toHours() >= 4) {
                                observeHolidayAmount = observeHolidayAmount.add(BigDecimal.valueOf(75000));
                                holidayAllowanceSun = holidayAllowanceSun.add(BigDecimal.valueOf(75000));
                                rtHolidaySunday4HCnt++;
                            }
                        } else {
                            if (startyn && endyn && duration.toHours() >= 8) {
                                holidayAllowance01 = holidayAllowance01.add(holidayBaseAmount.multiply(BigDecimal.valueOf(2)));
                                if (dataWeek.equals("6")) {
                                    rtHolidaySaturdayUsed = rtHolidaySaturdayUsed + 8.0;
                                    holidayAllowanceSat = holidayAllowanceSat.add(holidayBaseAmount.multiply(BigDecimal.valueOf(2)));
                                    rtHolidaySaturdayDay8HCnt++;
                                } else if (dataWeek.equals("7") || (adtDataDto.getWorkStatus().contains("공휴일") && !Arrays.asList("추석", "설날").contains(adtDataDto.getDescription()))) {
                                    rtHolidaySundayUsed = rtHolidaySundayUsed + 8.0;
                                    holidayAllowanceSun = holidayAllowanceSun.add(holidayBaseAmount.multiply(BigDecimal.valueOf(2)));
                                    rtHolidaySunday8HCnt++;
                                }
                            } else if ((startyn || endyn) && duration.toHours() >= 4) {
                                holidayAllowance01 = holidayAllowance01.add(holidayBaseAmount);
                                if (dataWeek.equals("6")) {
                                    rtHolidaySaturdayUsed = rtHolidaySaturdayUsed + 4.0;
                                    holidayAllowanceSat = holidayAllowanceSat.add(holidayBaseAmount);
                                    rtHolidaySaturdayDay4HCnt++;
                                } else if (dataWeek.equals("7") || (adtDataDto.getWorkStatus().contains("공휴일") && !Arrays.asList("추석", "설날").contains(adtDataDto.getDescription()))) {
                                    rtHolidaySundayUsed = rtHolidaySundayUsed + 4.0;
                                    holidayAllowanceSun = holidayAllowanceSun.add(holidayBaseAmount);
                                    rtHolidaySunday4HCnt++;
                                }
                            }
                        }
                    }
                    // 무급처리 count
                    if (adtDataDto.getWorkStatus().equals("무급")) {
                        nonPayCnt++;

                        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                        DateFormat outputFormat = new SimpleDateFormat("M/d");
                        DateFormat outputFormat2 = new SimpleDateFormat("d");
                        String dayOfMonth = "";
                        try {
                            dayOfMonth = outputFormat2.format(inputFormat.parse(adtDataDto.getWorkDate()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (nonPayDay.isEmpty()) {
                            nonPayDay = adtDataDto.getWorkDate();
                            try {
                                // 입력 문자열을 Date 객체로 파싱
                                Date date = inputFormat.parse(nonPayDay);
                                nonPayDay = outputFormat.format(date);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else {
                            String dayOfMonthAsString = String.valueOf(dayOfMonth);
                            StringBuilder nonPayDayBuilder = new StringBuilder(nonPayDay);
                            nonPayDay = nonPayDayBuilder.append(",").append(dayOfMonthAsString).toString();
                        }
                    }

                    // 연차/반차 count
                    // 연차수당 - 연차
                    if (adtDataDto.getWorkStatus().equals("연차")) {
                        rtAnnualLeaveUsed++;
                        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                        DateFormat outputFormat = new SimpleDateFormat("M/d");
                        DateFormat outputFormat2 = new SimpleDateFormat("d");
                        String dayOfMonth = "";
                        try {
                            dayOfMonth = outputFormat2.format(inputFormat.parse(adtDataDto.getWorkDate()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (rtAnnualLeaveUsedDay.isEmpty()) {
                            rtAnnualLeaveUsedDay = adtDataDto.getWorkDate();
                            try {
                                // 입력 문자열을 Date 객체로 파싱
                                Date date = inputFormat.parse(rtAnnualLeaveUsedDay);
                                rtAnnualLeaveUsedDay = outputFormat.format(date);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else {
                            rtAnnualLeaveUsedDay = rtAnnualLeaveUsedDay + "," + dayOfMonth;
                        }
                    }
//                    if (Arrays.asList("오전반차", "오후반차", "오전반차_정상", "오후반차_정상").contains(adtDataDto.getWorkStatus())
                    if (adtDataDto.getWorkStatus().contains("반차")) {
                        rtHalfLeaveCnt++;

                        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                        DateFormat outputFormat = new SimpleDateFormat("M/d");
                        DateFormat outputFormat2 = new SimpleDateFormat("d");
                        String dayOfMonth = "";
                        try {
                            dayOfMonth = outputFormat2.format(inputFormat.parse(adtDataDto.getWorkDate()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (rtHalfLeaveUseDay.isEmpty()) {
                            rtHalfLeaveUseDay = adtDataDto.getWorkDate();
                            try {
                                // 입력 문자열을 Date 객체로 파싱
                                Date date = inputFormat.parse(rtHalfLeaveUseDay);
                                rtHalfLeaveUseDay = outputFormat.format(date);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else {
                            rtHalfLeaveUseDay = rtHalfLeaveUseDay + "," + dayOfMonth;
                        }

                    }
                    // 결근처리 count
                    if (adtDataDto.getInStatus().equals("결근")) {
                        rtAbsence++;
                    }
                    // 기타수당 - 지각
                    if (adtDataDto.getInStatus().equals("지각")) {
                        Duration duration = null;
                        LocalDate workStartDate = null;
                        LocalDate workEndDate = null;
                        LocalDateTime workStartDateTime = LocalDateTime.parse(adtDataDto.getWorkStartDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        LocalDateTime workEndDateTime = LocalDateTime.parse(adtDataDto.getWorkEndDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                        if (adtDataDto.getWorkStatus().equals("야간")) {
                            duration = Duration.between(workStartDateTime.with(LocalTime.of(22, 00)), workStartDateTime);
                        } else {
                            duration = Duration.between(workStartDateTime.with(LocalTime.of(8, 30)), workStartDateTime);
                        }
                        double minutes = duration.toMinutes() % 60;
                        rtLateUsed = rtLateUsed + duration.toHours() + ((minutes >= 30) ? 1.0 : (minutes <= 0) ? 0.0 : 0.5);
                        rtLatecnt++;

                        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                        DateFormat outputFormat = new SimpleDateFormat("M/d");
                        DateFormat outputFormat2 = new SimpleDateFormat("d");
                        String dayOfMonth = "";
                        try {
                            dayOfMonth = outputFormat2.format(inputFormat.parse(adtDataDto.getWorkDate()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (rtLateDay.isEmpty()) {
                            rtLateDay = adtDataDto.getWorkDate();
                            try {
                                // 입력 문자열을 Date 객체로 파싱
                                Date date = inputFormat.parse(rtLateDay);
                                rtLateDay = outputFormat.format(date);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else {
                            rtLateDay = rtLateDay + "," + dayOfMonth;
                        }
                    }

                }
                // 무급처리
                // 책정임금등록의 (기본급/연장수당2/야간수당2/휴일수당2) / 30일 * 무급휴가일 (소숫점 첫째자리 ROUNDUP)
                if (!(nonPayCnt == 0)) {
                    nonPayAmount = calcNonPay(nonPayCnt, basicSalaryDto.getBasicSalary(), basicSalaryDto.getOvertimeAllowance02(), basicSalaryDto.getNightAllowance02(), basicSalaryDto.getHolidayAllowance02());
                }
                // 연봉제 중도/입사 퇴사자 총시간 조정
                if (!midStatus.equals("000") && diff != 0L) {
                    rtTotalTime = Math.floor(209.0 / 30.0 * diff);
                }
                // 01-01. 임원
            } else if (basicSalaryDto.getEmployeeType().equals("100")
                    && (Arrays.asList("120", "130", "140", "150").contains(basicSalaryDto.getPositionCode()) //사장, 부사장, 전무, 상무
                    || Arrays.asList("100", "910").contains(basicSalaryDto.getPositionType()))) { // 대표이사, 고문)
                List<ADTDataDto> adtDataDtos = salaryDao.selectADTData(requestDto.getCompanyId(), requestDto.getYyyymm(), basicSalaryDto.getEmployeeId());
                for (ADTDataDto adtDataDto : adtDataDtos) {
                    // 연차/반차 count
                    // 연차수당 - 연차
                    if (adtDataDto.getWorkStatus().equals("연차")) {
                        rtAnnualLeaveUsed++;
                        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                        DateFormat outputFormat = new SimpleDateFormat("M/d");
                        DateFormat outputFormat2 = new SimpleDateFormat("d");
                        String dayOfMonth = "";
                        try {
                            dayOfMonth = outputFormat2.format(inputFormat.parse(adtDataDto.getWorkDate()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (rtAnnualLeaveUsedDay.isEmpty()) {
                            rtAnnualLeaveUsedDay = adtDataDto.getWorkDate();
                            try {
                                // 입력 문자열을 Date 객체로 파싱
                                Date date = inputFormat.parse(rtAnnualLeaveUsedDay);
                                rtAnnualLeaveUsedDay = outputFormat.format(date);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else {
                            rtAnnualLeaveUsedDay = rtAnnualLeaveUsedDay + "," + dayOfMonth;
                        }
                    }
                    if (Arrays.asList("오전반차", "오후반차", "오전반차_정상", "오후반차_정상").contains(adtDataDto.getWorkStatus())) {
                        rtHalfLeaveCnt++;

                        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                        DateFormat outputFormat = new SimpleDateFormat("M/d");
                        DateFormat outputFormat2 = new SimpleDateFormat("d");
                        String dayOfMonth = "";
                        try {
                            dayOfMonth = outputFormat2.format(inputFormat.parse(adtDataDto.getWorkDate()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (rtHalfLeaveUseDay.isEmpty()) {
                            rtHalfLeaveUseDay = adtDataDto.getWorkDate();
                            try {
                                // 입력 문자열을 Date 객체로 파싱
                                Date date = inputFormat.parse(rtHalfLeaveUseDay);
                                rtHalfLeaveUseDay = outputFormat.format(date);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else {
                            rtHalfLeaveUseDay = rtHalfLeaveUseDay + "," + dayOfMonth;
                        }
                    }
                }
                // 02. 시급제
            } else if (basicSalaryDto.getEmployeeType().equals("200") && !basicSalaryDto.getDutyType().equals("201") && !(basicSalaryDto.getHourlyPay() == null)) {

                // 총휴일근무 시간
                Double holidayMinite = 0.0;
                // 야간조 근무일수
                Integer nightCnt = 0;
                hourlyPay = new BigDecimal(basicSalaryDto.getHourlyPay());
                // 주휴수당 count 변수
                paidHolidayindex = 0;
                paidHoliday = 0;
                Integer paidLeaveDay = 0;
                // 야간조 count 변수
                Integer nightTeamDay = 0;
                nightTeamPlus = 0;
                // 평일 실근무일수
                workcnt = 0;
                // 연차/반차 사용 count, 시간 변수
//                Integer annualLeave = 0;
//                Integer halfDayLeave = 0;
//                double halfDayLeaveTime = 0;
                // 기타수당. 지각, 조퇴, 외출
//                double lateTime = 0;
//                double earlyLeaveTime = 0;
//                double outingTime = 0;

                String yyyymm = LocalDate.parse(requestDto.getYyyymm() + "01", DateTimeFormatter.ofPattern("yyyyMMdd")).minusMonths(1).format(DateTimeFormatter.ofPattern("yyyyMM"));
                // 주휴수당 지난달 index값 있으면 해당값으로 초기화
                paidLeaveDay = salaryDao.selectpaidHolidayindex(info.getCompanyId(), yyyymm, basicSalaryDto.getEmployeeNumber());
                // 야간수당1 - 야간도(연장) 지난달 nightTeamDay 값이 있으면 가져와서 count
                nightTeamDay = salaryDao.selectNightdayindex(info.getCompanyId(), yyyymm, basicSalaryDto.getEmployeeNumber());

                paidHolidayindex = Optional.ofNullable(paidLeaveDay).orElse(paidHolidayindex);

                List<ADTDataDto> adtDataDtos = salaryDao.selectADTData(requestDto.getCompanyId(), requestDto.getYyyymm(), basicSalaryDto.getEmployeeId());
                for (ADTDataDto adtDataDto : adtDataDtos) {
                    LocalDate workStartDate = null;
                    LocalDate workEndDate = null;
                    LocalDateTime workStartDateTime = null;
                    LocalDateTime workEndDateTime = null;

                    if (!(adtDataDto.getWorkStartDate() == null) && !(adtDataDto.getWorkStartDate().equals(""))
                            && !(adtDataDto.getWorkEndDate() == null) && !(adtDataDto.getWorkEndDate().equals(""))) {

                        workStartDate = LocalDate.parse(adtDataDto.getWorkStartDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        workEndDate = LocalDate.parse(adtDataDto.getWorkEndDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        workStartDateTime = LocalDateTime.parse(adtDataDto.getWorkStartDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        workEndDateTime = LocalDateTime.parse(adtDataDto.getWorkEndDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    }

                    // 주휴수당 count
                    if ((adtDataDto.getDateType().equals("1") && !adtDataDto.getInStatus().equals("결근"))
                            || (Arrays.asList("1", "2", "3", "4", "5").contains(adtDataDto.getDateWeek()) && adtDataDto.getWorkStatus().equals("공휴일_생산"))) {
                        paidHolidayindex++;
                        workcnt++;
                    } else {
                        paidHolidayindex = 0;
                    }
                    if (paidHolidayindex == 5) {
                        paidHoliday++;
                        paidHolidayindex = 0;
                    }
                    // 마지막근무일이면서 마지막주이면서 금요일이 아니면 다음달에 연속하여 주휴수당 count
                    if ((adtDataDto == adtDataDtos.get(adtDataDtos.size() - 1)) && salaryDao.selectLastWeek(adtDataDto.getWorkDate())
                            && !adtDataDto.getDateWeek().equals("5")) {
                        salaryDao.insertNonPayDay(paidHolidayindex, requestDto.getCompanyId(), requestDto.getYyyymm(), basicSalaryDto.getEmployeeId());
                    }

                    // 연차/반차 count
                    // 연차수당 - 연차
                    if (adtDataDto.getWorkStatus().equals("연차")) {
                        rtAnnualLeaveUsed++;
                        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                        DateFormat outputFormat = new SimpleDateFormat("M/d");
                        DateFormat outputFormat2 = new SimpleDateFormat("d");
                        String dayOfMonth = "";
                        try {
                            dayOfMonth = outputFormat2.format(inputFormat.parse(adtDataDto.getWorkDate()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (rtAnnualLeaveUsedDay.isEmpty()) {
                            rtAnnualLeaveUsedDay = adtDataDto.getWorkDate();
                            try {
                                // 입력 문자열을 Date 객체로 파싱
                                Date date = inputFormat.parse(rtAnnualLeaveUsedDay);
                                rtAnnualLeaveUsedDay = outputFormat.format(date);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else {
                            rtAnnualLeaveUsedDay = rtAnnualLeaveUsedDay + "," + dayOfMonth;
                        }
                    }
                    // 연차수당 - 반차
                    else if (adtDataDto.getWorkStatus().contains("오전반차")) {
                        if (!workStartDateTime.isBefore(workEndDateTime.with(LocalTime.of(12, 00)))) {
                            rtHalfLeaverUsed = rtHalfLeaverUsed + 4.0;
                        } else {
                            Duration duration = Duration.between(workStartDateTime.with(LocalTime.of(8, 30)), workStartDateTime);
                            double minutes = duration.toMinutes() % 60;
                            rtHalfLeaverUsed = rtHalfLeaverUsed + duration.toHours() + ((minutes >= 30) ? 1.0 : (minutes <= 0) ? 0.0 : 0.5);
                        }
                        rtHalfLeaveCnt++;

                        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                        DateFormat outputFormat = new SimpleDateFormat("M/d");
                        DateFormat outputFormat2 = new SimpleDateFormat("d");
                        String dayOfMonth = "";
                        try {
                            dayOfMonth = outputFormat2.format(inputFormat.parse(adtDataDto.getWorkDate()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (rtHalfLeaveUseDay.isEmpty()) {
                            rtHalfLeaveUseDay = adtDataDto.getWorkDate();
                            try {
                                // 입력 문자열을 Date 객체로 파싱
                                Date date = inputFormat.parse(rtHalfLeaveUseDay);
                                rtHalfLeaveUseDay = outputFormat.format(date);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else {
                            rtHalfLeaveUseDay = rtHalfLeaveUseDay + "," + dayOfMonth;
                        }
                    } else if (adtDataDto.getWorkStatus().contains("오후반차")) {
                        if (!workEndDateTime.isAfter(workEndDateTime.with(LocalTime.of(13, 30)))) {
                            rtHalfLeaverUsed = rtHalfLeaverUsed + 4.0;
                        } else {
                            Duration duration = Duration.between(workEndDateTime, workEndDateTime.with(LocalTime.of(17, 30)));
                            double minutes = duration.toMinutes() % 60;
                            rtHalfLeaverUsed = rtHalfLeaverUsed + duration.toHours() + ((minutes >= 30) ? 1.0 : (minutes <= 0) ? 0.0 : 0.5);
                        }
                        rtHalfLeaveCnt++;

                        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                        DateFormat outputFormat = new SimpleDateFormat("M/d");
                        DateFormat outputFormat2 = new SimpleDateFormat("d");
                        String dayOfMonth = "";
                        try {
                            dayOfMonth = outputFormat2.format(inputFormat.parse(adtDataDto.getWorkDate()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (rtHalfLeaveUseDay.isEmpty()) {
                            rtHalfLeaveUseDay = adtDataDto.getWorkDate();
                            try {
                                // 입력 문자열을 Date 객체로 파싱
                                Date date = inputFormat.parse(rtHalfLeaveUseDay);
                                rtHalfLeaveUseDay = outputFormat.format(date);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else {
                            rtHalfLeaveUseDay = rtHalfLeaveUseDay + "," + dayOfMonth;
                        }
                    }
                    // 연장수당1
                    if (!adtDataDto.getOvertime().equals("00:00") && (adtDataDto.getOutStatus().equals("연장근무") || adtDataDto.getOutStatus().equals("연장/야간근무"))) {
                        Duration duration = null;
                        if (adtDataDto.getWorkStatus().equals("야간")) {
                            if (adtDataDto.getOutStatus().equals("연장근무")) {
                                duration = Duration.between(workEndDateTime.with(LocalTime.of(06, 00)), workEndDateTime).toMinutes() >= 60 ? Duration.between(workEndDateTime.with(LocalTime.of(06, 00)), workEndDateTime) : Duration.ofMinutes(0);
                            }
                        } else {
                            if (adtDataDto.getOutStatus().equals("연장근무")) {
                                duration = Duration.between(workEndDateTime.with(LocalTime.of(18, 00)), workEndDateTime).toMinutes() >= 60 ? Duration.between(workEndDateTime.with(LocalTime.of(18, 00)), workEndDateTime) : Duration.ofMinutes(0);
                                ;
                            } else if (adtDataDto.getOutStatus().equals("연장/야간근무") && workStartDate.equals(workEndDate)) {
                                duration = Duration.ofHours(4);
                            } else if (adtDataDto.getOutStatus().equals("연장/야간근무") && !workStartDate.equals(workEndDate)) {
                                duration = Duration.ofHours(4);
                                if (!workEndDateTime.isBefore(workEndDateTime.with(LocalTime.of(06, 00)))) {
                                    duration = duration.plus(Duration.between(workEndDateTime.with(LocalTime.of(06, 00)), workEndDateTime).toMinutes() >= 60 ? Duration.between(workEndDateTime.with(LocalTime.of(06, 00)), workEndDateTime) : Duration.ofMinutes(0));
                                }
                            }
                        }
                        if (duration != null) {
                            rtOverTimeUsed01 = rtOverTimeUsed01 + (duration.toHours() + (duration.toMinutes() % 60 >= 30 ? 0.5 : 0));
                            overtimeAllowance01 = BigDecimal.valueOf(rtOverTimeUsed01).multiply(BigDecimal.valueOf(1.5)).multiply(hourlyPay);
                        }
                    }
                    // 야간수당1 - 주간조
                    if (adtDataDto.getOutStatus().equals("연장/야간근무")) {
                        Duration duration = Duration.between(workStartDateTime.with(LocalTime.of(22, 00)), workEndDateTime);
                        // 휴게시간 빼기 ( 00:30 ~ 01:30 )
                        if (!workStartDateTime.toLocalDate().isEqual(workEndDateTime.toLocalDate())) {
                            if (workEndDateTime.isAfter(workEndDateTime.with(LocalTime.of(1, 30)))) {
                                duration = duration.minus(Duration.ofHours(1));
                            }
                        }
                        if (!workEndDateTime.isBefore(workEndDateTime.with(LocalTime.of(05, 30))) && !workStartDate.equals(workEndDate)) {
                            duration = Duration.ofMinutes(390);
                        }
//                        rtNightShift01 = rtNightShift01 + (duration.toHours() + (duration.toMinutes() % 60 >= 30 ? 0.5 : 0));
                        nightAllowance01Day = nightAllowance01Day.add(BigDecimal.valueOf(duration.toHours() + (duration.toMinutes() % 60 >= 30 ? 0.5 : 0)).multiply(hourlyPay).multiply(BigDecimal.valueOf(2)));
                        rtNSDayTimeUsed = rtNSDayTimeUsed + (duration.toHours() + (duration.toMinutes() % 60 >= 30 ? 0.5 : 0));
                        if (adtDataDto.getDateType().equals("2")) {
                            rtNSHoliDayCnt++;
                        }
                    }
                    // 야간수당1 - 야간조(연장)
                    if (adtDataDto.getWorkStatus().equals("야간")) {
                        nightAllowance01Night = nightAllowance01Night.add(BigDecimal.valueOf(6.5).multiply(hourlyPay).multiply(BigDecimal.valueOf(0.5)));
                        rtNSNightTimeUsed = rtNSNightTimeUsed + 6.5;
                    }
                    // 야간수당1 - 야간조(연장) count
                    if (adtDataDto.getWorkStatus().equals("야간")) {
                        nightTeamDay++;
                        nightCnt++;
                    } else {
                        nightTeamDay = 0;
                    }
                    // 야간수당1 - 야간조(연장)
                    //  (1) 마지막날이 마지막주이면서 금요일이면 다음달로 count이관하여 다음달급여에서 count이어간다.
                    //      ex. 2023.06.26~30일 근무하였지만 휴일수당은 다음달에 줘야한다.
                    if ((adtDataDto == adtDataDtos.get(adtDataDtos.size() - 1)) && salaryDao.selectLastWeek(adtDataDto.getWorkDate()) && adtDataDto.getDateWeek().equals("5") && nightTeamDay == 5) {
                        salaryDao.insertnightTeamDay(nightTeamDay, requestDto.getCompanyId(), requestDto.getYyyymm(), basicSalaryDto.getEmployeeId());
                        nightTeamPlus--;
                    }
                    //  (2) count 중 다음달로 넘어가면 다음달에 count를 이어간다
                    if ((adtDataDto == adtDataDtos.get(adtDataDtos.size() - 1)) && salaryDao.selectLastWeek(adtDataDto.getWorkDate()) && !adtDataDto.getDateWeek().equals("5")) {
                        salaryDao.insertnightTeamDay(nightTeamDay, requestDto.getCompanyId(), requestDto.getYyyymm(), basicSalaryDto.getEmployeeId());
                    }
                    // 야간수당1 - 야간조(연장) 초기화
                    if (nightTeamDay == 5) {
                        nightTeamPlus++;
                        nightTeamDay = 0;
                    }

                    // 휴일수당1 : 일반
                    if (!adtDataDto.getHolidayTime().equals("00:00")) {
                        LocalTime localTime = LocalTime.parse(adtDataDto.getHolidayTime(), DateTimeFormatter.ofPattern("HH:mm"));
                        if (adtDataDto.getDateWeek().equals("6")) {
                            rtHolidaySaturdayUsed = rtHolidaySaturdayUsed + (Duration.between(LocalTime.MIN, localTime).toHours() + (Duration.between(LocalTime.MIN, localTime).toMinutes() % 60 >= 30 ? 0.5 : 0));
                            if (Duration.between(LocalTime.MIN, localTime).toHours() >= 8) {
                                rtHolidaySaturdayDay8HCnt++;
                            } else if (Duration.between(LocalTime.MIN, localTime).toHours() >= 4) {
                                rtHolidaySaturdayDay4HCnt++;
                            }
                        } else if (adtDataDto.getDateWeek().equals("7") || adtDataDto.getWorkStatus().contains("공휴일")) {
                            rtHolidaySundayUsed = rtHolidaySundayUsed + (Duration.between(LocalTime.MIN, localTime).toHours() + (Duration.between(LocalTime.MIN, localTime).toMinutes() % 60 >= 30 ? 0.5 : 0));
                            if (Duration.between(LocalTime.MIN, localTime).toHours() >= 8) {
                                rtHolidaySunday8HCnt++;
                            } else if (Duration.between(LocalTime.MIN, localTime).toHours() >= 4) {
                                rtHolidaySunday4HCnt++;
                            }
                        }
                        holidayMinite = holidayMinite + (Duration.between(LocalTime.MIN, localTime).toHours() + (Duration.between(LocalTime.MIN, localTime).toMinutes() % 60 >= 30 ? 0.5 : 0));
                    }

                    /*
                    if (Arrays.asList("휴일", "휴일출근").contains(adtDataDto.getInStatus())
                            && (adtDataDto.getWorkStartDate() != null && !adtDataDto.getWorkStartDate().equals("")) && (adtDataDto.getWorkEndDate() != null && !adtDataDto.getWorkEndDate().equals(""))
                            && !adtDataDto.getWorkStatus().equals("연차")) {
                        LocalDateTime workStartTime = LocalDateTime.parse(adtDataDto.getWorkStartDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        LocalDateTime workEndTime = LocalDateTime.parse(adtDataDto.getWorkEndDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        String dataWeek = adtDataDto.getDateWeek();
                        // 10:30분 이전 출근
                        boolean startyn = (workStartTime.toLocalTime().isBefore(LocalTime.of(10, 30)) || workStartTime.toLocalTime().equals(LocalTime.of(10, 30)));
                        // 17:30분 이후 퇴근
                        boolean endyn = (workEndTime.toLocalTime().isAfter(LocalTime.of(17, 30)) || workEndTime.toLocalTime().equals(LocalTime.of(17, 30)));
                        // 출근시간과 퇴근시간 사이에 점심시간이 포함되는지 체크
                        Duration duration = Duration.between(workStartTime, workEndTime);
                        // 점심시간 제외
                        if (!workStartTime.toLocalTime().isAfter(LocalTime.of(12, 30)) && !workEndTime.toLocalTime().isBefore(LocalTime.of(13, 30))) {
                            duration = duration.minus(Duration.ofHours(1));
                        }
                        // 저녁시간 제외
                        if (!workStartTime.toLocalTime().isBefore(LocalTime.of(13, 30)) && !workEndTime.toLocalTime().isBefore(LocalTime.of(15, 30))) {
                            duration = duration.minus(Duration.ofMinutes(30));
                        }

                        if (startyn && endyn && duration.toHours() >= 8) {
                            holidayAllowance01 = holidayAllowance01.add(holidayBaseAmount.multiply(BigDecimal.valueOf(2)));
                            if (dataWeek.equals("6")) {
                                rtHolidaySaturdayUsed = rtHolidaySaturdayUsed + (duration.toHours() + (duration.toMinutes() % 60 >= 30 ? 0.5 : 0));
                                rtHolidaySaturdayDay8HCnt++;
                            } else if (dataWeek.equals("7")) {
                                rtHolidaySundayUsed = rtHolidaySundayUsed + (duration.toHours() + (duration.toMinutes() % 60 >= 30 ? 0.5 : 0));
                                rtHolidaySunday8HCnt++;
                            }
                        } else if ((startyn || endyn) && duration.toHours() >= 4) {
                            holidayAllowance01 = holidayAllowance01.add(holidayBaseAmount);
                            if (dataWeek.equals("6")) {
                                rtHolidaySaturdayUsed = rtHolidaySaturdayUsed + (duration.toHours() + (duration.toMinutes() % 60 >= 30 ? 0.5 : 0));
                                rtHolidaySaturdayDay4HCnt++;
                            } else if (dataWeek.equals("7")) {
                                rtHolidaySundayUsed = rtHolidaySundayUsed + (duration.toHours() + (duration.toMinutes() % 60 >= 30 ? 0.5 : 0));
                                rtHolidaySunday4HCnt++;
                            }
                        }
                    }
                    */

                    // 기타수당 - 지각
                    if (adtDataDto.getInStatus().equals("지각")) {
                        Duration duration = null;
                        if (adtDataDto.getWorkStatus().equals("야간")) {
                            duration = Duration.between(workStartDateTime.with(LocalTime.of(22, 00)), workStartDateTime);
                        } else {
                            if (adtDataDto.getWorkStatus().contains("오전반차")) {
                                duration = Duration.between(workStartDateTime.with(LocalTime.of(13, 30)), workStartDateTime);
                            } else {
                                duration = Duration.between(workStartDateTime.with(LocalTime.of(8, 30)), workStartDateTime);
                            }
                        }
                        double minutes = duration.toMinutes() % 60;
                        rtLateUsed = rtLateUsed + duration.toHours() + ((minutes >= 30) ? 1.0 : (minutes <= 0) ? 0.0 : 0.5);
                        rtLatecnt++;
                        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                        DateFormat outputFormat = new SimpleDateFormat("M/d");
                        DateFormat outputFormat2 = new SimpleDateFormat("d");
                        String dayOfMonth = "";
                        try {
                            dayOfMonth = outputFormat2.format(inputFormat.parse(adtDataDto.getWorkDate()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (rtLateDay.isEmpty()) {
                            rtLateDay = adtDataDto.getWorkDate();
                            try {
                                // 입력 문자열을 Date 객체로 파싱
                                Date date = inputFormat.parse(rtLateDay);
                                rtLateDay = outputFormat.format(date);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else {
                            rtLateDay = rtLateDay + "," + dayOfMonth;
                        }
                    }
                    // 기타수당 - 조퇴
                    if (adtDataDto.getOutStatus().equals("조퇴")) {
                        Duration duration = null;
                        if (adtDataDto.getWorkStatus().contains("오후반차")) {
                            duration = Duration.between(workEndDateTime, workEndDateTime.with(LocalTime.of(12, 30)));
                        } else {
                            duration = Duration.between(workEndDateTime, workEndDateTime.with(LocalTime.of(17, 30)));
                        }
                        double minutes = duration.toMinutes() % 60;
                        rtEarlyLeaveUsed = rtEarlyLeaveUsed + duration.toHours() + ((minutes > 30) ? 1.0 : (minutes <= 0) ? 0.0 : 0.5);
                        rtEarlyLeaveCnt++;
                        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                        DateFormat outputFormat = new SimpleDateFormat("M/d");
                        DateFormat outputFormat2 = new SimpleDateFormat("d");
                        String dayOfMonth = "";
                        try {
                            dayOfMonth = outputFormat2.format(inputFormat.parse(adtDataDto.getWorkDate()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (rtEarlyLeaveDay.isEmpty()) {
                            rtEarlyLeaveDay = adtDataDto.getWorkDate();
                            try {
                                // 입력 문자열을 Date 객체로 파싱
                                Date date = inputFormat.parse(rtEarlyLeaveDay);
                                rtEarlyLeaveDay = outputFormat.format(date);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else {
                            rtEarlyLeaveDay = rtEarlyLeaveDay + "," + dayOfMonth;
                        }
                    }
                    // 기타수당 - 외출
                    if (adtDataDto.getOutTime() != null && !adtDataDto.getOutTime().equals("")) {
                        Double addOutTime = 0.0;
                        if (adtDataDto.getWorkStatus().equals("야간")) {
                        } else {
                            // 점심시간 제외 12:30~13:30
                            // 점심시간 앞뒤 30분 제외.
                            // 12:00 ~13:30 급여차감x
                            // 12:30 ~ 14:00 급여차감x
                            addOutTime = getOutTime(adtDataDto.getOutStartDate(), adtDataDto.getOutEndDate());
                        }
                        rtOuterUsed = rtOuterUsed + addOutTime;
                        if (rtOuterUsed > 0) {
                            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                            DateFormat outputFormat = new SimpleDateFormat("M/d");
                            DateFormat outputFormat2 = new SimpleDateFormat("d");
                            String dayOfMonth = "";
                            try {
                                dayOfMonth = outputFormat2.format(inputFormat.parse(adtDataDto.getWorkDate()));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (rtOuterDay.isEmpty()) {
                                rtOuterDay = adtDataDto.getWorkDate();
                                try {
                                    // 입력 문자열을 Date 객체로 파싱
                                    Date date = inputFormat.parse(rtOuterDay);
                                    rtOuterDay = outputFormat.format(date);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                rtOuterDay = rtOuterDay + "," + dayOfMonth;
                            }
                        }
                    }
                    // 결근처리 count
                    if (adtDataDto.getInStatus().equals("결근")) {
                        rtAbsence++;
                    }

                    // 무급처리 count
                    if (adtDataDto.getWorkStatus().equals("무급")) {
                        nonPayCnt++;

                        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                        DateFormat outputFormat = new SimpleDateFormat("M/d");
                        DateFormat outputFormat2 = new SimpleDateFormat("d");
                        String dayOfMonth = "";
                        try {
                            dayOfMonth = outputFormat2.format(inputFormat.parse(adtDataDto.getWorkDate()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (nonPayDay.isEmpty()) {
                            nonPayDay = adtDataDto.getWorkDate();
                            try {
                                // 입력 문자열을 Date 객체로 파싱
                                Date date = inputFormat.parse(nonPayDay);
                                nonPayDay = outputFormat.format(date);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else {
                            String dayOfMonthAsString = String.valueOf(dayOfMonth);
                            StringBuilder nonPayDayBuilder = new StringBuilder(nonPayDay);
                            nonPayDay = nonPayDayBuilder.append(",").append(dayOfMonthAsString).toString();
                        }
                    }
                }
                // 기본급 퇴직여부
                // Y "000": 계약서 상의 시급 × 209H
                // N "001", "002": 일할 계산 시 "(실근무일+주휴수당)×8H" 로 계산
                if (midStatus.equals("000") || diff == 0) {
                    basicAmount = basicAmount.add(hourlyPay.multiply(BigDecimal.valueOf(209)));
                } else {
                    basicAmount = hourlyPay.multiply(BigDecimal.valueOf((workcnt + paidHoliday) * 8L));
                    rtTotalTime = (double) ((workcnt + paidHoliday) * 8);
                }
                // 연차수당
                // 한달 1개 연차 생성. 단 중도 입/퇴사자 제외
                if (!midStatus.equals("000")) {
                    annualAllowance = BigDecimal.ZERO;
                } else {
                    annualAllowance = annualAllowance.add(hourlyPay.multiply(BigDecimal.valueOf(8))).setScale(0, RoundingMode.CEILING);
                }
                // 연차 계산
                if (rtAnnualLeaveUsed != 0) {
                    annualAllowance = annualAllowance.subtract(hourlyPay.multiply(BigDecimal.valueOf(rtAnnualLeaveUsed * 8))).setScale(0, RoundingMode.CEILING);
                }
                // 연차수당 - 반차
                if (rtHalfLeaverUsed != 0) {
                    halfAllowance = halfAllowance.subtract(hourlyPay.multiply(BigDecimal.valueOf(rtHalfLeaverUsed))).setScale(0, RoundingMode.CEILING);
                }
                // 연차수당 - 해당월입사
                // 2023.10.11 김다은 중도입사/퇴사 연차 없음
                // 마이너스 금액은 그대로 표기
                if (!midStatus.equals("000") && (annualAllowance.compareTo(BigDecimal.ZERO) == 1)) {
                    annualAllowance = BigDecimal.ZERO;
                }
                if (!midStatus.equals("000") && (halfAllowance.compareTo(BigDecimal.ZERO) == 1)) {
                    halfAllowance = BigDecimal.ZERO;
                }


                // 야간조 야간수당, 교통비, 식대
                if (nightCnt != 0) {
                    transportationAmount = BigDecimal.valueOf(20000).multiply(BigDecimal.valueOf(nightCnt)).setScale(0, RoundingMode.CEILING);
                    mealsAmount = BigDecimal.valueOf(14000).multiply(BigDecimal.valueOf(nightCnt)).setScale(0, RoundingMode.CEILING);
                    rtTransportation = (double) nightCnt;
                    rtMeal = (double) nightCnt;
                }
                // 휴일수당1
                if (holidayMinite != 0) {
                    holidayAllowance01 = holidayAllowance01.add(BigDecimal.valueOf(holidayMinite / 8).multiply(hourlyPay.multiply(BigDecimal.valueOf(8))).multiply(BigDecimal.valueOf(1.5)));
                }
                // 휴일수당1(야간조 5일근무 +1)
                if (nightTeamPlus != 0) {
                    holidayAllowance01 = holidayAllowance01.add(BigDecimal.valueOf(nightTeamPlus).multiply(hourlyPay).multiply(BigDecimal.valueOf(8)).multiply(BigDecimal.valueOf(1.5)));
                }
                // 휴일수당(토요일)
                if (rtHolidaySaturdayUsed != 0) {
                    holidayAllowanceSat = holidayAllowanceSat.add(BigDecimal.valueOf(rtHolidaySaturdayUsed).multiply(hourlyPay).multiply(BigDecimal.valueOf(1.5)));
                }
                // 휴일수당(일요일)
                if (rtHolidaySundayUsed != 0) {
                    holidayAllowanceSun = holidayAllowanceSun.add(BigDecimal.valueOf(rtHolidaySundayUsed).multiply(hourlyPay).multiply(BigDecimal.valueOf(1.5)));
                }

                // 기타수당1
                if (rtLateUsed != 0) {
                    otherAmount = otherAmount.add(hourlyPay.multiply(BigDecimal.valueOf(rtLateUsed * -1))).setScale(0, RoundingMode.CEILING);
                    lateAmount = lateAmount.add(hourlyPay.multiply(BigDecimal.valueOf(rtLateUsed * -1))).setScale(0, RoundingMode.CEILING);
                }
                if (rtEarlyLeaveUsed != 0) {
                    otherAmount = otherAmount.add(hourlyPay.multiply(BigDecimal.valueOf(rtEarlyLeaveUsed * -1))).setScale(0, RoundingMode.CEILING);
                    earlyLeaverAmount = earlyLeaverAmount.add(hourlyPay.multiply(BigDecimal.valueOf(rtEarlyLeaveUsed * -1))).setScale(0, RoundingMode.CEILING);
                }
                if (rtOuterUsed != 0) {
                    otherAmount = otherAmount.add(hourlyPay.multiply(BigDecimal.valueOf(rtOuterUsed * -1))).setScale(0, RoundingMode.CEILING);
                    OuterAmount = OuterAmount.add(hourlyPay.multiply(BigDecimal.valueOf(rtOuterUsed * -1))).setScale(0, RoundingMode.CEILING);
                }
                // 무급계산
                if (!(nonPayCnt == 0)) {
                    nonPayAmount = BigDecimal.valueOf(Double.parseDouble(basicSalaryDto.getHourlyPay()) * 8 * nonPayCnt).multiply(BigDecimal.valueOf(-1));
                }
            }

            // 2023-12-11 DH(김다은) 요청으로 무급 전체 처리로 수정
//            {
//                List<ADTDataDto> adtDataDtos = salaryDao.selectADTData(requestDto.getCompanyId(), requestDto.getYyyymm(), basicSalaryDto.getEmployeeId());
//                workcnt = 0;
//                paidHoliday = 0;
//                for (ADTDataDto adtDataDto : adtDataDtos) {
//
//                    // 무급처리 count
//                    if (adtDataDto.getWorkStatus().equals("무급")) {
//                        nonPayCnt++;
//
//                        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
//                        DateFormat outputFormat = new SimpleDateFormat("M/d");
//                        DateFormat outputFormat2 = new SimpleDateFormat("d");
//                        String dayOfMonth = "";
//                        try {
//                            dayOfMonth = outputFormat2.format(inputFormat.parse(adtDataDto.getWorkDate()));
//                        } catch (ParseException e) {
//                            e.printStackTrace();
//                        }
//                        if (nonPayDay.isEmpty()) {
//                            nonPayDay = adtDataDto.getWorkDate();
//                            try {
//                                // 입력 문자열을 Date 객체로 파싱
//                                Date date = inputFormat.parse(nonPayDay);
//                                nonPayDay = outputFormat.format(date);
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                            }
//                        } else {
//                            nonPayDay = nonPayDay + "," + dayOfMonth;
//                        }
//                    }
//                }
//                // 무급처리
//                // 책정임금등록의 (기본급/연장수당2/야간수당2/휴일수당2) / 30일 * 무급휴가일 (소숫점 첫째자리 ROUNDUP)
//                if (!(nonPayCnt == 0)) {
//                    try {
//                        annualAllowance = calcNonPay(nonPayCnt,
//                                null == basicSalaryDto.getBasicSalary() ? "0" : basicSalaryDto.getBasicSalary(),
//                                null == basicSalaryDto.getOvertimeAllowance02() ? "0" : basicSalaryDto.getOvertimeAllowance02(),
//                                null == basicSalaryDto.getNightAllowance02() ? "0" : basicSalaryDto.getNightAllowance02(),
//                                null == basicSalaryDto.getHolidayAllowance02() ? "0" : basicSalaryDto.getHolidayAllowance02());
//                    } catch (NullPointerException e) {
//                        ;
//                    }
//                }
//            }

            monthlyKeunTaeDto.setAnnualLeaveUsed(rtAnnualLeaveUsed);
            monthlyKeunTaeDto.setAnnualLeaveUsedDay(rtAnnualLeaveUsedDay);
            monthlyKeunTaeDto.setHalfLeaveUsed(rtHalfLeaverUsed);
            monthlyKeunTaeDto.setHalfLeaveUsedDay(rtHalfLeaveUseDay);
            monthlyKeunTaeDto.setHalfLeaveCnt(rtHalfLeaveCnt);

            monthlyKeunTaeDto.setOvertimeDaytime(rtOverTimeUsed01);
            monthlyKeunTaeDto.setOverTimeDay2HCnt(rtOverTimeDay2HCnt);
            monthlyKeunTaeDto.setOverTimeDay4H3MCnt(rtOverTimeDay4H3MCnt);

            monthlyKeunTaeDto.setNightDaytime(rtNSDayTimeUsed);
            monthlyKeunTaeDto.setNightNighttime(rtNSNightTimeUsed);

            monthlyKeunTaeDto.setHolidaySaturday(rtHolidaySaturdayUsed);
            monthlyKeunTaeDto.setHolidaySaturdayDay4HCnt(rtHolidaySaturdayDay4HCnt);
            monthlyKeunTaeDto.setHolidaySaturdayDay8HCnt(rtHolidaySaturdayDay8HCnt);
            monthlyKeunTaeDto.setHolidaySunday(rtHolidaySundayUsed);
            monthlyKeunTaeDto.setHolidaySundayDay4HCnt(rtHolidaySunday4HCnt);
            monthlyKeunTaeDto.setHolidaySundayDay8HCnt(rtHolidaySunday8HCnt);

            monthlyKeunTaeDto.setTransportation(rtTransportation);
            monthlyKeunTaeDto.setMeal(rtMeal);

            monthlyKeunTaeDto.setLateTime(rtLateUsed);
            monthlyKeunTaeDto.setLatecnt(rtLatecnt);
            monthlyKeunTaeDto.setLateDay(rtLateDay);

            monthlyKeunTaeDto.setEarlyLeaveTime(rtEarlyLeaveUsed);
            monthlyKeunTaeDto.setEarlyLeaveDay(rtEarlyLeaveDay);
            monthlyKeunTaeDto.setEarlyLeaveCnt(rtEarlyLeaveCnt);
            monthlyKeunTaeDto.setOuterTime(rtOuterUsed);
            monthlyKeunTaeDto.setOuterDay(rtOuterDay);
            monthlyKeunTaeDto.setAbsence(rtAbsence);
            monthlyKeunTaeDto.setTotalDay(workcnt);
            monthlyKeunTaeDto.setJuhueDay(paidHoliday);

            monthlyKeunTaeDto.setTotalTime(rtTotalTime);
            monthlyKeunTaeDto.setNS01DayCnt(rtNS01DayCnt);
            monthlyKeunTaeDto.setNightTeamPlus((double) nightTeamPlus * 8);
            monthlyKeunTaeDto.setNSHoliDayCnt(rtNSHoliDayCnt);
            monthlyKeunTaeDto.setNonPayCnt(nonPayCnt);
            monthlyKeunTaeDto.setNonPayDay(nonPayDay);


            if (!basicAmount.equals(BigDecimal.ZERO))
                basicSalaryDto.setBasicSalary(basicAmount.toString());
            if (!hourlyPay.equals(BigDecimal.ZERO))
                basicSalaryDto.setHourlyPay(hourlyPay.toString());

            if (!annualAllowance.equals(BigDecimal.ZERO))
                basicSalaryDto.setAnnualAllowance(annualAllowance.toString());
            if (!halfAllowance.equals(BigDecimal.ZERO))
                basicSalaryDto.setHalfAllowance(halfAllowance.toString());

            if (!overtimeAllowance01.equals(BigDecimal.ZERO))
                basicSalaryDto.setOvertimeAllowance01(overtimeAllowance01.toString());
            if (!overtimeAllowance02.equals(BigDecimal.ZERO))
                basicSalaryDto.setOvertimeAllowance02(overtimeAllowance02.toString());

            if (!nightAllowance01Day.equals(BigDecimal.ZERO))
                basicSalaryDto.setNightAllowance01Day(nightAllowance01Day.toString());
            if (!nightAllowance01Night.equals(BigDecimal.ZERO))
                basicSalaryDto.setNightAllowance01Night(nightAllowance01Night.toString());
            if (!nightAllowance02.equals(BigDecimal.ZERO))
                basicSalaryDto.setNightAllowance02(nightAllowance02.toString());

            if (!holidayAllowance01.equals(BigDecimal.ZERO) || !observeHolidayAmount.equals(BigDecimal.ZERO))
                basicSalaryDto.setHolidayAllowance01(holidayAllowance01.add(observeHolidayAmount).toString());
            if (!holidayAllowanceSat.equals(BigDecimal.ZERO))
                basicSalaryDto.setHolidayAllowanceSat(holidayAllowanceSat.toString());
            if (!holidayAllowanceSun.equals(BigDecimal.ZERO))
                basicSalaryDto.setHolidayAllowanceSun(holidayAllowanceSun.toString());
            if (!holidayAllowance02.equals(BigDecimal.ZERO))
                basicSalaryDto.setHolidayAllowance02(holidayAllowance02.toString());

            if (!otherAmount.equals(BigDecimal.ZERO))
                basicSalaryDto.setOtherAllowance01(otherAmount.toString());
            if (!lateAmount.equals(BigDecimal.ZERO))
                basicSalaryDto.setLateAmount(lateAmount.toString());
            if (!earlyLeaverAmount.equals(BigDecimal.ZERO))
                basicSalaryDto.setEarlyAmount(earlyLeaverAmount.toString());
            if (!OuterAmount.equals(BigDecimal.ZERO))
                basicSalaryDto.setOuterAmount(OuterAmount.toString());

            if (!transportationAmount.equals(BigDecimal.ZERO))
                basicSalaryDto.setTransportationExpenses(transportationAmount.toString());
            if (!mealsAmount.equals(BigDecimal.ZERO))
                basicSalaryDto.setMealsExpenses(mealsAmount.toString());
            if (!nonPayAmount.equals(BigDecimal.ZERO))
                basicSalaryDto.setNonPayAmount(nonPayAmount.toString());

            monthlyKeunTaeDtos.add(monthlyKeunTaeDto);
            resultData.add(basicSalaryDto);
        }

        if (!basicSalaryDtos.isEmpty() && !resultData.isEmpty()) {
            salaryDao.insertCalcSalary(resultData);
            salaryDao.insertMonthlyKeunTae(monthlyKeunTaeDtos);
        }
        return responseDto;
    }

    public static boolean checkLunchTime(LocalDateTime workStartTime, LocalDateTime workEndTime, LocalTime
            lunchStartTime, LocalTime lunchEndTime) {
        LocalDateTime startTimeWithLunch = workStartTime.with(lunchStartTime);
        LocalDateTime endTimeWithLunch = workStartTime.with(lunchEndTime);

        boolean isLunchTimeIncluded = false;
        if ((startTimeWithLunch.isAfter(workStartTime) || startTimeWithLunch.equals(workStartTime))
                && (startTimeWithLunch.isBefore(workEndTime) || startTimeWithLunch.equals(workEndTime))
                && (endTimeWithLunch.isAfter(workStartTime) || endTimeWithLunch.equals(workStartTime))
                && (endTimeWithLunch.isBefore(workEndTime) || endTimeWithLunch.equals(workEndTime))) {
            isLunchTimeIncluded = true;
        }
        // test
        return isLunchTimeIncluded;
    }

    /**
     * 범위안에 해당되는 분을 리턴한다.
     *
     * @param start
     * @param end
     * @param range_s
     * @param range_e
     * @return
     */
    static long getTimeInRange(LocalDateTime start, LocalDateTime end, LocalDateTime range_s, LocalDateTime range_e) {

        Long rst = 0L;

        LocalDateTime from = (null == range_s || true == start.isAfter(range_s) ? start : range_s);
        LocalDateTime to = (null == range_e || true == end.isBefore(range_e) ? end : range_e);

        if (from.isBefore(to)) {
            rst = Duration.between(from, to).toMinutes();
        }

        return rst;
    }

    /**
     * DH 식 외출 시간 계산 함수
     *
     * @param start
     * @param end
     * @return
     */
    static Double getOutTime(String start, String end) {

        LocalDateTime sTime = LocalDateTime.parse(start, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime eTime = LocalDateTime.parse(end, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        Long outTime1 = getTimeInRange(sTime, eTime, null, sTime.with(LocalTime.of(12, 00)));
        Long gapTime1 = getTimeInRange(sTime, eTime, sTime.with(LocalTime.of(12, 00)), sTime.with(LocalTime.of(12, 30)));
        Long gapTime2 = getTimeInRange(sTime, eTime, sTime.with(LocalTime.of(13, 30)), sTime.with(LocalTime.of(14, 00)));
        Long outTime2 = getTimeInRange(sTime, eTime, sTime.with(LocalTime.of(14, 00)), null);
        Long outTime = (outTime1 + outTime2 + (gapTime1 + gapTime2 > 30 ? gapTime1 + gapTime2 - 30 : 0));
        Double rst = 0.5 * Math.ceil(1.0 * outTime / 30);

        logger.error(" %3d+%3d+%3d+%3d = %4d (%2d:%2d)", outTime1, gapTime1, gapTime2, outTime2, outTime, outTime / 60, outTime % 60);
        return rst;
    }

    public static BigDecimal calcNonPay(Integer nonPaycnt, String basicSalary, String overtimeAllowance02, String
            nightAllowance02, String holidayAllowance02) {
        BigDecimal nonPayBasicSalary = BigDecimal.ZERO;
        BigDecimal nonPayOvertimeAllowance02 = BigDecimal.ZERO;
        BigDecimal nonPayNightAllowance02 = BigDecimal.ZERO;
        BigDecimal nonPayHolidayAllowance02 = BigDecimal.ZERO;

        nonPayBasicSalary = new BigDecimal(basicSalary);
        if (overtimeAllowance02 != null) {
            nonPayOvertimeAllowance02 = new BigDecimal(overtimeAllowance02);
        }
        if (nightAllowance02 != null) {
            nonPayNightAllowance02 = new BigDecimal(nightAllowance02);
        }
        if (holidayAllowance02 != null) {
            nonPayHolidayAllowance02 = new BigDecimal(holidayAllowance02);
        }
        return (nonPayBasicSalary.add(nonPayOvertimeAllowance02).add(nonPayNightAllowance02).add(nonPayHolidayAllowance02)).multiply(BigDecimal.valueOf(nonPaycnt).divide(BigDecimal.valueOf(30), 8, RoundingMode.HALF_UP)).multiply(BigDecimal.valueOf(-1)).setScale(0, RoundingMode.UP);
    }

    public static boolean isBetween(LocalDateTime targetTime, LocalDateTime startTime, LocalDateTime endTime) {
        return targetTime.isAfter(startTime) && targetTime.isBefore(endTime);
    }

    /**
     * 중도입사 체크
     *
     * @param hireDate
     * @return
     */
    public static Boolean checkMidHire(String yyyymm, LocalDate hireDate) {
        if (hireDate.getYear() == Integer.parseInt(yyyymm.substring(0, 4))
                && hireDate.getMonthValue() == Integer.parseInt(yyyymm.substring(4, 6))) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    /**
     * 중도퇴사 체크
     *
     * @param retireDate
     * @return
     */
    public static Boolean checkMidRetire(String yyyymm, LocalDate retireDate) {
        if (retireDate.getYear() == Integer.parseInt(yyyymm.substring(0, 4))
                && retireDate.getMonthValue() == Integer.parseInt(yyyymm.substring(4, 6))) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }
}
