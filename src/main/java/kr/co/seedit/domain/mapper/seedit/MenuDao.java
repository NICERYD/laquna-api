package kr.co.seedit.domain.mapper.seedit;

import kr.co.seedit.domain.company.dto.CompanyAuthInfoDto;
import kr.co.seedit.domain.menu.dto.MenuDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

//@Repository
@Mapper
public interface MenuDao {
    List<MenuDto> selectMenuList(CompanyAuthInfoDto info);

}
