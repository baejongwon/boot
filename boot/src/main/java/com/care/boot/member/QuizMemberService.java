package com.care.boot.member;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.care.boot.PageService;

import jakarta.servlet.http.HttpSession;



@Service
public class QuizMemberService {
	@Autowired
	private IMemberMapper mapper; //IMemberMapper라는 자료형에 참조변수 memberDao
	@Autowired 
	private HttpSession session;
	
	public String registProc(QuizMemberDTO dto) {
		
		if(dto.getId()==null || dto.getId().trim().isEmpty()) {
			return "아이디를 입력하세요.";
		}
		if(dto.getPw()==null || dto.getPw().trim().isEmpty()) {
			return "비밀번호를 입력하세요.";
		}
		if(dto.getPw().equals(dto.getConfirm())==false) {
			return "두 비밀번호를 동일하게 입력하세요.";
		}
		if(dto.getUserName()==null || dto.getUserName().trim().isEmpty()) {
			return "이름을 입력하세요.";
		}
		
		QuizMemberDTO check = mapper.login(dto.getId());//id 가입 여부를 확인하기 위해
		if(check != null) {//id가 존재한다면
			return "이미 사용중인 아이디 입니다.";
		}
		/*비밀번호 암호화 과정*/
		/*메이븐 - Spring Security Web 필요*/
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String secretPass = encoder.encode(dto.getPw());
		dto.setPw(secretPass);
		System.out.println("암호문 : "+ secretPass);
		System.out.println("암호문길이 : "+ secretPass.length());
		/*회원 가입 시
		암호문 : $2a$10$tRFoaa7MGGyCfzHdBgU.p.UohCQ0pFsHBuwCulF71.j.Zahb5pEAW
		암호문길이 : 60
		pw 컬럼의 크기를 암호문 크기와 같거나 크게 변경
		alter table db_quiz modify pw varchar2(60);
		commit;
		*/
		
		int res = mapper.registProc(dto);
		if(res==1) {
			return "회원 등록 완료";	
		}
		return "회원등록을 다시 시도하세요.";
		
		
	}

	public void memberInfo(String select,String search,String cp,Model model) {
		
		int currentPage = 1;//현재 페이지
		try {
		currentPage = Integer.parseInt(cp);
		}catch (Exception e) {
			currentPage = 1;
		}
		
		if(select==null) {
			select="";//검색된 결과가 전부 나오게 하고싶어서.
		}
		int pageBlock = 3;//한 페이지에 보일 데이터의 수.
		int end = pageBlock * currentPage;//테이블에서 가져올 마지막 행 번호.
		int begin = end - pageBlock + 1; // 테이블에서 가져올 시작 행 번호.
		
		
		ArrayList<QuizMemberDTO> members = mapper.memberInfo(begin,end,select,search);
		int totalCount = mapper.totalCount(select,search); //테이블의 행의 갯수 를 구해 오기위함
		
		if(totalCount==0) {
			return ;
		}
		String url = "memberInfo?select="+select+"&search="+search+"&currentPage=";		
		String result = PageService.printPage(url, totalCount, pageBlock, currentPage);
		//result = jsp 에 변수 이름을 result로 해서
		
		model.addAttribute("members",members);//jsp에서 members를 참조하면  ArrayList에 접근 가능
		model.addAttribute("result",result);
		model.addAttribute("select", select);
		model.addAttribute("search", search);
		
	}

	public String userInfo(String id, Model model) {
		String sessionId = (String)session.getAttribute("id");
		if(sessionId == null)
			return "로그인 후 이용하세요.";
		
		if(sessionId.equals("admin") == false && sessionId.equals(id) == false) {
			return "본인의 아이디를 선택하세요.";
		}
		QuizMemberDTO member = mapper.login(id);
		model.addAttribute("member", member);
		return "회원 검색 완료";
	}
	
	public String updateProc(QuizMemberDTO member) {
		if(member.getPw() == null || member.getPw().trim().isEmpty()) {
			return "비밀번호를 입력하세요.";
		}
		if(member.getPw().equals(member.getConfirm()) == false) {
			return "두 비밀번호를 일치하여 입력하세요.";
		}
		if(member.getUserName() == null || member.getUserName().trim().isEmpty()) {
			return "이름을 입력하세요.";
		}
		
		/* 암호화 과정 */
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String secretPass = encoder.encode(member.getPw());
		member.setPw(secretPass);
		
		int result = mapper.updateProc(member);
		if(result == 1)
			return "회원 수정 완료";
		
		return "회원 수정을 다시 시도하세요.";
	}

	public String deleteProc(QuizMemberDTO member) {
		if(member.getPw() == null || member.getPw().trim().isEmpty()) {
			return "비밀번호를 입력하세요.";
		}
		if(member.getPw().equals(member.getConfirm()) == false) {
			return "두 비밀번호를 일치하여 입력하세요.";
		}
		QuizMemberDTO check = mapper.login(member.getId());//member에 getId를 얻어온다.
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		if(check!=null && encoder.matches(member.getPw(), check.getPw())==true) {
			int result = mapper.deleteProc(member.getId());
			if(result == 1) {
				return "회원 삭제 완료";
			}
			return "회원 삭제를 다시 시도하세요.";
		}
		return "아이디 또는 비밀번호를 확인 후 입력하세요.";
	}


}
