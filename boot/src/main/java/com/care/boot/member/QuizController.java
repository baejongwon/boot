package com.care.boot.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
public class QuizController {
	//private QuizMemberService service = new QuizMemberService();을 자동으로 하는 코드 가 아래 
	@Autowired
	private QuizMemberService service;
	@Autowired private HttpSession session;
	//QuizMemberService에 원하는 값이 있으면 참조변수 service에 넣어준다.

	@GetMapping("register")
	public String register() {
		return "member/regist";
	}

	@PostMapping("registProc")//registProc에 6개의 정보가 들어오는데 이것을 한번에 받기위해 QuizMemberDTO dto로 사용 
	public String registProc(QuizMemberDTO dto, Model model, RedirectAttributes ra) {//Jsp쪽에 제공이 필요할때 Model model을 사용 , jsp쪽에서는 el을 사용하여 접근 가능.
		String msg = service.registProc(dto); //성공 실패여부를 알아야하기 때문에 값을 msg에 담는다.
		if(msg.equals("회원 등록 완료")) {
		ra.addFlashAttribute("msg",msg);
		return "redirect:index"; //회원가입이 성공했을경우
		}else {
			model.addAttribute("msg",msg);
			return "member/regist";//실패했을경우
		}
	}
	@RequestMapping("logout")
	public String logout(RedirectAttributes ra) {
		session.invalidate();
		ra.addFlashAttribute("msg","로그아웃");
		
		kakaoService.unlink();
		return "redirect:index";
	}

	@RequestMapping("memberInfo")
	public String memberInfo(String select, String search,
			@RequestParam(value="currentPage",required=false)String cp,Model model) {		
		service.memberInfo(select,search,cp,model);
		return "member/memberInfo";
	}


	@RequestMapping("userInfo")
	public String userInfo(String id, Model model,  RedirectAttributes ra) {
		String msg = service.userInfo(id, model);
		if(msg.equals("회원 검색 완료"))
			return "member/userInfo";
		
		ra.addFlashAttribute("msg", msg);
		return "redirect:memberInfo";
	}
	@RequestMapping("update")
	public String update() {
		String sessionId = (String)session.getAttribute("id");
		if(sessionId == null)
			return "redirect:login";
		
		return "member/update";
	}
	
	@PostMapping("updateProc")
	public String updateProc(QuizMemberDTO member, Model model) {
		String sessionId = (String)session.getAttribute("id");
		if(sessionId == null)
			return "redirect:login";
		
		member.setId(sessionId);
		String msg = service.updateProc(member);
		if(msg.equals("회원 수정 완료")) {
			session.invalidate();
			return "redirect:index";
		}
		
		model.addAttribute("msg", msg);
		return "member/update";
	}
	
	@RequestMapping("delete")
	public String delete() {
		String sessionId= (String)session.getAttribute("id");
		if(sessionId == null)
			return "redirect:login";
			
		return "member/delete";
	}
	@PostMapping("deleteProc")
	public String deleteProc(QuizMemberDTO member, Model model) {
		String sessionId= (String)session.getAttribute("id");
		if(sessionId == null)
			return "redirect:login";
		
		member.setId(sessionId);
		String msg=service.deleteProc(member);
		if(msg.equals("회원 삭제 완료")) {
			session.invalidate();
			return "redirect:index";
		}
		model.addAttribute("msg",msg);
		return "member/delete";
	}
	/*
	 * http://localhost:8087/dbQuiz/kakaoLogin?
	 * code=U7282sLRisIgBakPiGlnUwxZ3Z-7pYquxYtPqeJFCseKNPlx7Fq3Ru2QVVNm5wrG9yVZZAoqJU8AAAGLA55xmg
	 * */
	@GetMapping("kakaoLogin")
	public String kakaoLogin(String code) {
		System.out.println("code"+code);
		kakaoService.getAccessToken(code);
		kakaoService.getUserInfo();
		
		return "redirect:index";
		
	}
	@Autowired private KaKaoService kakaoService;
}
