package kr.co.seedit.domain.hr.application;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.extensions.XSSFHeaderFooter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import kr.co.seedit.domain.hr.dto.PayrollDto;
import kr.co.seedit.domain.mapper.seedit.PayrollDao;
import kr.co.seedit.global.common.dto.ResponseDto;
import kr.co.seedit.global.utils.SeedXSSFUtil;
import kr.co.seedit.global.utils.StringUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PayrollService {

	private static final Logger logger = LoggerFactory.getLogger(PayrollService.class);

//	final private String sheetName = "급여표";
//	final private String PAYROLL_XLSX_FILE = "classpath:hr/payrollSample.xlsx";
//	final private String PAYROLL_FULLTIME_FILE = "classpath:hr/payrollParttime.xlsx";
//	final private String PAYROLL_PARTTIME_FILE = "classpath:hr/payrollFulltime.xlsx";
	final private String PAYROLL_INPUT_FILE = "classpath:hr/payroll.xlsx";
	
    private final PayrollDao payrollDao;
    private final ResourceLoader resourceLoader;
	
	final private int sheetFulltime  = 0 ; // sheet number
	final private int countHeadLineFulltime = 17;
	final private int sheetParttime  = 17; // sheet number
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
		SeedXSSFUtil.copyHeadlineCubeFormat(workbook, sheetFulltime, countHeadLineFulltime, 8, dataCountInRow, dataOffsetCell);
		
		// set full-time user information
		
		
		
		// get set part-time user data
		
		// part-time format
		SeedXSSFUtil.copyHeadlineCubeFormat(workbook, sheetParttime, countHeadLineParttime, 10, dataCountInRow, dataOffsetCell);

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
		List<PayrollDto> listFulltime = payrollDao.getFulltimePayrollList(in);
		
		// set full-time user information
		setFulltimePayrollList(workbook, listFulltime);

		
		// get set part-time user data
		List<PayrollDto> listParttime = payrollDao.getParttimePayrollList(in);
		
		// set part-time user information
		setParttimePayrollList(workbook, listParttime);

		
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
	
//	@SuppressWarnings("unused")
//	private static void setCellStringFromMap(final XSSFSheet sheet, final int row, final int cell, final Object value) {
//		try {
//			sheet.getRow(row).getCell(cell).setCellValue((String        )value);
//		}
//		catch (Exception e) {
//			logger.debug("setCellStringFromMap(,,,"+value+") Exception: "+e.getMessage());
//		}
//	}

//	@SuppressWarnings("unused")
//	private static void setCellDoubleFromMap(final XSSFSheet sheet, final int row, final int cell, final Object value) {
//		try {
//			double in = BigDecimal.valueOf(value); //(double) value.;
//System.out.println("in:" +value +" > "+ in);
//			sheet.getRow(curRow).getCell(cell).setCellValueImpl(in);
//		}
//		catch (Exception e) {
//			logger.debug("setCellDoubleFromMap(,,,"+value+") Exception: "+e.getMessage());
//		}
//	}

//	private static String final Object value)
//	{
//		String rst;
//		String def = "0";
//		try {
//			final DecimalFormat df1 = new DecimalFormat("#,##0");
//			if (null == value)
//				return def;
//			
//			rst = df1.format(value);
//			
//			if ("0".equals(rst))
//					return def;
//		} catch (IllegalArgumentException | NullPointerException e) {
//			rst = def;
//		}
//		return rst;
//	}

//	private static String final Object value, final String def)
//	{
//		String rst;
//		try {
//			final DecimalFormat df1 = new DecimalFormat("#,##0");
//			if (null == value)
//				return def;
//			
//			rst = df1.format(value);
//			
//			if ("0".equals(rst))
//					return def;
//		} catch (IllegalArgumentException | NullPointerException e) {
//			rst = def;
//		}
//		return rst;
//	}

	private boolean setFulltimePayrollList(XSSFWorkbook workbook, List<PayrollDto> list) throws IOException
	{
		final int sheetNumber    = 0;
		final int countHeadLine  = 17; // 출력 포멧의 라인수
		final int dataCountInRow = 3;  // 1열에 출력 포멧의 개수
		final int dataOffsetCell = 6;  // 출력 포멧의 열 수 (+ 구분열 1개)

		// set format
		SeedXSSFUtil.copyHeadlineCubeFormat(workbook, sheetNumber, countHeadLine, list.size(), dataCountInRow, dataOffsetCell);
		
		int curRow = 0, curCol = 0;
		XSSFSheet sheet = workbook.getSheetAt(sheetNumber);
		for (int i=0;i<list.size();i++)
		{
			PayrollDto data = list.get(i);
			curRow = Math.floorDiv(i, dataCountInRow) * countHeadLine;
			curCol = Math.floorMod(i, dataCountInRow)*dataOffsetCell;
			

			////////////////////////////////////////////
			// data setting - start
			
			//NO.
			sheet.getRow(curRow).getCell(curCol+1).setCellValue(i+1); //1
			if (null != data.getDefinedName()) {
				sheet.getRow(curRow).getCell(curCol+2).setCellValue(data.getDefinedName()); //대리
			}
			if (null != data.getKoreanName()) {
				sheet.getRow(curRow).getCell(curCol+3).setCellValue(data.getKoreanName()); //김빨강
			}
			if (null != data.getMonthSalary()) {
				sheet.getRow(curRow).getCell(curCol+4).setCellValue(data.getMonthSalary()); //2000000
			}

			//44875
			if (null != data.getHireDate()) {
				sheet.getRow(curRow+1).getCell(curCol).setCellValue(data.getHireDate()); // 입사일자
				if ("".equals(data.getHireDiv())) {
					; // default color - do nothing
				} else if ("RETIRE".equals(data.getHireDiv())) {
					sheet.getRow(curRow+1).getCell(curCol).getCellStyle().setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
					sheet.getRow(curRow+1).getCell(curCol).getCellStyle().setFillPattern(FillPatternType.SOLID_FOREGROUND);
				}
				else if ("NEW".equals(data.getHireDiv())) {
					sheet.getRow(curRow+1).getCell(curCol).getCellStyle().setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());
					sheet.getRow(curRow+1).getCell(curCol).getCellStyle().setFillPattern(FillPatternType.SOLID_FOREGROUND);
				}
			}
			if (null != data.getYearSalary()) {
				sheet.getRow(curRow+1).getCell(curCol+4).setCellValue(data.getYearSalary()); //26000000
			}

			//기본급
			if (null != data.getHourlyPay()) {
				sheet.getRow(curRow+2).getCell(curCol+1).setCellValue(data.getHourlyPay()); //6688.96321070234
			}
			if (null != data.getTotalTime()) {
				sheet.getRow(curRow+2).getCell(curCol+2).setCellValue(data.getTotalTime()); //209
			}
			if (null != data.getBasicSalary()) {
				sheet.getRow(curRow+2).getCell(curCol+3).setCellValue(data.getBasicSalary()); //1397994
			}

			//연차정산

			//연장1
			if (null != data.getOvertime1Standard()) {
				sheet.getRow(curRow+4).getCell(curCol+2).setCellValue(data.getOvertime1Standard()); //2
			}
			if (null != data.getOvertime01Amt()) {
				sheet.getRow(curRow+4).getCell(curCol+3).setCellValue(data.getOvertime01Amt()); //40000
			}

			//연장2 //포괄
			if (null != data.getOvertime2Standard()) {
				sheet.getRow(curRow+5).getCell(curCol+2).setCellValue(data.getOvertime2Standard()); //52
			}
			if (null != data.getOvertime02Amt()) {
				sheet.getRow(curRow+5).getCell(curCol+3).setCellValue(data.getOvertime02Amt()); //521739
			}

			//야간1
			if (null != data.getNightShift1Standard()) {
				sheet.getRow(curRow+6).getCell(curCol+2).setCellValue(data.getNightShift1Standard()); //
			}
			if (null != data.getNight01Allowance()) {
				sheet.getRow(curRow+6).getCell(curCol+3).setCellValue(data.getNight01Allowance()); //0
			}

			//야간2 //포괄
			if (null != data.getNightShift2Standard()) {
				sheet.getRow(curRow+7).getCell(curCol+2).setCellValue(data.getNightShift2Standard()); //24
			}
			if (null != data.getNight02Allowance()) {
				sheet.getRow(curRow+7).getCell(curCol+3).setCellValue(data.getNight02Allowance()); //80268
			}

			//휴일1 //토요
			if (null != data.getHolidaySaturday1Standard()) {
				sheet.getRow(curRow+8).getCell(curCol+2).setCellValue(data.getHolidaySaturday1Standard()); //1
			}
			if (null != data.getHoliday01SaturdayAmt()) {
				sheet.getRow(curRow+8).getCell(curCol+3).setCellValue(data.getHoliday01SaturdayAmt()); //50000
			}

			// //일요
			if (null != data.getHolidaySunday1Standard()) {
				sheet.getRow(curRow+9).getCell(curCol+2).setCellValue(data.getHolidaySunday1Standard()); //0.5
			}
			if (null != data.getHoliday01SundayAmt()) {
				sheet.getRow(curRow+9).getCell(curCol+3).setCellValue(data.getHoliday01SundayAmt()); //25000
			}

			//휴일2 //포괄
			if (null != data.getHoliday2Standard()) {
				sheet.getRow(curRow+10).getCell(curCol+2).setCellValue(data.getHoliday2Standard()); //0
			}
			if (null != data.getHoliday02Amt()) {
				sheet.getRow(curRow+10).getCell(curCol+3).setCellValue(data.getHoliday02Amt()); //0
			}

			//연차사용
			if (null != data.getAnnualLeaveUsedDay()) {
				sheet.getRow(curRow+11).getCell(curCol+1).setCellValue(data.getAnnualLeaveUsedDay()); //3/17
			}
			if (null != data.getAnnualLeaveUsed()) {
				sheet.getRow(curRow+11).getCell(curCol+2).setCellValue(data.getAnnualLeaveUsed()); //1
			}

			//반차사용
			if (null != data.getHalfDay()) {
				sheet.getRow(curRow+12).getCell(curCol+1).setCellValue(data.getHalfDay()); //지각3회로 오전반차
			}
			if (null != data.getHalfTime()) {
				sheet.getRow(curRow+12).getCell(curCol+2).setCellValue(data.getHalfTime()); //1
			}

			//지각횟수
			if (null != data.getLateDay()) {
				sheet.getRow(curRow+13).getCell(curCol+1).setCellValue(data.getLateDay()); //3/20,21,22
			}
			if (null != data.getLateTime()) {
				sheet.getRow(curRow+13).getCell(curCol+2).setCellValue(data.getLateTime()); //3
			}

			//초과상여
//			if (null != data.get()) {
//				setCellValueFromMap(sheet, curRow+14).getCell(curCol+1).setCellValue(data.get()); //
//			}
//			if (null != data.get()) {
//				setCellValueFromMap(sheet, curRow+14).getCell(curCol+2).setCellValue(data.get()); //
//			}
//			if (null != data.get()) {
//				setCellValueFromMap(sheet, curRow+14).getCell(curCol+3).setCellValue(data.get()); //
//			}
//			if (null != data.get()) {
//				setCellValueFromMap(sheet, curRow+14).getCell(curCol+4).setCellValue(data.get()); //
//			}

			//합 계
			if (null != data.getTotalSalary()) {
				sheet.getRow(curRow+16).getCell(curCol+3).setCellValue(data.getTotalSalary()); //2115001
			}


			// test
//			for (int rownum = 0;rownum<17;rownum++) {
//System.out.print("    row="+curRow+rownum+", Cell="+curCol+", value="+curRow+"/"+rownum+"-"+curCol+"\n");
//				try { sheet.getRow(curRow+rownum).getCell(curCol+0).setCellValue(curRow+"/"+rownum+"-"+(curCol+0)); } catch (Exception e) { ;}
//				try { sheet.getRow(curRow+rownum).getCell(curCol+1).setCellValue(curRow+"/"+rownum+"-"+(curCol+1)); } catch (Exception e) { ;}
//				try { sheet.getRow(curRow+rownum).getCell(curCol+2).setCellValue(curRow+"/"+rownum+"-"+(curCol+2)); } catch (Exception e) { ;}
//				try { sheet.getRow(curRow+rownum).getCell(curCol+3).setCellValue(curRow+"/"+rownum+"-"+(curCol+3)); } catch (Exception e) { ;}
//				try { sheet.getRow(curRow+rownum).getCell(curCol+4).setCellValue(curRow+"/"+rownum+"-"+(curCol+4)); } catch (Exception e) { ;}
//			}
			
			// data setting - end
		}
		
		return true;
	}
		
	private boolean setParttimePayrollList(XSSFWorkbook workbook, List<PayrollDto> list) throws IOException
	{
		final int sheetNumber    = 1;
		final int countHeadLine  = 18; // 출력 포멧의 라인수
		final int dataCountInRow = 3;  // 1열에 출력 포멧의 개수
		final int dataOffsetCell = 6;  // 출력 포멧의 열 수 (+ 구분열 1개)

		final XSSFCellStyle styleLightGreen = workbook.createCellStyle();
		styleLightGreen.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());  // 배경색
		styleLightGreen.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleLightGreen.setShrinkToFit(true);
		final XSSFCellStyle styleLemonChiffon = workbook.createCellStyle();
		styleLemonChiffon.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());  // 배경색
		styleLemonChiffon.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleLemonChiffon.setShrinkToFit(true);

		// set format
		SeedXSSFUtil.copyHeadlineCubeFormat(workbook, sheetNumber, countHeadLine, list.size(), dataCountInRow, dataOffsetCell);

		int curRow = 0, curCol = 0;
		XSSFSheet sheet = workbook.getSheetAt(sheetNumber);
		for (int i=0;i<list.size();i++)
		{
			PayrollDto data = list.get(i);
			curRow = Math.floorDiv(i, dataCountInRow) * countHeadLine;
			curCol = Math.floorMod(i, dataCountInRow)*dataOffsetCell;

			////////////////////////////////////////////
			// data setting - start
			
			//NO.
			sheet.getRow(curRow).getCell(curCol+1).setCellValue(i+1); //1
			if (null != data.getDefinedName()) {
				sheet.getRow(curRow).getCell(curCol+2).setCellValue(data.getDefinedName()); //대리
			}
			if (null != data.getKoreanName()) {
				sheet.getRow(curRow).getCell(curCol+3).setCellValue(data.getKoreanName()); //김빨강
			}
			if (null != data.getDayPay()) {
				sheet.getRow(curRow).getCell(curCol+4).setCellValue(data.getDayPay()); //2000000
			}

			//44875
			if (null != data.getHireDate()) {
				sheet.getRow(curRow+1).getCell(curCol).setCellValue(data.getHireDate()); //
				if ("".equals(data.getHireDiv())) {
					; // default color - do nothing
				} else if ("RETIRE".equals(data.getHireDiv())) {
					sheet.getRow(curRow+1).getCell(curCol).setCellStyle(styleLightGreen);
//System.out.println("data.getHireDate()="+data.getHireDate()+" data.getHireDiv()="+data.getHireDiv());
//					sheet.getRow(curRow+1).getCell(curCol).getCellStyle().setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
//					sheet.getRow(curRow+1).getCell(curCol).getCellStyle().setFillPattern(FillPatternType.SOLID_FOREGROUND);
				}
				else if ("NEW".equals(data.getHireDiv())) {
					sheet.getRow(curRow+1).getCell(curCol).setCellStyle(styleLemonChiffon);
//System.out.println("data.getHireDate()="+data.getHireDate()+" data.getHireDiv()="+data.getHireDiv());
//					sheet.getRow(curRow+1).getCell(curCol).getCellStyle().setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());
//					sheet.getRow(curRow+1).getCell(curCol).getCellStyle().setFillPattern(FillPatternType.SOLID_FOREGROUND);
				}
			}
			if (null != data.getHourlyPay()) {
				sheet.getRow(curRow+1).getCell(curCol+4).setCellValue(data.getHourlyPay()); //26000000
			}
	
			//기본급
			if (null != data.getTotalTime()) {
				sheet.getRow(curRow+2).getCell(curCol+2).setCellValue(data.getTotalTime()); //209
			}
			if (null != data.getBasicSalary()) {
				sheet.getRow(curRow+2).getCell(curCol+3).setCellValue(data.getBasicSalary()); //1397994
			}
	
			//연차수당
			if (null != data.getAnnualLeaveUsedDay()) {
				sheet.getRow(curRow+3).getCell(curCol+2).setCellValue(data.getAnnualLeaveUsedDay());
			}
			if (null != data.getAnnualLeaveAmt()) {
				sheet.getRow(curRow+3).getCell(curCol+3).setCellValue(data.getAnnualLeaveAmt());
			}
			//연차수당(정산)
//			if (null != data.getAnnualLeaveUsed()) {
//				sheet.getRow(curRow+3).getCell(curCol+2).setCellValue(data.getAnnualLeaveUsed());
//			}
//			if (null != data.getAnnualLeaveAmt()) {
//				sheet.getRow(curRow+3).getCell(curCol+3).setCellValue(data.getAnnualLeaveAmt());
//			}
	
			//연장
			if (null != data.getOvertime1()) {
				sheet.getRow(curRow+5).getCell(curCol+2).setCellValue(data.getOvertime1()); //52
			}
			if (null != data.getOvertime01Amt()) {
				sheet.getRow(curRow+5).getCell(curCol+3).setCellValue(data.getOvertime01Amt()); //521739
			}
	
			//야간수당 (주간)
			if (null != data.getDaytimeHours()) {
				sheet.getRow(curRow+6).getCell(curCol+ 2).setCellValue(data.getDaytimeHours()); //주간조야간
			}
			if (null != data.getDaytimeHoursAmt()) {
				sheet.getRow(curRow+6).getCell(curCol+3).setCellValue(data.getDaytimeHoursAmt()); //
			}
	
			//야간수당 (야간)
			if (null != data.getNighttimeHours()) {
				sheet.getRow(curRow+7).getCell(curCol+2).setCellValue(data.getNighttimeHours()); //야간조야간
			}
			if (null != data.getNighttimeHoursAmt()) {
				sheet.getRow(curRow+7).getCell(curCol+3).setCellValue(data.getNighttimeHoursAmt()); //80268
			}
	
			//휴일(토요)
			if (null != data.getHolidaySaturday1()) {
				sheet.getRow(curRow+8).getCell(curCol+2).setCellValue(data.getHolidaySaturday1()); //1
			}
			if (null != data.getHoliday01SaturdayAmt()) {
				sheet.getRow(curRow+8).getCell(curCol+3).setCellValue(data.getHoliday01SaturdayAmt()); //50000
			}
	
			//휴일(일요)
			if (null != data.getHolidaySunday1()) {
				sheet.getRow(curRow+9).getCell(curCol+2).setCellValue(data.getHolidaySunday1()); //0.5
			}
			if (null != data.getHoliday01SundayAmt()) {
				sheet.getRow(curRow+9).getCell(curCol+3).setCellValue(data.getHoliday01SundayAmt()); //25000
			}
	
			//보조금(교통비)
			if (null != data.getTransportation()) {
				sheet.getRow(curRow+10).getCell(curCol+2).setCellValue(data.getTransportation());
			}
			if (null != data.getAttribute15()) {
				sheet.getRow(curRow+10).getCell(curCol+3).setCellValue(data.getAttribute15());
			}
	
			//보조금(식대)
			if (null != data.getMeal()) {
				sheet.getRow(curRow+11).getCell(curCol+1).setCellValue(data.getMeal()); //3/17
			}
			if (null != data.getAttribute16()) {
				sheet.getRow(curRow+11).getCell(curCol+2).setCellValue(data.getAttribute16()); //1
			}
	
			//반차사용
			if (null != data.getHalfDay()) {
				sheet.getRow(curRow+12).getCell(curCol+1).setCellValue(data.getHalfDay());
			}
			if (null != data.getHalfTime()) {
				sheet.getRow(curRow+12).getCell(curCol+2).setCellValue(data.getHalfTime());
			}
			if (null != data.getHalfTimeAmt()) {
				sheet.getRow(curRow+12).getCell(curCol+3).setCellValue(data.getHalfTimeAmt());
			}
	
			//조퇴
			if (null != data.getEarlyLeaveDay()) {
				sheet.getRow(curRow+13).getCell(curCol+1).setCellValue(data.getEarlyLeaveDay()); //3/20,21,22
			}
			if (null != data.getEarlyLeaveTime()) {
				sheet.getRow(curRow+13).getCell(curCol+2).setCellValue(data.getEarlyLeaveTime()); //3
			}
			if (null != data.getEarlyLeaveAmt()) {
				sheet.getRow(curRow+14).getCell(curCol+3).setCellValue(data.getEarlyLeaveAmt()); //
			}
	
			//지각
			if (null != data.getLateDay()) {
				sheet.getRow(curRow+14).getCell(curCol+1).setCellValue(data.getLateDay()); //
			}
			if (null != data.getLateTime()) {
				sheet.getRow(curRow+14).getCell(curCol+2).setCellValue(data.getLateTime()); //
			}
			if (null != data.getLateAmt()) {
				sheet.getRow(curRow+14).getCell(curCol+3).setCellValue(data.getLateAmt()); //
			}
	
			//합 계
			if (null != data.getTotalSalary()) {
				sheet.getRow(curRow+16).getCell(curCol+3).setCellValue(data.getTotalSalary()); //2115001
			}

			// data setting - end	
		}
		
		return true;
	}


}
