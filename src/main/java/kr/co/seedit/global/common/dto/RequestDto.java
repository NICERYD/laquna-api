package kr.co.seedit.global.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class RequestDto {
        private Integer companyId;
        private Integer estId;
        private String yyyymm;
        private Integer loginUserId;
}
