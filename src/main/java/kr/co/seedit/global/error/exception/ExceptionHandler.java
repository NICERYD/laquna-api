package kr.co.seedit.global.error.exception;

import kr.co.seedit.global.common.dto.ExceptionResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionHandler {

      private static final Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

  /**
   * Controller 전범위 Exception (임시) //각 Exception 과 메시지 정의 필요
   */
  @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
  protected ResponseEntity<ExceptionResponseDto> handleAll(final Exception e) {
    logger.error("error", e);

    Map<String, String> map = new HashMap<>();
    map.put("code", "9999");

    ExceptionResponseDto responseDto = ExceptionResponseDto.builder()
      .error(map)
      .message("서버 에러입니다.")
      .build();

    return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * MethodArgumentNotValidException : validation check
   */
  @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
  protected ResponseEntity<ExceptionResponseDto> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
    logger.error("MethodArgumentNotValidException", e);

    Map<String, String> map = new HashMap<>();
    map.put("code", "2001");

    ExceptionResponseDto responseDto = ExceptionResponseDto.builder()
      .error(map)
      .message("필수 파라미터 없음("
        + e.getBindingResult().getAllErrors().get(0).getDefaultMessage()
        + ")")
      .build();

    return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
  }

  /**
   * Controller 전범위 Exception (임시) //각 Exception 과 메시지 정의 필요
   */
  @org.springframework.web.bind.annotation.ExceptionHandler(CustomException.class)
  protected ResponseEntity<ExceptionResponseDto> customException(final Exception e) {
    logger.error("error", e);
    String[] err = e.getMessage().split(",");

    ExceptionResponseDto responseDto = ExceptionResponseDto.builder().build();
    Map<String, String> map = new HashMap<>();
    // 0001,알 수 없는 문제가 발생했습니다. 잠시 후에 다시 시도해주세요. <- , 있으면
    if (err.length > 1) {
      map.put("code", err[0]);
      map.put("message", err[1]);
      responseDto.setError(map);
      responseDto.setMessage(err[1]);
      responseDto.setData(map);
    } else {
      map.put("code", "999");
      map.put("message", err[0]);
      responseDto.setError(map);
      responseDto.setMessage(err[0]);
      responseDto.setData(map);
    }

    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }
}
