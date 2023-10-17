package kr.co.seedit.domain.hr.application;

import kr.co.seedit.domain.hr.dto.*;
import kr.co.seedit.domain.mapper.seedit.Payroll6InPageDao;
import kr.co.seedit.domain.mapper.seedit.ReportDao;
import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HeaderFooter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);

    private final ReportDao reportDao;
    private final Payroll6InPageDao payroll6InPageDao;
    private final Payroll6InPageService payroll6InPageService;
    private final ResourceLoader resourceLoader;

    @Autowired
    Payroll6InPageService Payroll6InPageService;

    @SuppressWarnings("resource")
    @Transactional
    public ResponseEntity<Resource> downloadFile(ReportParamsDto reportParamsDto) throws Exception {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        XSSFWorkbook workbook = null;

        switch (reportParamsDto.getReportType()) {
            case "ERPIU":    //ERP IU 업로드용 엑셀
                workbook = createERPIU(reportParamsDto);
                break;
            case "Payroll":        //급여대장
                if ("All".equals(reportParamsDto.getPayrollSort()))
                    workbook = createPayroll(reportParamsDto);    //개인별
                else if ("Est".equals(reportParamsDto.getPayrollSort()))
                    workbook = createPayrollEst(reportParamsDto);    //지사별
                break;
            case "Paystub":        //급여명세서
                workbook = createPaystub(reportParamsDto);
                break;
            case "PersonalPayroll":        //개인급여내역
                workbook = createPersonalPayroll(reportParamsDto);
                break;
            case "Payroll6InTable":        //급여표(6쪽)
                workbook = Payroll6InPageService.createPayroll6InPage(reportParamsDto);
                break;
        }

        try {
            workbook.write(byteArrayOutputStream);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } finally {
            if (null != workbook)
                workbook.close();
        }

        String fileName = reportParamsDto.getYyyymm() + "_" + reportParamsDto.getReportType();
        if ("Payroll".equals(reportParamsDto.getReportType())) {
            fileName += "_" + reportParamsDto.getSort();
        }

        return ResponseEntity.ok()
                .header("Content-Transfer-Encoding", "binary")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8") + ".xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new ByteArrayResource(byteArrayOutputStream.toByteArray()));

    }

    // excel sum 수식 파라미터(열 이름, 시작 행, 끝나는 행, 간격)
    private static String determineSumFormula(String cellName, Integer start, Integer end, Integer interval) {
        String formula = "SUM(";
        do {
            formula += cellName + start + ",";
            start += interval;
        } while (start < end);

        formula = formula.substring(0, formula.length() - 1); // 마지막 콤마 제거
        formula += ")";

        String zeroCutFormula = "IF(" + formula + "=0,\"\" ," + formula + ")";    //수식 결과값이 0일 경우 빈칸으로 표시

        return zeroCutFormula;
    }

    //0.0 to 0:00 formatting
    private static String determineTimeFormat(Double time) {
        String result = "";
        if (time != null) {
            Double temp = Math.floor(time);
            result = temp.intValue() + "";
            if (time % 1 == 0.0) {
                result += ":00";
            } else {
                result += ":30";
            }
        } else {
            result = "0:00";
        }

        return result;
    }

    public XSSFWorkbook createERPIU(ReportParamsDto reportParamsDto) throws Exception {
        // Get Data
        List<SalaryExcelDto> salaryExcelDtoList = new ArrayList<>();
        salaryExcelDtoList = reportDao.findERPIUData(reportParamsDto);

        
     // Open Sample Excel
        Resource resource = resourceLoader.getResource("classpath:hr/erpiuSample.xlsx");
        InputStream fis = resource.getInputStream();
        
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        XSSFSheet sheet = workbook.getSheetAt(0);
        int rowindex = 2;
        int cellindex = 0;

        // Excel Header Setting
//        XSSFRow headerRow = sheet.createRow(rowindex++);
//        headerRow.createCell(cellindex++).setCellValue("CD_COMPANY");
//        headerRow.createCell(cellindex++).setCellValue("CD_BIZAREA");
//        headerRow.createCell(cellindex++).setCellValue("YM");
//        headerRow.createCell(cellindex++).setCellValue("CD_EMP");
//        headerRow.createCell(cellindex++).setCellValue("TP_EMP");
//        headerRow.createCell(cellindex++).setCellValue("TP_PAY");
//        headerRow.createCell(cellindex++).setCellValue("NO_SEQ");
//        headerRow.createCell(cellindex++).setCellValue("NO_EMP");
//        headerRow.createCell(cellindex++).setCellValue("AM_PAY01");
//        headerRow.createCell(cellindex++).setCellValue("AM_PAY02");
//        headerRow.createCell(cellindex++).setCellValue("AM_PAY03");
//        headerRow.createCell(cellindex++).setCellValue("AM_PAY04");
//        headerRow.createCell(cellindex++).setCellValue("AM_PAY05");
//        headerRow.createCell(cellindex++).setCellValue("AM_PAY06");
//        headerRow.createCell(cellindex++).setCellValue("AM_PAY07");
//        headerRow.createCell(cellindex++).setCellValue("AM_PAY08");
//        headerRow.createCell(cellindex++).setCellValue("AM_PAY09");
//        headerRow.createCell(cellindex++).setCellValue("AM_PAY10");
//        headerRow.createCell(cellindex++).setCellValue("AM_PAY11");
//        headerRow.createCell(cellindex++).setCellValue("AM_PAY12");
//        headerRow.createCell(cellindex).setCellValue("AM_PAY13");

        // Data Insert
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

        return workbook;
    }

    @Transactional
    public XSSFWorkbook createPayroll(ReportParamsDto reportParamsDto) throws Exception {

        // Get Data
        List<ReportPayrollDto> reportPayrollDtoList = new ArrayList<>();
        if (reportParamsDto.getSort().equals("Salary")) {
            reportPayrollDtoList = reportDao.findPayrollData(reportParamsDto);
        } else if (reportParamsDto.getSort().equals("Bonus")) {
            reportPayrollDtoList = reportDao.findPayrollBonusData(reportParamsDto);
        }
        // Open Sample Excel
        Resource resource = resourceLoader.getResource("classpath:hr/payrollSample.xlsx");
        InputStream fis = resource.getInputStream();

        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        XSSFSheet sheet = workbook.getSheetAt(0);
        XSSFFormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
        int rowindex = 8;
        int cellindex = 0;
        XSSFRow row = null;
        XSSFCell cell = null;

        // Print Setting
        XSSFPrintSetup printSetup = sheet.getPrintSetup();
        printSetup.setPaperSize(XSSFPrintSetup.A4_PAPERSIZE);
        printSetup.setLandscape(true); // 인쇄방향 가로
        printSetup.setFitWidth((short) 1);
        printSetup.setBottomMargin((double) 0.787402);

        Footer footer = sheet.getFooter();
        footer.setRight("page : " + HeaderFooter.page() + " / " + HeaderFooter.numPages());

        // Style Setting
        Font bodyFont = workbook.createFont();
        bodyFont.setFontName("바탕체");
        bodyFont.setFontHeightInPoints((short) 9);

        CellStyle ThinBorderCenterStyle = workbook.createCellStyle();
        ThinBorderCenterStyle.setBorderTop(BorderStyle.THIN);
        ThinBorderCenterStyle.setBorderBottom(BorderStyle.THIN);
        ThinBorderCenterStyle.setBorderLeft(BorderStyle.THIN);
        ThinBorderCenterStyle.setBorderRight(BorderStyle.THIN);
        ThinBorderCenterStyle.setFont(bodyFont);
        ThinBorderCenterStyle.setAlignment(HorizontalAlignment.CENTER);
        ThinBorderCenterStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        ThinBorderCenterStyle.setShrinkToFit(true); // text 셀 맞춤

        CellStyle ThinBorderStyle = workbook.createCellStyle();
        ThinBorderStyle.setBorderTop(BorderStyle.THIN);
        ThinBorderStyle.setBorderBottom(BorderStyle.THIN);
        ThinBorderStyle.setBorderLeft(BorderStyle.THIN);
        ThinBorderStyle.setBorderRight(BorderStyle.THIN);
        ThinBorderStyle.setFont(bodyFont);
        ThinBorderStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        ThinBorderStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0")); // 1000단위 콤마
        ThinBorderStyle.setShrinkToFit(true); // text 셀 맞춤

        CellStyle RightBorderStyle = workbook.createCellStyle();
        RightBorderStyle.setBorderTop(BorderStyle.THIN);
        RightBorderStyle.setBorderBottom(BorderStyle.THIN);
        RightBorderStyle.setBorderLeft(BorderStyle.THIN);
        RightBorderStyle.setBorderRight(BorderStyle.MEDIUM);
        RightBorderStyle.setFont(bodyFont);
        RightBorderStyle.setAlignment(HorizontalAlignment.CENTER);
        RightBorderStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        RightBorderStyle.setShrinkToFit(true); // text 셀 맞춤

        CellStyle TopBorderCenterStyle = workbook.createCellStyle();
        TopBorderCenterStyle.setBorderTop(BorderStyle.MEDIUM);
        TopBorderCenterStyle.setBorderBottom(BorderStyle.THIN);
        TopBorderCenterStyle.setBorderLeft(BorderStyle.THIN);
        TopBorderCenterStyle.setBorderRight(BorderStyle.THIN);
        TopBorderCenterStyle.setFont(bodyFont);
        TopBorderCenterStyle.setAlignment(HorizontalAlignment.CENTER);
        TopBorderCenterStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        TopBorderCenterStyle.setShrinkToFit(true); // text 셀 맞춤

        CellStyle TopBorderStyle = workbook.createCellStyle();
        TopBorderStyle.setBorderTop(BorderStyle.MEDIUM);
        TopBorderStyle.setBorderBottom(BorderStyle.THIN);
        TopBorderStyle.setBorderLeft(BorderStyle.THIN);
        TopBorderStyle.setBorderRight(BorderStyle.THIN);
        TopBorderStyle.setFont(bodyFont);
        TopBorderStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        TopBorderStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0")); // 1000단위 콤마
        TopBorderStyle.setShrinkToFit(true); // text 셀 맞춤

        CellStyle TopRightBorderCenterStyle = workbook.createCellStyle();
        TopRightBorderCenterStyle.setBorderTop(BorderStyle.MEDIUM);
        TopRightBorderCenterStyle.setBorderBottom(BorderStyle.THIN);
        TopRightBorderCenterStyle.setBorderLeft(BorderStyle.THIN);
        TopRightBorderCenterStyle.setBorderRight(BorderStyle.MEDIUM);
        TopRightBorderCenterStyle.setFont(bodyFont);
        TopRightBorderCenterStyle.setAlignment(HorizontalAlignment.CENTER);
        TopRightBorderCenterStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        TopRightBorderCenterStyle.setShrinkToFit(true); // text 셀 맞춤

        CellStyle TopRightBorderStyle = workbook.createCellStyle();
        TopRightBorderStyle.setBorderTop(BorderStyle.MEDIUM);
        TopRightBorderStyle.setBorderBottom(BorderStyle.THIN);
        TopRightBorderStyle.setBorderLeft(BorderStyle.THIN);
        TopRightBorderStyle.setBorderRight(BorderStyle.MEDIUM);
        TopRightBorderStyle.setFont(bodyFont);
        TopRightBorderStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        TopRightBorderStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0")); // 1000단위 콤마
        TopRightBorderStyle.setShrinkToFit(true); // text 셀 맞춤

        CellStyle BottomBorderStyle = workbook.createCellStyle();
        BottomBorderStyle.setBorderTop(BorderStyle.THIN);
        BottomBorderStyle.setBorderBottom(BorderStyle.MEDIUM);
        BottomBorderStyle.setBorderLeft(BorderStyle.THIN);
        BottomBorderStyle.setBorderRight(BorderStyle.THIN);
        BottomBorderStyle.setFont(bodyFont);
        BottomBorderStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        BottomBorderStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0")); // 1000단위 콤마
        BottomBorderStyle.setShrinkToFit(true); // text 셀 맞춤

        CellStyle AllBorderStyle = workbook.createCellStyle();
        AllBorderStyle.setBorderTop(BorderStyle.MEDIUM);
        AllBorderStyle.setBorderBottom(BorderStyle.MEDIUM);
        AllBorderStyle.setBorderLeft(BorderStyle.MEDIUM);
        AllBorderStyle.setBorderRight(BorderStyle.MEDIUM);
        AllBorderStyle.setFont(bodyFont);
        AllBorderStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        AllBorderStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0")); // 1000단위 콤마
        AllBorderStyle.setShrinkToFit(true); // text 셀 맞춤

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
        GrayAllBorderStyle.setShrinkToFit(true); // text 셀 맞춤

        CellStyle NoneBorderStyle1 = workbook.createCellStyle();
        NoneBorderStyle1.setBorderTop(BorderStyle.NONE);
        NoneBorderStyle1.setBorderBottom(BorderStyle.MEDIUM);
        NoneBorderStyle1.setBorderRight(BorderStyle.MEDIUM);
        NoneBorderStyle1.setBorderLeft(BorderStyle.MEDIUM);

        CellStyle NoneBorderStyle2 = workbook.createCellStyle();
        NoneBorderStyle2.setBorderTop(BorderStyle.NONE);
        NoneBorderStyle2.setBorderBottom(BorderStyle.NONE);
        NoneBorderStyle2.setBorderRight(BorderStyle.MEDIUM);
        NoneBorderStyle2.setBorderLeft(BorderStyle.MEDIUM);

        if (reportPayrollDtoList.size() != 0) {
            // 양식 내 상단 날짜 변경
            String yyyy = reportParamsDto.getYyyymm().substring(0, 4);
            String mm = reportParamsDto.getYyyymm().substring(4, 6);

            String dtPay = reportPayrollDtoList.get(0).getDtPay();    //data list의 첫번째 지급일자를 가져옴
            dtPay = dtPay.substring(0, 4) + "년 " + dtPay.substring(4, 6) + "월 " + dtPay.substring(6, 8) + "일";

            row = sheet.getRow(0);
            cell = row.getCell(7);
            cell.setCellValue(yyyy + "년 " + mm + "월분 " + "급여대장");
            row = sheet.getRow(2);
            cell = row.getCell(7);
            cell.setCellValue("[귀속:" + yyyy + "년" + mm + "월] [지급:" + dtPay + "]");

            // 양식 내 상단 사업장 변경
            row = sheet.getRow(2);
            cell = row.getCell(0);
            if (reportParamsDto.getEstId() != null && reportParamsDto.getEstId() != 999) {
                cell.setCellValue(reportPayrollDtoList.get(0).getEstName());
            } else {
                cell.setCellValue("디에이치(주)");
            }

            sheet.setColumnWidth(0, 2500);
            sheet.setColumnWidth(1, 2000);
            sheet.setColumnWidth(2, 2900);
            sheet.setColumnWidth(3, 2400);
            sheet.setColumnWidth(4, 2400);
            sheet.setColumnWidth(5, 2400);
            sheet.setColumnWidth(6, 2400);
            sheet.setColumnWidth(7, 2400);
            sheet.setColumnWidth(8, 2900);
            sheet.setColumnWidth(9, 2400);
            sheet.setColumnWidth(10, 2400);
            sheet.setColumnWidth(11, 2400);
            sheet.setColumnWidth(12, 2400);
            sheet.setColumnWidth(13, 2400);
            sheet.setColumnWidth(14, 2900);
            sheet.setColumnWidth(15, 1000);

            // Data Insert into Excel
            for (ReportPayrollDto m : reportPayrollDtoList) {

                // 1열
                cellindex = 0;
                row = sheet.createRow(rowindex++);
                row.setHeightInPoints((float) 12.8);

                cell = row.createCell(cellindex++);
                cell.setCellValue(m.getEmployeeNumber());
                cell.setCellStyle(TopBorderCenterStyle);

                cell = row.createCell(cellindex++);
                cell.setCellValue(m.getKoreanName());
                cell.setCellStyle(TopRightBorderCenterStyle);

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

                cell = row.createCell(cellindex++);
                cell.setCellValue(m.getNationalPension());
                cell.setCellStyle(TopBorderStyle);
                cell.setCellType(CellType.NUMERIC);

                cell = row.createCell(cellindex++);
                cell.setCellValue(m.getHealthInsurance());
                cell.setCellStyle(TopBorderStyle);
                cell.setCellType(CellType.NUMERIC);

                cell = row.createCell(cellindex++);
                cell.setCellValue(m.getEmploymentInsurance());
                cell.setCellStyle(TopBorderStyle);
                cell.setCellType(CellType.NUMERIC);

                cell = row.createCell(cellindex++);
                cell.setCellValue(m.getCareInsurance());
                cell.setCellStyle(TopBorderStyle);
                cell.setCellType(CellType.NUMERIC);

                cell = row.createCell(cellindex++);
                cell.setCellValue(m.getIncomtax());
                cell.setCellStyle(TopBorderStyle);
                cell.setCellType(CellType.NUMERIC);

                cell = row.createCell(cellindex++);
                cell.setCellValue(m.getResidtax());
                cell.setCellStyle(TopBorderStyle);
                cell.setCellType(CellType.NUMERIC);

                cell = row.createCell(cellindex++);
                cell.setBlank();
                cell.setCellStyle(NoneBorderStyle2);

                // 2열
                cellindex = 0;
                row = sheet.createRow(rowindex++);
                row.setHeightInPoints((float) 12.8);

                cell = row.createCell(cellindex++);
                cell.setCellValue(m.getHireDate());
                cell.setCellStyle(ThinBorderCenterStyle);

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

                cell = row.createCell(cellindex++);
                cell.setCellValue(m.getAdvance());
                cell.setCellStyle(ThinBorderStyle);
                cell.setCellType(CellType.NUMERIC);

                cell = row.createCell(cellindex++);
                cell.setCellValue(m.getOtherTax());
                cell.setCellStyle(ThinBorderStyle);
                cell.setCellType(CellType.NUMERIC);

                cell = row.createCell(cellindex++);
                cell.setCellValue(m.getGyeongjobi());
                cell.setCellStyle(ThinBorderStyle);
                cell.setCellType(CellType.NUMERIC);

                cell = row.createCell(cellindex++);
                cell.setCellValue(m.getYearend());
                cell.setCellStyle(ThinBorderStyle);
                cell.setCellType(CellType.NUMERIC);

                cell = row.createCell(cellindex++);
                cell.setCellValue(m.getHealthInsuranceSettlement());
                cell.setCellStyle(ThinBorderStyle);
                cell.setCellType(CellType.NUMERIC);

                cell = row.createCell(cellindex++);
                cell.setCellValue(m.getTaxSum());
                cell.setCellStyle(AllBorderStyle);
                cell.setCellType(CellType.NUMERIC);

                cell = row.createCell(cellindex++);
                cell.setBlank();
                cell.setCellStyle(NoneBorderStyle2);

                // 3열
                cellindex = 0;
                row = sheet.createRow(rowindex++);
                row.setHeightInPoints((float) 12.8);

                cell = row.createCell(cellindex++);
                cell.setCellValue(m.getRetireDate());
                cell.setCellStyle(ThinBorderCenterStyle);

                cell = row.createCell(cellindex++);
                cell.setCellValue(m.getDepartmentName());
                cell.setCellStyle(RightBorderStyle);

                for (int i = 0; i < 6; i++) { // blank cell 6개
                    cell = row.createCell(cellindex++);
                    cell.setBlank();
                    cell.setCellStyle(ThinBorderCenterStyle);
                }

                cell = row.createCell(cellindex++);
                cell.setCellValue(m.getSalarySum());
                cell.setCellStyle(AllBorderStyle);
                cell.setCellType(CellType.NUMERIC);

                cell = row.createCell(cellindex++);
                cell.setCellValue(m.getCareInsuranceSettlement());
                cell.setCellStyle(ThinBorderStyle);

                cell = row.createCell(cellindex++);
                cell.setCellValue(m.getHolidayTax());
                cell.setCellStyle(ThinBorderStyle);

                for (int i = 0; i < 3; i++) { // blank cell 3개
                    cell = row.createCell(cellindex++);
                    cell.setBlank();
                    cell.setCellStyle(ThinBorderStyle);
                }

                cell = row.createCell(cellindex++);
                cell.setCellStyle(AllBorderStyle);
                cell.setCellFormula(new CellAddress(rowindex - 1, 8) + "-" + new CellAddress(rowindex - 2, 14));

                cell = row.createCell(cellindex++);
                cell.setBlank();
                cell.setCellStyle(NoneBorderStyle1);

            }

            // 리스트 하단 합계

            row = sheet.createRow(rowindex++);
            row.setHeightInPoints((float) 12.8);
            for (cellindex = 0; cellindex < 2; cellindex++) {
                cell = row.createCell(cellindex);
                cell.setCellStyle(GrayAllBorderStyle);
                cell.setCellValue("합계 (" + reportPayrollDtoList.size() + "명)");
            }

            cell = row.createCell(cellindex++);
            cell.setCellStyle(TopBorderStyle);
            cell.setCellFormula(determineSumFormula("C", 9, 9 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setCellStyle(TopBorderStyle);
            cell.setCellFormula(determineSumFormula("D", 9, 9 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setCellStyle(TopBorderStyle);
            cell.setCellFormula(determineSumFormula("E", 9, 9 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setCellStyle(TopBorderStyle);
            cell.setCellFormula(determineSumFormula("F", 9, 9 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setCellStyle(TopBorderStyle);
            cell.setCellFormula(determineSumFormula("G", 9, 9 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setCellStyle(TopBorderStyle);
            cell.setCellFormula(determineSumFormula("H", 9, 9 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setCellStyle(TopRightBorderStyle);
            cell.setCellFormula(determineSumFormula("I", 9, 9 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setCellStyle(TopBorderStyle);
            cell.setCellFormula(determineSumFormula("J", 9, 9 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setCellStyle(TopBorderStyle);
            cell.setCellFormula(determineSumFormula("K", 9, 9 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setCellStyle(TopBorderStyle);
            cell.setCellFormula(determineSumFormula("L", 9, 9 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setCellStyle(TopBorderStyle);
            cell.setCellFormula(determineSumFormula("M", 9, 9 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setCellStyle(TopBorderStyle);
            cell.setCellFormula(determineSumFormula("N", 9, 9 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setCellStyle(TopBorderStyle);
            cell.setCellFormula(determineSumFormula("O", 9, 9 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setBlank();
            cell.setCellStyle(NoneBorderStyle2);

            row = sheet.createRow(rowindex++);
            row.setHeightInPoints((float) 12.8);
            for (cellindex = 0; cellindex < 2; cellindex++) {
                cell = row.createCell(cellindex);
                cell.setCellStyle(GrayAllBorderStyle);
                //             cell.setCellValue("합계 ("+reportPayrollDtoList.size()+"명)");
            }

            cell = row.createCell(cellindex++);
            cell.setCellStyle(ThinBorderStyle);
            cell.setCellFormula(determineSumFormula("C", 10, 10 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setCellStyle(ThinBorderStyle);
            cell.setCellFormula(determineSumFormula("D", 10, 10 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setCellStyle(ThinBorderStyle);
            cell.setCellFormula(determineSumFormula("E", 10, 10 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setCellStyle(ThinBorderStyle);
            cell.setCellFormula(determineSumFormula("F", 10, 10 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setCellStyle(ThinBorderStyle);
            cell.setCellFormula(determineSumFormula("G", 10, 10 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setCellStyle(ThinBorderStyle);
            cell.setCellFormula(determineSumFormula("H", 10, 10 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setCellStyle(RightBorderStyle);
            cell.setBlank();

            cell = row.createCell(cellindex++);
            cell.setCellStyle(ThinBorderStyle);
            cell.setCellFormula(determineSumFormula("J", 10, 10 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setCellStyle(ThinBorderStyle);
            cell.setCellFormula(determineSumFormula("K", 10, 10 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setCellStyle(ThinBorderStyle);
            cell.setCellFormula(determineSumFormula("L", 10, 10 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setCellStyle(ThinBorderStyle);
            cell.setCellFormula(determineSumFormula("M", 10, 10 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setCellStyle(ThinBorderStyle);
            cell.setCellFormula(determineSumFormula("N", 10, 10 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setCellStyle(AllBorderStyle);
            cell.setCellFormula(determineSumFormula("O", 10, 10 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setBlank();
            cell.setCellStyle(NoneBorderStyle2);

            row = sheet.createRow(rowindex++);
            row.setHeightInPoints((float) 12.8);
            for (cellindex = 0; cellindex < 2; cellindex++) {
                cell = row.createCell(cellindex);
                cell.setCellStyle(GrayAllBorderStyle);
                //             cell.setCellValue("합계 ("+reportPayrollDtoList.size()+"명)");
            }

            for (int i = 0; i < 6; i++) { // blank cell 6개
                cell = row.createCell(cellindex++);
                cell.setBlank();
                cell.setCellStyle(BottomBorderStyle);
            }

            cell = row.createCell(cellindex++);
            cell.setCellStyle(AllBorderStyle);
            cell.setCellFormula(determineSumFormula("I", 11, 11 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setCellStyle(BottomBorderStyle);
            cell.setCellFormula(determineSumFormula("J", 11, 11 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setCellStyle(BottomBorderStyle);
            cell.setCellFormula(determineSumFormula("K", 11, 11 + reportPayrollDtoList.size() * 3, 3));

            for (int i = 0; i < 3; i++) { // blank cell 3개
                cell = row.createCell(cellindex++);
                cell.setBlank();
                cell.setCellStyle(BottomBorderStyle);
            }

            cell = row.createCell(cellindex++);
            cell.setCellStyle(AllBorderStyle);
            cell.setCellFormula(determineSumFormula("O", 11, 11 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setBlank();
            cell.setCellStyle(NoneBorderStyle1);

            formulaEvaluator.evaluateAll(); // 수식 전체 실행

            sheet.addMergedRegion(new CellRangeAddress((8 + reportPayrollDtoList.size() * 3),
                    (9 + reportPayrollDtoList.size() * 3) + 1, 0, 1));

            //        workbook.setPrintArea(0, "$A$1:$P$35");

            for (int i = 5; i < rowindex; i++) {
                row = sheet.getRow(i);
                for (int k = 0; k < 15; k++) {
                    cell = row.getCell(k);
                    if (cell != null) {
                        if (cell.getCellType() == CellType.NUMERIC) {
                            if (cell.getNumericCellValue() == 0) {
                                cell.setBlank();
                            }
                        }
                    }
                }
            }
        }
        return workbook;
    }

    @Transactional
    public XSSFWorkbook createPayrollEst(ReportParamsDto reportParamsDto) throws Exception {

        // Get Data
        List<ReportPayrollDto> reportPayrollDtoList = new ArrayList<>();
        if (reportParamsDto.getSort().equals("Salary")) {
            reportPayrollDtoList = reportDao.findEstPayrollData(reportParamsDto);
        } else if (reportParamsDto.getSort().equals("Bonus")) {
            reportPayrollDtoList = reportDao.findEstPayrollBonusData(reportParamsDto);
        }
        // Open Sample Excel
        Resource resource = resourceLoader.getResource("classpath:hr/payrollSample.xlsx");
        InputStream fis = resource.getInputStream();

        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        XSSFSheet sheet = workbook.getSheetAt(0);
        XSSFFormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
        int rowindex = 8;
        int cellindex = 0;
        XSSFRow row = null;
        XSSFCell cell = null;

        // Print Setting
        XSSFPrintSetup printSetup = sheet.getPrintSetup();
        printSetup.setPaperSize(XSSFPrintSetup.A4_PAPERSIZE);
        printSetup.setLandscape(true); // 인쇄방향 가로
        printSetup.setFitWidth((short) 1);
        printSetup.setBottomMargin((double) 0.787402);

        Footer footer = sheet.getFooter();
        footer.setRight("page : " + HeaderFooter.page() + " / " + HeaderFooter.numPages());

        // Style Setting
        Font bodyFont = workbook.createFont();
        bodyFont.setFontName("바탕체");
        bodyFont.setFontHeightInPoints((short) 9);

        CellStyle ThinBorderCenterStyle = workbook.createCellStyle();
        ThinBorderCenterStyle.setBorderTop(BorderStyle.THIN);
        ThinBorderCenterStyle.setBorderBottom(BorderStyle.THIN);
        ThinBorderCenterStyle.setBorderLeft(BorderStyle.THIN);
        ThinBorderCenterStyle.setBorderRight(BorderStyle.THIN);
        ThinBorderCenterStyle.setFont(bodyFont);
        ThinBorderCenterStyle.setAlignment(HorizontalAlignment.CENTER);
        ThinBorderCenterStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        ThinBorderCenterStyle.setShrinkToFit(true); // text 셀 맞춤

        CellStyle ThinBorderStyle = workbook.createCellStyle();
        ThinBorderStyle.setBorderTop(BorderStyle.THIN);
        ThinBorderStyle.setBorderBottom(BorderStyle.THIN);
        ThinBorderStyle.setBorderLeft(BorderStyle.THIN);
        ThinBorderStyle.setBorderRight(BorderStyle.THIN);
        ThinBorderStyle.setFont(bodyFont);
        ThinBorderStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        ThinBorderStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0")); // 1000단위 콤마
        ThinBorderStyle.setShrinkToFit(true); // text 셀 맞춤

        CellStyle RightBorderStyle = workbook.createCellStyle();
        RightBorderStyle.setBorderTop(BorderStyle.THIN);
        RightBorderStyle.setBorderBottom(BorderStyle.THIN);
        RightBorderStyle.setBorderLeft(BorderStyle.THIN);
        RightBorderStyle.setBorderRight(BorderStyle.MEDIUM);
        RightBorderStyle.setFont(bodyFont);
        RightBorderStyle.setAlignment(HorizontalAlignment.CENTER);
        RightBorderStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        RightBorderStyle.setShrinkToFit(true); // text 셀 맞춤

        CellStyle TopBorderCenterStyle = workbook.createCellStyle();
        TopBorderCenterStyle.setBorderTop(BorderStyle.MEDIUM);
        TopBorderCenterStyle.setBorderBottom(BorderStyle.THIN);
        TopBorderCenterStyle.setBorderLeft(BorderStyle.THIN);
        TopBorderCenterStyle.setBorderRight(BorderStyle.THIN);
        TopBorderCenterStyle.setFont(bodyFont);
        TopBorderCenterStyle.setAlignment(HorizontalAlignment.CENTER);
        TopBorderCenterStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        TopBorderCenterStyle.setShrinkToFit(true); // text 셀 맞춤

        CellStyle TopBorderStyle = workbook.createCellStyle();
        TopBorderStyle.setBorderTop(BorderStyle.MEDIUM);
        TopBorderStyle.setBorderBottom(BorderStyle.THIN);
        TopBorderStyle.setBorderLeft(BorderStyle.THIN);
        TopBorderStyle.setBorderRight(BorderStyle.THIN);
        TopBorderStyle.setFont(bodyFont);
        TopBorderStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        TopBorderStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0")); // 1000단위 콤마
        TopBorderStyle.setShrinkToFit(true); // text 셀 맞춤

        CellStyle TopRightBorderCenterStyle = workbook.createCellStyle();
        TopRightBorderCenterStyle.setBorderTop(BorderStyle.MEDIUM);
        TopRightBorderCenterStyle.setBorderBottom(BorderStyle.THIN);
        TopRightBorderCenterStyle.setBorderLeft(BorderStyle.THIN);
        TopRightBorderCenterStyle.setBorderRight(BorderStyle.MEDIUM);
        TopRightBorderCenterStyle.setFont(bodyFont);
        TopRightBorderCenterStyle.setAlignment(HorizontalAlignment.CENTER);
        TopRightBorderCenterStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        TopRightBorderCenterStyle.setShrinkToFit(true); // text 셀 맞춤

        CellStyle TopRightBorderStyle = workbook.createCellStyle();
        TopRightBorderStyle.setBorderTop(BorderStyle.MEDIUM);
        TopRightBorderStyle.setBorderBottom(BorderStyle.THIN);
        TopRightBorderStyle.setBorderLeft(BorderStyle.THIN);
        TopRightBorderStyle.setBorderRight(BorderStyle.MEDIUM);
        TopRightBorderStyle.setFont(bodyFont);
        TopRightBorderStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        TopRightBorderStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0")); // 1000단위 콤마
        TopRightBorderStyle.setShrinkToFit(true); // text 셀 맞춤

        CellStyle BottomBorderStyle = workbook.createCellStyle();
        BottomBorderStyle.setBorderTop(BorderStyle.THIN);
        BottomBorderStyle.setBorderBottom(BorderStyle.MEDIUM);
        BottomBorderStyle.setBorderLeft(BorderStyle.THIN);
        BottomBorderStyle.setBorderRight(BorderStyle.THIN);
        BottomBorderStyle.setFont(bodyFont);
        BottomBorderStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        BottomBorderStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0")); // 1000단위 콤마
        BottomBorderStyle.setShrinkToFit(true); // text 셀 맞춤

        CellStyle AllBorderStyle = workbook.createCellStyle();
        AllBorderStyle.setBorderTop(BorderStyle.MEDIUM);
        AllBorderStyle.setBorderBottom(BorderStyle.MEDIUM);
        AllBorderStyle.setBorderLeft(BorderStyle.MEDIUM);
        AllBorderStyle.setBorderRight(BorderStyle.MEDIUM);
        AllBorderStyle.setFont(bodyFont);
        AllBorderStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        AllBorderStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0")); // 1000단위 콤마
        AllBorderStyle.setShrinkToFit(true); // text 셀 맞춤

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
        GrayAllBorderStyle.setShrinkToFit(true); // text 셀 맞춤

        CellStyle SumBorderStyle = workbook.createCellStyle();
        SumBorderStyle.setBorderTop(BorderStyle.MEDIUM);
        SumBorderStyle.setBorderBottom(BorderStyle.MEDIUM);
        SumBorderStyle.setBorderLeft(BorderStyle.MEDIUM);
        SumBorderStyle.setBorderRight(BorderStyle.MEDIUM);
        SumBorderStyle.setFont(bodyFont);
        SumBorderStyle.setAlignment(HorizontalAlignment.CENTER);
        SumBorderStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        SumBorderStyle.setShrinkToFit(true); // text 셀 맞춤

        CellStyle NoneBorderStyle1 = workbook.createCellStyle();
        NoneBorderStyle1.setBorderTop(BorderStyle.NONE);
        NoneBorderStyle1.setBorderBottom(BorderStyle.MEDIUM);
        NoneBorderStyle1.setBorderRight(BorderStyle.MEDIUM);
        NoneBorderStyle1.setBorderLeft(BorderStyle.MEDIUM);

        CellStyle NoneBorderStyle2 = workbook.createCellStyle();
        NoneBorderStyle2.setBorderTop(BorderStyle.NONE);
        NoneBorderStyle2.setBorderBottom(BorderStyle.NONE);
        NoneBorderStyle2.setBorderRight(BorderStyle.MEDIUM);
        NoneBorderStyle2.setBorderLeft(BorderStyle.MEDIUM);

        int cntSum = 0;        //하단 총합계
        if (reportPayrollDtoList.size() != 0) {
            // 양식 내 상단 날짜 변경
            String yyyy = reportParamsDto.getYyyymm().substring(0, 4);
            String mm = reportParamsDto.getYyyymm().substring(4, 6);

            String dtPay = reportPayrollDtoList.get(0).getDtPay();    //data list의 첫번째 지급일자를 가져옴
            dtPay = dtPay.substring(0, 4) + "년 " + dtPay.substring(4, 6) + "월 " + dtPay.substring(6, 8) + "일";

            row = sheet.getRow(0);
            cell = row.getCell(7);
            cell.setCellValue(yyyy + "년 " + mm + "월분 " + "급여대장");
            row = sheet.getRow(2);
            cell = row.getCell(7);
            cell.setCellValue("[귀속:" + yyyy + "년" + mm + "월] [지급:" + dtPay + "]");

            // 양식 내 상단 사업장 변경
            row = sheet.getRow(2);
            cell = row.getCell(0);
            cell.setCellValue("디에이치(주)/본사");

            cell = row.getCell(1);
            cell.setCellValue("정렬:");
            cell = row.getCell(2);
            cell.setCellValue("부서>직급순");


            sheet.setColumnWidth(0, 2500);
            sheet.setColumnWidth(1, 2000);
            sheet.setColumnWidth(2, 2900);
            sheet.setColumnWidth(3, 2400);
            sheet.setColumnWidth(4, 2400);
            sheet.setColumnWidth(5, 2400);
            sheet.setColumnWidth(6, 2400);
            sheet.setColumnWidth(7, 2400);
            sheet.setColumnWidth(8, 2900);
            sheet.setColumnWidth(9, 2400);
            sheet.setColumnWidth(10, 2400);
            sheet.setColumnWidth(11, 2400);
            sheet.setColumnWidth(12, 2400);
            sheet.setColumnWidth(13, 2400);
            sheet.setColumnWidth(14, 2900);
            sheet.setColumnWidth(15, 1000);

            // Data Insert into Excel
            for (ReportPayrollDto m : reportPayrollDtoList) {
                cntSum += m.getCnt();
                // 1열
                cellindex = 0;
                row = sheet.createRow(rowindex++);
                row.setHeightInPoints((float) 12.8);

                cell = row.createCell(cellindex++);
                cell.setCellValue(m.getEstName() + " (" + m.getCnt() + "명)");
                cell.setCellStyle(GrayAllBorderStyle);

                row.createCell(cellindex++).setCellStyle(GrayAllBorderStyle);

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

                cell = row.createCell(cellindex++);
                cell.setCellValue(m.getNationalPension());
                cell.setCellStyle(TopBorderStyle);
                cell.setCellType(CellType.NUMERIC);

                cell = row.createCell(cellindex++);
                cell.setCellValue(m.getHealthInsurance());
                cell.setCellStyle(TopBorderStyle);
                cell.setCellType(CellType.NUMERIC);

                cell = row.createCell(cellindex++);
                cell.setCellValue(m.getEmploymentInsurance());
                cell.setCellStyle(TopBorderStyle);
                cell.setCellType(CellType.NUMERIC);

                cell = row.createCell(cellindex++);
                cell.setCellValue(m.getCareInsurance());
                cell.setCellStyle(TopBorderStyle);
                cell.setCellType(CellType.NUMERIC);

                cell = row.createCell(cellindex++);
                cell.setCellValue(m.getIncomtax());
                cell.setCellStyle(TopBorderStyle);
                cell.setCellType(CellType.NUMERIC);

                cell = row.createCell(cellindex++);
                cell.setCellValue(m.getResidtax());
                cell.setCellStyle(TopBorderStyle);
                cell.setCellType(CellType.NUMERIC);

                cell = row.createCell(cellindex++);
                cell.setBlank();
                cell.setCellStyle(NoneBorderStyle2);

                // 2열
                cellindex = 0;
                row = sheet.createRow(rowindex++);
                row.setHeightInPoints((float) 12.8);

                row.createCell(cellindex++).setCellStyle(GrayAllBorderStyle);
                row.createCell(cellindex++).setCellStyle(GrayAllBorderStyle);

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

                cell = row.createCell(cellindex++);
                cell.setCellValue(m.getAdvance());
                cell.setCellStyle(ThinBorderStyle);
                cell.setCellType(CellType.NUMERIC);

                cell = row.createCell(cellindex++);
                cell.setCellValue(m.getOtherTax());
                cell.setCellStyle(ThinBorderStyle);
                cell.setCellType(CellType.NUMERIC);

                cell = row.createCell(cellindex++);
                cell.setCellValue(m.getGyeongjobi());
                cell.setCellStyle(ThinBorderStyle);
                cell.setCellType(CellType.NUMERIC);

                cell = row.createCell(cellindex++);
                cell.setCellValue(m.getYearend());
                cell.setCellStyle(ThinBorderStyle);
                cell.setCellType(CellType.NUMERIC);

                cell = row.createCell(cellindex++);
                cell.setCellValue(m.getHealthInsuranceSettlement());
                cell.setCellStyle(ThinBorderStyle);
                cell.setCellType(CellType.NUMERIC);

                cell = row.createCell(cellindex++);
                cell.setCellValue(m.getTaxSum());
                cell.setCellStyle(AllBorderStyle);
                cell.setCellType(CellType.NUMERIC);

                cell = row.createCell(cellindex++);
                cell.setBlank();
                cell.setCellStyle(NoneBorderStyle2);

                // 3열
                cellindex = 0;
                row = sheet.createRow(rowindex++);
                row.setHeightInPoints((float) 12.8);

                row.createCell(cellindex++).setCellStyle(GrayAllBorderStyle);
                row.createCell(cellindex++).setCellStyle(GrayAllBorderStyle);

                for (int i = 0; i < 6; i++) { // blank cell 6개
                    cell = row.createCell(cellindex++);
                    cell.setBlank();
                    cell.setCellStyle(ThinBorderCenterStyle);
                }

                cell = row.createCell(cellindex++);
                cell.setCellValue(m.getSalarySum());
                cell.setCellStyle(AllBorderStyle);
                cell.setCellType(CellType.NUMERIC);

                cell = row.createCell(cellindex++);
                cell.setCellValue(m.getCareInsuranceSettlement());
                cell.setCellStyle(ThinBorderStyle);

                cell = row.createCell(cellindex++);
                cell.setCellValue(m.getHolidayTax());
                cell.setCellStyle(ThinBorderStyle);

                for (int i = 0; i < 3; i++) { // blank cell 3개
                    cell = row.createCell(cellindex++);
                    cell.setBlank();
                    cell.setCellStyle(ThinBorderStyle);
                }

                cell = row.createCell(cellindex++);
                cell.setCellStyle(AllBorderStyle);
                cell.setCellFormula(new CellAddress(rowindex - 1, 8) + "-" + new CellAddress(rowindex - 2, 14));

                cell = row.createCell(cellindex++);
                cell.setBlank();
                cell.setCellStyle(NoneBorderStyle1);

                sheet.addMergedRegion(new CellRangeAddress(rowindex - 3, rowindex - 1, 0, 1));

            }

            // 리스트 하단 합계
            row = sheet.createRow(rowindex++);
            row.setHeightInPoints((float) 12.8);
            for (cellindex = 0; cellindex < 2; cellindex++) {
                cell = row.createCell(cellindex);
                cell.setCellStyle(SumBorderStyle);
                cell.setCellValue("합계 (" + cntSum + "명)");
            }

            cell = row.createCell(cellindex++);
            cell.setCellStyle(TopBorderStyle);
            cell.setCellFormula(determineSumFormula("C", 9, 9 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setCellStyle(TopBorderStyle);
            cell.setCellFormula(determineSumFormula("D", 9, 9 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setCellStyle(TopBorderStyle);
            cell.setCellFormula(determineSumFormula("E", 9, 9 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setCellStyle(TopBorderStyle);
            cell.setCellFormula(determineSumFormula("F", 9, 9 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setCellStyle(TopBorderStyle);
            cell.setCellFormula(determineSumFormula("G", 9, 9 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setCellStyle(TopBorderStyle);
            cell.setCellFormula(determineSumFormula("H", 9, 9 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setCellStyle(TopRightBorderStyle);
            cell.setCellFormula(determineSumFormula("I", 9, 9 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setCellStyle(TopBorderStyle);
            cell.setCellFormula(determineSumFormula("J", 9, 9 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setCellStyle(TopBorderStyle);
            cell.setCellFormula(determineSumFormula("K", 9, 9 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setCellStyle(TopBorderStyle);
            cell.setCellFormula(determineSumFormula("L", 9, 9 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setCellStyle(TopBorderStyle);
            cell.setCellFormula(determineSumFormula("M", 9, 9 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setCellStyle(TopBorderStyle);
            cell.setCellFormula(determineSumFormula("N", 9, 9 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setCellStyle(TopBorderStyle);
            cell.setCellFormula(determineSumFormula("O", 9, 9 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setBlank();
            cell.setCellStyle(NoneBorderStyle2);

            row = sheet.createRow(rowindex++);
            row.setHeightInPoints((float) 12.8);
            for (cellindex = 0; cellindex < 2; cellindex++) {
                cell = row.createCell(cellindex);
                cell.setCellStyle(SumBorderStyle);
            }

            cell = row.createCell(cellindex++);
            cell.setCellStyle(ThinBorderStyle);
            cell.setCellFormula(determineSumFormula("C", 10, 10 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setCellStyle(ThinBorderStyle);
            cell.setCellFormula(determineSumFormula("D", 10, 10 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setCellStyle(ThinBorderStyle);
            cell.setCellFormula(determineSumFormula("E", 10, 10 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setCellStyle(ThinBorderStyle);
            cell.setCellFormula(determineSumFormula("F", 10, 10 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setCellStyle(ThinBorderStyle);
            cell.setCellFormula(determineSumFormula("G", 10, 10 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setCellStyle(ThinBorderStyle);
            cell.setCellFormula(determineSumFormula("H", 10, 10 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setCellStyle(RightBorderStyle);
            cell.setBlank();

            cell = row.createCell(cellindex++);
            cell.setCellStyle(ThinBorderStyle);
            cell.setCellFormula(determineSumFormula("J", 10, 10 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setCellStyle(ThinBorderStyle);
            cell.setCellFormula(determineSumFormula("K", 10, 10 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setCellStyle(ThinBorderStyle);
            cell.setCellFormula(determineSumFormula("L", 10, 10 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setCellStyle(ThinBorderStyle);
            cell.setCellFormula(determineSumFormula("M", 10, 10 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setCellStyle(ThinBorderStyle);
            cell.setCellFormula(determineSumFormula("N", 10, 10 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setCellStyle(AllBorderStyle);
            cell.setCellFormula(determineSumFormula("O", 10, 10 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setBlank();
            cell.setCellStyle(NoneBorderStyle2);

            row = sheet.createRow(rowindex++);
            row.setHeightInPoints((float) 12.8);
            for (cellindex = 0; cellindex < 2; cellindex++) {
                cell = row.createCell(cellindex);
                cell.setCellStyle(SumBorderStyle);
            }

            for (int i = 0; i < 6; i++) { // blank cell 6개
                cell = row.createCell(cellindex++);
                cell.setBlank();
                cell.setCellStyle(BottomBorderStyle);
            }

            cell = row.createCell(cellindex++);
            cell.setCellStyle(AllBorderStyle);
            cell.setCellFormula(determineSumFormula("I", 11, 11 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setCellStyle(BottomBorderStyle);
            cell.setCellFormula(determineSumFormula("J", 11, 11 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setCellStyle(BottomBorderStyle);
            cell.setCellFormula(determineSumFormula("K", 11, 11 + reportPayrollDtoList.size() * 3, 3));

            for (int i = 0; i < 3; i++) { // blank cell 3개
                cell = row.createCell(cellindex++);
                cell.setBlank();
                cell.setCellStyle(BottomBorderStyle);
            }

            cell = row.createCell(cellindex++);
            cell.setCellStyle(AllBorderStyle);
            cell.setCellFormula(determineSumFormula("O", 11, 11 + reportPayrollDtoList.size() * 3, 3));

            cell = row.createCell(cellindex++);
            cell.setBlank();
            cell.setCellStyle(NoneBorderStyle1);

            formulaEvaluator.evaluateAll(); // 수식 전체 실행

            sheet.addMergedRegion(new CellRangeAddress((8 + reportPayrollDtoList.size() * 3),
                    (9 + reportPayrollDtoList.size() * 3) + 1, 0, 1));

//	        workbook.setPrintArea(0, "$A$1:$P$35");

            for (int i = 5; i < rowindex; i++) {
                row = sheet.getRow(i);
                for (int k = 0; k < 15; k++) {
                    cell = row.getCell(k);
                    if (cell != null) {
                        if (cell.getCellType() == CellType.NUMERIC) {
                            if (cell.getNumericCellValue() == 0) {
                                cell.setBlank();
                            }
                        }
                    }
                }
            }
        }
        return workbook;
    }

    @Transactional
    public XSSFWorkbook createPaystub(ReportParamsDto reportParamsDto) throws Exception {

        // Get Data
        List<ReportPayrollDto> reportPayrollDtoList = new ArrayList<>();

        if (reportParamsDto.getSort().equals("Salary")) {
            reportPayrollDtoList = reportDao.findPayrollData(reportParamsDto);
        } else if (reportParamsDto.getSort().equals("Bonus")) {
            reportPayrollDtoList = reportDao.findPayrollBonusData(reportParamsDto);
        }
        String[] payArr = {"기본급", "연차수당", "연장수당1", "연장수당2", "야간수당1", "야간수당2", "휴일수당1", "휴일수당2", "직책수당", "기타수당", "보조금",
                "교통비", "식대"};
        String[] taxArr = {"국민연금", "건강보험", "고용보험", "장기요양보험료", "소득세", "지방소득세", "가불금", "기타공제", "경조비", "연말정산", "건강보험정산",
                "장기요양보험정산", "연차수당"};

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFFormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
        XSSFSheet sheet = workbook.createSheet(reportParamsDto.getYyyymm() + "Paystub");
        int rowindex = 0;
        int cellindex = 0;
        XSSFRow row = null;
        XSSFCell cell = null;

        // Print Setting
        XSSFPrintSetup printSetup = sheet.getPrintSetup();
        printSetup.setPaperSize(XSSFPrintSetup.A4_PAPERSIZE);
        printSetup.setLandscape(false);        //인쇄방향 세로
        printSetup.setFitWidth((short) 1);
        printSetup.setBottomMargin((double) 6.496063);
        printSetup.setScale((short) 85);

        // Style Setting
        Font titleFont = workbook.createFont();
        titleFont.setFontName("바탕체");
        titleFont.setFontHeightInPoints((short) 16);
        titleFont.setBold(true);

        Font headerFont = workbook.createFont();
        headerFont.setFontName("돋움체");
        headerFont.setFontHeightInPoints((short) 11);

        Font headerFont2 = workbook.createFont();
        headerFont2.setFontName("돋움체");
        headerFont2.setFontHeightInPoints((short) 10);
        headerFont2.setBold(true);

        Font headerFont3 = workbook.createFont();
        headerFont3.setFontName("바탕체");
        headerFont3.setFontHeightInPoints((short) 10);
        headerFont3.setBold(true);

        Font bodyFont = workbook.createFont();
        bodyFont.setFontName("바탕체");
        bodyFont.setFontHeightInPoints((short) 9);

        Font footerfont = workbook.createFont();
        footerfont.setFontName("돋움체");
        footerfont.setFontHeightInPoints((short) 9);

        Font footerBatangfont = workbook.createFont();
        footerBatangfont.setFontName("바탕체");
        footerBatangfont.setFontHeightInPoints((short) 9);

        CellStyle TitleStyle = workbook.createCellStyle();
        TitleStyle.setFont(titleFont);
        TitleStyle.setAlignment(HorizontalAlignment.CENTER);
        TitleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle HeaderStyle = workbook.createCellStyle();
        HeaderStyle.setFont(headerFont);
        HeaderStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle TopLeftBorderHeaderStyle = workbook.createCellStyle();
        TopLeftBorderHeaderStyle.setBorderTop(BorderStyle.THIN);
        TopLeftBorderHeaderStyle.setBorderLeft(BorderStyle.THIN);
        TopLeftBorderHeaderStyle.setFont(headerFont);
        TopLeftBorderHeaderStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle LeftBorderHeaderStyle = workbook.createCellStyle();
        LeftBorderHeaderStyle.setBorderLeft(BorderStyle.THIN);
        LeftBorderHeaderStyle.setFont(headerFont);
        LeftBorderHeaderStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle TopBorderHeaderStyle = workbook.createCellStyle();
        TopBorderHeaderStyle.setBorderTop(BorderStyle.THIN);
        TopBorderHeaderStyle.setFont(headerFont);
        TopBorderHeaderStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle TopRightBorderHeaderStyle = workbook.createCellStyle();
        TopRightBorderHeaderStyle.setBorderTop(BorderStyle.THIN);
        TopRightBorderHeaderStyle.setBorderRight(BorderStyle.THIN);
        TopRightBorderHeaderStyle.setFont(headerFont);
        TopRightBorderHeaderStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle RightBorderHeaderStyle = workbook.createCellStyle();
        RightBorderHeaderStyle.setBorderRight(BorderStyle.THIN);
        RightBorderHeaderStyle.setFont(headerFont);
        RightBorderHeaderStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle LeftRightBorderBodyStyle = workbook.createCellStyle();
        LeftRightBorderBodyStyle.setBorderLeft(BorderStyle.THIN);
        LeftRightBorderBodyStyle.setBorderRight(BorderStyle.THIN);
        LeftRightBorderBodyStyle.setFont(bodyFont);
        LeftRightBorderBodyStyle.setAlignment(HorizontalAlignment.CENTER);
        LeftRightBorderBodyStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle NumberBodyStyle = workbook.createCellStyle();
        NumberBodyStyle.setBorderRight(BorderStyle.THIN);
        NumberBodyStyle.setFont(bodyFont);
        NumberBodyStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        NumberBodyStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0")); // 1000단위 콤마

        CellStyle GrayBorderStyle = workbook.createCellStyle();
        GrayBorderStyle.setBorderTop(BorderStyle.THIN);
        GrayBorderStyle.setBorderBottom(BorderStyle.THIN);
        GrayBorderStyle.setBorderLeft(BorderStyle.THIN);
        GrayBorderStyle.setBorderRight(BorderStyle.THIN);
        GrayBorderStyle.setFont(headerFont2);
        GrayBorderStyle.setAlignment(HorizontalAlignment.CENTER);
        GrayBorderStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        GrayBorderStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        GrayBorderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle NumberGrayBorderStyle = workbook.createCellStyle();
        NumberGrayBorderStyle.setBorderTop(BorderStyle.THIN);
        NumberGrayBorderStyle.setBorderBottom(BorderStyle.THIN);
        NumberGrayBorderStyle.setBorderLeft(BorderStyle.THIN);
        NumberGrayBorderStyle.setBorderRight(BorderStyle.THIN);
        NumberGrayBorderStyle.setFont(headerFont3);
        NumberGrayBorderStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        NumberGrayBorderStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        NumberGrayBorderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        NumberGrayBorderStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0")); // 1000단위 콤마

        CellStyle FooterStyle = workbook.createCellStyle();
        FooterStyle.setFont(footerfont);
        FooterStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle FooterRightStyle = workbook.createCellStyle();
        FooterRightStyle.setFont(footerBatangfont);
        FooterRightStyle.setAlignment(HorizontalAlignment.RIGHT);
        FooterRightStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        sheet.setColumnWidth(0, 3000);
        sheet.setColumnWidth(1, 3000);
        sheet.setColumnWidth(2, 6000);
        sheet.setColumnWidth(3, 3000);
        sheet.setColumnWidth(4, 3000);
        sheet.setColumnWidth(5, 6000);

        for (ReportPayrollDto m : reportPayrollDtoList) {
            cellindex = 0;
            // 1열
            row = sheet.createRow(rowindex++);
            for (int i = 0; i < 6; i++) {
                row.createCell(cellindex++);
            }
            cell = row.getCell(0);
            String yyyy = reportParamsDto.getYyyymm().substring(0, 4);
            String mm = reportParamsDto.getYyyymm().substring(4, 6);
            cell.setCellValue(yyyy + "년 " + mm + "월분 급여명세서");
            sheet.addMergedRegion(new CellRangeAddress(rowindex - 1, rowindex - 1, 0, 5));
            cell.setCellStyle(TitleStyle);
            // 2열 (공백)
            sheet.createRow(rowindex++).setHeightInPoints((short) 10);

            // 3열
            cellindex = 0;
            row = sheet.createRow(rowindex++);
            row.setHeightInPoints((short) 21);

            cell = row.createCell(cellindex++);
            cell.setCellValue("사원코드 :");
            cell.setCellStyle(TopLeftBorderHeaderStyle);
            cell = row.createCell(cellindex++);
            cell.setCellValue(m.getEmployeeNumber());
            cell.setCellStyle(TopBorderHeaderStyle);

            cell = row.createCell(cellindex++);
            cell.setCellValue("사 원 명 : " + m.getKoreanName());
            cell.setCellStyle(TopBorderHeaderStyle);
            row.createCell(cellindex++).setCellStyle(TopBorderHeaderStyle);

            cell = row.createCell(cellindex++);
            cell.setCellValue("입 사 일 :");
            cell.setCellStyle(TopBorderHeaderStyle);
            cell = row.createCell(cellindex++);
            cell.setCellValue(m.getHireDate());
            cell.setCellStyle(TopRightBorderHeaderStyle);

            // 4열
            cellindex = 0;
            row = sheet.createRow(rowindex++);
            row.setHeightInPoints((short) 21);

            cell = row.createCell(cellindex++);
            cell.setCellValue("부    서 :");
            cell.setCellStyle(LeftBorderHeaderStyle);
            cell = row.createCell(cellindex++);
            cell.setCellValue(m.getDepartmentName());
            cell.setCellStyle(HeaderStyle);

            cell = row.createCell(cellindex++);
            cell.setCellValue("직    급 : " + m.getDefinedName());
            cell.setCellStyle(HeaderStyle);
            cell = row.createCell(cellindex++);

            cell = row.createCell(cellindex++);
            cell.setCellValue("호    봉 :");
            cell.setCellStyle(HeaderStyle);
            row.createCell(cellindex++).setCellStyle(RightBorderHeaderStyle); // 호봉은 원래 없으나, 양식상 넣음

            // 5열
            cellindex = 0;
            row = sheet.createRow(rowindex++);
            row.setHeightInPoints((short) 21);

            cell = row.createCell(cellindex++);
            cell.setCellValue("지   급   내   역");
            cell.setCellStyle(GrayBorderStyle);
            row.createCell(cellindex++).setCellStyle(GrayBorderStyle); // merge
            sheet.addMergedRegion(new CellRangeAddress(rowindex - 1, rowindex - 1, 0, 1));
//             sheet.addMergedRegion(new CellRangeAddress(4,4,0,1));

            cell = row.createCell(cellindex++);
            cell.setCellValue("지   급   액");
            cell.setCellStyle(GrayBorderStyle);

            cell = row.createCell(cellindex++);
            cell.setCellValue("공  제  내  역");
            cell.setCellStyle(GrayBorderStyle);
            row.createCell(cellindex++).setCellStyle(GrayBorderStyle); // merge
            sheet.addMergedRegion(new CellRangeAddress(rowindex - 1, rowindex - 1, 3, 4));
//             sheet.addMergedRegion(new CellRangeAddress(4,4,3,4));

            cell = row.createCell(cellindex++);
            cell.setCellValue("공  제  액");
            cell.setCellStyle(GrayBorderStyle);

            int saveRow = rowindex;
            for (int i = 0; i < payArr.length; i++) {
                row = sheet.createRow(rowindex);
                row.setHeightInPoints((short) 11);

                cell = row.createCell(0);
                cell.setCellValue(payArr[i]);
                cell.setCellStyle(LeftRightBorderBodyStyle);
                row.createCell(1).setCellStyle(LeftRightBorderBodyStyle);
                sheet.addMergedRegion(new CellRangeAddress(rowindex, rowindex, 0, 1));
                rowindex++;
            }

            rowindex = saveRow;
            for (int i = 0; i < taxArr.length; i++) {
                row = sheet.getRow(rowindex);

                cell = row.createCell(3);
                cell.setCellValue(taxArr[i]);
                cell.setCellStyle(LeftRightBorderBodyStyle);
                row.createCell(4).setCellStyle(LeftRightBorderBodyStyle);
                sheet.addMergedRegion(new CellRangeAddress(rowindex, rowindex, 3, 4));
                cell.setCellStyle(LeftRightBorderBodyStyle);
                rowindex++;
            }

            rowindex = saveRow;

            row = sheet.getRow(rowindex++);
            cell = row.createCell(2);
            cell.setCellValue(m.getBasicSalary());
            cell.setCellStyle(NumberBodyStyle);
            cell.setCellType(CellType.NUMERIC);

            cell = row.createCell(5);
            cell.setCellValue(m.getNationalPension()); // 국민연금
            cell.setCellStyle(NumberBodyStyle);
            cell.setCellType(CellType.NUMERIC);

            row = sheet.getRow(rowindex++);
            cell = row.createCell(2);
            cell.setCellValue(m.getAnnualAllowance());
            cell.setCellStyle(NumberBodyStyle);
            cell.setCellType(CellType.NUMERIC);

            cell = row.createCell(5);
            cell.setCellValue(m.getHealthInsurance()); // 건강보험
            cell.setCellStyle(NumberBodyStyle);
            cell.setCellType(CellType.NUMERIC);

            row = sheet.getRow(rowindex++);
            cell = row.createCell(2);
            cell.setCellValue(m.getOvertimeAllowance01());
            cell.setCellStyle(NumberBodyStyle);
            cell.setCellType(CellType.NUMERIC);

            cell = row.createCell(5);
            cell.setCellValue(m.getEmploymentInsurance()); // 고용보험
            cell.setCellStyle(NumberBodyStyle);
            cell.setCellType(CellType.NUMERIC);

            row = sheet.getRow(rowindex++);
            cell = row.createCell(2);
            cell.setCellValue(m.getOvertimeAllowance02());
            cell.setCellStyle(NumberBodyStyle);
            cell.setCellType(CellType.NUMERIC);

            cell = row.createCell(5);
            cell.setCellValue(m.getCareInsurance());
            cell.setCellStyle(NumberBodyStyle);
            cell.setCellType(CellType.NUMERIC);

            row = sheet.getRow(rowindex++);
            cell = row.createCell(2);
            cell.setCellValue(m.getNightAllowance01());
            cell.setCellStyle(NumberBodyStyle);
            cell.setCellType(CellType.NUMERIC);

            cell = row.createCell(5);
            cell.setCellValue(m.getIncomtax());
            cell.setCellStyle(NumberBodyStyle);
            cell.setCellType(CellType.NUMERIC);

            row = sheet.getRow(rowindex++);
            cell = row.createCell(2);
            cell.setCellValue(m.getNightAllowance02());
            cell.setCellStyle(NumberBodyStyle);
            cell.setCellType(CellType.NUMERIC);

            cell = row.createCell(5);
            cell.setCellValue(m.getResidtax());
            cell.setCellStyle(NumberBodyStyle);
            cell.setCellType(CellType.NUMERIC);

            row = sheet.getRow(rowindex++);
            cell = row.createCell(2);
            cell.setCellValue(m.getHolidayAllowance01());
            cell.setCellStyle(NumberBodyStyle);
            cell.setCellType(CellType.NUMERIC);

            cell = row.createCell(5);
            cell.setCellValue(m.getAdvance());
            cell.setCellStyle(NumberBodyStyle);
            cell.setCellType(CellType.NUMERIC);

            row = sheet.getRow(rowindex++);
            cell = row.createCell(2);
            cell.setCellValue(m.getHolidayAllowance02());
            cell.setCellStyle(NumberBodyStyle);
            cell.setCellType(CellType.NUMERIC);

            cell = row.createCell(5);
            cell.setCellValue(m.getOtherTax());
            cell.setCellStyle(NumberBodyStyle);
            cell.setCellType(CellType.NUMERIC);

            row = sheet.getRow(rowindex++);
            cell = row.createCell(2);
            cell.setCellValue(m.getPositionAllowance());
            cell.setCellStyle(NumberBodyStyle);
            cell.setCellType(CellType.NUMERIC);

            cell = row.createCell(5);
            cell.setCellValue(m.getGyeongjobi());
            cell.setCellStyle(NumberBodyStyle);
            cell.setCellType(CellType.NUMERIC);

            row = sheet.getRow(rowindex++);
            cell = row.createCell(2);
            cell.setCellValue(m.getOtherAllowances());
            cell.setCellStyle(NumberBodyStyle);
            cell.setCellType(CellType.NUMERIC);

            cell = row.createCell(5);
            cell.setCellValue(m.getYearend());
            cell.setCellStyle(NumberBodyStyle);
            cell.setCellType(CellType.NUMERIC);

            row = sheet.getRow(rowindex++);
            cell = row.createCell(2);
            cell.setCellValue(m.getSubsidies());
            cell.setCellStyle(NumberBodyStyle);
            cell.setCellType(CellType.NUMERIC);

            cell = row.createCell(5);
            cell.setCellValue(m.getHealthInsuranceSettlement());
            cell.setCellStyle(NumberBodyStyle);
            cell.setCellType(CellType.NUMERIC);

            row = sheet.getRow(rowindex++);
            cell = row.createCell(2);
            cell.setCellValue(m.getTransportationExpenses());
            cell.setCellStyle(NumberBodyStyle);
            cell.setCellType(CellType.NUMERIC);

            cell = row.createCell(5);
            cell.setCellValue(m.getCareInsuranceSettlement());
            cell.setCellStyle(NumberBodyStyle);
            cell.setCellType(CellType.NUMERIC);

            row = sheet.getRow(rowindex++);
            cell = row.createCell(2);
            cell.setCellValue(m.getMealsExpenses());
            cell.setCellStyle(NumberBodyStyle);
            cell.setCellType(CellType.NUMERIC);

            cell = row.createCell(5);
            cell.setCellValue(m.getHolidayTax());
            cell.setCellStyle(NumberBodyStyle);
            cell.setCellType(CellType.NUMERIC);

            for (int i = 0; i < 3; i++) {
                row = sheet.createRow(rowindex++);
                row.setHeightInPoints((short) 11);
                for (cellindex = 0; cellindex < 6; cellindex++) {
                    row.createCell(cellindex).setCellStyle(LeftRightBorderBodyStyle);
                }
                sheet.addMergedRegion(new CellRangeAddress(rowindex - 1, rowindex - 1, 0, 1));
                sheet.addMergedRegion(new CellRangeAddress(rowindex - 1, rowindex - 1, 3, 4));
            }

            row = sheet.createRow(rowindex++);
            row.setHeightInPoints((short) 21);

            for (cellindex = 0; cellindex < 3; cellindex++) {
                row.createCell(cellindex).setCellStyle(LeftRightBorderBodyStyle);
            }
            sheet.addMergedRegion(new CellRangeAddress(rowindex - 1, rowindex - 1, 0, 1));
            cell = row.createCell(cellindex++);
            cell.setCellValue("공  제  액  계");
            cell.setCellStyle(GrayBorderStyle);
            row.createCell(cellindex++).setCellStyle(GrayBorderStyle); // merge
            sheet.addMergedRegion(new CellRangeAddress(rowindex - 1, rowindex - 1, 3, 4));
            cell = row.createCell(cellindex++);
            cell.setCellValue(m.getTaxSum());    //공제액계
            cell.setCellStyle(NumberGrayBorderStyle);
            cell.setCellType(CellType.NUMERIC);

            row = sheet.createRow(rowindex++);
            row.setHeightInPoints((short) 21);
            cellindex = 0;
            cell = row.createCell(cellindex++);
            cell.setCellValue("지 급 액 계");
            cell.setCellStyle(GrayBorderStyle);
            row.createCell(cellindex++).setCellStyle(GrayBorderStyle); // merge
            sheet.addMergedRegion(new CellRangeAddress(rowindex - 1, rowindex - 1, 0, 1));
            cell = row.createCell(cellindex++);
//			cell.setCellFormula(determineSumFormula("C", saveRow + 1, saveRow + 14, 1));
            cell.setCellValue(m.getSalarySum());    //지급액계
            cell.setCellStyle(NumberGrayBorderStyle);
            cell.setCellType(CellType.NUMERIC);

            cell = row.createCell(cellindex++);
            cell.setCellValue("차 인 지 급 액");
            cell.setCellStyle(GrayBorderStyle);
            row.createCell(cellindex++).setCellStyle(GrayBorderStyle); // merge
            sheet.addMergedRegion(new CellRangeAddress(rowindex - 1, rowindex - 1, 3, 4));
            cell = row.createCell(cellindex++);
            cell.setCellFormula(new CellAddress(rowindex - 1, 2) + "-" + new CellAddress(rowindex - 2, 5));
            cell.setCellStyle(NumberGrayBorderStyle);

            row = sheet.createRow(rowindex++);
            row.setHeightInPoints((short) 15);
            cell = row.createCell(0);
            cell.setCellValue("※  귀하의 노고에 감사드립니다");
            cell.setCellStyle(FooterStyle);
            cell = row.createCell(5);
            cell.setCellValue(m.getEstName());
//			if (reportParamsDto.getEstId() != null && reportParamsDto.getEstId() != 999) {
//				cell.setCellValue(m.getEstName());
//			}
            cell.setCellStyle(FooterRightStyle);

            sheet.createRow(rowindex++);
            sheet.createRow(rowindex++);
            formulaEvaluator.evaluateAll(); // 수식 전체 실행

            for (int i = 5; i < rowindex; i++) {
                row = sheet.getRow(i);
                for (int k = 0; k < 6; k++) {
                    cell = row.getCell(k);
                    if (cell != null) {
                        if (cell.getCellType() == CellType.NUMERIC) {
                            if (cell.getNumericCellValue() == 0) {
                                cell.setBlank();
                            }
                        }
                    }
                }
            }

        }

        return workbook;
    }

    @SuppressWarnings("static-access")
    @Transactional
    public XSSFWorkbook createPersonalPayroll(ReportParamsDto reportParamsDto) throws Exception {
        // Get Employee List
        List<Payroll6InPageDto> employees = new ArrayList<>();
        employees = payroll6InPageDao.getPayroll6InPageList(reportParamsDto);
//		List<PersonalPayrollParamsDto> employees = new ArrayList<>();
//		employees = reportDao.findEmployees(reportParamsDto);

        // Open Sample Excel
        Resource resource = resourceLoader.getResource("classpath:hr/personalPayrollSample.xlsx");
        InputStream fis = resource.getInputStream();

        XSSFWorkbook workbook = new XSSFWorkbook(fis);

        // Style Setting
        Font font = workbook.createFont();
        font.setFontName("맑은 고딕");
        font.setFontHeightInPoints((short) 11);

        CellStyle YellowStyle = workbook.createCellStyle();
        YellowStyle.setBorderTop(BorderStyle.THIN);
        YellowStyle.setBorderBottom(BorderStyle.THIN);
        YellowStyle.setBorderLeft(BorderStyle.THIN);
        YellowStyle.setBorderRight(BorderStyle.THIN);
        YellowStyle.setFont(font);
        YellowStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        YellowStyle.setAlignment(HorizontalAlignment.CENTER);
        YellowStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        YellowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle GreenStyle = workbook.createCellStyle();
        GreenStyle.setBorderTop(BorderStyle.THIN);
        GreenStyle.setBorderBottom(BorderStyle.THIN);
        GreenStyle.setBorderLeft(BorderStyle.THIN);
        GreenStyle.setBorderRight(BorderStyle.THIN);
        GreenStyle.setFont(font);
        GreenStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        GreenStyle.setAlignment(HorizontalAlignment.CENTER);
        GreenStyle.setFillForegroundColor(IndexedColors.LIME.getIndex());
        GreenStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle SkyBlueStyle = workbook.createCellStyle();
        SkyBlueStyle.setBorderTop(BorderStyle.THIN);
        SkyBlueStyle.setBorderBottom(BorderStyle.THIN);
        SkyBlueStyle.setBorderLeft(BorderStyle.THIN);
        SkyBlueStyle.setBorderRight(BorderStyle.THIN);
        SkyBlueStyle.setFont(font);
        SkyBlueStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        SkyBlueStyle.setAlignment(HorizontalAlignment.CENTER);
        SkyBlueStyle.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());
        SkyBlueStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFCellStyle RoseStyle = workbook.createCellStyle();
        RoseStyle.setBorderTop(BorderStyle.THIN);
        RoseStyle.setBorderBottom(BorderStyle.THIN);
        RoseStyle.setBorderLeft(BorderStyle.THIN);
        RoseStyle.setBorderRight(BorderStyle.THIN);
        RoseStyle.setFont(font);
        RoseStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        RoseStyle.setAlignment(HorizontalAlignment.CENTER);
        RoseStyle.setFillForegroundColor(IndexedColors.ROSE.getIndex());
        RoseStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFCellStyle styleLightGreen = workbook.createCellStyle();
        styleLightGreen.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());  // 배경색
        styleLightGreen.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleLightGreen.setBorderTop(BorderStyle.THIN);
        styleLightGreen.setBorderBottom(BorderStyle.THIN);
        styleLightGreen.setBorderLeft(BorderStyle.THIN);
        styleLightGreen.setBorderRight(BorderStyle.THIN);
        styleLightGreen.setShrinkToFit(true);
        styleLightGreen.setVerticalAlignment(VerticalAlignment.CENTER);
        styleLightGreen.setAlignment(HorizontalAlignment.CENTER);

        XSSFCellStyle styleLemonChiffon = workbook.createCellStyle();
        styleLemonChiffon.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());  // 배경색
        styleLemonChiffon.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleLemonChiffon.setBorderTop(BorderStyle.THIN);
        styleLemonChiffon.setBorderBottom(BorderStyle.THIN);
        styleLemonChiffon.setBorderLeft(BorderStyle.THIN);
        styleLemonChiffon.setBorderRight(BorderStyle.THIN);
        styleLemonChiffon.setShrinkToFit(true);
        styleLemonChiffon.setVerticalAlignment(VerticalAlignment.CENTER);
        styleLemonChiffon.setAlignment(HorizontalAlignment.CENTER);
        
        CellStyle NumberBodyStyle = workbook.createCellStyle();
        NumberBodyStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        NumberBodyStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0")); // 1000단위 콤마
        
        int testSum = 0;

        for (Payroll6InPageDto e : employees) {

            int rowindex = 0;
            int cellindex = 0;
            XSSFRow row = null;
            XSSFCell cell = null;
            XSSFSheet sheet = null;

            int cell01 = 13;
            int cell02 = 16;
            String employeeType = e.getEmployeeType();

            if (employeeType.equals("100")) {    //연봉제
                sheet = workbook.cloneSheet(0, e.getKoreanName());
                // Print Setting
                XSSFPrintSetup printSetup = sheet.getPrintSetup();
                printSetup.setPaperSize(XSSFPrintSetup.A4_PAPERSIZE);
                printSetup.setLandscape(true); // 인쇄방향 가로
                rowindex = 2;
                row = sheet.getRow(rowindex++);
                row.getCell(cell01).setCellValue(e.getAnnualLeaveUsedCnt());
                row.getCell(cell02).setCellValue(e.getHalfLeaveCnt());    //반차횟수

                row = sheet.getRow(rowindex++);
                row.getCell(cell01).setCellValue(determineTimeFormat(e.getLateTime()));
                row.getCell(cell02).setCellValue(e.getLateCntNum());

                row = sheet.getRow(rowindex++);
                row.getCell(cell01).setCellValue(e.getAbsenceCnt());

                row = sheet.getRow(rowindex++);
                row.getCell(cell01).setCellValue(e.getOvertimeDay2HCnt());    //평일연장2H
                row.getCell(cell02).setCellValue(e.getOverTimeDay4H3MCnt());    //철야익일4.5H

                rowindex++;

                row = sheet.getRow(rowindex++);
                row.getCell(cell01).setCellValue(e.getHolidaySaturdayDay4HCnt());    //토요4H
                row.getCell(cell02).setCellValue(e.getHolidaySundayDay4HCnt());    //일요4H

                row = sheet.getRow(rowindex++);
                row.getCell(cell01).setCellValue(e.getHolidaySaturdayDay8HCnt());    //토요8H
                row.getCell(cell02).setCellValue(e.getHolidaySundayDay8HCnt());    //일요8H

                row = sheet.getRow(rowindex++);
                row.getCell(cell01).setCellValue(e.getHolidaySaturdayDay4HCnt() * 0.5 + e.getHolidaySaturdayDay8HCnt());    //토요 계
                row.getCell(cell02).setCellValue(e.getHolidaySundayDay4HCnt() * 0.5 + e.getHolidaySundayDay8HCnt());    //일요 계

            } else if (employeeType.equals("200")) {    //시급제
                sheet = workbook.cloneSheet(1, e.getKoreanName());
                rowindex = 1;
                row = sheet.getRow(rowindex++);
                row.getCell(cell01).setCellValue(determineTimeFormat(e.getOvertimeDaytime01()));
                row.getCell(cell02).setCellValue(e.getNSHoliDayCnt());    //공휴야간횟수

                row = sheet.getRow(rowindex++);
				row.getCell(cell01).setCellValue(determineTimeFormat(e.getNtDaytime01()));	//야간근무시간
                row.getCell(cell02).setCellValue(e.getTranspotation());  //야간일수

                row = sheet.getRow(rowindex++);
                row.getCell(cell01).setCellValue(determineTimeFormat(e.getNtNighttime01()));    //야간 (0.0시간)

                row = sheet.getRow(rowindex++);
                row.getCell(cell01).setCellValue(determineTimeFormat(e.getNightTeamPlus()));    //토요출근 시간계수표1이면 8:00
                row.getCell(cell02).setCellValue(e.getNightTeamPlus()/8);    //토요 시간계수표

                row = sheet.getRow(rowindex++);
                row.getCell(cell01).setCellValue(determineTimeFormat(e.getHolidaySaturday()));    //토요출근시간
                row.getCell(cell02).setCellValue(e.getHolidaySaturday()/8);    //토요 시간계수표

                row = sheet.getRow(rowindex++);
                row.getCell(cell01).setCellValue(false);    //토요출근계
                row.getCell(cell02).setCellValue(false);    //토요 시간계

                row = sheet.getRow(rowindex++);
                row.getCell(cell01).setCellValue(determineTimeFormat(e.getHolidaySunday()));    //휴일출근시간
                row.getCell(cell02).setCellValue(e.getHolidaySunday()/8);    //휴일 시간계수표

                row = sheet.getRow(rowindex++);
                row.getCell(cell01).setCellValue(e.getAnnualLeaveUsedCnt());
                row.getCell(cell02).setCellValue(e.getHalfLeaveCnt());        //반차횟수

                row = sheet.getRow(rowindex++);
                row.getCell(cell01).setCellValue(e.getEarlyLeaveTime());        //조퇴시간/횟수
                row.getCell(cell02).setCellValue(determineTimeFormat(e.getLateTime()));

                row = sheet.getRow(rowindex++);
                row.getCell(cell01).setCellValue(e.getAbsenceCnt());

            }

            row = sheet.getRow(0);
            cell = row.getCell(cell01);
            cell.setCellValue(e.getKoreanName());
            
            e.setTotalAmt(e.getBasicAmount()
                    + e.getOvertimeAllowance01()
                    + e.getOvertimeAllowance02()
                    + e.getNtDayAllowance01()
                    + e.getNtNightAllowance01()
                    + e.getNtNightAllowance02()
                    + e.getHolidaySatAllowance01()
                    + e.getHolidaySunAllowance01()
                    + e.getHolidayAllowance02()
                    + e.getAttribute01()
                    + e.getAttribute15()
                    + e.getAttribute13()
                    + e.getAttribute14()
                    + e.getAttribute17()
                    + e.getAttribute16()
                    + e.getAttribute11()
                    + e.getAttribute12()
                    + e.getAttribute23());
            
            testSum += e.getTotalAmt();

            payroll6InPageService.setValuePayrollTableForm(sheet, e, -1, 11, 12, styleLightGreen, styleLemonChiffon);
            
            
            //0값 빈칸처리
            for (int i = 1; i < rowindex; i++) {
                row = sheet.getRow(i);
                for (int k = cell01; k < cell02+1; k++) {
                    cell = row.getCell(k);
                    if (cell != null) {
                        if (cell.getCellType() == CellType.NUMERIC) {
                            if (cell.getNumericCellValue() == 0) {
                                cell.setBlank();
                            }
                        }else if(cell.getCellType() == CellType.STRING) {
                        	if(cell.getStringCellValue().equals("0") || cell.getStringCellValue().equals("0:00")) {
                        		cell.setBlank();
                        	}
                        }
                    }
                }
            }
            
            //sheet 퇴사자 입사자 색지정
            byte [] lightGreen = DefaultIndexedColorMap.getDefaultRGB(IndexedColors.LIGHT_GREEN.getIndex());
            byte [] lemonChiffon = DefaultIndexedColorMap.getDefaultRGB(IndexedColors.LEMON_CHIFFON.getIndex());
            
            if ("RETIRE".equals(e.getHireDiv())) {
            	sheet.setTabColor(new XSSFColor(lightGreen, null));
            } else if ("NEW".equals(e.getHireDiv())) {
            	sheet.setTabColor(new XSSFColor(lemonChiffon, null));
            }

            // Get ADT Data
            reportParamsDto.setEmployeeId(e.getEmployeeId().intValue());
            List<EmployeeInformationDto> personalAdtList = new ArrayList<>();
            personalAdtList = reportDao.findPersonalPayroll(reportParamsDto);

            rowindex = 1;
            for (EmployeeInformationDto p : personalAdtList) {
                row = sheet.getRow(rowindex++);
                cellindex = 0;
                cell = row.getCell(cellindex++);
                cell.setCellValue(p.getWorkDate());

                cell = row.getCell(cellindex++);
                String workStatus = p.getWorkStatus();
                if ("오전반차_정상".equals(workStatus) || "오전반차_생산".equals(workStatus)) {
                    workStatus = "오전반차";
                }
                cell.setCellValue(workStatus);

                if (workStatus.matches("(.*)반차|휴가|연차|무급(.*)")) {
                    cell.setCellStyle(GreenStyle);
                } else if (workStatus.matches("(.*)야간(.*)")) {
                    cell.setCellStyle(YellowStyle);
                } else if (workStatus.matches("(.*)토요(.*)")) {
                    for (int i = 0; i < 12; i++) {
                        row.getCell(i).setCellStyle(SkyBlueStyle);
                    }
                } else if (workStatus.matches("(.*)휴일(.*)")) {
                    for (int i = 0; i < 12; i++) {
                        row.getCell(i).setCellStyle(RoseStyle);
                    }
                }

                cell = row.getCell(cellindex++);
                cell.setCellValue(p.getWorkStartDate());

                cell = row.getCell(cellindex++);
                cell.setCellValue(p.getWorkEndDate());

                cell = row.getCell(cellindex++);
                cell.setCellValue(p.getInStatus());
                if ("지각".equals(p.getInStatus())) {
                    cell.setCellStyle(YellowStyle);
                }

                cell = row.getCell(cellindex++);
                cell.setCellValue(p.getOutStatus());
                if ("조퇴".equals(p.getOutStatus())) {
                    cell.setCellStyle(YellowStyle);
                }

                cell = row.getCell(cellindex++);
                cell.setCellValue(p.getLateTime());

                cell = row.getCell(cellindex++);
                cell.setCellValue(p.getOverTime());

                cell = row.getCell(cellindex++);
                cell.setCellValue(p.getNightTime());

                cell = row.getCell(cellindex++);
                cell.setCellValue(p.getHolidayTime());

                cell = row.getCell(cellindex++);
                cell.setCellValue(p.getDefaultTime());

                cell = row.getCell(cellindex++);
                cell.setCellValue(p.getRealWorkTime());
            }

            sheet.setAutoFilter(new CellRangeAddress(0, personalAdtList.size() + 1, 0, 11));

        }

        //sample양식 sheet 삭제
        workbook.removeSheetAt(0);
        workbook.removeSheetAt(0);
        
        workbook.getSheetAt(0).getRow(0).createCell(18, CellType.NUMERIC).setCellValue(testSum);
        workbook.getSheetAt(0).getRow(0).getCell(18).setCellStyle(NumberBodyStyle);

        return workbook;
    }

//	@Transactional
//	public XSSFWorkbook createPersonalPayrollOrigin(ReportParamsDto reportParamsDto) throws Exception {
//		// Get Employee List
//		List<PersonalPayrollParamsDto> employees = new ArrayList<>();
//		employees = reportDao.findEmployees(reportParamsDto);
//
//		String[] headerArr = { "근무일자", "근무스케줄", "출근시간", "퇴근시간", "출근판정", "퇴근판정", "지각", "연장", "야간", "휴일", "정상", "실근무" };
//
//		XSSFWorkbook workbook = new XSSFWorkbook();
//		XSSFFormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
//		// Style Setting
//		Font font = workbook.createFont();
//		font.setFontName("맑은 고딕");
//		font.setFontHeightInPoints((short) 11);
//
//		Font boldFont = workbook.createFont();
//		boldFont.setFontName("맑은 고딕");
//		boldFont.setFontHeightInPoints((short) 11);
//		boldFont.setBold(true);
//		
//		CellStyle DefaultStyle = workbook.createCellStyle();
//		DefaultStyle.setBorderTop(BorderStyle.THIN);
//		DefaultStyle.setBorderBottom(BorderStyle.THIN);
//		DefaultStyle.setBorderLeft(BorderStyle.THIN);
//		DefaultStyle.setBorderRight(BorderStyle.THIN);
//		DefaultStyle.setFont(font);
//		DefaultStyle.setVerticalAlignment(VerticalAlignment.CENTER);
//
//		CellStyle DefaultCenterStyle = workbook.createCellStyle();
//		DefaultCenterStyle.setBorderTop(BorderStyle.THIN);
//		DefaultCenterStyle.setBorderBottom(BorderStyle.THIN);
//		DefaultCenterStyle.setBorderLeft(BorderStyle.THIN);
//		DefaultCenterStyle.setBorderRight(BorderStyle.THIN);
//		DefaultCenterStyle.setFont(font);
//		DefaultCenterStyle.setVerticalAlignment(VerticalAlignment.CENTER);
//		DefaultCenterStyle.setAlignment(HorizontalAlignment.CENTER);
//
//		CellStyle DefaultBoldStyle = workbook.createCellStyle();
//		DefaultBoldStyle.setBorderTop(BorderStyle.THIN);
//		DefaultBoldStyle.setBorderBottom(BorderStyle.THIN);
//		DefaultBoldStyle.setBorderLeft(BorderStyle.THIN);
//		DefaultBoldStyle.setBorderRight(BorderStyle.THIN);
//		DefaultBoldStyle.setFont(boldFont);
//		DefaultBoldStyle.setVerticalAlignment(VerticalAlignment.CENTER);
//		DefaultBoldStyle.setAlignment(HorizontalAlignment.CENTER);
//		
//		CellStyle DefaultCountStyle = workbook.createCellStyle();
//		DefaultCountStyle.setBorderTop(BorderStyle.THIN);
//		DefaultCountStyle.setBorderBottom(BorderStyle.THIN);
//		DefaultCountStyle.setBorderLeft(BorderStyle.THIN);
//		DefaultCountStyle.setBorderRight(BorderStyle.THIN);
//		DefaultCountStyle.setFont(font);
//		DefaultCountStyle.setVerticalAlignment(VerticalAlignment.CENTER);
//		DefaultCountStyle.setAlignment(HorizontalAlignment.CENTER);
//		DefaultCountStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("##회"));
//		
//		CellStyle DefaultCountDoubleStyle = workbook.createCellStyle();
//		DefaultCountDoubleStyle.setBorderTop(BorderStyle.THIN);
//		DefaultCountDoubleStyle.setBorderBottom(BorderStyle.THIN);
//		DefaultCountDoubleStyle.setBorderLeft(BorderStyle.THIN);
//		DefaultCountDoubleStyle.setBorderRight(BorderStyle.THIN);
//		DefaultCountDoubleStyle.setFont(font);
//		DefaultCountDoubleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
//		DefaultCountDoubleStyle.setAlignment(HorizontalAlignment.CENTER);
//		DefaultCountDoubleStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("##.#회"));
//		
//		CellStyle RightBorderStyle = workbook.createCellStyle();
//		RightBorderStyle.setBorderRight(BorderStyle.THIN);
//		
//		CellStyle YellowStyle = workbook.createCellStyle();
//		YellowStyle.setBorderTop(BorderStyle.THIN);
//		YellowStyle.setBorderBottom(BorderStyle.THIN);
//		YellowStyle.setBorderLeft(BorderStyle.THIN);
//		YellowStyle.setBorderRight(BorderStyle.THIN);
//		YellowStyle.setFont(font);
//		YellowStyle.setVerticalAlignment(VerticalAlignment.CENTER);
//		YellowStyle.setAlignment(HorizontalAlignment.CENTER);
//		YellowStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
//		YellowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//
//		CellStyle GreenStyle = workbook.createCellStyle();
//		GreenStyle.setBorderTop(BorderStyle.THIN);
//		GreenStyle.setBorderBottom(BorderStyle.THIN);
//		GreenStyle.setBorderLeft(BorderStyle.THIN);
//		GreenStyle.setBorderRight(BorderStyle.THIN);
//		GreenStyle.setFont(font);
//		GreenStyle.setVerticalAlignment(VerticalAlignment.CENTER);
//		GreenStyle.setAlignment(HorizontalAlignment.CENTER);
//		GreenStyle.setFillForegroundColor(IndexedColors.LIME.getIndex());
//		GreenStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//
//		for (PersonalPayrollParamsDto e : employees) {
//			String employeeType = e.getEmployeeType();
//			// Get ADT Data
//			reportParamsDto.setEmployeeId(e.getEmployeeId());
//			List<EmployeeInformationDto> personalAdtList = new ArrayList<>();
//			personalAdtList = reportDao.findPersonalPayroll(reportParamsDto);
//
//			XSSFSheet sheet = workbook.createSheet(e.getKoreanName());
//			int rowindex = 0;
//			int cellindex = 0;
//			XSSFRow row = null;
//			XSSFCell cell = null;
//
//			sheet.setColumnWidth(0, 4000);
//			sheet.setColumnWidth(1, 3000);
//			sheet.setColumnWidth(2, 5000);
//			sheet.setColumnWidth(3, 5000);
//			sheet.setColumnWidth(4, 3000);
//			sheet.setColumnWidth(5, 3000);
//			sheet.setColumnWidth(6, 3000);
//			sheet.setColumnWidth(7, 3000);
//			sheet.setColumnWidth(8, 3000);
//			sheet.setColumnWidth(9, 3000);
//			sheet.setColumnWidth(10, 3000);
//			sheet.setColumnWidth(11, 3000);
//			sheet.setColumnWidth(12, 3000);
//			sheet.setColumnWidth(13, 3000);
//			sheet.setColumnWidth(14, 1500);
//			sheet.setColumnWidth(15, 1500);
//			sheet.setColumnWidth(16, 3000);
//
//			row = sheet.createRow(rowindex++);
//			for (cellindex = 0; cellindex < headerArr.length; cellindex++) {
//				cell = row.createCell(cellindex);
//				cell.setCellValue(headerArr[cellindex]);
//				cell.setCellStyle(DefaultCenterStyle);
//			}
//
//			for (EmployeeInformationDto p : personalAdtList) {
//				row = sheet.createRow(rowindex++);
//				cellindex = 0;
//				cell = row.createCell(cellindex++);
//				cell.setCellValue(p.getWorkDate());
//				cell.setCellStyle(DefaultCenterStyle);
//
//				cell = row.createCell(cellindex++);
//				cell.setCellValue(p.getWorkStatus());
//				switch (p.getWorkStatus()) {
//				case "연차":
//				case "오후반차":
//				case "오전반차_정상":
//				case "오전반차_생산":
//				case "휴가":
//					cell.setCellStyle(GreenStyle);
//					break;
//				case "야간":
//					cell.setCellStyle(YellowStyle);
//					break;
//				default:
//					cell.setCellStyle(DefaultCenterStyle);
//				}
//
//				cell = row.createCell(cellindex++);
//				cell.setCellValue(p.getWorkStartDate());
//				cell.setCellStyle(DefaultCenterStyle);
//
//				cell = row.createCell(cellindex++);
//				cell.setCellValue(p.getWorkEndDate());
//				cell.setCellStyle(DefaultCenterStyle);
//
//				cell = row.createCell(cellindex++);
//				cell.setCellValue(p.getInStatus());
//				if ("지각".equals(p.getInStatus()))
//					cell.setCellStyle(YellowStyle);
//				else
//					cell.setCellStyle(DefaultCenterStyle);
//
//				cell = row.createCell(cellindex++);
//				cell.setCellValue(p.getOutStatus());
//				if ("조퇴".equals(p.getOutStatus()))
//					cell.setCellStyle(YellowStyle);
//				else
//					cell.setCellStyle(DefaultCenterStyle);
//
//				cell = row.createCell(cellindex++);
//				cell.setCellValue(p.getLateTime());
//				cell.setCellStyle(DefaultCenterStyle);
//
//				cell = row.createCell(cellindex++);
//				cell.setCellValue(p.getOverTime());
//				cell.setCellStyle(DefaultCenterStyle);
//
//				cell = row.createCell(cellindex++);
//				cell.setCellValue(p.getNightTime());
//				cell.setCellStyle(DefaultCenterStyle);
//
//				cell = row.createCell(cellindex++);
//				cell.setCellValue(p.getHolidayTime());
//				cell.setCellStyle(DefaultCenterStyle);
//
//				cell = row.createCell(cellindex++);
//				cell.setCellValue(p.getDefaultTime());
//				cell.setCellStyle(DefaultCenterStyle);
//
//				cell = row.createCell(cellindex++);
//				cell.setCellValue(p.getRealWorkTime());
//				cell.setCellStyle(DefaultCenterStyle);
//			}
//
//			sheet.setAutoFilter(new CellRangeAddress(0, personalAdtList.size() + 1, 0, headerArr.length - 1));
//			
//			rowindex = 0;
//			cellindex = headerArr.length;
//			row = sheet.getRow(rowindex++);
//			cell = row.createCell(cellindex++);
//			cell.setCellValue("성명");
//			cell.setCellStyle(DefaultBoldStyle);
//
//			cell = row.createCell(cellindex++);
//			cell.setCellValue(e.getKoreanName());
//			cell.setCellStyle(DefaultBoldStyle);
//
//			cell = row.createCell(cellindex++);
//			if ("100".equals(employeeType)) {
//				cell.setCellValue("연봉");
//				cell.setCellStyle(DefaultBoldStyle);
//				row.createCell(cellindex++).setCellStyle(DefaultBoldStyle); // merge
//				row.createCell(cellindex++).setCellStyle(DefaultBoldStyle); // merge
//				sheet.addMergedRegion(new CellRangeAddress(rowindex-1, rowindex-1, 14, 16));
//				
//				cellindex = headerArr.length;
//				row = sheet.getRow(rowindex++);
//				cell = row.createCell(cellindex++);
//				cell.setCellValue("구분");
//				cell.setCellStyle(DefaultCenterStyle);
//				row.createCell(cellindex++).setCellStyle(DefaultCenterStyle); // merge
//				sheet.addMergedRegion(new CellRangeAddress(rowindex-1, rowindex-1, 12, 13));
//
//				cell = row.createCell(cellindex++);
//				cell.setCellValue("구분");
//				cell.setCellStyle(DefaultCenterStyle);
//				row.createCell(cellindex++).setCellStyle(DefaultCenterStyle); // merge
//				row.createCell(cellindex++).setCellStyle(DefaultCenterStyle); // merge
//				sheet.addMergedRegion(new CellRangeAddress(rowindex-1, rowindex-1, 14, 16));
//
//				cellindex = headerArr.length;
//				row = sheet.getRow(rowindex++);
//				cell = row.createCell(cellindex++);
//				cell.setCellValue("연차(휴가)");
//				cell.setCellStyle(DefaultStyle);
//
//				cell = row.createCell(cellindex++);
//				cell.setCellFormula("SUM( COUNTIF(B2:B32,\"연차\"), COUNTIF(B2:B32,\"휴가\") )");
//				cell.setCellStyle(DefaultCountStyle);
//
//				cell = row.createCell(cellindex++);
//				cell.setCellValue("반차");
//				cell.setCellStyle(DefaultStyle);
//				row.createCell(cellindex++).setCellStyle(DefaultStyle); // merge
//
//				cell = row.createCell(cellindex++);
//				cell.setCellFormula("COUNTIF(B2:B32,\"*반차*\")");
//				cell.setCellStyle(DefaultCountStyle);
//
//				cellindex = headerArr.length;
//				row = sheet.getRow(rowindex++);
//				cell = row.createCell(cellindex++);
//				cell.setCellValue("지각");
//				cell.setCellStyle(DefaultStyle);
//
//				cell = row.createCell(cellindex++);
//				cell.setCellFormula("SUM(G2:G32)");
//				cell.setCellStyle(DefaultCenterStyle);
//
//				cell = row.createCell(cellindex++);
//				cell.setCellValue("지각횟수");
//				cell.setCellStyle(DefaultStyle);
//				row.createCell(cellindex++).setCellStyle(DefaultStyle); // merge
//
//				cell = row.createCell(cellindex++);
//				cell.setCellFormula("COUNTIF(E2:E32,\"지각\")");
//				cell.setCellStyle(DefaultCountStyle);
//				
//				cellindex = headerArr.length;
//				row = sheet.getRow(rowindex++);
//				cell = row.createCell(cellindex++);
//				cell.setCellValue("결근");
//				cell.setCellStyle(DefaultStyle);
//				
//				cell = row.createCell(cellindex++);
//				cell.setCellFormula("COUNTIF(E2:E32,\"결근\")");
//				cell.setCellStyle(DefaultCountStyle);
//				
//				row.createCell(16).setCellStyle(RightBorderStyle);
//				
//				cellindex = headerArr.length;
//				row = sheet.getRow(rowindex++);
//				cell = row.createCell(cellindex++);
//				cell.setCellValue("평일연장 2H");
//				cell.setCellStyle(DefaultBoldStyle);
//				
//				cell = row.createCell(cellindex++);
//				cell.setCellStyle(DefaultCountStyle);
//				
//				cell = row.createCell(cellindex++);
//				cell.setCellValue("철야익일 4.5H");
//				cell.setCellStyle(DefaultBoldStyle);
//				row.createCell(cellindex++).setCellStyle(DefaultBoldStyle); // merge
//				
//				cell = row.createCell(cellindex++);
//				cell.setCellStyle(DefaultCountStyle);
//				
//				rowindex++;
//				row.createCell(16).setCellStyle(RightBorderStyle);
//				
//				cellindex = headerArr.length;
//				row = sheet.getRow(rowindex++);
//				cell = row.createCell(cellindex++);
//				cell.setCellValue("토요 4H↑");
//				cell.setCellStyle(DefaultStyle);
//
//				cell = row.createCell(cellindex++);
//				cell.setCellStyle(DefaultCountDoubleStyle);
//				
//				cell = row.createCell(cellindex++);
//				cell.setCellValue("일요 4H↑");
//				cell.setCellStyle(DefaultStyle);
//				row.createCell(cellindex++).setCellStyle(DefaultStyle); // merge
//
//				cell = row.createCell(cellindex++);
//				cell.setCellStyle(DefaultCountDoubleStyle);
//				
//				cellindex = headerArr.length;
//				row = sheet.getRow(rowindex++);
//				cell = row.createCell(cellindex++);
//				cell.setCellValue("토요 8H");
//				cell.setCellStyle(DefaultStyle);
//
//				cell = row.createCell(cellindex++);
//				cell.setCellStyle(DefaultCountStyle);
//				
//				cell = row.createCell(cellindex++);
//				cell.setCellValue("일요 8H");
//				cell.setCellStyle(DefaultStyle);
//				row.createCell(cellindex++).setCellStyle(DefaultStyle); // merge
//
//				cell = row.createCell(cellindex++);
//				cell.setCellStyle(DefaultCountStyle);
//				
//				cellindex = headerArr.length;
//				row = sheet.getRow(rowindex++);
//				cell = row.createCell(cellindex++);
//				cell.setCellValue("토요 계");
//				cell.setCellStyle(DefaultBoldStyle);
//				
//				cell = row.createCell(cellindex++);
//				cell.setCellStyle(DefaultCountDoubleStyle);
//				
//				cell = row.createCell(cellindex++);
//				cell.setCellValue("일요 계");
//				cell.setCellStyle(DefaultBoldStyle);
//				row.createCell(cellindex++).setCellStyle(DefaultBoldStyle); // merge
//				
//				cell = row.createCell(cellindex++);
//				cell.setCellStyle(DefaultCountDoubleStyle);
//				
//				//2행부터 9행까지 merge
//				for(int i=2; i<10; i++) {
//					sheet.addMergedRegion(new CellRangeAddress(i, i, 14, 15));
//				}
//				
//				row = sheet.getRow(rowindex++);
//				row.createCell(16).setCellStyle(RightBorderStyle);
//				
//			} else if ("200".equals(employeeType)) {
//				cell.setCellValue("시급(야간)");
//				cell.setCellStyle(DefaultBoldStyle);
//				row.createCell(cellindex++).setCellStyle(DefaultBoldStyle); // merge
//				row.createCell(cellindex++).setCellStyle(DefaultBoldStyle); // merge
//				sheet.addMergedRegion(new CellRangeAddress(rowindex-1, rowindex-1, 14, 16));
//
//			}
//
//		}
//		formulaEvaluator.evaluateAll(); // 수식 전체 실행
//		return workbook;
//	}

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
