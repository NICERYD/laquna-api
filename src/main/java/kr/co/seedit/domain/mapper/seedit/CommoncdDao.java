package kr.co.seedit.domain.mapper.seedit;

import kr.co.seedit.domain.commoncd.dto.CommonCodeDetailDto;
import kr.co.seedit.domain.commoncd.dto.CommonCodeDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
//@Resource(name = "masterSqlSessionFactory")
public interface CommoncdDao {

    /***
   * 공통코드 전체 찾기
   *
   * @return 그룹및상세코드 리스트
   */
    List<CommonCodeDto> findCodeAllList();
    List<CommonCodeDto> commonCodelistSearch(CommonCodeDto commonCodeDto);
    List<CommonCodeDto> findCommonModuleList();
    List<CommonCodeDetailDto> findCodeDetailList(CommonCodeDetailDto commonCodeDetailDto);
    void insertCodeDetailComAdd(CommonCodeDetailDto commonCodeDetailDto);
    void codeDetailComUpdate(CommonCodeDetailDto commonCodeDetailDto);
    void codeDetailComDelete(CommonCodeDetailDto commonCodeDetailDto);
}
