package kr.co.seedit.domain.hr.application;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.seedit.domain.hr.dto.BasicSalaryDto;
import kr.co.seedit.domain.hr.dto.ReportParamsDto;
import kr.co.seedit.domain.hr.dto.ReportPayrollDto;
import kr.co.seedit.domain.hr.dto.SalaryExcelDto;
import kr.co.seedit.domain.mapper.seedit.ReportDao;
import kr.co.seedit.global.common.dto.ResponseDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {

    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);
    
    private final ReportDao reportDao;
    private final ResourceLoader resourceLoader;
    
    @SuppressWarnings("resource")
	@Transactional
    public Resource downloadFile(ReportParamsDto reportParamsDto) throws Exception {
    	
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        XSSFWorkbook workbook = new XSSFWorkbook();
        
        switch(reportParamsDto.getReportType()) {
	        case "ERPIU":
	        	workbook = createERPIU(reportParamsDto);
	        	break;
	        case "Payroll":
	        	workbook = createPayroll(reportParamsDto);
	        	break;
        }

        workbook.write(byteArrayOutputStream);
        workbook.close();
        return new ByteArrayResource(byteArrayOutputStream.toByteArray());

    }
   
    public XSSFWorkbook createERPIU(ReportParamsDto reportParamsDto) throws Exception {
        //Get Data
         List<SalaryExcelDto> salaryExcelDtoList = new ArrayList<>();
         salaryExcelDtoList = reportDao.findERPIUData(reportParamsDto);
         
         XSSFWorkbook workbook = new XSSFWorkbook();
         XSSFSheet sheet = workbook.createSheet(reportParamsDto.getYyyymm() + "SalaryUpload");
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

         //Data Insert
         for (SalaryExcelDto s : salaryExcelDtoList) {
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
         

         return  workbook;
     }

     @Transactional
     public XSSFWorkbook createPayroll(ReportParamsDto reportParamsDto) throws Exception{
         
    	//Get Data
         List<ReportPayrollDto> reportPayrollDtoList = new ArrayList<>();
         reportPayrollDtoList = reportDao.findPayrollData(reportParamsDto);
         
     	//Open Sample Excel
         Resource resource = resourceLoader.getResource("classpath:hr/payrollSample.xlsx");
         InputStream fis = resource.getInputStream();
         
         XSSFWorkbook workbook = new XSSFWorkbook(fis);
         XSSFSheet sheet = workbook.getSheetAt(0);
         XSSFFormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
         int rowindex = 8;
         int cellindex = 0;
         XSSFRow row = null;
         XSSFCell cell = null;
         
         //Style Setting
         Font bodyFont = workbook.createFont();
         bodyFont.setFontName("바탕체");
         bodyFont.setFontHeightInPoints((short)8);
         
         CellStyle ThinBorderStyle = workbook.createCellStyle();
         ThinBorderStyle.setBorderTop(BorderStyle.THIN);
         ThinBorderStyle.setBorderBottom(BorderStyle.THIN);
         ThinBorderStyle.setBorderLeft(BorderStyle.THIN);
         ThinBorderStyle.setBorderRight(BorderStyle.THIN);
         ThinBorderStyle.setFont(bodyFont);
         ThinBorderStyle.setAlignment(HorizontalAlignment.CENTER);
         ThinBorderStyle.setVerticalAlignment(VerticalAlignment.CENTER);
         ThinBorderStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0")); // 1000단위 콤마
         ThinBorderStyle.setShrinkToFit(true);		//text 셀 맞춤
         
         CellStyle RightBorderStyle = workbook.createCellStyle();
         RightBorderStyle.setBorderTop(BorderStyle.THIN);
         RightBorderStyle.setBorderBottom(BorderStyle.THIN);
         RightBorderStyle.setBorderLeft(BorderStyle.THIN);
         RightBorderStyle.setBorderRight(BorderStyle.MEDIUM);
         RightBorderStyle.setFont(bodyFont);
         RightBorderStyle.setAlignment(HorizontalAlignment.CENTER);
         RightBorderStyle.setVerticalAlignment(VerticalAlignment.CENTER);
         RightBorderStyle.setShrinkToFit(true);		//text 셀 맞춤
         
         CellStyle TopBorderStyle = workbook.createCellStyle();
         TopBorderStyle.setBorderTop(BorderStyle.MEDIUM);
         TopBorderStyle.setBorderBottom(BorderStyle.THIN);
         TopBorderStyle.setBorderLeft(BorderStyle.THIN);
         TopBorderStyle.setBorderRight(BorderStyle.THIN);
         TopBorderStyle.setFont(bodyFont);
         TopBorderStyle.setAlignment(HorizontalAlignment.CENTER);
         TopBorderStyle.setVerticalAlignment(VerticalAlignment.CENTER);
         TopBorderStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0")); // 1000단위 콤마
         TopBorderStyle.setShrinkToFit(true);		//text 셀 맞춤
         
         CellStyle TopRightBorderStyle = workbook.createCellStyle();
         TopRightBorderStyle.setBorderTop(BorderStyle.MEDIUM);
         TopRightBorderStyle.setBorderBottom(BorderStyle.THIN);
         TopRightBorderStyle.setBorderLeft(BorderStyle.THIN);
         TopRightBorderStyle.setBorderRight(BorderStyle.MEDIUM);
         TopRightBorderStyle.setFont(bodyFont);
         TopRightBorderStyle.setAlignment(HorizontalAlignment.CENTER);
         TopRightBorderStyle.setVerticalAlignment(VerticalAlignment.CENTER);
         TopRightBorderStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0")); // 1000단위 콤마
         TopRightBorderStyle.setShrinkToFit(true);		//text 셀 맞춤
         
         CellStyle BottomBorderStyle = workbook.createCellStyle();
         BottomBorderStyle.setBorderTop(BorderStyle.THIN);
         BottomBorderStyle.setBorderBottom(BorderStyle.MEDIUM);
         BottomBorderStyle.setBorderLeft(BorderStyle.THIN);
         BottomBorderStyle.setBorderRight(BorderStyle.THIN);
         BottomBorderStyle.setFont(bodyFont);
         BottomBorderStyle.setAlignment(HorizontalAlignment.CENTER);
         BottomBorderStyle.setVerticalAlignment(VerticalAlignment.CENTER);
         BottomBorderStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0")); // 1000단위 콤마
         BottomBorderStyle.setShrinkToFit(true);		//text 셀 맞춤
         
         CellStyle AllBorderStyle = workbook.createCellStyle();
         AllBorderStyle.setBorderTop(BorderStyle.MEDIUM);
         AllBorderStyle.setBorderBottom(BorderStyle.MEDIUM);
         AllBorderStyle.setBorderLeft(BorderStyle.MEDIUM);
         AllBorderStyle.setBorderRight(BorderStyle.MEDIUM);
         AllBorderStyle.setFont(bodyFont);
         AllBorderStyle.setAlignment(HorizontalAlignment.CENTER);
         AllBorderStyle.setVerticalAlignment(VerticalAlignment.CENTER);
         AllBorderStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));	// 1000단위 콤마
         AllBorderStyle.setShrinkToFit(true);		//text 셀 맞춤
         
         CellStyle GrayAllBorderStyle = workbook.createCellStyle();
         GrayAllBorderStyle.setBorderTop(BorderStyle.MEDIUM);
         GrayAllBorderStyle.setBorderBottom(BorderStyle.MEDIUM);
         GrayAllBorderStyle.setBorderLeft(BorderStyle.MEDIUM);
         GrayAllBorderStyle.setBorderRight(BorderStyle.MEDIUM);
         GrayAllBorderStyle.setFont(bodyFont);
         GrayAllBorderStyle.setAlignment(HorizontalAlignment.CENTER);
         GrayAllBorderStyle.setVerticalAlignment(VerticalAlignment.CENTER);
         GrayAllBorderStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
         GrayAllBorderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
         GrayAllBorderStyle.setShrinkToFit(true);		//text 셀 맞춤
         
         //양식 내 상단 날짜 변경
         String yyyy = reportParamsDto.getYyyymm().substring(0,4);
         String mm = reportParamsDto.getYyyymm().substring(4,6);
         
         Date now = new Date();
         SimpleDateFormat formatter = new SimpleDateFormat("yyyy년 MM월 dd일");
         String today = formatter.format(now);
         
         row = sheet.getRow(0);
         cell = row.getCell(7);
         cell.setCellValue(yyyy +"년 "+ mm +"월분 "+"급여대장");
         row = sheet.getRow(2);
         cell = row.getCell(7);
         cell.setCellValue("[귀속:"+yyyy+"년"+mm+"월] [지급:"+ today +"]");
         
         //양식 내 상단 사업장 변경
         row = sheet.getRow(2);
     	cell = row.getCell(0);
         if(reportParamsDto.getEstId() != null && reportParamsDto.getEstId() != 999) {
         	cell.setCellValue(reportPayrollDtoList.get(0).getEstName());
         }else {
         	cell.setCellValue("전체 사업장");
         }
         
         
         //Data Insert into Excel
         for (ReportPayrollDto m : reportPayrollDtoList) {
         	
         	//1열
             cellindex = 0;
             row = sheet.createRow(rowindex++);
             
             cell = row.createCell(cellindex++);
             cell.setCellValue(m.getEmployeeNumber());
             cell.setCellStyle(TopBorderStyle);
             
             cell = row.createCell(cellindex++);
             cell.setCellValue(m.getKoreanName());
             cell.setCellStyle(TopRightBorderStyle);
             
             cell = row.createCell(cellindex++);
             cell.setCellValue(m.getBasicSalary());
             cell.setCellStyle(TopBorderStyle);
             cell.setCellType(CellType.NUMERIC);
             
             cell = row.createCell(cellindex++);
             cell.setCellValue(m.getAnnualAllowance());
             cell.setCellStyle(TopBorderStyle);
             cell.setCellType(CellType.NUMERIC);
             
             cell = row.createCell(cellindex++);
             cell.setCellValue(m.getOvertimeAllowance01());
             cell.setCellStyle(TopBorderStyle);
             cell.setCellType(CellType.NUMERIC);
             
             cell = row.createCell(cellindex++);
             cell.setCellValue(m.getOvertimeAllowance02());
             cell.setCellStyle(TopBorderStyle);
             cell.setCellType(CellType.NUMERIC);
             
             cell = row.createCell(cellindex++);
             cell.setCellValue(m.getNightAllowance01());
             cell.setCellStyle(TopBorderStyle);
             cell.setCellType(CellType.NUMERIC);
             
             cell = row.createCell(cellindex++);
             cell.setCellValue(m.getNightAllowance02());
             cell.setCellStyle(TopBorderStyle);
             cell.setCellType(CellType.NUMERIC);
             
             cell = row.createCell(cellindex++);
             cell.setCellValue(m.getHolidayAllowance01());
             cell.setCellStyle(TopRightBorderStyle);
             cell.setCellType(CellType.NUMERIC);

             //2열
             cellindex = 0;
             row = sheet.createRow(rowindex++);
             
             cell = row.createCell(cellindex++);
             cell.setCellValue(m.getHireDate());
             cell.setCellStyle(ThinBorderStyle);
             
             cell = row.createCell(cellindex++);
             cell.setCellValue(m.getDefinedName());
             cell.setCellStyle(RightBorderStyle);
             
             cell = row.createCell(cellindex++);
             cell.setCellValue(m.getHolidayAllowance02());
             cell.setCellStyle(ThinBorderStyle);
             cell.setCellType(CellType.NUMERIC);
             
             cell = row.createCell(cellindex++);
             cell.setCellValue(m.getPositionAllowance());
             cell.setCellStyle(ThinBorderStyle);
             cell.setCellType(CellType.NUMERIC);
             
             cell = row.createCell(cellindex++);
             cell.setCellValue(m.getOtherAllowances());
             cell.setCellStyle(ThinBorderStyle);
             cell.setCellType(CellType.NUMERIC);
             
             cell = row.createCell(cellindex++);
             cell.setCellValue(m.getSubsidies());
             cell.setCellStyle(ThinBorderStyle);
             cell.setCellType(CellType.NUMERIC);
             
             cell = row.createCell(cellindex++);
             cell.setCellValue(m.getTransportationExpenses());
             cell.setCellStyle(ThinBorderStyle);
             cell.setCellType(CellType.NUMERIC);
             
             cell = row.createCell(cellindex++);
             cell.setCellValue(m.getMealsExpenses());
             cell.setCellStyle(ThinBorderStyle);
             cell.setCellType(CellType.NUMERIC);
             
             cell = row.createCell(cellindex++);
             cell.setBlank();
             cell.setCellStyle(RightBorderStyle);
             
             //3열
             cellindex = 0;
             row = sheet.createRow(rowindex++);
             
             cell = row.createCell(cellindex++);
             cell.setCellValue(m.getRetireDate());
             cell.setCellStyle(ThinBorderStyle);
             
             cell = row.createCell(cellindex++);
             cell.setCellValue(m.getDepartmentName());
             cell.setCellStyle(RightBorderStyle);
             
             for(int i=0; i<6 ; i++) {	//blank cell 6개
             	cell = row.createCell(cellindex++);
                 cell.setBlank();
                 cell.setCellStyle(ThinBorderStyle);
             }
             
             cell = row.createCell(cellindex++);
             cell.setCellValue(m.getSalarySum());
             cell.setCellStyle(AllBorderStyle);
             cell.setCellType(CellType.NUMERIC);
             

         }
         
         //리스트 하단 합계

     	row = sheet.createRow(rowindex++);
     	for(cellindex=0; cellindex<2; cellindex++) {
         	cell = row.createCell(cellindex);
             cell.setCellStyle(GrayAllBorderStyle);
             cell.setCellValue("합계 ("+reportPayrollDtoList.size()+"명)");
         }

     	cell = row.createCell(cellindex++);
     	cell.setCellStyle(TopBorderStyle);
     	cell.setCellFormula(determineSumFormula("C", 9, 9+reportPayrollDtoList.size()*3, 3));
     	formulaEvaluator.evaluateFormulaCell(cell);
     	
     	cell = row.createCell(cellindex++);
     	cell.setCellStyle(TopBorderStyle);
     	cell.setCellFormula(determineSumFormula("D", 9, 9+reportPayrollDtoList.size()*3, 3));
     	formulaEvaluator.evaluateFormulaCell(cell);
     	
     	cell = row.createCell(cellindex++);
     	cell.setCellStyle(TopBorderStyle);
     	cell.setCellFormula(determineSumFormula("E", 9, 9+reportPayrollDtoList.size()*3, 3));
     	formulaEvaluator.evaluateFormulaCell(cell);
     	
     	cell = row.createCell(cellindex++);
     	cell.setCellStyle(TopBorderStyle);
     	cell.setCellFormula(determineSumFormula("F", 9, 9+reportPayrollDtoList.size()*3, 3));
     	formulaEvaluator.evaluateFormulaCell(cell);
     	
     	cell = row.createCell(cellindex++);
     	cell.setCellStyle(TopBorderStyle);
     	cell.setCellFormula(determineSumFormula("G", 9, 9+reportPayrollDtoList.size()*3, 3));
     	formulaEvaluator.evaluateFormulaCell(cell);
     	
     	cell = row.createCell(cellindex++);
     	cell.setCellStyle(TopBorderStyle);
     	cell.setCellFormula(determineSumFormula("H", 9, 9+reportPayrollDtoList.size()*3, 3));
     	formulaEvaluator.evaluateFormulaCell(cell);
     	
     	cell = row.createCell(cellindex++);
     	cell.setCellStyle(TopRightBorderStyle);
     	cell.setCellFormula(determineSumFormula("I", 9, 9+reportPayrollDtoList.size()*3, 3));
     	formulaEvaluator.evaluateFormulaCell(cell);
     	
     	row = sheet.createRow(rowindex++);
     	for(cellindex=0; cellindex<2; cellindex++) {
         	cell = row.createCell(cellindex);
             cell.setCellStyle(GrayAllBorderStyle);
//             cell.setCellValue("합계 ("+reportPayrollDtoList.size()+"명)");
         }
     	
     	cell = row.createCell(cellindex++);
     	cell.setCellStyle(ThinBorderStyle);
     	cell.setCellFormula(determineSumFormula("C", 10, 10+reportPayrollDtoList.size()*3, 3));
     	formulaEvaluator.evaluateFormulaCell(cell);
     	
     	cell = row.createCell(cellindex++);
     	cell.setCellStyle(ThinBorderStyle);
     	cell.setCellFormula(determineSumFormula("D", 10, 10+reportPayrollDtoList.size()*3, 3));
     	formulaEvaluator.evaluateFormulaCell(cell);
     	
     	cell = row.createCell(cellindex++);
     	cell.setCellStyle(ThinBorderStyle);
     	cell.setCellFormula(determineSumFormula("E", 10, 10+reportPayrollDtoList.size()*3, 3));
     	formulaEvaluator.evaluateFormulaCell(cell);
     	
     	cell = row.createCell(cellindex++);
     	cell.setCellStyle(ThinBorderStyle);
     	cell.setCellFormula(determineSumFormula("F", 10, 10+reportPayrollDtoList.size()*3, 3));
     	formulaEvaluator.evaluateFormulaCell(cell);
     	
     	cell = row.createCell(cellindex++);
     	cell.setCellStyle(ThinBorderStyle);
     	cell.setCellFormula(determineSumFormula("G", 10, 10+reportPayrollDtoList.size()*3, 3));
     	formulaEvaluator.evaluateFormulaCell(cell);
     	
     	cell = row.createCell(cellindex++);
     	cell.setCellStyle(ThinBorderStyle);
     	cell.setCellFormula(determineSumFormula("H", 10, 10+reportPayrollDtoList.size()*3, 3));
     	formulaEvaluator.evaluateFormulaCell(cell);
     	
     	cell = row.createCell(cellindex++);
     	cell.setCellStyle(RightBorderStyle);
     	cell.setBlank();
     	
     	row = sheet.createRow(rowindex++);
     	for(cellindex=0; cellindex<2; cellindex++) {
         	cell = row.createCell(cellindex);
             cell.setCellStyle(GrayAllBorderStyle);
//             cell.setCellValue("합계 ("+reportPayrollDtoList.size()+"명)");
         }
     	
     	for(int i=0; i<6 ; i++) {	//blank cell 6개
         	cell = row.createCell(cellindex++);
         	cell.setBlank();
             cell.setCellStyle(BottomBorderStyle);
         }
     	
     	cell = row.createCell(cellindex++);
     	cell.setCellStyle(AllBorderStyle);
     	cell.setCellFormula(determineSumFormula("I", 11, 11+reportPayrollDtoList.size()*3, 3));
     	formulaEvaluator.evaluateFormulaCell(cell);

        sheet.addMergedRegion(new CellRangeAddress((8+reportPayrollDtoList.size()*3), (9+reportPayrollDtoList.size()*3)+1, 0, 1));
         
     	return workbook;
     }
     
     //excel sum 수식 파라미터(열 이름, 시작 행, 끝나는 행, 간격)
     private static String determineSumFormula(String cellName, Integer start, Integer end, Integer interval) {
     	String formula = "SUM(";
     	do {
     		formula += cellName + start + ",";
     		start += interval;
     	}while(start< end);
     	
     	formula = formula.substring(0, formula.length()-1);	//마지막 콤마 제거
     	formula += ")";
     	
     	return formula;
     }
    

// 예전 급여대장
//    @Transactional
//    public ResponseDto monthlyKeunTaeReport(MonthlyKeunTaeDto monthlyKeunTaeDto, HttpServletResponse response) throws
//            Exception {
//        ResponseDto responseDto = ResponseDto.builder().build();
//        
//        Resource resource = resourceLoader.getResource("classpath:hr/monthlyKeunTaeSample.xlsx");
//        InputStream fis = resource.getInputStream();
//        
////        //Local Path
////        File formPath = new File("C:/Users/admin/Documents/reportSample/payrollSample.xlsx");
////        InputStream fis = new FileInputStream(formPath);
//        XSSFWorkbook workbook = new XSSFWorkbook(fis);
//        XSSFSheet sheet = workbook.getSheetAt(0);
//        int rowindex = 5;
//        int cellindex = 1;
//        int no = 1;
//        
//        
//        // test
//        System.out.print("sheet.getLastRowNum():"+sheet.getLastRowNum());
//        System.out.print("oldSheet.getFirstRowNum():"+sheet.getFirstRowNum());
//        for (int i=1;i<6;i++)
//        {
//        	XSSFRow row = sheet.getRow(i);
//        	XSSFCell cell = row.getCell(i);
//        	switch(i) {
//        	case 1:cell.setCellValue(false);break;
//        	case 2:cell.setCellValue(2);break;
//        	case 3:cell.setCellValue("3");break;
//        	case 4:cell.setCellValue("4");break;
//        	case 5:cell.setCellValue(LocalDate.now());break;
//        	}
//        	
//        	;
//        }
//
//        //Excel Data Select
//        List<ReportMonthlyKeunTaeDto> reportMonthlyKeunTaeDtoList = new ArrayList<>();
//        reportMonthlyKeunTaeDtoList = reportDao.findPayroll(monthlyKeunTaeDto);
//
////        XSSFWorkbook workbook = new XSSFWorkbook();
////        XSSFSheet sheet = workbook.createSheet(monthlyKeunTaeDto.getYyyymm() + "SalaryUpload");
////        int rowindex = 0;
////        int cellindex = 0;
//
//        //Data Insert
//        for (ReportMonthlyKeunTaeDto m : reportMonthlyKeunTaeDtoList) {
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
//
//        try {
////            File xlsxFile = new File("C:/Users/admin/Downloads/" + monthlyKeunTaeDto.getYyyymm() + "monthlyKeunTae" + ".xlsx");
//            File xlsxFile = new File("C:/" + monthlyKeunTaeDto.getYyyymm() + "monthlyKeunTae" + ".xlsx");
//            FileOutputStream fileOut = new FileOutputStream(xlsxFile);
//            workbook.write(fileOut);
//            workbook.close();
//            fileOut.close();
//        } catch (Exception e) {
//            logger.error("Exception", e);
//            throw e;
//        }
//
//
//        return responseDto;
//    }

}
