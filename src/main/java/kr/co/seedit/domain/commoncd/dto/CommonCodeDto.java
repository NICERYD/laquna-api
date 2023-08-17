package kr.co.seedit.domain.commoncd.dto;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommonCodeDto implements Serializable {
    @Serial
    private static final long serialVersionUID = -152612977201152735L;

    private Integer codeId;
    private String codeField;
    private String codeName;
    private String codeEngName;
    private String codeType;
    private String remark;
    private Integer moduleId;
    private String useYn;
    private String keyword;
    private Integer companyId;
}
