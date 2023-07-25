package kr.co.seedit.global.common.application;
import kr.co.seedit.domain.mapper.seedit.CompanyDao;
import kr.co.seedit.domain.company.dto.CompanyDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

  private final CompanyDao companyDao;

  @Override
  public UserDetails loadUserByUsername(String emailId) throws UsernameNotFoundException {
    if (emailId != null) {
      CompanyDto companyDto = companyDao.selectFindByUsername(emailId);

      if (companyDto != null) {
        return new User(emailId, companyDto.getPassword(), new ArrayList<>());
      } else {
        return new User(emailId, "", new ArrayList<>());
      }
    } else {
      throw new UsernameNotFoundException("User not found with username: " + emailId);
    }
  }

}
