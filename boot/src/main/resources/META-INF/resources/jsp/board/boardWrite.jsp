<%@ page language="java" contentType="text/html; charset=UTF-8"   pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:import url="/header" />

<div align="center">
	<form action="boardWriteProc" method='post' enctype="multipart/form-data">
	<!-- enctype="multipart/form-data" - 파일에 데이터를 전송을 하기 위해서 필요 -->
		<table>
			<caption>
				<font size="5"><b>게시글 등록</b></font>
			</caption>
			<tr>
				<th width="100px">제목</th>
				<td><input style="width: 100%;" type="text" name="title"></td>
			</tr>
			<tr>
				<th>내용</th>
				<td>
					<textarea style="width: 100%;" rows="10" cols="30" name="content"></textarea>
				</td>
			</tr>
			<tr>
				<th>파일첨부</th>
				<td><input type="file" name="upfile"></td>
			</tr>
			<tr>
				<td colspan="2" align="center">
					<input type="submit" value="글쓰기"> 
					<input type="button" value="목록"	 onclick="location.href='boardForm'">
				</td>
			</tr>
		</table>
	</form>
</div>
<c:import url="/footer" />












