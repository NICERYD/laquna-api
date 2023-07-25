package kr.co.seedit.domain.hr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class WorkDayDto {
    private Integer companyId;
    private String yyyymmdd;
    private String dateType;
    private String dateWeek;
    private String description;
    private String yyyymm;
    private Integer loginUserId;
}
