package kr.co.seedit.domain.hr.application;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellCopyPolicy;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
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
	final private int headRowFulltime  = 0 ; // point is 1 less than column number
	final private int countRowFulltime = 17;
	final private int headRowParttime  = 17; // point is 1 less than column number
	final private int countRowParttime = 18;
	final private int dataCountInRow   = 3;
	final private int dataOffsetCell   = 6;
	
	//test
	final private int sampleCellPoint = 0;
	final private int sampleCellCount = 5;

//    private final PayrollDao payrollDao;
	@Autowired
    PayrollDao payrollDao;

	@Autowired
	ResourceLoader resourceLoader;
	

//	@Transactional
	public ResponseDto payrollReportFull(String in/* PayrollDto payrollDto */, HttpServletResponse response)
			throws Exception {
		ResponseDto responseDto = ResponseDto.builder().build();

		Resource resource = resourceLoader.getResource(PAYROLL_INPUT_FILE);
		InputStream fis = resource.getInputStream();
		XSSFWorkbook workbook = new XSSFWorkbook(fis);

		// get full-time user data
		
		// full-time format
		SeedXSSFUtil.setPayrollFormat(workbook.getSheetAt(0), 0, countRowFulltime, 8, 3, dataOffsetCell);
		
		// set full-time user information
		
		
		
		// get set part-time user data
		
		// part-time format
		SeedXSSFUtil.setPayrollFormat(workbook.getSheetAt(1), 1, countRowParttime, 10, 3, dataOffsetCell);

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
	public ResponseDto payrollReport(String in/* PayrollDto payrollDto */, HttpServletResponse response)
			throws Exception {
		ResponseDto responseDto = ResponseDto.builder().build();

		Resource resource = resourceLoader.getResource(PAYROLL_INPUT_FILE);
		InputStream fis = resource.getInputStream();
		XSSFWorkbook workbook = new XSSFWorkbook(fis);

		// get full-time user data
		Map<String, String> list = payrollDao.getPayrollList(null);
		
		// full-time format
		SeedXSSFUtil.setPayrollFormat(workbook.getSheetAt(0), 0, countRowFulltime, 8, 3, dataOffsetCell);
		
		// set full-time user information
		
		
		
		// get set part-time user data
		
		// part-time format
		SeedXSSFUtil.setPayrollFormat(workbook.getSheetAt(1), 1, countRowParttime, 10, 3, dataOffsetCell);

		// set part-time user information

		
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

		return responseDto;
	}
}
