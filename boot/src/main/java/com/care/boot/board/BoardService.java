package com.care.boot.board;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.care.boot.PageService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


@Service
public class BoardService {

	@Autowired
	private IBoardMapper mapper;
	private String filePath = "/opt/tomcat/tomcat-10/webapps/upload/";
	
	public void boardForm(String cp,Model model) {
		
		int currentPage = 1;//현재 페이지
		
		try {
		currentPage = Integer.parseInt(cp);
		}catch (Exception e) {
			currentPage = 1;
		}
		
		int pageBlock = 3;//한 페이지에 보일 데이터의 수.
		int end = pageBlock * currentPage;//테이블에서 가져올 마지막 행 번호.
		int begin = end - pageBlock + 1; // 테이블에서 가져올 시작 행 번호.
		
		
		ArrayList<BoardDTO> boards = mapper.boardForm(begin,end);
		int totalCount = mapper.totalCount(); //테이블의 행의 갯수 를 구해 오기위함
		
		if(totalCount==0) {
			return ;
		}
		String url = "boardForm?currentPage=";		
		String result = PageService.printPage(url, totalCount, pageBlock, currentPage);
		
		model.addAttribute("boards",boards);
		model.addAttribute("result",result);
	}

	@Autowired
	private HttpSession session;
	public String boardWriteProc(MultipartHttpServletRequest multi) {
		//System.out.println("title : " + multi.getParameter("title"));
		
		String sessionId = (String) session.getAttribute("id");
		if(sessionId==null)
			return "redirect:login";
		
		String title = multi.getParameter("title");
		if(title == null || title.trim().isEmpty()) {
			return "redirect:boardWrite";
		}
		
		BoardDTO board = new BoardDTO();
		board.setTitle(title);
		board.setContent(multi.getParameter("content"));
		//board.setId(multi.getParameter("id")); 작성자는 칸이 없으므로 넣어줄 수 없음
		board.setId(sessionId);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		board.setWriteDate(sdf.format(new Date()));
		board.setFileName("");
		
		MultipartFile file = multi.getFile("upfile");
		if(file.getSize()!=0) {
			//파일이름
			sdf = new SimpleDateFormat("yyyyMMddHHmmss-");
			String fileTime = sdf.format(new Date());
			String fileName = file.getOriginalFilename();
			
			String suffix = fileName.substring(fileName.lastIndexOf(".")+1, fileName.length());
			System.out.println("BoardService-boardWriteProc-suffix : " + suffix);
			if(suffix.equalsIgnoreCase("pdf") == false)
				return "redirect:boardWrite";
			
			//파일의 저장 경로
			String fileSaveDirectory = filePath+sessionId;
			File f = new File(fileSaveDirectory);//파일 객체 생성 으로 sessionId위치에 폴더가 있는지 없는지 확인 하기
			if(f.exists() == false) {
				//저장경로가 없을 경우
				//f.mkdir();
				f.mkdirs();//자동으로 만들어 줄 수 있게 한다.
			}
			// \\(역슬러시)를 /(슬러시로)
			String fullPath = fileSaveDirectory + "/" + fileTime + fileName;
			board.setFileName(fullPath);
			f = new File(fullPath); //파일 객체에 저장경로에 대한 정보 넣어주기.
			try {
				file.transferTo(f);//파일 저장.
			} catch (IOException e) {
				e.printStackTrace();
				board.setFileName("");//예외가 발생하면 빈문자열을 넣어줄때. 
			}
			/*
			 * file.transferTo(); 
			 * 파일을 이동시키는 기능
			 * <input type="file" name="upfile">을 사용하여 서버에 파일 데이터가 전달되면
			 * 웹 서버가 임시파일로 저장을 한다.
			 * 임시파일로 저장된 파일을 개발자가 원하는 경로로 이동시킬 때 file.transferTo()를 사용한다.
			 * */
		}
		//조회수랑 게시글 번호는 insert 명령 시 입력..
		mapper.boardWriteProc(board);
		return "redirect:boardForm";
	}
	public BoardDTO boardContent(String no) {
		int n = 1;
		try {
			n = Integer.parseInt(no);
		}catch (Exception e) {
			return null;
		}
		BoardDTO board = mapper.boardContent(n);
		if(board != null) {//db에서 가져온 값이 null이 아니라면, 조회수 를 증가 시킨다.
			mapper.incrementHits(n);//hits값에 +1 을 하여 조회수 증가..
			board.setHits(board.getHits()+1);
			//다운로드 파일 이름 간략화 시키기..
			if(board.getFileName()!=null && board.getFileName().trim().isEmpty() == false) {
			String[] names=board.getFileName().split("/");
			/*/opt/tomcat/tomcat-10/webapps/upload/user1/22392514126-pom.xml*/
			for(String name : names) {
			System.out.println("name: " +name);
			}
			/*22392514126-pom.xml가7번째 이기 때문에 names[7]*/
			String[] fileNames = names[7].split("-",2);//분리할 데이터의 총 갯수는 2개 이다
			for(String fileName : fileNames) {
				System.out.println("fileName: " +fileName);
				}
			board.setFileName(fileNames[1]);
			}
		}
		return board;
	}
	public void boardDownload(String no, HttpServletResponse response) {
		int n=1;
		try {
			n=Integer.parseInt(no);
		}catch (Exception e) {
			return ;
		}
		String fullPath = mapper.boardDownload(n);
		if(fullPath == null) {
			return ;
		}
		
		String[] names = fullPath.split("/");
		String[] fileNames = names[7].split("-", 2);
		
		try {
			File file = new File(fullPath);
			if(file.exists()==false)
				return;
			response.setHeader(//헤더에 값을 셋팅해서 서버가 클라이언트에게 값을 전달할때 같이 전달 된다. 아래 문장 외우기.. 한글 안쓸시에 인코딩 필요x
					"Content-Disposition", 
					//attachment;filename=pom.xml
					"attachment;filename=" + URLEncoder.encode(fileNames[1], "UTF-8"));
			
			FileInputStream fis = new FileInputStream(file);//받아온 파일을 인풋 스트림 형태로 맞춰줌.
			FileCopyUtils.copy(fis, response.getOutputStream());//fis 파일의 위치 ,response.getOutputStream()= 파일을 전달할 매체
			//response.getOutputStream()을 사용할때 형식을 맞춰주기위해 FileInputStream으로 맞춰줌
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	public String boardModify(String no,Model model) {
		int n = 0;
		try {
			n = Integer.parseInt(no);
		} catch (Exception e) {
			return "redirect:boardForm";
		}
		BoardDTO board = mapper.boardContent(n);
		if(board == null)
			return "redirect:boardForm";
		
		if(board.getFileName() != null && board.getFileName().trim().isEmpty() == false) {
			String[] names = board.getFileName().split("/");
			String[] fileNames = names[7].split("-", 2);
			board.setFileName(fileNames[1]);
		}
		
		model.addAttribute("board",board);
		return "board/boardModify";
	}
	public String boardModifyProc(BoardDTO board) {
		BoardDTO check = mapper.boardContent(board.getNo());
		if(check == null)
			return "게시글 번호에 문제가 발생했습니다. 다시 시도하세요.";
		
		String sessionId = (String)session.getAttribute("id");
		if(check.getId().equals(sessionId) == false)
			return "작성자만 삭제 할 수 있습니다.";
		
		if(board.getTitle() == null || board.getTitle().trim().isEmpty()) {
			return "제목을 입력하세요.";
		}
		
		int result = mapper.boardModifyProc(board);
		if(result == 0)
			return "게시글 수정에 실패했습니다. 다시 시도하세요.";
		
		return "게시글 수정 성공";
	}
	public String boardDeleteProc(String no) {
		// 파일이 존재하면 삭제
				int n = 0;
				try {
					n = Integer.parseInt(no);
				} catch (Exception e) {
					return "게시글 번호에 문제가 발생했습니다. 다시 시도하세요.";
				}
				
				BoardDTO board = mapper.boardContent(n);
				if(board == null)
					return "게시글 번호에 문제가 발생했습니다. 다시 시도하세요.";
				
				// 로그인한 아이디와 작성자 아이디가 같은지 확인
				String sessionId = (String)session.getAttribute("id");
				if(board.getId().equals(sessionId) == false)
					return "작성자만 삭제 할 수 있습니다.";
				
				String fullPath = board.getFileName();
				if(fullPath != null) { // 테이블에 파일의 경로와 이름이 있다면
					File f = new File(fullPath);
					if(f.exists() == true) // 파일 저장소에 파일이 존재한다면
						f.delete();
				}
				
				// 테이블에서 게시글번호와 일치하는 행(row)삭제
				mapper.boardDeleteProc(n);
				return "게시글 삭제 완료";
	}


}
