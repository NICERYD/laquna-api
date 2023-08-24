package kr.co.seedit.domain.hr.application;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.extensions.XSSFHeaderFooter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import kr.co.seedit.domain.mapper.seedit.PayrollDao;
import kr.co.seedit.global.common.dto.ResponseDto;
import kr.co.seedit.global.utils.SeedXSSFUtil;
import kr.co.seedit.global.utils.StringUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PayrollService {

	private static final Logger logger = LoggerFactory.getLogger(PayrollService.class);

	final private String sheetName = "급여표";
	final private String PAYROLL_XLSX_FILE = "classpath:hr/payrollSample.xlsx";
	final private String PAYROLL_FULLTIME_FILE = "classpath:hr/payrollParttime.xlsx";
	final private String PAYROLL_PARTTIME_FILE = "classpath:hr/payrollFulltime.xlsx";
	final private String PAYROLL_INPUT_FILE = "classpath:hr/payroll.xlsx";
	
	//test
	final private int sampleCellPoint = 0;
	final private int sampleCellCount = 5;

//    private final PayrollDao payrollDao;
	@Autowired
    PayrollDao payrollDao;

	@Autowired
	ResourceLoader resourceLoader;
	
	final private int headRowFulltime  = 0 ; // point is 1 less than column number
	final private int countHeadLineFulltime = 17;
	final private int headRowParttime  = 17; // point is 1 less than column number
	final private int countHeadLineParttime = 18;
	final private int dataCountInRow   = 3;
	final private int dataOffsetCell   = 6;

//	@Transactional
	public ResponseDto payrollReportFull(String in/* PayrollDto payrollDto */, HttpServletResponse response)
			throws Exception {

//		final int headRowFulltime  = 0 ; // point is 1 less than column number
//		final int countHeadLineFulltime = 17;
//		final int headRowParttime  = 17; // point is 1 less than column number
//		final int countHeadLineParttime = 18;
//		final int dataCountInRow   = 3;
//		final int dataOffsetCell   = 6;

		ResponseDto responseDto = ResponseDto.builder().build();

		Resource resource = resourceLoader.getResource(PAYROLL_INPUT_FILE);
		InputStream fis = resource.getInputStream();
		XSSFWorkbook workbook = new XSSFWorkbook(fis);

		// get full-time user data
		
		// full-time format
		SeedXSSFUtil.copyHeadlineCubeFormat(workbook, 0, countHeadLineFulltime, 8, dataCountInRow, dataOffsetCell);
		
		// set full-time user information
		
		
		
		// get set part-time user data
		
		// part-time format
		SeedXSSFUtil.copyHeadlineCubeFormat(workbook, 1, countHeadLineParttime, 10, dataCountInRow, dataOffsetCell);

		// set part-time user information

//		XSSFSheet sheet = workbook.getSheetAt(0);
//		XSSFRow curRow;
//		XSSFCell curCell;
//
//		int rowindex = 5;
//		int cellindex = 1;
//		int no = 1;

//        // test
//        System.out.print("sheet.getLastRowNum():"+sheet.getLastRowNum());
//        System.out.print("oldSheet.getFirstRowNum():"+sheet.getFirstRowNum());
//        for (int i=sheet.getLastRowNum();i>=0;i++)
//        {
//            for (int j=sheet.get();j>=0;j++)
//            {
//	        	XSSFRow row = sheet.getRow(i);
//	        	XSSFCell cell = row.getCell(i);
//	        	cell.get
//	        	switch(i) {
//	        	case 1:cell.setCellValue(false);break;
//	        	case 2:cell.setCellValue(2);break;
//	        	case 3:cell.setCellValue("3");break;
//	        	case 4:cell.setCellValue("4");break;
//	        	case 5:cell.setCellValue(LocalDate.now());break;
//        	}
//        }

		// Excel Data Select
//        List<ReportPayrollDto> reportPayrollDtoList = new ArrayList<>();
//        reportPayrollDtoList = reportDao.findPayroll(payrollDto);

//        XSSFWorkbook workbook = new XSSFWorkbook();
//        XSSFSheet sheet = workbook.createSheet(payrollDto.getYyyymm() + "SalaryUpload");
//        int rowindex = 0;
//        int cellindex = 0;

		// Data Insert
//        for (ReportPayrollDto m : reportPayrollDtoList) {
//            cellindex = 1;
//            XSSFRow row = sheet.createRow(rowindex++);
//            row.createCell(cellindex++).setCellValue(no++);
//            row.createCell(cellindex++).setCellValue(m.getKoreanName());
//            row.createCell(cellindex++).setCellValue(m.getDefinedName());
//            row.createCell(cellindex++).setCellValue(m.getDepartmentName());
//            row.createCell(cellindex++).setCellValue(m.getHireDate());
//            row.createCell(cellindex++).setBlank();
//            row.createCell(cellindex++).setBlank();
//            row.createCell(cellindex++).setBlank();
//            row.createCell(cellindex++).setBlank();
//            row.createCell(cellindex++).setBlank();
//            row.createCell(cellindex++).setBlank();
//            row.createCell(cellindex++).setBlank();
//            row.createCell(cellindex++).setCellValue(m.getAnnualLeaveUsedDay());
//            row.createCell(cellindex++).setCellValue(m.getAnnualLeaveUsed());
//            row.createCell(cellindex++).setBlank();
//            row.createCell(cellindex++).setCellValue(m.getOverTime1());
//            row.createCell(cellindex++).setBlank();
//            row.createCell(cellindex++).setCellValue(m.getOverTime2());
//            row.createCell(cellindex++).setBlank();
//            row.createCell(cellindex++).setCellValue(m.getNightShift1());
//            row.createCell(cellindex++).setBlank();
//            row.createCell(cellindex++).setCellValue(m.getNightShift2());
//            row.createCell(cellindex++).setBlank();
//            row.createCell(cellindex++).setCellValue(m.getDayTimeHours());
//            row.createCell(cellindex++).setBlank();
//            row.createCell(cellindex++).setCellValue(m.getNightTimeHours());
//            row.createCell(cellindex++).setBlank();
//            row.createCell(cellindex++).setCellValue(m.getHolidaySaturday1());
//            row.createCell(cellindex++).setBlank();
//            row.createCell(cellindex++).setCellValue(m.getHolidaySunday1());
//            row.createCell(cellindex++).setBlank();
//            row.createCell(cellindex++).setCellValue(m.getHoliday2());
//            row.createCell(cellindex++).setBlank();
//            row.createCell(cellindex++).setCellValue(m.getTransportation());
//            row.createCell(cellindex++).setBlank();
//            row.createCell(cellindex++).setCellValue(m.getMeal());
//            row.createCell(cellindex++).setBlank();
//            row.createCell(cellindex++).setBlank();		//보조금
//            row.createCell(cellindex++).setCellValue(m.getOther());
//            row.createCell(cellindex++).setCellValue(m.getHalfDay());
//            row.createCell(cellindex++).setCellValue(m.getHalfTime());
//            row.createCell(cellindex++).setBlank();
//            row.createCell(cellindex++).setCellValue(m.getEarlyLeaveDay());
//            row.createCell(cellindex++).setCellValue(m.getEarlyLeaveTime());
//            row.createCell(cellindex++).setBlank();
//            row.createCell(cellindex++).setCellValue(m.getLateDay());
//            row.createCell(cellindex++).setCellValue(m.getLateTime());
//            row.createCell(cellindex++).setBlank();
//            row.createCell(cellindex++).setBlank();
//        }

		try {
//          File xlsxFile = new File("C:/Users/admin/Downloads/" + payrollDto.getYyyymm() + "payroll" + ".xlsx");
	        Date time = new Date();
	        SimpleDateFormat format = new SimpleDateFormat( "yyyyMMdd_HHmmss");
	        String time1 = format.format(time);
	        String dir = System.getProperty("user.home");
	        String fileName = new StringBuilder().append(dir).append("/downloads/payRoll_").append(time1).append(".xlsx").toString(); 
	        File xlsxFile = new File(fileName);
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

//		responseDto.setSuccess(false);
//		responseDto.setCode("");
//		responseDto.setMessage("");

		return responseDto;
	}

//	@Transactional
	public ResponseDto payrollReport(Map<String, Object> in, HttpServletResponse response)
			throws Exception {
		
		ResponseDto responseDto = ResponseDto.builder().build();

		Resource resource = resourceLoader.getResource(PAYROLL_INPUT_FILE);
		InputStream fis = resource.getInputStream();
		XSSFWorkbook workbook = new XSSFWorkbook(fis);

		// get full-time user data
		List<Map<String, Object>> listFulltime = payrollDao.getFulltimePayrollList(in);
		
		// set full-time user information
		setFulltimePayrollDatas(workbook, listFulltime);

		
		// get set part-time user data
		List<Map<String, Object>> listParttime = payrollDao.getParttimePayrollList(in);
		
		// set part-time user information
//		setParttimePayrollDatas(workbook, listParttime);

		
		// print header setting
		XSSFHeaderFooter header = (XSSFHeaderFooter) workbook.getSheetAt(0).getHeader();
		header.setLeft(in.get("yyyymm").toString().substring(4,6)+"월 급여");
		header = (XSSFHeaderFooter) workbook.getSheetAt(1).getHeader();
		header.setLeft(StringUtils.getNumberString(in.get("yyyymm").toString().substring(4,6)) +"월 급여");
		
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
	
	@SuppressWarnings("unused")
	private static void setCelValueFromMap(final XSSFSheet sheet, final int row, final int cell, 
			final Object value) {
		try {
			if (null != value) {
				sheet.getRow(row).getCell(cell).setCellValue(value.toString()); //대리
			}
		}
		catch (Exception e) {
			logger.debug("");
		}
	}

	private static String getCommaString(final Object value)
	{
		String rst;
		String def = "0";
		try {
			final DecimalFormat df1 = new DecimalFormat("#,##0");
			if (null == value)
				return def;
			
			rst = df1.format(value);
			
			if ("0".equals(rst))
					return def;
		} catch (IllegalArgumentException | NullPointerException e) {
			rst = def;
		}
		return rst;
	}

	private static String getCommaStringWithDefault(final Object value, final String def)
	{
		String rst;
		try {
			final DecimalFormat df1 = new DecimalFormat("#,##0");
			if (null == value)
				return def;
			
			rst = df1.format(value);
			
			if ("0".equals(rst))
					return def;
		} catch (IllegalArgumentException | NullPointerException e) {
			rst = def;
		}
		return rst;
	}

	private boolean setFulltimePayrollDatas(XSSFWorkbook workbook, List<Map<String, Object>> datas) throws IOException
	{
		final int sheetNumber    = 0;
		final int countHeadLine  = 17; // 출력 포멧의 라인수
		final int dataCountInRow = 3;  // 1열에 출력 포멧의 개수
		final int dataOffsetCell = 6;  // 출력 포멧의 열 수 (+ 구분열 1개)

		// set format
		SeedXSSFUtil.copyHeadlineCubeFormat(workbook, sheetNumber, countHeadLine, datas.size(), dataCountInRow, dataOffsetCell);
		
		final XSSFCellStyle styleLightGreen = workbook.createCellStyle();
		styleLightGreen.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());  // 배경색
		styleLightGreen.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		final XSSFCellStyle styleLemonChiffon = workbook.createCellStyle();
		styleLightGreen.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());  // 배경색
		styleLightGreen.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		

		int curRowPoint = 0, curColumnPoint = 0;
		XSSFSheet sheet = workbook.getSheetAt(sheetNumber);
		for (int i=0;i<datas.size();i++)
		{
			Map<String, Object> data = datas.get(i);
			curRowPoint = Math.floorDiv(i, dataCountInRow) * countHeadLine;
			curColumnPoint = Math.floorMod(i, dataCountInRow)*dataOffsetCell;
			

			////////////////////////////////////////////
			// data setting - start
			
			//NO.
			setCelValueFromMap(sheet, curRowPoint, curColumnPoint+1, i+1); //1
			setCelValueFromMap(sheet, curRowPoint, curColumnPoint+2, getCommaString(data.get("definedName"))); //대리
			setCelValueFromMap(sheet, curRowPoint, curColumnPoint+3, data.get("koreanName")); //김빨강
			setCelValueFromMap(sheet, curRowPoint, curColumnPoint+4, getCommaString(data.get("monthSalary"))); //2000000

			//44875
			if ("".equals(data.get("hire_div"))) {
				; // default color - do nothing
			} else if ("RETIRE".equals(data.get("hire_div"))) {
				sheet.getRow(curRowPoint+1).getCell(curColumnPoint+1).setCellStyle(styleLightGreen);
			}
			else if ("NEW".equals(data.get("hire_div"))) {
				sheet.getRow(curRowPoint+1).getCell(curColumnPoint+1).setCellStyle(styleLemonChiffon);
			}
			setCelValueFromMap(sheet, curRowPoint+1, curColumnPoint+1, data.get("hireDate")); // 입사일자			
			setCelValueFromMap(sheet, curRowPoint+1, curColumnPoint+4, getCommaString(data.get("yearSalary"))); //26000000
			

			//기본급
			setCelValueFromMap(sheet, curRowPoint+2, curColumnPoint+1, data.get("hourly_pay")); //6688.96321070234
			setCelValueFromMap(sheet, curRowPoint+2, curColumnPoint+2, data.get("totalTime")); //209
			setCelValueFromMap(sheet, curRowPoint+2, curColumnPoint+3, getCommaStringWithDefault(data.get("basicSalary"),"-")); //1397994

			//연차정산

			//연장1
			setCelValueFromMap(sheet, curRowPoint+4, curColumnPoint+2, data.get("overtime1Standard")); //2
			setCelValueFromMap(sheet, curRowPoint+4, curColumnPoint+3, getCommaStringWithDefault(data.get("overtime01Amt"),"-")); //40000

			//연장2 //포괄
			setCelValueFromMap(sheet, curRowPoint+5, curColumnPoint+2, data.get("overtime2Standard")); //52
			setCelValueFromMap(sheet, curRowPoint+5, curColumnPoint+3, getCommaStringWithDefault(data.get("overtime02Amt"),"-")); //521739

			//야간1
			setCelValueFromMap(sheet, curRowPoint+6, curColumnPoint+2, data.get("nightShift1Standard")); //
			setCelValueFromMap(sheet, curRowPoint+6, curColumnPoint+3, getCommaStringWithDefault(data.get("night01Allowance"),"-")); //0

			//야간2 //포괄
			setCelValueFromMap(sheet, curRowPoint+7, curColumnPoint+2, data.get("nightShift2Standard")); //24
			setCelValueFromMap(sheet, curRowPoint+7, curColumnPoint+3, getCommaStringWithDefault(data.get("night02Allowance"),"-")); //80268

			//휴일1 //토요
			setCelValueFromMap(sheet, curRowPoint+8, curColumnPoint+2, data.get("holidaySaturday1Standard")); //1
			setCelValueFromMap(sheet, curRowPoint+8, curColumnPoint+3, getCommaStringWithDefault(data.get("holiday01SaturdayAmt"),"-")); //50000

			// //일요
			setCelValueFromMap(sheet, curRowPoint+9, curColumnPoint+2, data.get("holidaySunday1Standard")); //0.5
			setCelValueFromMap(sheet, curRowPoint+9, curColumnPoint+3, getCommaStringWithDefault(data.get("holiday01SundayAmt"),"-")); //25000

			//휴일2 //포괄
			setCelValueFromMap(sheet, curRowPoint+10, curColumnPoint+2, data.get("holiday2Standard")); //0
			setCelValueFromMap(sheet, curRowPoint+10, curColumnPoint+3, getCommaStringWithDefault(data.get("holiday02Amt"),"-")); //0

			//연차사용
			setCelValueFromMap(sheet, curRowPoint+11, curColumnPoint+1, data.get("annualLeaveUsedDay")); //3/17
			setCelValueFromMap(sheet, curRowPoint+11, curColumnPoint+2, data.get("annualLeaveUsed")); //1

			//반차사용
			setCelValueFromMap(sheet, curRowPoint+12, curColumnPoint+1, data.get("halfDay")); //지각3회로 오전반차
			setCelValueFromMap(sheet, curRowPoint+12, curColumnPoint+2, data.get("halfTime")); //1

			//지각횟수
			setCelValueFromMap(sheet, curRowPoint+13, curColumnPoint+1, data.get("lateDay")); //3/20,21,22
			setCelValueFromMap(sheet, curRowPoint+13, curColumnPoint+2, data.get("lateTime")); //3

			//초과상여
//			setCelValueFromMap(sheet, curRowPoint+14, curColumnPoint+1, data.get("")); //
//			setCelValueFromMap(sheet, curRowPoint+14, curColumnPoint+2, data.get("")); //
//			setCelValueFromMap(sheet, curRowPoint+14, curColumnPoint+3, data.get(""),"-")); //
//			setCelValueFromMap(sheet, curRowPoint+14, curColumnPoint+4, data.get("")); //

			//합 계
			setCelValueFromMap(sheet, curRowPoint+16, curColumnPoint+3, getCommaStringWithDefault(data.get("totalSalary"),"-")); //2115001

			// test
//			for (int rownum = 0;rownum<17;rownum++) {
//System.out.print("    row="+curRowPoint+rownum+", Cell="+curColumnPoint+", value="+curRowPoint+"/"+rownum+"-"+curColumnPoint+"\n");
//				try { sheet.getRow(curRowPoint+rownum).getCell(curColumnPoint+0).setCellValue(curRowPoint+"/"+rownum+"-"+(curColumnPoint+0)); } catch (Exception e) { ;}
//				try { sheet.getRow(curRowPoint+rownum).getCell(curColumnPoint+1).setCellValue(curRowPoint+"/"+rownum+"-"+(curColumnPoint+1)); } catch (Exception e) { ;}
//				try { sheet.getRow(curRowPoint+rownum).getCell(curColumnPoint+2).setCellValue(curRowPoint+"/"+rownum+"-"+(curColumnPoint+2)); } catch (Exception e) { ;}
//				try { sheet.getRow(curRowPoint+rownum).getCell(curColumnPoint+3).setCellValue(curRowPoint+"/"+rownum+"-"+(curColumnPoint+3)); } catch (Exception e) { ;}
//				try { sheet.getRow(curRowPoint+rownum).getCell(curColumnPoint+4).setCellValue(curRowPoint+"/"+rownum+"-"+(curColumnPoint+4)); } catch (Exception e) { ;}
//			}
			
			// data setting - end
		}
		
		return true;
	}
		
	private boolean setParttimePayrollDatas(XSSFWorkbook workbook, List<Map<String, Object>> datas) throws IOException
	{
		final int sheetNumber    = 1;
		final int countHeadLine  = 18; // 출력 포멧의 라인수
		final int dataCountInRow = 3;  // 1열에 출력 포멧의 개수
		final int dataOffsetCell = 6;  // 출력 포멧의 열 수 (+ 구분열 1개)

		// set format
		SeedXSSFUtil.copyHeadlineCubeFormat(workbook, sheetNumber, countHeadLine, datas.size(), dataCountInRow, dataOffsetCell);

		final XSSFCellStyle styleLightGreen = workbook.createCellStyle();
		styleLightGreen.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());  // 배경색
		styleLightGreen.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		final XSSFCellStyle styleLemonChiffon = workbook.createCellStyle();
		styleLightGreen.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());  // 배경색
		styleLightGreen.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		int curRowPoint = 0, curColumnPoint = 0;
		XSSFSheet sheet = workbook.getSheetAt(sheetNumber);
		for (int i=0;i<datas.size();i++)
		{
			Map<String, Object> data = datas.get(i);
			curRowPoint = Math.floorDiv(i, dataCountInRow) * countHeadLine;
			curColumnPoint = Math.floorMod(i, dataCountInRow)*dataOffsetCell;

			////////////////////////////////////////////
			// data setting - start
			
			//NO.
			setCelValueFromMap(sheet, curRowPoint, curColumnPoint+1, i+1); //1
			setCelValueFromMap(sheet, curRowPoint, curColumnPoint+2, data.get("definedName")); //대리
			setCelValueFromMap(sheet, curRowPoint, curColumnPoint+3, getCommaStringWithDefault(data.get("koreanName"),"-")); //김빨강
			setCelValueFromMap(sheet, curRowPoint, curColumnPoint+4, getCommaString(data.get("day_pay"))); //2000000

			//44875
			if ("".equals(data.get("hire_div"))) {
				; // default color - do nothing
			} else if ("RETIRE".equals(data.get("hire_div"))) {
				sheet.getRow(curRowPoint+1).getCell(curColumnPoint+1).setCellStyle(styleLightGreen);
			}
			else if ("NEW".equals(data.get("hire_div"))) {
				sheet.getRow(curRowPoint+1).getCell(curColumnPoint+1).setCellStyle(styleLemonChiffon);
			}
			setCelValueFromMap(sheet, curRowPoint+1, curColumnPoint+1, data.get("hireDate")); //
			setCelValueFromMap(sheet, curRowPoint+1, curColumnPoint+4, getCommaString(data.get("hourlyPay"))); //26000000

			//기본급
			setCelValueFromMap(sheet, curRowPoint+2, curColumnPoint+2, data.get("totalTime")); //209
			setCelValueFromMap(sheet, curRowPoint+2, curColumnPoint+3, getCommaStringWithDefault(data.get("basicSalary"),"-")); //1397994

			//연차수당
			setCelValueFromMap(sheet, curRowPoint+3, curColumnPoint+2, data.get("annualLeaveUsedDay"));
			setCelValueFromMap(sheet, curRowPoint+3, curColumnPoint+3, getCommaStringWithDefault(data.get("annualLeaveAmt"),"-"));
			
			//연차수당(정산)
//			setCelValueFromMap(sheet, curRowPoint+3, curColumnPoint+2, data.get("annualLeaveUsed"));
//			setCelValueFromMap(sheet, curRowPoint+3, curColumnPoint+3, getCommaStringWithDefault(data.get("annualLeaveAmt"),"-"));

			//연장
			setCelValueFromMap(sheet, curRowPoint+5, curColumnPoint+2, data.get("overtime1")); //52
			setCelValueFromMap(sheet, curRowPoint+5, curColumnPoint+3, getCommaStringWithDefault(data.get("overtime01Amt"),"-")); //521739

			//야간수당 (주간)
			setCelValueFromMap(sheet, curRowPoint+6, curColumnPoint+	2, data.get("daytimeHours")); //주간조야간
			setCelValueFromMap(sheet, curRowPoint+6, curColumnPoint+3, getCommaStringWithDefault(data.get("daytimeHoursAmt"),"-")); //

			//야간수당 (야간)
			setCelValueFromMap(sheet, curRowPoint+7, curColumnPoint+2, data.get("nighttimeHours")); //야간조야간
			setCelValueFromMap(sheet, curRowPoint+7, curColumnPoint+3, getCommaStringWithDefault(data.get("nighttimeHoursAmt"),"-")); //80268

			//휴일(토요)
			setCelValueFromMap(sheet, curRowPoint+8, curColumnPoint+2, data.get("holidaySaturday1")); //1
			setCelValueFromMap(sheet, curRowPoint+8, curColumnPoint+3, getCommaStringWithDefault(data.get("holiday01SaturdayAmt"),"-")); //50000

			//휴일(일요)
			setCelValueFromMap(sheet, curRowPoint+9, curColumnPoint+2, data.get("holidaySunday1")); //0.5
			setCelValueFromMap(sheet, curRowPoint+9, curColumnPoint+3, getCommaStringWithDefault(data.get("holiday01SundayAmt"),"-")); //25000

			//보조금(교통비)
			setCelValueFromMap(sheet, curRowPoint+10, curColumnPoint+2, data.get("transportation"));
			setCelValueFromMap(sheet, curRowPoint+10, curColumnPoint+3, getCommaStringWithDefault(data.get("attribute15"),"-"));

			//보조금(식대)
			setCelValueFromMap(sheet, curRowPoint+11, curColumnPoint+1, data.get("meal")); //3/17
			setCelValueFromMap(sheet, curRowPoint+11, curColumnPoint+2, data.get("attribute16")); //1

			//반차사용
			setCelValueFromMap(sheet, curRowPoint+12, curColumnPoint+1, data.get("halfDay"));
			setCelValueFromMap(sheet, curRowPoint+12, curColumnPoint+2, data.get("halfTime"));
			setCelValueFromMap(sheet, curRowPoint+12, curColumnPoint+3, data.get("halfTimeAmt"));
			setCelValueFromMap(sheet, curRowPoint+14, curColumnPoint+3, getCommaStringWithDefault(data.get("halfTimeAmt"),"0")); //

			//조퇴
			setCelValueFromMap(sheet, curRowPoint+13, curColumnPoint+1, data.get("earlyLeaveDay")); //3/20,21,22
			setCelValueFromMap(sheet, curRowPoint+13, curColumnPoint+2, data.get("earlyLeaveTime")); //3
			setCelValueFromMap(sheet, curRowPoint+14, curColumnPoint+3, getCommaStringWithDefault(data.get("earlyLeaveAmt"),"0")); //

			//지각
			setCelValueFromMap(sheet, curRowPoint+14, curColumnPoint+1, data.get("lateDay")); //
			setCelValueFromMap(sheet, curRowPoint+14, curColumnPoint+2, data.get("lateTime")); //
			setCelValueFromMap(sheet, curRowPoint+14, curColumnPoint+3, getCommaStringWithDefault(data.get("lateAmt"),"0")); //

			//합 계
			setCelValueFromMap(sheet, curRowPoint+16, curColumnPoint+3, getCommaStringWithDefault(data.get(""),"-")); //2115001

			// data setting - end	
		}
		
		return true;
	}


}
