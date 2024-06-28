package com.care.boot.board;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface IBoardMapper {

	void boardWriteProc(BoardDTO board);

	ArrayList<BoardDTO> boardForm(@Param("begin")int begin, @Param("end")int end);

	int totalCount();

	BoardDTO boardContent(int n);

	void incrementHits(int n);

	String boardDownload(int n);

	int boardModifyProc(BoardDTO board);

	void boardDeleteProc(int n);

	

	

}
