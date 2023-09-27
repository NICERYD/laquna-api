package kr.co.seedit.domain.hr.application;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.extensions.XSSFHeaderFooter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import kr.co.seedit.domain.hr.dto.Payroll6InPageDto;
import kr.co.seedit.domain.hr.dto.ReportParamsDto;
import kr.co.seedit.domain.mapper.seedit.Payroll6InPageDao;
import kr.co.seedit.global.common.dto.ResponseDto;
import kr.co.seedit.global.utils.SeedXSSFUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class Payroll6InPageService {

    private static final Logger logger = LoggerFactory.getLogger(Payroll6InPageService.class);

    final private String PAYROLL_TABLE_FORMAT_FILE = "classpath:hr/payroll6Table.xlsx";

    private final Payroll6InPageDao payroll6InPageDao;
    private final ResourceLoader resourceLoader;

    /**
     * 6쪽 (3열, 페이지당 6개 출력) 급여표 엑셀다운로드 데이터 생성 함수
     *
     * @param reportParamsDto
     * @return
     * @throws IOException
     */
    public XSSFWorkbook createPayroll6InPage(ReportParamsDto reportParamsDto) throws IOException {

        Resource resource = resourceLoader.getResource(PAYROLL_TABLE_FORMAT_FILE);
        InputStream fis;
        XSSFWorkbook workbook;
        try {
            fis = resource.getInputStream();
            workbook = new XSSFWorkbook(fis);
        } catch (IOException e) {
            throw new IOException("Format file read fail. file name is " + PAYROLL_TABLE_FORMAT_FILE);
        }

        // get full-time user data
//		reportParamsDto.setEmployeeType(100);
        List<Payroll6InPageDto> listFulltime = payroll6InPageDao.getPayroll6InPageList(reportParamsDto);

        try {
            // set full-time user information
            setFulltimePayroll6InPageList(workbook, listFulltime);
        } catch (IOException e) {
            throw new IOException("Full-time data write fail.");
        }


//		// get set part-time user data
//		reportParamsDto.setEmployeeType(200);
//		List<Payroll6InPageDto> listParttime = payroll6InPageDao.getPayroll6InPageList(reportParamsDto);
//		
//		try {
//			// set part-time user information
//			setParttimePayroll6InPageList(workbook, listParttime);
//		} catch (IOException e) {
//			throw new IOException("Part-time data write fail.");
//		}

        // print header setting
        XSSFHeaderFooter header = (XSSFHeaderFooter) workbook.getSheetAt(0).getHeader();
        header.setLeft(reportParamsDto.getYyyymm().substring(4, 6) + "월 급여");
//		header = (XSSFHeaderFooter) workbook.getSheetAt(1).getHeader();
//		header.setLeft(StringUtils.getNumberString(reportParamsDto.getYyyymm().substring(4,6)) +"월 급여");

        return workbook;
    }

    private boolean setFulltimePayroll6InPageList(XSSFWorkbook workbook, List<Payroll6InPageDto> list) throws IOException {
        XSSFCellStyle styleLightGreen = workbook.createCellStyle();
        styleLightGreen.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());  // 배경색
        styleLightGreen.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleLightGreen.setBorderTop(BorderStyle.THIN);
        styleLightGreen.setBorderBottom(BorderStyle.THIN);
        styleLightGreen.setBorderLeft(BorderStyle.THIN);
        styleLightGreen.setBorderRight(BorderStyle.THIN);
        styleLightGreen.setShrinkToFit(true);
        XSSFCellStyle styleLemonChiffon = workbook.createCellStyle();
        styleLemonChiffon.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());  // 배경색
        styleLemonChiffon.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleLemonChiffon.setBorderTop(BorderStyle.THIN);
        styleLemonChiffon.setBorderBottom(BorderStyle.THIN);
        styleLemonChiffon.setBorderLeft(BorderStyle.THIN);
        styleLemonChiffon.setBorderRight(BorderStyle.THIN);
        styleLemonChiffon.setShrinkToFit(true);

        final int sheetNumber = 0;
        final int dataCountInRow = 3;  // 1열에 출력 포멧의 개수
        final int dataOffsetCell = 6;  // 출력 포멧의 열 수 (+ 구분열 1개)
        int countHeadLine = workbook.getSheetAt(0).getLastRowNum() + 1; //23 출력 포멧의 라인수

        // set format
        SeedXSSFUtil.copyHeadlineCubeFormat(workbook, sheetNumber, countHeadLine, list.size(), dataCountInRow, dataOffsetCell);

        Payroll6InPageDto sum = new Payroll6InPageDto();
        int curRow = 0, curCol = 0;
        XSSFSheet sheet = workbook.getSheetAt(sheetNumber);
        for (int no = 0; no < list.size(); no++) {
            Payroll6InPageDto data = list.get(no);
            curRow = Math.floorDiv(no, dataCountInRow) * countHeadLine;
            curCol = Math.floorMod(no, dataCountInRow) * dataOffsetCell;

            data.setTotalAmt(data.getBasicAmount()
                    + data.getOvertimeAllowance01()
                    + data.getOvertimeAllowance02()
                    + data.getNtDayAllowance01()
                    + data.getNtNightAllowance01()
                    + data.getNtNightAllowance02()
                    + data.getHolidaySatAllowance01()
                    + data.getHolidaySunAllowance01()
                    + data.getHolidayAllowance02()
                    + data.getAttribute01()
                    + data.getAttribute15()
                    + data.getAttribute13()
                    + data.getAttribute14()
                    + data.getAttribute17()
                    + data.getAttribute16()
                    + data.getAttribute11()
                    + data.getAttribute12());

            setValuePayrollTableForm(sheet, data, no, curRow, curCol, styleLightGreen, styleLemonChiffon);

            // sum
            sum.setSalaryAmt(sum.getSalaryAmt() + data.getSalaryAmt());
            sum.setSalaryAmt2(sum.getSalaryAmt2() + data.getSalaryAmt2());
            sum.setTotalTime(sum.getTotalTime() + data.getTotalTime());
            sum.setBasicAmount(sum.getBasicAmount() + data.getBasicAmount());

            sum.setOvertimeDaytime01(sum.getOvertimeDaytime01() + data.getOvertimeDaytime01());
            sum.setOvertimeAllowance01(sum.getOvertimeAllowance01() + data.getOvertimeAllowance01());
            sum.setOvertimeDaytime02(sum.getOvertimeDaytime02() + data.getOvertimeDaytime02());
            sum.setOvertimeAllowance02(sum.getOvertimeAllowance02() + data.getOvertimeAllowance02());

            sum.setNtDaytime01(sum.getNtDaytime01() + data.getNtDaytime01());
            sum.setNtDayAllowance01(sum.getNtDayAllowance01() + data.getNtDayAllowance01());
            sum.setNtNighttime01(sum.getNtNighttime01() + data.getNtNighttime01());
            sum.setNtNightAllowance01(sum.getNtNightAllowance01() + data.getNtNightAllowance01());
            sum.setNtNighttime02(sum.getNtNighttime02() + data.getNtNighttime02());
            sum.setNtNightAllowance02(sum.getNtNightAllowance02() + data.getNtNightAllowance02());

            sum.setHolidaySatTime01(sum.getHolidaySatTime01() + data.getHolidaySatTime01());
            sum.setHolidaySatAllowance01(sum.getHolidaySatAllowance01() + data.getHolidaySatAllowance01());
            sum.setHolidaySunTime01(sum.getHolidaySunTime01() + data.getHolidaySunTime01());
            sum.setHolidaySunAllowance01(sum.getHolidaySunAllowance01() + data.getHolidaySunAllowance01());
            sum.setHolidayTime02(sum.getHolidayTime02() + data.getHolidayTime02());
            sum.setHolidayAllowance02(sum.getHolidayAllowance02() + data.getHolidayAllowance02());

            sum.setAnnualLeaveUsed(sum.getAnnualLeaveUsed() + data.getAnnualLeaveUsed());
            sum.setAttribute01(sum.getAttribute01() + data.getAttribute01());

            sum.setAttribute24(sum.getAttribute24() + data.getAttribute24());

            sum.setHalfLeaveUsed(sum.getHalfLeaveUsed() + data.getHalfLeaveUsed());
            sum.setAttribute15(sum.getAttribute15() + data.getAttribute15());
            sum.setHalfLeaveCnt(sum.getHalfLeaveCnt());

            sum.setEarlyLeaveTime(sum.getEarlyLeaveTime() + data.getEarlyLeaveTime());
            sum.setAttribute13(sum.getAttribute13() + data.getAttribute13());

            sum.setLateTime(sum.getLateTime() + data.getLateTime());
            sum.setAttribute14(sum.getAttribute14() + data.getAttribute14());

            sum.setOuterTime(sum.getOuterTime() + data.getOuterTime());
            sum.setAttribute17(sum.getAttribute17() + data.getAttribute17());

            sum.setOther02Used(sum.getOther02Used() + data.getOther02Used());
            sum.setAttribute16(sum.getAttribute16() + data.getAttribute16());

            sum.setAttribute11(sum.getAttribute11() + data.getAttribute11());
            sum.setAttribute12(sum.getAttribute12() + data.getAttribute12());

            sum.setTotalAmt(sum.getTotalAmt() + data.getTotalAmt());
        }

        setSumPayrollTable(sheet, sum, 0, dataCountInRow * dataOffsetCell);

        return true;
    }

    /**
     * 엑셀 시트 급여표(6쪽의 테이블 양식) 값
     *
     * @param sheet  입력할 시트정보
     * @param data   값을 설정할 data
     * @param no     순번 칸에 입력될 값 (빈칸으로 남겨두려면 음수를 넣으면된다)
     * @param curRow excel start row point number
     * @param curCol excel start column point number
     * @return
     */
    public static void setValuePayrollTableForm(XSSFSheet sheet, Payroll6InPageDto data, int no, int curRow, int curCol,
                                                XSSFCellStyle retireHivediv, XSSFCellStyle newHivediv) {
        //NO.
        if (no >= 0) {
            sheet.getRow(curRow + 0).getCell(curCol + 1).setCellValue(no + 1);
        }
        if (null != data.getDefinedName()) {
            sheet.getRow(curRow + 0).getCell(curCol + 2).setCellValue(data.getDefinedName());
        }
        if (null != data.getKoreanName()) {
            sheet.getRow(curRow + 0).getCell(curCol + 3).setCellValue(data.getKoreanName());
        }
			if (null != data.getSalaryAmt()) {
            sheet.getRow(curRow + 0).getCell(curCol + 4).setCellValue(data.getSalaryAmt());
        }
        if (null != data.getHireDate()) {
            if ("".equals(data.getHireDiv())) {
                ; // default color - do nothing
            } else if ("RETIRE".equals(data.getHireDiv())) {
                sheet.getRow(curRow + 1).getCell(curCol + 0).setCellStyle(retireHivediv);
            } else if ("NEW".equals(data.getHireDiv())) {
                sheet.getRow(curRow + 1).getCell(curCol + 0).setCellStyle(newHivediv);
            }
            sheet.getRow(curRow + 1).getCell(curCol + 0).setCellValue(data.getHireDate());
        }
			if (null != data.getSalaryAmt2() && 0 != data.getSalaryAmt2()) {
            sheet.getRow(curRow + 1).getCell(curCol + 4).setCellValue(data.getSalaryAmt2());
        }
        //기본급
		if (null != data.getTotalTime()) {
            sheet.getRow(curRow + 2).getCell(curCol + 2).setCellValue(data.getTotalTime());
        }
		if (null != data.getBasicAmount() && 0 != data.getBasicAmount()) {
            sheet.getRow(curRow + 2).getCell(curCol + 3).setCellValue(data.getBasicAmount());
        }
        //연장1 //연봉/시급
			if (null != data.getOvertimeDaytime01() && 0 != data.getOvertimeDaytime01()) {
            sheet.getRow(curRow + 3).getCell(curCol + 2).setCellValue(data.getOvertimeDaytime01());
        }
        if (null != data.getOvertimeAllowance01() && 0 != data.getOvertimeAllowance01()) {
            sheet.getRow(curRow + 3).getCell(curCol + 3).setCellValue(data.getOvertimeAllowance01());
        }
        //연장2
        //연봉/포괄
			if (null != data.getOvertimeDaytime02() && 0 != data.getOvertimeDaytime02()) {
            sheet.getRow(curRow + 4).getCell(curCol + 2).setCellValue(data.getOvertimeDaytime02());
        }
			if (null != data.getOvertimeAllowance02() && 0 != data.getOvertimeAllowance02()) {
            sheet.getRow(curRow + 4).getCell(curCol + 3).setCellValue(data.getOvertimeAllowance02());
        }
        //야간수당
        //주간(시급)
			if (null != data.getNtDaytime01() && 0 != data.getNtDaytime01()) {
				sheet.getRow(curRow + 5).getCell(curCol + 2).setCellValue(data.getNtDaytime01());
			}
			if (null != data.getNtDayAllowance01() && 0 != data.getNtDayAllowance01()) {
				sheet.getRow(curRow + 5).getCell(curCol + 3).setCellValue(data.getNtDayAllowance01());
			}
			//야간(연봉/시급)
			if (null != data.getNtNighttime01() && 0 != data.getNtNighttime01()) {
				sheet.getRow(curRow + 6).getCell(curCol + 2).setCellValue(data.getNtNighttime01());
			}
			if (null != data.getNtNightAllowance01() && 0 != data.getNtNightAllowance01()) {
				sheet.getRow(curRow + 6).getCell(curCol + 3).setCellValue(data.getNtNightAllowance01());
			}
			//야간2
			//연봉/포괄
			if (null != data.getNtNighttime02() && 0 != data.getNtNighttime02()) {
				sheet.getRow(curRow + 7).getCell(curCol + 2).setCellValue(data.getNtNighttime02());
			}
			if (null != data.getNtNightAllowance02() && 0 != data.getNtNightAllowance02()) {
				sheet.getRow(curRow + 7).getCell(curCol + 3).setCellValue(data.getNtNightAllowance02());
			}
			//휴일1
			//토요(연봉/시급)
			if (null != data.getHolidaySatTime01() && 0 != data.getHolidaySatTime01()) {
				sheet.getRow(curRow + 8).getCell(curCol + 2).setCellValue(data.getHolidaySatTime01());
			}
			if (null != data.getHolidaySatAllowance01() && 0 != data.getHolidaySatAllowance01()) {
				sheet.getRow(curRow + 8).getCell(curCol + 3).setCellValue(data.getHolidaySatAllowance01());
			}
			//일요(연봉/시급)
			if (null != data.getHolidaySunTime01() && 0 != data.getHolidaySunTime01()) {
				sheet.getRow(curRow + 9).getCell(curCol + 2).setCellValue(data.getHolidaySunTime01());
			}
			if (null != data.getHolidaySunAllowance01() && 0 != data.getHolidaySunAllowance01()) {
				sheet.getRow(curRow + 9).getCell(curCol + 3).setCellValue(data.getHolidaySunAllowance01());
			}
			//휴일2
			//연봉/포괄
			if (null != data.getHolidayTime02() && 0 != data.getHolidayTime02()) {
            sheet.getRow(curRow + 10).getCell(curCol + 2).setCellValue(data.getHolidayTime02());
        }
			if (null != data.getHolidayAllowance02() && 0 != data.getHolidayAllowance02()) {
            sheet.getRow(curRow + 10).getCell(curCol + 3).setCellValue(data.getHolidayAllowance02());
        }
        //연차사용
        if (null != data.getAnnualLeaveUsedDay() && data.getEmployeeType().equals("100")) {
            sheet.getRow(curRow + 11).getCell(curCol + 1).setCellValue(data.getAnnualLeaveUsedDay());
        }
        if (null != data.getAnnualLeaveUsed() && data.getEmployeeType().equals("100")) {
            sheet.getRow(curRow + 11).getCell(curCol + 2).setCellValue(data.getAnnualLeaveUsed());
        }
        //연차수당
        if (null != data.getAnnualLeaveUsedDay() && data.getEmployeeType().equals("200")) {
            sheet.getRow(curRow + 12).getCell(curCol + 1).setCellValue(data.getAnnualLeaveUsedDay());
        }
        if (null != data.getAnnualLeaveUsed() && data.getEmployeeType().equals("200")) {
            sheet.getRow(curRow + 12).getCell(curCol + 2).setCellValue(data.getAnnualLeaveUsed());
        }
        if (null != data.getAttribute01() && data.getEmployeeType().equals("200")) {
            sheet.getRow(curRow + 12).getCell(curCol + 3).setCellValue(data.getAttribute01());
        }

        //연차정산
        if (null != data.getAnnualLeaveCalc()) {
            sheet.getRow(curRow + 13).getCell(curCol + 2).setCellValue(data.getAnnualLeaveCalc());
        }
			if (null != data.getAttribute24() && 0 != data.getAttribute24()) {
            sheet.getRow(curRow + 13).getCell(curCol + 3).setCellValue(data.getAttribute24());
        }

        //반차
        if (null != data.getHalfLeaveUsedDay()) {
            sheet.getRow(curRow + 14).getCell(curCol + 1).setCellValue(data.getHalfLeaveUsedDay());
        }
        if (null != data.getHalfLeaveCnt() && !data.getHalfLeaveCnt().equals("")) {
            sheet.getRow(curRow + 14).getCell(curCol + 2).setCellValue(data.getHalfLeaveCnt());
        }
        if (null != data.getAttribute15() && 0 != data.getAttribute15()) {
            sheet.getRow(curRow + 14).getCell(curCol + 3).setCellValue(data.getAttribute15());
        }
        //조퇴
        if (null != data.getEarlyLeaveDay()) {
            sheet.getRow(curRow + 15).getCell(curCol + 1).setCellValue(data.getEarlyLeaveDay());
        }
        if (data.getEmployeeType().equals("100")) {
        	if (0 != data.getEarlyLeaveTime())
        		sheet.getRow(curRow + 15).getCell(curCol + 2).setCellValue(data.getEarlyLeaveTime());
        } else if (data.getEmployeeType().equals("200")) {
        	if (0 != data.getEarlyLeaveUsedCnt())
        		sheet.getRow(curRow + 15).getCell(curCol + 2).setCellValue(data.getEarlyLeaveUsedCnt());
        }
        if (null != data.getAttribute13() && 0 != data.getAttribute13()) {
            sheet.getRow(curRow + 15).getCell(curCol + 3).setCellValue(data.getAttribute13());
        }
        //지각
        if (null != data.getLateDay()) {
            sheet.getRow(curRow + 16).getCell(curCol + 1).setCellValue(data.getLateDay());
        }
        if (0 != data.getLateTime() && data.getEmployeeType().equals("100")) {
            sheet.getRow(curRow + 16).getCell(curCol + 2).setCellValue(data.getLateCnt());
        } else if (0 != data.getLateTime() && data.getEmployeeType().equals("200")) {
            sheet.getRow(curRow + 16).getCell(curCol + 2).setCellValue(data.getLateTime());
        }
        if (null != data.getAttribute14() && 0 != data.getAttribute14()) {
            sheet.getRow(curRow + 16).getCell(curCol + 3).setCellValue(data.getAttribute14());
        }
        //외출
        if (null != data.getOuterDay()) {
            sheet.getRow(curRow + 17).getCell(curCol + 1).setCellValue(data.getOuterDay());
        }
        if (0 != data.getOuterTime()) {
            sheet.getRow(curRow + 17).getCell(curCol + 2).setCellValue(data.getOuterTime());
        }
        if (null != data.getAttribute17() && 0 != data.getAttribute17()) {
            sheet.getRow(curRow + 17).getCell(curCol + 3).setCellValue(data.getAttribute17());
        }
        //초과수당
        if (0 != data.getOther02Used()) {
            sheet.getRow(curRow + 18).getCell(curCol + 2).setCellValue(data.getOther02Used());
        }
        if (0 != data.getAttribute16()) {
            sheet.getRow(curRow + 18).getCell(curCol + 3).setCellValue(data.getAttribute16());
        }
        //보조금
        //교통비
        if (0 != data.getAttribute11()) {
            sheet.getRow(curRow + 20).getCell(curCol + 3).setCellValue(data.getAttribute11());
        }
        //식대
        if (0 != data.getAttribute12()) {
            sheet.getRow(curRow + 21).getCell(curCol + 3).setCellValue(data.getAttribute12());
        }
        //합 계
        if (0 != data.getTotalAmt()) {
            sheet.getRow(curRow + 22).getCell(curCol + 3).setCellValue(data.getTotalAmt());
        }
    }

    /**
     * 엑셀 시트 급여표(6쪽의 테이블 양식) 전체 숫자 합계
     *
     * @param sheet  입력할 시트정보
     * @param data   값을 설정할 data
     * @param no     순번 칸에 입력될 값 (빈칸으로 남겨두려면 음수를 넣으면된다)
     * @param curRow excel start row point number
     * @param curCol excel start column point number
     * @return
     */
    public static void setSumPayrollTable(XSSFSheet sheet, Payroll6InPageDto data, int startRow, int startCol) {
        int curRow = startRow;
        int curCol = startCol;
        sheet.getRow(curRow).createCell(curCol++).setCellValue("NO.");
        curCol++;
        curCol++;
        curCol++;
        sheet.getRow(curRow).createCell(curCol++).setCellValue(data.getSalaryAmt());
        curRow++;
        curCol = startCol;
        curCol++;
        curCol++;
        curCol++;
        curCol++;
        sheet.getRow(curRow).createCell(curCol++).setCellValue(data.getSalaryAmt2());
        curRow++;
        curCol = startCol;
        sheet.getRow(curRow).createCell(curCol++).setCellValue("기본급");
        curCol++;
        sheet.getRow(curRow).createCell(curCol++).setCellValue(data.getTotalTime());
        sheet.getRow(curRow).createCell(curCol++).setCellValue(data.getBasicAmount());
        curCol++;
        curRow++;
        curCol = startCol;
        sheet.getRow(curRow).createCell(curCol++).setCellValue("연장1");
        sheet.getRow(curRow).createCell(curCol++).setCellValue("연봉/시급");
        sheet.getRow(curRow).createCell(curCol++).setCellValue(data.getOvertimeDaytime01());
        sheet.getRow(curRow).createCell(curCol++).setCellValue(data.getOvertimeAllowance01());
        curCol++;
        curRow++;
        curCol = startCol;
        sheet.getRow(curRow).createCell(curCol++).setCellValue("연장2");
        sheet.getRow(curRow).createCell(curCol++).setCellValue("연봉/포괄");
        sheet.getRow(curRow).createCell(curCol++).setCellValue(data.getOvertimeDaytime02());
        sheet.getRow(curRow).createCell(curCol++).setCellValue(data.getOvertimeAllowance02());
        curCol++;
        curRow++;
        curCol = startCol;
        sheet.getRow(curRow).createCell(curCol++).setCellValue("야간수당");
        sheet.getRow(curRow).createCell(curCol++).setCellValue("주간(시급)");
        sheet.getRow(curRow).createCell(curCol++).setCellValue(data.getNtDaytime01());
        sheet.getRow(curRow).createCell(curCol++).setCellValue(data.getNtDayAllowance01());
        curCol++;
        curRow++;
        curCol = startCol;
        curCol++;
        sheet.getRow(curRow).createCell(curCol++).setCellValue("야간(연봉/시급)");
        sheet.getRow(curRow).createCell(curCol++).setCellValue(data.getNtNighttime01());
        sheet.getRow(curRow).createCell(curCol++).setCellValue(data.getNtNightAllowance01());
        curCol++;
        curRow++;
        curCol = startCol;
        sheet.getRow(curRow).createCell(curCol++).setCellValue("야간2");
        sheet.getRow(curRow).createCell(curCol++).setCellValue("연봉/포괄");
        sheet.getRow(curRow).createCell(curCol++).setCellValue(data.getNtNighttime02());
        sheet.getRow(curRow).createCell(curCol++).setCellValue(data.getNtNightAllowance02());
        curCol++;
        curRow++;
        curCol = startCol;
        sheet.getRow(curRow).createCell(curCol++).setCellValue("휴일1");
        sheet.getRow(curRow).createCell(curCol++).setCellValue("토요(연봉/시급)");
        sheet.getRow(curRow).createCell(curCol++).setCellValue(data.getHolidaySatTime01());
        sheet.getRow(curRow).createCell(curCol++).setCellValue(data.getHolidaySatAllowance01());
        curCol++;
        curRow++;
        curCol = startCol;
        curCol++;
        sheet.getRow(curRow).createCell(curCol++).setCellValue("일요(연봉/시급)");
        sheet.getRow(curRow).createCell(curCol++).setCellValue(data.getHolidaySunTime01());
        sheet.getRow(curRow).createCell(curCol++).setCellValue(data.getHolidaySunAllowance01());
        curCol++;
        curRow++;
        curCol = startCol;
        sheet.getRow(curRow).createCell(curCol++).setCellValue("휴일2");
        sheet.getRow(curRow).createCell(curCol++).setCellValue("연봉/포괄");
        sheet.getRow(curRow).createCell(curCol++).setCellValue(data.getHolidayTime02());
        sheet.getRow(curRow).createCell(curCol++).setCellValue(data.getHolidayAllowance02());
        curCol++;
        curRow++;
        curCol = startCol;
        sheet.getRow(curRow).createCell(curCol++).setCellValue("연차사용");
        curCol++;
        curCol++; // sheet.getRow(curRow).createCell(curCol++).setCellValue(data.getAnnualLeaveUsed());
        curCol++;
        curCol++;
        curRow++;
        curCol = startCol;
        sheet.getRow(curRow).createCell(curCol++).setCellValue("연차수당");
        curCol++;
        curCol++;
        sheet.getRow(curRow).createCell(curCol++).setCellValue(data.getAttribute01());
        curCol++;
        curRow++;
        curCol = startCol;
        sheet.getRow(curRow).createCell(curCol++).setCellValue("연차정산");
        curCol++;
        curCol++;
        sheet.getRow(curRow).createCell(curCol++).setCellValue(data.getAttribute24());
        curCol++;
        curRow++;
        curCol = startCol;
        sheet.getRow(curRow).createCell(curCol++).setCellValue("반차");
        curCol++;
        sheet.getRow(curRow).createCell(curCol++).setCellValue(data.getHalfLeaveUsed());
        sheet.getRow(curRow).createCell(curCol++).setCellValue(data.getAttribute15());
        curCol++;
        curRow++;
        curCol = startCol;
        sheet.getRow(curRow).createCell(curCol++).setCellValue("조퇴");
        curCol++;
        sheet.getRow(curRow).createCell(curCol++).setCellValue(data.getEarlyLeaveTime());
        sheet.getRow(curRow).createCell(curCol++).setCellValue(data.getAttribute13());
        curCol++;
        curRow++;
        curCol = startCol;
        sheet.getRow(curRow).createCell(curCol++).setCellValue("지각");
        curCol++;
        sheet.getRow(curRow).createCell(curCol++).setCellValue(data.getLateTime());
        sheet.getRow(curRow).createCell(curCol++).setCellValue(data.getAttribute14());
        curCol++;
        curRow++;
        curCol = startCol;
        sheet.getRow(curRow).createCell(curCol++).setCellValue("외출");
        curCol++;
        sheet.getRow(curRow).createCell(curCol++).setCellValue(data.getOuterTime());
        sheet.getRow(curRow).createCell(curCol++).setCellValue(data.getAttribute17());
        curCol++;
        curRow++;
        curCol = startCol;
        sheet.getRow(curRow).createCell(curCol++).setCellValue("초과수당");
        curCol++;
        sheet.getRow(curRow).createCell(curCol++).setCellValue(data.getOther02Used());
        sheet.getRow(curRow).createCell(curCol++).setCellValue(data.getAttribute16());
        curCol++;
        curRow++;
        curCol = startCol;
        curCol++;
        curCol++;
        curCol++;
        curCol++;
        curCol++;
        curRow++;
        curCol = startCol;
        sheet.getRow(curRow).createCell(curCol++).setCellValue("보조금");
        sheet.getRow(curRow).createCell(curCol++).setCellValue("교통비");
        curCol++;
        sheet.getRow(curRow).createCell(curCol++).setCellValue(data.getAttribute11());
        curCol++;
        curRow++;
        curCol = startCol;
        curCol++;
        sheet.getRow(curRow).createCell(curCol++).setCellValue("식대");
        curCol++;
        sheet.getRow(curRow).createCell(curCol++).setCellValue(data.getAttribute12());
        curCol++;
        curRow++;
        curCol = startCol;
        sheet.getRow(curRow).createCell(curCol++).setCellValue("합 계");
        curCol++;
        curCol++;
        sheet.getRow(curRow).createCell(curCol++).setCellValue(data.getTotalAmt());
        curCol++;

    }

    //	@Transactional
    public Resource createPayroll6InPageLocalTest(Map<String, Object> in, HttpServletResponse response)
            throws Exception {
        ReportParamsDto reportParamsDto = new ReportParamsDto();
        reportParamsDto.setYyyymm(in.get("yyyymm").toString());
        reportParamsDto.setCompanyId(5);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        XSSFWorkbook workbook = createPayroll6InPage(reportParamsDto);

        try {
            workbook.write(byteArrayOutputStream);
        } catch (IOException e) {
            ;
        } finally {
            if (null != workbook) workbook.close();
        }
        return new ByteArrayResource(byteArrayOutputStream.toByteArray());
    }

    /**
     * 로컬다운로드 테스트 함수
     *
     * @param in
     * @param response
     * @return
     * @throws Exception
     */
//	@Transactional
    public ResponseDto createPayroll6InPageLocal(Map<String, Object> in, HttpServletResponse response)
            throws Exception {

        ReportParamsDto reportParamsDto = new ReportParamsDto();
        reportParamsDto.setYyyymm(in.get("yyyymm").toString());
        reportParamsDto.setCompanyId(5);


        XSSFWorkbook workbook = createPayroll6InPage(reportParamsDto);
        ResponseDto responseDto = ResponseDto.builder().build();

//		Resource resource = resourceLoader.getResource(PAYROLL_TABLE_FORMAT_FILE);
//		InputStream fis = resource.getInputStream();
//		XSSFWorkbook workbook = new XSSFWorkbook(fis);
//
//		// get full-time user data
//		List<PayrollDto> listFulltime = payrollDao.getFulltimePayroll6InPageList(reportParamsDto);
//		
//		// set full-time user information
//		setFulltimePayroll6InPageList(workbook, listFulltime);
//
//		
//		// get set part-time user data
//		List<PayrollDto> listParttime = payrollDao.getParttimePayroll6InPageList(reportParamsDto);
//		
//		// set part-time user information
//		setParttimePayroll6InPageList(workbook, listParttime);
//
//		
//		// print header setting
//		XSSFHeaderFooter header = (XSSFHeaderFooter) workbook.getSheetAt(0).getHeader();
//		header.setLeft(reportParamsDto.getYyyymm().substring(4,6)+"월 급여");
//		header = (XSSFHeaderFooter) workbook.getSheetAt(1).getHeader();
//		header.setLeft(StringUtils.getNumberString(reportParamsDto.getYyyymm().substring(4,6)) +"월 급여");

        try {
            File xlsxFile;
            if (null == in.get("file")) {
                Date time = new Date();
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
                String time1 = format.format(time);
                String dir = System.getProperty("user.home");
                String fileName = new StringBuilder().append(dir).append("/downloads/payRoll_").append(time1).append(".xlsx").toString();
                xlsxFile = new File(fileName);
            } else {
                xlsxFile = new File(in.get("file").toString());
            }
//			File xlsxFile = new File("D:\\GitHub\\laquna-api\\src\\main\\resources\\hr/"
//					+ /* payrollDto.getYyyymm() + */ "payrollOutput" + ".xlsx");
            FileOutputStream fileOut = new FileOutputStream(xlsxFile);
            workbook.write(fileOut);
            workbook.close();
            fileOut.close();
        } catch (Exception e) {
            logger.error("Exception", e);
            throw e;
        }

        return responseDto;
    }
}
