package kr.co.seedit.domain.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class MenuDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -888379880323644093L;

    private int respGroupId;
    private int menuId;
    private String respGroupName;
    private String menuName;
    private String menuApi;
    private String menuType;
    private String menuUrl;
}
