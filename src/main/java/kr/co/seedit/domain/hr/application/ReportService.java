package kr.co.seedit.domain.hr.application;

import kr.co.seedit.domain.company.application.CompanyService;
import kr.co.seedit.domain.hr.dto.BasicSalaryDto;
import kr.co.seedit.domain.hr.dto.MonthlyKeunTaeDto;
import kr.co.seedit.domain.hr.dto.ReportMonthlyKeunTaeDto;
import kr.co.seedit.domain.hr.dto.SalaryExcelDto;
import kr.co.seedit.domain.mapper.seedit.ReportDao;
import kr.co.seedit.global.common.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);
    
    private final ReportDao reportDao;
    
    @Transactional
    public ResponseDto salaryReport(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	ResponseDto responseDto = ResponseDto.builder().build();

        try{
            //local
//            File formPath = new File("C:/Users/admin/Downloads/sample.xlsx");
            //server
            File formPath = new File("/server/seedit-dh-dev/files/SalarySample.xlsx");
            InputStream fis = new FileInputStream(formPath);
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            XSSFSheet sheet = workbook.getSheetAt(0);
            int rowindex = 5;
            int cellindex = 1;

            XSSFRow row = sheet.createRow(rowindex++);
            row.createCell(cellindex++).setCellValue("1");
            row.createCell(cellindex++).setCellValue("김경진");
            row.createCell(cellindex++).setCellValue("과장");
            row.createCell(cellindex++).setCellValue("NULL");
            row.createCell(cellindex++).setCellValue("2020-04-13");
            row.createCell(cellindex++).setCellValue("52000000");
            row.createCell(cellindex++).setCellValue("4000000");
            row.createCell(cellindex++).setCellValue("NULL");
            row.createCell(cellindex++).setCellValue("NULL");
            row.createCell(cellindex++).setCellValue("13,378");
            row.createCell(cellindex++).setCellValue("209");
            row.createCell(cellindex++).setCellValue("2,795,987");
            row.createCell(cellindex++).setCellValue("0");
            row.createCell(cellindex++).setCellValue("0");
            row.createCell(cellindex++).setCellValue("NULL");
            row.createCell(cellindex++).setCellValue("0");
            row.createCell(cellindex++).setCellValue("0");
            row.createCell(cellindex++).setCellValue("52");
            row.createCell(cellindex++).setCellValue("1,043,477");
            row.createCell(cellindex++).setCellValue("0");
            row.createCell(cellindex++).setCellValue("0");

//            File xlsxFile = new File("C:/Users/admin/Downloads/" + basicSalaryDto.getYyyymm() + "SalarySample" + ".xlsx");
//            File xlsxFile = new File("C:\\files\\" + "20230731_" + "SalarySample" + ".xlsx");
//            FileOutputStream fileOut = new FileOutputStream(xlsxFile);
//            workbook.write(fileOut);
//            workbook.close();

        // 컨텐츠 타입과 파일명 지정
        response.setContentType("ms-vnd/excel");
        response.setHeader("Content-Disposition", "attachment;filename=example.xlsx");

        // Excel File Output
        workbook.write(response.getOutputStream());
        workbook.close();

        } catch (Exception e){
            logger.error("Exception", e);
            throw e;
        }

        return responseDto;
    }
    
    @Transactional
    public ResponseDto monthlyKeunTaeReport(MonthlyKeunTaeDto monthlyKeunTaeDto, HttpServletResponse response) throws
            Exception {
        ResponseDto responseDto = ResponseDto.builder().build();
        
        //Local Path
        File formPath = new File("C:/Users/admin/Documents/reportSample/payrollSample.xlsx");
        InputStream fis = new FileInputStream(formPath);
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        XSSFSheet sheet = workbook.getSheetAt(0);
        int rowindex = 5;
        int cellindex = 1;
        int no = 1;

        //Excel Data Select
        List<ReportMonthlyKeunTaeDto> reportMonthlyKeunTaeDtoList = new ArrayList<>();
        reportMonthlyKeunTaeDtoList = reportDao.findPayroll(monthlyKeunTaeDto);

//        XSSFWorkbook workbook = new XSSFWorkbook();
//        XSSFSheet sheet = workbook.createSheet(monthlyKeunTaeDto.getYyyymm() + "SalaryUpload");
//        int rowindex = 0;
//        int cellindex = 0;

        //Data Insert
        for (ReportMonthlyKeunTaeDto m : reportMonthlyKeunTaeDtoList) {
            cellindex = 1;
            XSSFRow row = sheet.createRow(rowindex++);
            row.createCell(cellindex++).setCellValue(no++);
            row.createCell(cellindex++).setCellValue(m.getKoreanName());
            row.createCell(cellindex++).setCellValue(m.getDefinedName());
            row.createCell(cellindex++).setCellValue(m.getDepartmentName());
            row.createCell(cellindex++).setCellValue(m.getHireDate());
            row.createCell(cellindex++).setBlank();
            row.createCell(cellindex++).setBlank();
            row.createCell(cellindex++).setBlank();
            row.createCell(cellindex++).setBlank();
            row.createCell(cellindex++).setBlank();
            row.createCell(cellindex++).setBlank();
            row.createCell(cellindex++).setBlank();
            row.createCell(cellindex++).setCellValue(m.getAnnualLeaveUsedDay());
            row.createCell(cellindex++).setCellValue(m.getAnnualLeaveUsed());
            row.createCell(cellindex++).setBlank();
            row.createCell(cellindex++).setCellValue(m.getOverTime1());
            row.createCell(cellindex++).setBlank();
            row.createCell(cellindex++).setCellValue(m.getOverTime2());
            row.createCell(cellindex++).setBlank();
            row.createCell(cellindex++).setCellValue(m.getNightShift1());
            row.createCell(cellindex++).setBlank();
            row.createCell(cellindex++).setCellValue(m.getNightShift2());
            row.createCell(cellindex++).setBlank();
            row.createCell(cellindex++).setCellValue(m.getDayTimeHours());
            row.createCell(cellindex++).setBlank();
            row.createCell(cellindex++).setCellValue(m.getNightTimeHours());
            row.createCell(cellindex++).setBlank();
            row.createCell(cellindex++).setCellValue(m.getHolidaySaturday1());
            row.createCell(cellindex++).setBlank();
            row.createCell(cellindex++).setCellValue(m.getHolidaySunday1());
            row.createCell(cellindex++).setBlank();
            row.createCell(cellindex++).setCellValue(m.getHoliday2());
            row.createCell(cellindex++).setBlank();
            row.createCell(cellindex++).setCellValue(m.getTransportation());
            row.createCell(cellindex++).setBlank();
            row.createCell(cellindex++).setCellValue(m.getMeal());
            row.createCell(cellindex++).setBlank();
            row.createCell(cellindex++).setBlank();		//보조금
            row.createCell(cellindex++).setCellValue(m.getOther());
            row.createCell(cellindex++).setCellValue(m.getHalfDay());
            row.createCell(cellindex++).setCellValue(m.getHalfTime());
            row.createCell(cellindex++).setBlank();
            row.createCell(cellindex++).setCellValue(m.getEarlyLeaveDay());
            row.createCell(cellindex++).setCellValue(m.getEarlyLeaveTime());
            row.createCell(cellindex++).setBlank();
            row.createCell(cellindex++).setCellValue(m.getLateDay());
            row.createCell(cellindex++).setCellValue(m.getLateTime());
            row.createCell(cellindex++).setBlank();
            row.createCell(cellindex++).setBlank();
        }

        try {
            File xlsxFile = new File("C:/Users/admin/Downloads/" + monthlyKeunTaeDto.getYyyymm() + "monthlyKeunTae" + ".xlsx");
            FileOutputStream fileOut = new FileOutputStream(xlsxFile);
            workbook.write(fileOut);
            workbook.close();
        } catch (Exception e) {
            logger.error("Exception", e);
            throw e;
        }


        return responseDto;
    }
}
