package com.care.boot.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.care.boot.member.IMemberMapper;
import com.care.boot.member.QuizMemberDTO;

import jakarta.servlet.http.HttpSession;


@Service
public class QuizLoginService {
	
	@Autowired
	private HttpSession session;
	
	@Autowired
	private ILoginMapper loginDao;
	@Autowired
	private IMemberMapper mapper; 
	/*
	public String loginProc(QuizLoginDTO dto, Model model) {
		if (dto.getId() == null || dto.getId().trim().equals("")){
			return "login";
		}
		String dbPass = loginDao.checkLogin(dto.getId());
		int res;
		if (dbPass==null) {	//입력된 값과 DB의 값을 비교하여 같으면	
			res= 1;
		}else if(dbPass.equals(dto.getPw())) {							//값이 틀리다면...
			res= 0;
		}else {								//아이디 찾기 실패시...
			res= 2;
	}
		
		String msg = null, url = null;
		switch(res) {
		case 0 :
			QuizMemberDTO memberDto = loginDao.getMember(dto.getId());
			session.setAttribute("id", memberDto.getId());
			msg = memberDto.getId() + "님이 로그인 하셨습니다.";
			url = "index";
			break;
		case 1 :
			msg = "없는 아이디 입니다. 다시 입력해 주세요!!";
			url = "login";
			break;
		case 2 :
			msg = "비밀번호가 틀렸습니다. 다시 입력해 주세요!!";
			url = "login";
			break;
		}
		model.addAttribute("msg", msg);
		model.addAttribute("url", url);
		return url;
	}
	 */

	public String loginProc(String id, String pw) {
		if(id==null || id.trim().isEmpty()) {
			return "아이디를 입력하세요.";
		}
		if(pw==null || pw.trim().isEmpty()) {
			return "비밀번호를 입력하세요.";
		}
		//데이터베이스에 있는 id와 pw가 같은지 아닌지 비교.
		QuizMemberDTO check = mapper.login(id);//id 가입 여부를 확인하기 위해
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		/*check.getPw().equals(pw)*/ 
		if(check !=null && encoder.matches(pw, check.getPw()) == true) {//암호화를 했기 때문에, 기존에 암호화 하지 않은 계정은 로그인 불가.
			session.setAttribute("id", check.getId());
			session.setAttribute("userName", check.getUserName());
			session.setAttribute("address", check.getAddress());
			session.setAttribute("mobile", check.getMobile());
			/* session.setAttribute("member", check);로 사용한다면
			 * ${sessionScope.member.id}
			 * ${sessionScope.member.pw}
			 * ${sessionScope.member.userName}으로 jsp에서 사용할 수 있다.
			 */
			return "로그인 성공";
		}else	
			return "아이디 또는 비밀번호를 확인 후 다시 입력하세요.";
	}
}
