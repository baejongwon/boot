package com.care.boot.login;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.care.boot.member.QuizMemberDTO;


@Mapper
public interface ILoginMapper {

	QuizMemberDTO getMember(String id);

	String checkLogin(String id);
	
}
