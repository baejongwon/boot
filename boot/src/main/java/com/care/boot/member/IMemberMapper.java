package com.care.boot.member;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface IMemberMapper {
	//추상메소드들임
	int registProc(QuizMemberDTO dto);

	ArrayList<QuizMemberDTO> memberInfo(@Param("begin")int begin, @Param("end")int end,	@Param("select")String select,@Param("search")String search);

	List<QuizMemberDTO> userInfo(QuizMemberDTO dto);

	QuizMemberDTO login(String id);

	int totalCount(@Param("select")String select,@Param("search")String search);//테이블의 행의 갯수 를 구해 오기위함

	int updateProc(QuizMemberDTO member);

	int deleteProc(String id);

		
}










