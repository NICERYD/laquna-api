package kr.co.seedit.domain.hr.api;

import kr.co.seedit.domain.hr.application.ERPIUService;
import kr.co.seedit.domain.hr.dto.ErpIUDto;
import kr.co.seedit.global.common.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ERPIUApi {
    private final ERPIUService erpiuService;
    @PostMapping("/hr/erpiu/getBasicSalaryData")
    public ResponseEntity<ResponseDto> getBasicSalaryData(@RequestBody ErpIUDto.RequestDto erpIUDto, HttpServletRequest request) throws IOException, InvalidFormatException {
        ResponseDto responseDto = erpiuService.getBasicSalaryData(erpIUDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PostMapping("/hr/erpiu/getDataErpIU")
    public ResponseEntity<ResponseDto> getDataErpIU(@RequestBody ErpIUDto.RequestDto erpIUDto, HttpServletRequest request) throws IOException, InvalidFormatException {
        ResponseDto responseDto = erpiuService.getDataErpIU(erpIUDto, request);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
