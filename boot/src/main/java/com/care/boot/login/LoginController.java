package com.care.boot.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {
	@Autowired
	private QuizLoginService service;
	@Autowired
	private HttpSession session;
	
	@RequestMapping("login")
	public String login() {
		return "member/login";
	}

	@PostMapping("loginProc")
	public String loginProc(String id,String pw, Model model,RedirectAttributes ra) {
		
		String msg = service.loginProc(id,pw);
		if(msg.equals("로그인 성공")) {
			ra.addFlashAttribute("msg",msg);//jsp에서 msg라는 이름으로 접근 가능
			return "redirect:index";
		}
		model.addAttribute("msg",msg);
		return "member/login";
	}

}
