package com.care.boot.member;

import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpSession;

@Service
public class KaKaoService {
	private String accessToken;
	/*
	 * 카카오 개발자센터 문서
	 https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#request-token
	 */
	public void getAccessToken(String code) {
		String requrl = "https://kauth.kakao.com/oauth/token";
		String reqParam = "grant_type=authorization_code";
		reqParam += "&client_id=2f3401d146604c832240eaa73fc8c153";
		reqParam += "&redirect_uri=http://localhost:8087/dbQuiz/kakaoLogin";
		reqParam += "&code="+code;
		
		
		/*서버에 데이터 전달*/
		
		try {
			URL url = new URL(requrl);//POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");// POST 요청을 위해 기본값 false에서 setDoOutput으로 true로 만든다
			conn.setDoOutput(true);//POST 메소드를 이용해서 데이터를 전달하기 위한 설정
			// 기본 outputStream을 통해 문자열로 처리할 수 있는
			// OutPutStreamWriter 변환 후 처리속도를 빠르게 하기위한
		    // BufferedWriter로 변환해서 사용한다.
		
			BufferedWriter bw = new BufferedWriter(
					new OutputStreamWriter(conn.getOutputStream())
					);
			bw.write(reqParam);
			bw.flush();//데이터 보낸후 BufferedWriter초기화
			
			//요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
			InputStreamReader isr = new InputStreamReader(conn.getInputStream());
			ObjectMapper om = new ObjectMapper();
			Map<String,String> map= om.readValue(isr, new TypeReference<Map<String,String>>() {});
			accessToken = map.get("access_token");
			
			System.out.println("accessToken: "+map.get("access_token"));
			System.out.println(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	@Autowired private HttpSession session;
	public void getUserInfo() {
		String reqUrl = "https://kapi.kakao.com/v2/user/me";
		
	//	Authorization: Bearer ${ACCESS_TOKEN}
	
		try {
			URL url = new URL(reqUrl); // POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Authorization", "Bearer " + accessToken);//띄어쓰기 조심
			
			int responseCode = conn.getResponseCode();//결과 코드가 200이라면 성공
			System.out.println("responseCode:" + responseCode);
			
			ObjectMapper om = new ObjectMapper();
			
			JsonNode jsonNode = om.readTree(conn.getInputStream());
			
			System.out.println(jsonNode.get("kakao_account"));
			System.out.println(jsonNode.get("kakao_account").get("profile").get("nickname"));
			
			//session.setAttribute("id",jsonNode.get("kakao_account").get("email"));
			session.setAttribute("id",jsonNode.get("id"));
			
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void unlink() {
		String reqUrl = "https://kapi.kakao.com/v1/user/unlink";
		
	//	Authorization: Bearer ${ACCESS_TOKEN}
	
		try {
			URL url = new URL(reqUrl); // POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Authorization", "Bearer " + accessToken);//띄어쓰기 조심
			
			int responseCode = conn.getResponseCode();//결과 코드가 200이라면 성공
			System.out.println("responseCode:" + responseCode);
			
			ObjectMapper om = new ObjectMapper();
			JsonNode jsonNode = om.readTree(conn.getInputStream());
			System.out.println(jsonNode.get("id"));
						
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
