package com.care.boot.board;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


@Controller
public class BoardController {
	@Autowired
	private BoardService service;
	
	@RequestMapping("boardForm")
	public String boardForm(
			@RequestParam(value="currentPage", required = false)String cp, Model model) {
		service.boardForm(cp,model);
		return "board/boardForm";
	}
	
	@Autowired
	private HttpSession session;//로그인한 사용자만 사용할수 있게
	@RequestMapping("boardWrite")
	public String boardWrite() {
		String sessionID=(String)session.getAttribute("id");
		if(sessionID==null) {
			return "redirect:login";
		}
		return "board/boardWrite";
	}
	
	@PostMapping("boardWriteProc")//registProc에 6개의 정보가 들어오는데 이것을 한번에 받기위해 QuizMemberDTO dto로 사용 
	public String boardWriteProc(MultipartHttpServletRequest multi) {//Jsp쪽에 제공이 필요할때 Model model을 사용 , jsp쪽에서는 el을 사용하여 접근 가능.
		String path = service.boardWriteProc(multi);
		return path;
	}
	
	@RequestMapping("boardContent")
	public String boardContent(String no, Model model) { //boardForm에서 게시글의 번호를 가지고 오기 떄문에 Stirng no
		BoardDTO board = service.boardContent(no);
		if(board == null) {
			return "redirect:boardForm";
		}
		model.addAttribute("board",board);
		return "board/boardContent";
	}
	@RequestMapping("boardDownload")
	public void boardDownload(String no, HttpServletResponse response) {//HttpServletResponse응답에 담아주기 위해서 필요하다.
		service.boardDownload(no, response);
	}
	
	@RequestMapping("boardModify")
	public String boardModify(String no, Model model) {
		String sessionId = (String) session.getAttribute("id");
		if(sessionId == null)
			return "redirect:login";
		String path = service.boardModify(no, model);
		return path;
	}
	
	@PostMapping("boardModifyProc")
	public String boardModifyProc(BoardDTO board, RedirectAttributes ra) {
		String sessionId = (String) session.getAttribute("id");
		if(sessionId == null)
			return "redirect:login";
		String msg = service.boardModifyProc(board);
		ra.addFlashAttribute("msg", msg);
		 
		if(msg.equals("게시글 수정 성공"))
			return "redirect:boardContent?no="+board.getNo();
		
		return "redirect:boardModify?no="+board.getNo();
	}
	
	@RequestMapping("boardDeleteProc")
	public String boardDeleteProc(String no) {
		String sessionId = (String) session.getAttribute("id");
		if(sessionId == null)
			return "redirect:login";
		
		String msg = service.boardDeleteProc(no);
		if(msg.equals("작성자만 삭제 할 수 있습니다."))
			return "redirect:boardContent?no="+no;
		
		return "redirect:boardForm";
	}
}

