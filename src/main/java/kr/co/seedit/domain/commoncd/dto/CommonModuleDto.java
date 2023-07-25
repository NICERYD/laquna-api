package kr.co.seedit.domain.commoncd.dto;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommonModuleDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 7648941163102883891L;

    private int moduleId;
    private String moduleName;
    private String description;
}
