package kr.co.seedit.domain.mapper.seedit;


import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import kr.co.seedit.domain.hr.dto.PayStubMailHistDto;

@Repository
@Mapper
public interface DHPaystubmailHistDao {

	/**
	 * 이메일전송 요청 및 이력테이블 조회
	 * @param eDTO 조회조건
	 * @return 결과 목록
	 */
	public List<PayStubMailHistDto> selectDHPaystubmailHistList(PayStubMailHistDto eDTO) throws SQLException, DataAccessException;

	/**
	 * 이메일전송 요청 및 이력테이블 데이터 갯수 조회
	 * @param eDTO 조회조건
	 * @return 데이터 갯수
	 */
	public int selectDHPaystubmailHistCount(PayStubMailHistDto eDTO) throws SQLException, DataAccessException;

	/**
	 * 이메일전송 요청 및 이력테이블 생성
	 * @param eDTO 생성할 데이터
	 * @return 입력 시 selectKey 를 사용하여 key 를 딴 경우 해당 key
	 * @throws Exception
	 */
	public int insertDHPaystubmailHist(PayStubMailHistDto eDTO) throws SQLException, DataAccessException;

	/**
	 * 이메일전송 요청 및 이력테이블 수정
	 * @param eDTO 수정할 데이터
	 * @return 성공여부 (수정된 데이터 개수)
	 * @throws Exception
	 */
	public int updateDHPaystubmailHist(PayStubMailHistDto eDTO) throws SQLException, DataAccessException;

	/**
	 * 이메일전송 요청 및 이력테이블 삭제
	 * @param eDTO 삭제할 데이터 키값
	 * @return 성공여부 (삭제된 데이터 개수)
	 * @throws Exception
	 */
	public int deleteDHPaystubmailHist(String keyId) throws SQLException, DataAccessException;
}
