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

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import kr.co.seedit.domain.mapper.seedit.PayrollDao;
import kr.co.seedit.global.common.dto.ResponseDto;
import kr.co.seedit.global.utils.SeedXSSFUtil;
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
		setParttimePayrollDatas(workbook, listParttime);

		
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

	private static String getCommaString(Object value)
	{
		final DecimalFormat df1 = new DecimalFormat("#,##0");
		if (null == value)
			return "";
		
		return df1.format(value);
	}

	private boolean setFulltimePayrollDatas(XSSFWorkbook workbook, List<Map<String, Object>> datas) throws IOException
	{
		final int sheetNumber    = 1;
		final int countHeadLine  = 17; // 출력 포멧의 라인수
		final int dataCountInRow = 3;  // 1열에 출력 포멧의 개수
		final int dataOffsetCell = 6;  // 출력 포멧의 열 수 (+ 구분열 1개)

		// set format
		SeedXSSFUtil.copyHeadlineCubeFormat(workbook, 0, countHeadLine, datas.size(), dataCountInRow, dataOffsetCell);

//		int i=0;
		int curRowPoint = 0, curColumnPoint = 0;
		XSSFSheet sheet = workbook.getSheetAt(sheetNumber);
		for (int i=0;i<datas.size();i++) {
			Map<String, Object> data = datas.get(i);
//		}
//		for (Map<String, String> map : datas) {
			curRowPoint = Math.floorDiv(i, dataCountInRow) * countHeadLine;
			curColumnPoint = Math.floorMod(i, dataCountInRow)*dataOffsetCell;

			////////////////////////////////////////////
			// data setting - start
			
			//NO.
			setCelValueFromMap(sheet, curRowPoint, curColumnPoint+1, i+1); //1
			setCelValueFromMap(sheet, curRowPoint, curColumnPoint+2, data.get("definedName")); //대리
			setCelValueFromMap(sheet, curRowPoint, curColumnPoint+3, data.get("koreanName")); //김빨강
			setCelValueFromMap(sheet, curRowPoint, curColumnPoint+4, getCommaString(data.get("monthSalary"))); //2000000

			//44875
			setCelValueFromMap(sheet, curRowPoint+1, curColumnPoint+1, data.get("hireDate")); //
			setCelValueFromMap(sheet, curRowPoint+1, curColumnPoint+4, getCommaString(data.get("yearSalary"))); //26000000

			//기본급
			setCelValueFromMap(sheet, curRowPoint+2, curColumnPoint+1, data.get("basicSalary")); //6688.96321070234
			setCelValueFromMap(sheet, curRowPoint+2, curColumnPoint+2, data.get("totalTime")); //209
			setCelValueFromMap(sheet, curRowPoint+2, curColumnPoint+3, data.get("basicSalary")); //1397994

			//연차정산

			//연장1
			setCelValueFromMap(sheet, curRowPoint+4, curColumnPoint+2, data.get("overtime1")); //2
			setCelValueFromMap(sheet, curRowPoint+4, curColumnPoint+3, data.get("overtime01Amt")); //40000

			//연장2 //포괄
			setCelValueFromMap(sheet, curRowPoint+5, curColumnPoint+2, data.get("overtime2")); //52
			setCelValueFromMap(sheet, curRowPoint+5, curColumnPoint+3, data.get("overtime02Amt")); //521739

			//야간1
			setCelValueFromMap(sheet, curRowPoint+6, curColumnPoint+2, data.get("nightShift1")); //
			setCelValueFromMap(sheet, curRowPoint+6, curColumnPoint+3, data.get("night01Allowance")); //0

			//야간2 //포괄
			setCelValueFromMap(sheet, curRowPoint+7, curColumnPoint+2, data.get("nightShift2")); //24
			setCelValueFromMap(sheet, curRowPoint+7, curColumnPoint+3, data.get("night02Allowance")); //80268

			//휴일1 //토요
			setCelValueFromMap(sheet, curRowPoint+8, curColumnPoint+2, data.get("holidaySaturday1")); //1
			setCelValueFromMap(sheet, curRowPoint+8, curColumnPoint+3, data.get("holiday01SaturdayAmt")); //50000

			// //일요
			setCelValueFromMap(sheet, curRowPoint+9, curColumnPoint+2, data.get("holidaySunday1")); //0.5
			setCelValueFromMap(sheet, curRowPoint+9, curColumnPoint+3, data.get("holiday01SundayAmt")); //25000

			//휴일2 //포괄
			setCelValueFromMap(sheet, curRowPoint+10, curColumnPoint+2, data.get("holiday2")); //0
			setCelValueFromMap(sheet, curRowPoint+10, curColumnPoint+3, data.get("holiday02Amt")); //0

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
//			setCelValueFromMap(sheet, curRowPoint+14, curColumnPoint+3, data.get("")); //
//			setCelValueFromMap(sheet, curRowPoint+14, curColumnPoint+4, data.get("")); //

			//합 계
			setCelValueFromMap(sheet, curRowPoint+16, curColumnPoint+3, data.get("")); //2115001

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
		SeedXSSFUtil.copyHeadlineCubeFormat(workbook, 0, countHeadLine, datas.size(), dataCountInRow, dataOffsetCell);

//			int i=0;
		int curRowPoint = 0, curColumnPoint = 0;
		XSSFSheet sheet = workbook.getSheetAt(sheetNumber);
		for (int i=0;i<datas.size();i++) {
			Map<String, Object> data = datas.get(i);
//			}
//			for (Map<String, String> map : datas) {
			curRowPoint = Math.floorDiv(i, dataCountInRow) * countHeadLine;
			curColumnPoint = Math.floorMod(i, dataCountInRow)*dataOffsetCell;

			////////////////////////////////////////////
			// data setting - start
			
//			//NO.
//			setCelValueFromMap(sheet, curRowPoint, curColumnPoint+1, i+1); //1
//			setCelValueFromMap(sheet, curRowPoint, curColumnPoint+2, data.get("definedName")); //대리
//			setCelValueFromMap(sheet, curRowPoint, curColumnPoint+3, data.get("koreanName")); //김빨강
//			setCelValueFromMap(sheet, curRowPoint, curColumnPoint+4, getCommaString(data.get("monthSalary"))); //2000000
//
//			//44875
//			setCelValueFromMap(sheet, curRowPoint+1, curColumnPoint+1, data.get("hireDate")); //
//			setCelValueFromMap(sheet, curRowPoint+1, curColumnPoint+4, getCommaString(data.get("yearSalary"))); //26000000
//
//			//기본급
//			setCelValueFromMap(sheet, curRowPoint+2, curColumnPoint+1, data.get("basicSalary")); //6688.96321070234
//			setCelValueFromMap(sheet, curRowPoint+2, curColumnPoint+2, data.get("totalTime")); //209
//			setCelValueFromMap(sheet, curRowPoint+2, curColumnPoint+3, data.get("basicSalary")); //1397994
//
//			//연차정산
//
//			//연장1
//			setCelValueFromMap(sheet, curRowPoint+4, curColumnPoint+2, data.get("overtime1")); //2
//			setCelValueFromMap(sheet, curRowPoint+4, curColumnPoint+3, data.get("overtime01Amt")); //40000
//
//			//연장2 //포괄
//			setCelValueFromMap(sheet, curRowPoint+5, curColumnPoint+2, data.get("overtime2")); //52
//			setCelValueFromMap(sheet, curRowPoint+5, curColumnPoint+3, data.get("overtime02Amt")); //521739
//
//			//야간1
//			setCelValueFromMap(sheet, curRowPoint+6, curColumnPoint+2, data.get("nightShift1")); //
//			setCelValueFromMap(sheet, curRowPoint+6, curColumnPoint+3, data.get("night01Allowance")); //0
//
//			//야간2 //포괄
//			setCelValueFromMap(sheet, curRowPoint+7, curColumnPoint+2, data.get("nightShift2")); //24
//			setCelValueFromMap(sheet, curRowPoint+7, curColumnPoint+3, data.get("night02Allowance")); //80268
//
//			//휴일1 //토요
//			setCelValueFromMap(sheet, curRowPoint+8, curColumnPoint+2, data.get("holidaySaturday1")); //1
//			setCelValueFromMap(sheet, curRowPoint+8, curColumnPoint+3, data.get("holiday01SaturdayAmt")); //50000
//
//			// //일요
//			setCelValueFromMap(sheet, curRowPoint+9, curColumnPoint+2, data.get("holidaySunday1")); //0.5
//			setCelValueFromMap(sheet, curRowPoint+9, curColumnPoint+3, data.get("holiday01SundayAmt")); //25000
//
//			//휴일2 //포괄
//			setCelValueFromMap(sheet, curRowPoint+10, curColumnPoint+2, data.get("holiday2")); //0
//			setCelValueFromMap(sheet, curRowPoint+10, curColumnPoint+3, data.get("holiday02Amt")); //0
//
//			//연차사용
//			setCelValueFromMap(sheet, curRowPoint+11, curColumnPoint+1, data.get("annualLeaveUsedDay")); //3/17
//			setCelValueFromMap(sheet, curRowPoint+11, curColumnPoint+2, data.get("annualLeaveUsed")); //1
//
//			//반차사용
//			setCelValueFromMap(sheet, curRowPoint+12, curColumnPoint+1, data.get("halfDay")); //지각3회로 오전반차
//			setCelValueFromMap(sheet, curRowPoint+12, curColumnPoint+2, data.get("halfTime")); //1
//
//			//지각횟수
//			setCelValueFromMap(sheet, curRowPoint+13, curColumnPoint+1, data.get("lateDay")); //3/20,21,22
//			setCelValueFromMap(sheet, curRowPoint+13, curColumnPoint+2, data.get("lateTime")); //3
//
//			//초과상여
////				setCelValueFromMap(sheet, curRowPoint+14, curColumnPoint+1, data.get("")); //
////				setCelValueFromMap(sheet, curRowPoint+14, curColumnPoint+2, data.get("")); //
////				setCelValueFromMap(sheet, curRowPoint+14, curColumnPoint+3, data.get("")); //
////				setCelValueFromMap(sheet, curRowPoint+14, curColumnPoint+4, data.get("")); //
//
//			//합 계
//			setCelValueFromMap(sheet, curRowPoint+16, curColumnPoint+3, data.get("")); //2115001

			// data setting - end	
		}
		
		return true;
	}


}
