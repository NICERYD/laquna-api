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
import kr.co.seedit.global.utils.StringUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class Payroll6InPageService {

	private static final Logger logger = LoggerFactory.getLogger(Payroll6InPageService.class);

//	final private String sheetName = "급여표";
//	final private String PAYROLL_XLSX_FILE = "classpath:hr/payrollSample.xlsx";
//	final private String PAYROLL_FULLTIME_FILE = "classpath:hr/payrollParttime.xlsx";
//	final private String PAYROLL_PARTTIME_FILE = "classpath:hr/payrollFulltime.xlsx";
	final private String PAYROLL_TABLE_FORMAT_FILE = "classpath:hr/payroll.xlsx";
	
    private final Payroll6InPageDao payroll6InPageDao;
    private final ResourceLoader resourceLoader;
	
	/**
	 * 6쪽 (3열, 페이지당 6개 출력) 급여표 엑셀다운로드 데이터 생성 함수
	 * 
	 * @param reportParamsDto
	 * @return
	 * @throws IOException
	 */
	public XSSFWorkbook createPayroll6InPage(ReportParamsDto reportParamsDto) throws IOException  {
		
		Resource resource = resourceLoader.getResource(PAYROLL_TABLE_FORMAT_FILE);
		InputStream fis;
		XSSFWorkbook workbook;
		try {
			fis = resource.getInputStream();
			workbook = new XSSFWorkbook(fis);
		} catch (IOException e) {
			throw new IOException("Format file read fail. file name is "+PAYROLL_TABLE_FORMAT_FILE);
		}

		// get full-time user data
		reportParamsDto.setEmployeeType(100);
		List<Payroll6InPageDto> listFulltime = payroll6InPageDao.getPayroll6InPageList(reportParamsDto);
		
		try {
			// set full-time user information
			setFulltimePayroll6InPageList(workbook, listFulltime);
		} catch (IOException e) {
			throw new IOException("Full-time data write fail.");
		}

		
		// get set part-time user data
		reportParamsDto.setEmployeeType(200);
		List<Payroll6InPageDto> listParttime = payroll6InPageDao.getPayroll6InPageList(reportParamsDto);
		
		try {
			// set part-time user information
			setParttimePayroll6InPageList(workbook, listParttime);
		} catch (IOException e) {
			throw new IOException("Part-time data write fail.");
		}

		// print header setting
		XSSFHeaderFooter header = (XSSFHeaderFooter) workbook.getSheetAt(0).getHeader();
		header.setLeft(reportParamsDto.getYyyymm().substring(4,6)+"월 급여");
		header = (XSSFHeaderFooter) workbook.getSheetAt(1).getHeader();
		header.setLeft(StringUtils.getNumberString(reportParamsDto.getYyyymm().substring(4,6)) +"월 급여");

		return workbook;
	}
	
	private boolean setFulltimePayroll6InPageList(XSSFWorkbook workbook, List<Payroll6InPageDto> list) throws IOException
	{
		final int sheetNumber    = 0;
		final int countHeadLine  = 18; // 출력 포멧의 라인수
		final int dataCountInRow = 3;  // 1열에 출력 포멧의 개수
		final int dataOffsetCell = 6;  // 출력 포멧의 열 수 (+ 구분열 1개)

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

		// set format
		SeedXSSFUtil.copyHeadlineCubeFormat(workbook, sheetNumber, countHeadLine, list.size(), dataCountInRow, dataOffsetCell);
		
		int curRow = 0, curCol = 0;
		XSSFSheet sheet = workbook.getSheetAt(sheetNumber);
		for (int i=0;i<list.size();i++)
		{
			Payroll6InPageDto data = list.get(i);
			curRow = Math.floorDiv(i, dataCountInRow) * countHeadLine;
			curCol = Math.floorMod(i, dataCountInRow)*dataOffsetCell;
			
			////////////////////////////////////////////
			// data setting - start
			

			//NO.
			sheet.getRow(curRow+0).getCell(curCol+1).setCellValue(i+1); //1
			if (null != data.getDefinedName()) {
				sheet.getRow(curRow+0).getCell(curCol+2).setCellValue(data.getDefinedName());
			}
			if (null != data.getKoreanName()) {
				sheet.getRow(curRow+0).getCell(curCol+3).setCellValue(data.getKoreanName());
			}
			if (null != data.getBasicAmount()) {
				sheet.getRow(curRow+0).getCell(curCol+4).setCellValue(data.getBasicAmount());
			}

			//44875
			if (null != data.getHireDate()) {
				if ("".equals(data.getHireDiv())) {
					; // default color - do nothing
				} else  if ("RETIRE".equals(data.getHireDiv())) {
					sheet.getRow(curRow+1).getCell(curCol+0).setCellStyle(styleLightGreen);
				} else if ("NEW".equals(data.getHireDiv())) {
					sheet.getRow(curRow+1).getCell(curCol+0).setCellStyle(styleLemonChiffon);
				}
				sheet.getRow(curRow+1).getCell(curCol+0).setCellValue(data.getHireDate());
			}
			//연봉
			if (null != data.getBasicAmount()) {
				sheet.getRow(curRow+0).getCell(curCol+4).setCellValue(data.getBasicAmount()*12);
			}

			//기본급
			if (null != data.getRtTotalTime()) {
				sheet.getRow(curRow+2).getCell(curCol+2).setCellValue(data.getRtTotalTime());
			}
			if (null != data.getBasicAmount()) {
				sheet.getRow(curRow+2).getCell(curCol+3).setCellValue(data.getBasicAmount());
			}
			//연차수당
			//연장1
			//주간
			if (null != data.getRtOverDayTimeHours()) {
				sheet.getRow(curRow+4).getCell(curCol+2).setCellValue(data.getRtOverDayTimeHours());
			}
			if (null != data.getOvertimeAllowance01()) {
				sheet.getRow(curRow+4).getCell(curCol+3).setCellValue(data.getOvertimeAllowance01());
			}
			//야간
			if (null != data.getRtOverNightTimeHours()) {
				sheet.getRow(curRow+5).getCell(curCol+2).setCellValue(data.getRtOverNightTimeHours());
			}
			//연장2
			//포괄
			if (null != data.getOvertimeAllowance02()) {
				sheet.getRow(curRow+6).getCell(curCol+3).setCellValue(data.getOvertimeAllowance02());
			}
			//야간1
			//주간
			if (null != data.getRtNSDayTimeHours()) {
				sheet.getRow(curRow+7).getCell(curCol+2).setCellValue(data.getRtNSDayTimeHours());
			}
			if (null != data.getNightAllowance01()) {
				sheet.getRow(curRow+7).getCell(curCol+3).setCellValue(data.getNightAllowance01());
			}
			//야간
			if (null != data.getRtNSNightTimeHours()) {
				sheet.getRow(curRow+8).getCell(curCol+2).setCellValue(data.getRtNSNightTimeHours());
			}
			//야간2
			//포괄
			if (null != data.getNightAllowance02()) {
				sheet.getRow(curRow+9).getCell(curCol+3).setCellValue(data.getNightAllowance02());
			}
			//휴일1
			//토요
			if (null != data.getRtHolidaySaturday01()) {
				sheet.getRow(curRow+10).getCell(curCol+2).setCellValue(data.getRtHolidaySaturday01());
			}
			if (null != data.getHolidayAllowance01()) {
				sheet.getRow(curRow+10).getCell(curCol+3).setCellValue(data.getHolidayAllowance01());
			}
			//일요
			if (null != data.getRtHolidaySunday01()) {
				sheet.getRow(curRow+11).getCell(curCol+2).setCellValue(data.getRtHolidaySunday01());
			}
			//휴일2
			//포괄
			if (null != data.getHolidayAllowance02()) {
				sheet.getRow(curRow+12).getCell(curCol+3).setCellValue(data.getHolidayAllowance02());
			}
			//연차사용
			if (null != data.getRtAnnualLeaveUsedDay()) {
				sheet.getRow(curRow+13).getCell(curCol+1).setCellValue(data.getRtAnnualLeaveUsedDay());
			}
			if (null != data.getAnnualLeave()) {
				sheet.getRow(curRow+13).getCell(curCol+2).setCellValue(data.getAnnualLeave());
			}
			if (null != data.getAnnualAllowance()) {
				sheet.getRow(curRow+13).getCell(curCol+3).setCellValue(data.getAnnualAllowance());
			}
			//반차사용
			if (null != data.getRtHalfDay()) {
				sheet.getRow(curRow+14).getCell(curCol+1).setCellValue(data.getRtHalfDay());
			}
			if (null != data.getRtHalfTime()) {
				sheet.getRow(curRow+14).getCell(curCol+2).setCellValue(data.getRtHalfTime());
			}
			//halfTimeAllowance
			//지각횟수
			if (null != data.getRtLateDay()) {
				sheet.getRow(curRow+15).getCell(curCol+1).setCellValue(data.getRtLateDay());
			}
			if (null != data.getRtLateTime()) {
				sheet.getRow(curRow+15).getCell(curCol+2).setCellValue(data.getRtLateTime());
			}
			//초과상여
			//합 계
			if (null != data.getTotalSalary()) {
				sheet.getRow(curRow+17).getCell(curCol+3).setCellValue(data.getTotalSalary());
			}
			
			// data setting - end
			////////////////////////////////////////////
		}
		
		return true;
	}
		
	private boolean setParttimePayroll6InPageList(XSSFWorkbook workbook, List<Payroll6InPageDto> list) throws IOException
	{
		final int sheetNumber    = 1;
		final int countHeadLine  = 18; // 출력 포멧의 라인수
		final int dataCountInRow = 3;  // 1열에 출력 포멧의 개수
		final int dataOffsetCell = 6;  // 출력 포멧의 열 수 (+ 구분열 1개)

//		final XSSFCellStyle styleWhite = workbook.createCellStyle();
//		styleWhite.setFillForegroundColor(IndexedColors.WHITE.getIndex());  // 배경색
//		styleWhite.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//		styleWhite.setShrinkToFit(true);
		XSSFCellStyle styleLightGreen = workbook.createCellStyle();
		styleLightGreen.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());  // 배경색
		styleLightGreen.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleLightGreen.setShrinkToFit(true);
		XSSFCellStyle styleLemonChiffon = workbook.createCellStyle();
		styleLemonChiffon.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());  // 배경색
		styleLemonChiffon.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleLemonChiffon.setShrinkToFit(true);

		// set format
		SeedXSSFUtil.copyHeadlineCubeFormat(workbook, sheetNumber, countHeadLine, list.size(), dataCountInRow, dataOffsetCell);

		int curRow = 0, curCol = 0;
		XSSFSheet sheet = workbook.getSheetAt(sheetNumber);
		for (int i=0;i<list.size();i++)
		{
			Payroll6InPageDto data = list.get(i);
			curRow = Math.floorDiv(i, dataCountInRow) * countHeadLine;
			curCol = Math.floorMod(i, dataCountInRow)*dataOffsetCell;

			////////////////////////////////////////////
			// data setting - start
			
			//NO.
			sheet.getRow(curRow).getCell(curCol+1).setCellValue(i+1); //1
			if (null != data.getDefinedName()) {
				sheet.getRow(curRow+0).getCell(curCol+2).setCellValue(data.getDefinedName());
			}
			if (null != data.getKoreanName()) {
				sheet.getRow(curRow+0).getCell(curCol+3).setCellValue(data.getKoreanName());
			}
			//hourlyPay*8
			if (null != data.getHourlyPay()) {
				sheet.getRow(curRow+1).getCell(curCol+4).setCellValue(data.getHourlyPay()*8);
			}
			if (null != data.getHireDate()) {
				sheet.getRow(curRow+1).getCell(curCol+0).setCellValue(data.getHireDate());
			}
			if (null != data.getHourlyPay()) {
				sheet.getRow(curRow+1).getCell(curCol+4).setCellValue(data.getHourlyPay());
			}
			//기본급
			if (null != data.getRtTotalTime()) {
				sheet.getRow(curRow+2).getCell(curCol+2).setCellValue(data.getRtTotalTime());
			}
			if (null != data.getBasicAmount()) {
				sheet.getRow(curRow+2).getCell(curCol+3).setCellValue(data.getBasicAmount());
			}
			//연차수당
			if (null != data.getAnnualAllowance()) {
				sheet.getRow(curRow+3).getCell(curCol+3).setCellValue(data.getAnnualAllowance());
			}
			//연차수당(정산)
			if (null != data.getAnnualLeave()) {
				sheet.getRow(curRow+4).getCell(curCol+2).setCellValue(data.getAnnualLeave());
			}
			if (null != data.getAnnualAllowance()) {
				sheet.getRow(curRow+4).getCell(curCol+3).setCellValue(data.getAnnualAllowance());
			}
			//연장
			//주간
			if (null != data.getRtOverDayTimeHours()) {
				sheet.getRow(curRow+5).getCell(curCol+2).setCellValue(data.getRtOverDayTimeHours());
			}
			if (null != data.getOvertimeAllowance01()) {
				sheet.getRow(curRow+5).getCell(curCol+3).setCellValue(data.getOvertimeAllowance01());
			}
			//야간
			if (null != data.getRtOverNightTimeHours()) {
				sheet.getRow(curRow+6).getCell(curCol+2).setCellValue(data.getRtOverNightTimeHours());
			}
			//야간 수당
			//주간
			if (null != data.getRtNSDayTimeHours()) {
				sheet.getRow(curRow+7).getCell(curCol+2).setCellValue(data.getRtNSDayTimeHours());
			}
			if (null != data.getNightAllowance01()) {
				sheet.getRow(curRow+7).getCell(curCol+3).setCellValue(data.getNightAllowance01());
			}
			//야간
			if (null != data.getRtNSNightTimeHours()) {
				sheet.getRow(curRow+8).getCell(curCol+2).setCellValue(data.getRtNSNightTimeHours());
			}
			//휴일
			//토요
			if (null != data.getRtHolidaySaturday01()) {
				sheet.getRow(curRow+9).getCell(curCol+2).setCellValue(data.getRtHolidaySaturday01());
			}
			if (null != data.getHolidayAllowance01()) {
				sheet.getRow(curRow+9).getCell(curCol+3).setCellValue(data.getHolidayAllowance01());
			}
			//일요
			if (null != data.getRtHolidaySunday01()) {
				sheet.getRow(curRow+10).getCell(curCol+2).setCellValue(data.getRtHolidaySunday01());
			}
			//보조금
			//교통비
			if (null != data.getRtTransportation()) {
				sheet.getRow(curRow+11).getCell(curCol+2).setCellValue(data.getRtTransportation());
			}
			if (null != data.getTransportationAmount()) {
				sheet.getRow(curRow+11).getCell(curCol+3).setCellValue(data.getTransportationAmount());
			}
			//식대
			if (null != data.getRtMeal()) {
				sheet.getRow(curRow+12).getCell(curCol+2).setCellValue(data.getRtMeal());
			}
			if (null != data.getMealsAmount()) {
				sheet.getRow(curRow+12).getCell(curCol+3).setCellValue(data.getMealsAmount());
			}
			//반차
			if (null != data.getRtHalfDay()) {
				sheet.getRow(curRow+13).getCell(curCol+1).setCellValue(data.getRtHalfDay());
			}
			if (null != data.getRtHalfTime()) {
				sheet.getRow(curRow+13).getCell(curCol+2).setCellValue(data.getRtHalfTime());
			}
			//halfTimeAllowance
			//조퇴
			if (null != data.getRtEarlyLeaveDay()) {
				sheet.getRow(curRow+14).getCell(curCol+1).setCellValue(data.getRtEarlyLeaveDay());
			}
			if (null != data.getRtEarlyLeaveTime()) {
				sheet.getRow(curRow+14).getCell(curCol+2).setCellValue(data.getRtEarlyLeaveTime());
			}
			if (null != data.getAnnualAllowance()) {
				sheet.getRow(curRow+14).getCell(curCol+3).setCellValue(data.getAnnualAllowance());
			}
			//지각
			if (null != data.getRtLateDay()) {
				sheet.getRow(curRow+15).getCell(curCol+1).setCellValue(data.getRtLateDay());
			}
			if (null != data.getRtLateTime()) {
				sheet.getRow(curRow+15).getCell(curCol+2).setCellValue(data.getRtLateTime());
			}
			//halfTimeAllowance
			//외출
			if (null != data.getRtOuterDay()) {
				sheet.getRow(curRow+16).getCell(curCol+1).setCellValue(data.getRtOuterDay());
			}
			if (null != data.getRtOuterTime()) {
				sheet.getRow(curRow+16).getCell(curCol+2).setCellValue(data.getRtOuterTime());
			}
			//합 계
			if (null != data.getTotalSalary()) {
				sheet.getRow(curRow+17).getCell(curCol+3).setCellValue(data.getTotalSalary()); //2115001
			}

			// data setting - end	
		}
		
		return true;
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
		        SimpleDateFormat format = new SimpleDateFormat( "yyyyMMdd_HHmmss");
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
