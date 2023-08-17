package kr.co.seedit.domain.hr.application;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

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


		return responseDto;
	}

//	@Transactional
	public ResponseDto payrollReport(String in/* PayrollDto payrollDto */, HttpServletResponse response)
			throws Exception {
		ResponseDto responseDto = ResponseDto.builder().build();

		Resource resource = resourceLoader.getResource("classpath:hr/payrollSample.xlsx");
		InputStream fis = resource.getInputStream();
		XSSFWorkbook workbook = new XSSFWorkbook(fis);

//System.out.printf("workbook.getNumberOfSheets()="+workbook.getNumberOfSheets()+"\n");
//		for (int iSheet = 0; iSheet < workbook.getNumberOfSheets(); iSheet++) {
		int iSheet = 0; { // 급여표 (샘플)만 확인
			
			XSSFSheet sheet = workbook.getSheetAt(iSheet);
//System.out.printf("[" + iSheet + "] srcRow getFirstRowNum()="+sheet.getFirstRowNum()+" getLastRowNum()"+sheet.getLastRowNum()+"\n");

			int curCellPoint = 0;
			int curRowPoint = sheet.getLastRowNum()+1;//countRowFulltime;
			final CellCopyPolicy options = new CellCopyPolicy();
			options.setCondenseRows(true);
			options.setMergeHyperlink(true);
//for(int itest=0;itest<4;itest++) {
//
//			
//				}
			
//			curCellPoint += sampleCellCount+1;
//			if (curCellPoint > (sampleCellCount+1) * (dataCountInRow-1))
//			{
//				curCellPoint = 0;
//				curRowPoint = curRowPoint + countRowFulltime;
//			}

		
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
}//iTest

		try {
//            File xlsxFile = new File("C:/Users/admin/Downloads/" + payrollDto.getYyyymm() + "payroll" + ".xlsx");
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

	private XSSFWorkbook getWorkbookPayrollFormat(int numberFulltimeUser, int numberParttimeUser) throws IOException
	{
		Resource resource = resourceLoader.getResource(PAYROLL_XLSX_FILE);
		InputStream fis;
//		try {
			fis = resource.getInputStream();
//		} catch (IOException e) {
//			throw new IOException("file open fail. " + PAYROLL_XLSX_FILE);
//		}
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		XSSFSheet sheet = workbook.getSheetAt(0);
		
		final CellCopyPolicy options = new CellCopyPolicy();
		options.setCondenseRows(true);
		options.setMergeHyperlink(true);
		
		int curRowPoint = (numberFulltimeUser/dataCountInRow)*(countRowFulltime - headRowFulltime) + countRowFulltime + 1;
		
		// 1. set part-time user
		if (numberParttimeUser <= 0)
		{
			// remove head
			sheet.copyRows(headRowParttime, headRowParttime+countRowParttime, headRowParttime, options);
		}
		else if (numberFulltimeUser > dataCountInRow)
		{
			for (int i=numberParttimeUser/dataCountInRow;i>=0;i--)
			{
				sheet.copyRows(headRowParttime, headRowParttime + countRowParttime, 
						curRowPoint, options);
				curRowPoint += countRowParttime;
			}
			
			for (int i = 1 + numberParttimeUser%dataCountInRow;i<=dataCountInRow;i++)
			{
System.out.print("int i = numberParttimeUser%dataCountInRow : "+ i+"\n");
//				sheet.getRow(curRowPoint).getCell(i);
//				sheet.setRow;
//				Row row;
//				Cell cell;
//				cell.m
//				row.mer
			}
			
		}
		
//		for (int i = 0;i<=5;i++) {
//		XSSFRow row = sheet.getRow(i);
//System.out.print("["+i+"] row.getHeight()               : "+ row.getHeight()               +"\n");               
//System.out.print("["+i+"] row.getHeightInPoints()       : "+ row.getHeightInPoints()       +"\n");
//System.out.print("["+i+"] row.getOutlineLevel()         : "+ row.getOutlineLevel()         +"\n");
//System.out.print("["+i+"] row.getPhysicalNumberOfCells(): "+ row.getPhysicalNumberOfCells()+"\n");
//System.out.print("["+i+"] row.getRowStyle()             : "+ row.getRowStyle()             +"\n");
//for (int j = 0;j<8;j++) {
//	XSSFCell cell = row.getCell(j);
//try{System.out.printf("    CellType()                   =" + cell.getCellType()               +"\n");} catch (Exception e) {;}
//try{System.out.printf("    CellComment()                =" + cell.getCellComment()            +"\n");} catch (Exception e) {;}
//try{System.out.printf("    StringCellValue()            =" + cell.getStringCellValue()        +"\n");} catch (Exception e) {;}
//try{System.out.printf("    Reference()                  =" + cell.getReference()              +"\n");} catch (Exception e) {;}
//
//try{System.out.printf("    RichStringCellValue()        =" + cell.getRichStringCellValue()    +"\n");} catch (Exception e) {;}
//try{System.out.printf("    Address()                    =" + cell.getAddress()                +"\n");} catch (Exception e) {;}
//	
////unknown					try{System.out.printf("    NumericCellValue()           =" + cell.getNumericCellValue()       +"\n");} catch (Exception e) {;}
////	try{System.out.printf("    CellStyle()                  =" + cell.getCellStyle()              +"\n");} catch (Exception e) {;}
////	try{System.out.printf("    RawValue()                   =" + cell.getRawValue()               +"\n");} catch (Exception e) {;}
////tagVale					try{System.out.printf("    CTCell()                     =" + cell.getCTCell()                 +"\n");} catch (Exception e) {;}
////exception					try{System.out.printf("    ErrorCellString()            =" + cell.getErrorCellString()        +"\n");} catch (Exception e) {;}
////	try{System.out.printf("    DateCellValue()              =" + cell.getDateCellValue()          +"\n");} catch (Exception e) {;}
////	try{System.out.printf("    CachedFormulaResultType()    =" + cell.getCachedFormulaResultType()+"\n");} catch (Exception e) {;}
////	try{System.out.printf("    CellFormula()                =" + cell.getCellFormula()            +"\n");} catch (Exception e) {;}
////	try{System.out.printf("    LocalDateTimeCellValue()     =" + cell.getLocalDateTimeCellValue() +"\n");} catch (Exception e) {;}
////	try{System.out.printf("    BooleanCellValue()           =" + cell.getBooleanCellValue()       +"\n");} catch (Exception e) {;}
//}
//		}

		
//		for (int i=headRowParttime;i<headRowParttime + countRowParttime;i++)
//		{
////System.out.print("["+i+"] removeRow \n");
//			sheet.removeRow(sheet.getRow(i));
//			sheet.removeMergedRegion(i);
//		}
//System.out.print("[] remove part-time head done \n");
		
		// 2. set full-time user
		if (numberFulltimeUser > dataCountInRow)
		{
			// remove part-time head
			for (int i=headRowParttime;i<headRowParttime + countRowParttime;i++)
			{
//System.out.print("["+i+"] removeRow \n");
				sheet.removeRow(sheet.getRow(i));
				sheet.removeMergedRegion(i);
			}
			
			curRowPoint = headRowFulltime + countRowFulltime + 1;
			for (int i=numberFulltimeUser/dataCountInRow;i>0;i--)
			{
				for (int j=0;j<=countRowFulltime;j++) {
					XSSFRow row = sheet.createRow(curRowPoint);
					row.getCell(0).setCellType(CellType.NUMERIC);
					row.getCell(0).setCellValue(j);
				}
System.out.print("sheet.copyRows("+headRowFulltime+", "+(headRowFulltime + countRowFulltime)+","+curRowPoint+",option)"+"\n");
//				sheet.copyRows(headRowFulltime, (headRowFulltime + countRowFulltime),
//						curRowPoint, options);
				curRowPoint += countRowFulltime;
			}
		}
		
		return workbook;
	}
	
	public ResponseDto payrollReportR(String in/* PayrollDto payrollDto */, HttpServletResponse response)
			throws Exception {
		ResponseDto responseDto = ResponseDto.builder().build();

//		getWorkbookPayrollFormat
//
//		Resource resource = resourceLoader.getResource("classpath:hr/payrollSample.xlsx");
//		InputStream fis = resource.getInputStream();
//		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		
//		XSSFSheet sheet = workbook.getSheetAt(0);//e.createSheet(); //.getSheetAt(iSheet);
//		setRowPayrollFormat(workbook, 4, 1);
//		workbook.set
		
		XSSFWorkbook workbook = getWorkbookPayrollFormat(7, 4);

////System.out.printf("workbook.getNumberOfSheets()="+workbook.getNumberOfSheets()+"\n");
////		for (int iSheet = 0; iSheet < workbook.getNumberOfSheets(); iSheet++) {
//		int iSheet = 0; // 급여표 (샘플)만 확인
//		final CellCopyPolicy options = new CellCopyPolicy();
//			
//			XSSFSheet sheet = workbook.createSheet(); //.getSheetAt(iSheet);
////System.out.printf("[" + iSheet + "] srcRow getFirstRowNum()="+sheet.getFirstRowNum()+" getLastRowNum()"+sheet.getLastRowNum()+"\n");
//
//			int curCellPoint = 0;
//			int curRowPoint = sheet.getLastRowNum()+1;//countRowFulltime;
//			int numberHeadRow = sheet.getLastRowNum();
//			options.setCondenseRows(true);
//			
//			// sheet.copyRows(iSheet, curCellPoint, curRowPoint, options)
////			sheet.copyRows(sheet.getRow, numberHeadRow, options)
////			sheet.copyRows(0, numberHeadRow, curRowPoint, options);
//			
////			List<XSSFRow> srcRows = sheet.groupRow(sheet.getFirstRowNum(), sheet.getLastRowNum());
////			sheet.getRow
////			sheet.copyRows(null, curRowPoint, options)
//						
////			workbook.removeSheetAt(0);

		try {
//            File xlsxFile = new File("C:/Users/admin/Downloads/" + payrollDto.getYyyymm() + "payroll" + ".xlsx");
			File xlsxFile = new File("D:\\GitHub\\laquna-api\\src\\main\\resources\\hr/"
					+ /* payrollDto.getYyyymm() + */ "payrollOutput" + ".xlsx");
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

	private static void copyRow(XSSFWorkbook workbook, XSSFSheet worksheet, int sourceRowNum, int destinationRowNum, int contadorDinamico) {
	    Row newRow = worksheet.getRow(destinationRowNum);
	    Row sourceRow = worksheet.getRow(sourceRowNum);

	    if (newRow != null) {
	        worksheet.shiftRows(destinationRowNum, worksheet.getLastRowNum(), 1);
	    }
	    newRow = worksheet.createRow(destinationRowNum);
	    
	    for (int i = 0; i < sourceRow.getLastCellNum(); i++) {
	        Cell oldCell = sourceRow.getCell(i);
	        Cell newCell = newRow.createCell(i);

	        if (oldCell == null) {
	            newCell = null;
	            continue;
	        }

	        // Copy style from old cell and apply to new cell
	        XSSFCellStyle newCellStyle = workbook.createCellStyle();
	        newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
	        ;
	        newCell.setCellStyle(newCellStyle);

	        // If there is a cell comment, copy
	        if (oldCell.getCellComment() != null) {
	            newCell.setCellComment(oldCell.getCellComment());
	        }

	        // If there is a cell hyperlink, copy
	        if (oldCell.getHyperlink() != null) {
	            newCell.setHyperlink(oldCell.getHyperlink());
	        }

	        // Set the cell data Style
	        newCell.setCellStyle(oldCell.getCellStyle());

	        if(oldCell.getStringCellValue() != null) {
	            newCell.setCellValue(oldCell.getStringCellValue().contains("#") ? oldCell.getStringCellValue().substring(0, oldCell.getStringCellValue().length()-2) + contadorDinamico + oldCell.getStringCellValue().substring(oldCell.getStringCellValue().length()-1, oldCell.getStringCellValue().length()) : oldCell.getStringCellValue());
	        }
	        
	    }

//	    for (int i = 0; i < worksheet.getNumMergedRegions(); i++) {
//	        CellRangeAddress cellRangeAddress = worksheet.getMergedRegion(i);
//	        if (cellRangeAddress.getFirstRow() == sourceRow.getRowNum()) {
//	            CellRangeAddress newCellRangeAddress = new CellRangeAddress(newRow.getRowNum(),
//	                    (newRow.getRowNum() +
//	                            (cellRangeAddress.getLastRow() - cellRangeAddress.getFirstRow()
//	                                    )),
//	                    cellRangeAddress.getFirstColumn(),
//	                    cellRangeAddress.getLastColumn());
//	            worksheet.addMergedRegion(newCellRangeAddress);
//	        }
//	    }
	}
}
