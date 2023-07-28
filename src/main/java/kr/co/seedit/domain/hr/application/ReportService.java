package kr.co.seedit.domain.hr.application;

import kr.co.seedit.domain.company.application.CompanyService;
import kr.co.seedit.domain.hr.dto.BasicSalaryDto;
import kr.co.seedit.global.common.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class ReportService {

    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);
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

            File xlsxFile = new File("C:/Users/admin/Downloads/"+ "seedSample" + ".xlsx");
            FileOutputStream fileOut = new FileOutputStream(xlsxFile);
            workbook.write(fileOut);
            workbook.close();

        } catch (Exception e){
            logger.error("Exception", e);
            throw e;
        }

        return responseDto;
    }
}
