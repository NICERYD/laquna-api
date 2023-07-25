package kr.co.seedit.domain.mapper.seedit;

import kr.co.seedit.domain.company.dto.CompanyAuthInfoDto;
import kr.co.seedit.domain.company.dto.CompanyDto;
import org.apache.ibatis.annotations.Mapper;


@Mapper
//@Resource(name = "masterSqlSessionFactory")
public interface CompanyDao {

    void insertUserTbJoinInfo(CompanyDto companyDto);
    void insertCompanyTbJoinInfo(CompanyDto companyDto);
    void insertApproveTbJoinInfo(CompanyDto companyDto);
    void insertPasswordTbJoinInfo(CompanyDto companyDto);
    CompanyDto selectTokenInfo(CompanyDto companyDto);
    CompanyAuthInfoDto selectUserAuthInfo(CompanyDto companyDto);
    CompanyDto signUpCheck(CompanyDto companyDto);
    CompanyDto selectFindByUsername(String emailId);
    int selectMemberAccountChk(CompanyDto companyDto);
    int selectIdCnt(CompanyDto companyDto);

}
