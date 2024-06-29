<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:choose>
	<c:when test="${res == 1 }">
		<script type="text/javascript">
			alert("회원가입 성공!! 로그인을 해 주세요")
			location.href="index"
		</script>
	</c:when>
	<c:otherwise>
		<script type="text/javascript">
			alert("회원가입 실패!! 다시입력해 주세요")
			location.href="regist"
		</script>
	</c:otherwise>
</c:choose>







