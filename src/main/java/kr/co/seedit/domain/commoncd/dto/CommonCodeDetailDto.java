package kr.co.seedit.domain.commoncd.dto;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommonCodeDetailDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 948825974578562866L;

    private Integer codeDetailId;
    private Integer codeId;
    private Integer companyId;
    private String definedCd;
    private String definedName;
    private String useYn;
    private String value01;
    private String value02;
    private String value03;
    private String remark;
    private String attribute1;
    private String attribute2;
    private String attribute3;
    private String attribute4;
    private String attribute5;
    private String definedEngName;
    private String definedType;
    private int lastUpdatedBy;
}
