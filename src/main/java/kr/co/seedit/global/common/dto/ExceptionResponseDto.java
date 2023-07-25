package kr.co.seedit.global.common.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ExceptionResponseDto {

  private final boolean success = false;
  private Object data;
  private Object error;
  private String message;

  @Builder
  public ExceptionResponseDto(Object error, String message) {
    this.error = error;
    this.message = message;
    this.data = error;
  }

  @Builder
  public ExceptionResponseDto(Object error, String message, Object data) {
    this.error = error;
    this.message = message;
    this.data = data;
  }

}
