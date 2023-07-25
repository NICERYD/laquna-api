package kr.co.seedit.global.common.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.http.codec.ServerSentEvent;

@Data
@ToString
public class ResponseDto {
    private boolean success = true;
    private String code;
    private String message = "success";
    private Object data = null;
    private String totalRow = "";
    private String searchPaging = "";

    public ResponseDto(Object data) {
        this.data = data;
    }

    @Builder
    public ResponseDto(String code, String message, Object data, String totalRow,
                        String searchPaging, String token) {
        this.code = code;
        this.message = message == null ? "success" : message;
        this.data = data;
        this.totalRow = totalRow == null ? "" : totalRow;
        this.searchPaging = searchPaging == null ? "" : searchPaging;
    }

}
