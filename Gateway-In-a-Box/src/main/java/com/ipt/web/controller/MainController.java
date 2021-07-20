package com.ipt.web.controller;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.context.annotation.Scope;

import com.ipt.web.model.Comment;
import com.ipt.web.model.Job;
import com.ipt.web.model.MappedUser;
import com.ipt.web.model.Reply;
import com.ipt.web.repository.JobHistoryRepository;
import com.ipt.web.service.CommentService;
import com.ipt.web.service.ReplyService;

import java.net.*;
import java.io.*;

@Controller
public class MainController {

		
	@Autowired
    private JobHistoryRepository jobHistoryRepository;

    private String TOKEN_URL="https://eagerapp1.herokuapp.com/getToken";
    private String LOG_URL="https://eagerapp1.herokuapp.com/log";

    public String getToken() throws Exception {
      StringBuilder result = new StringBuilder();
      URL url = new URL(TOKEN_URL);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
      try (BufferedReader reader = new BufferedReader(
                  new InputStreamReader(conn.getInputStream()))) {
          for (String line; (line = reader.readLine()) != null; ) {
              result.append(line);
          }
      }
      return result.toString();
   }

   public void log() throws Exception {
      URL url = null;
      String jsonInputString=null;
      StringBuilder result=new StringBuilder();
      try{
            url = new URL(LOG_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setDoOutput(true);

            jsonInputString = "{\"token\":\""+getToken()+"\", \"application\":\"GIB Web Application\"}";
            try(OutputStream os = conn.getOutputStream()) {
              byte[] input = jsonInputString.getBytes("utf-8");
              os.write(input, 0, input.length);
            }
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            //curl_output=abc;
            while ((line = rd.readLine()) != null) {
              result.append(line);
            }
            rd.close();
          }catch(MalformedURLException e){
            e.printStackTrace();
          }catch(ProtocolException e){
            e.printStackTrace();
          }catch(IOException e){
            e.printStackTrace();
          }
   }

   public void log(String metric) throws Exception {
      URL url = null;
      String jsonInputString=null;
      StringBuilder result=new StringBuilder();

      try{
            url = new URL(LOG_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setDoOutput(true);

            jsonInputString = "{\"token\":\""+getToken()+"\", \"metric\":\""+metric+"\", \"application\":\"GIB Web Application\"}";
            try(OutputStream os = conn.getOutputStream()) {
              byte[] input = jsonInputString.getBytes("utf-8");
              os.write(input, 0, input.length);
            }
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            //curl_output=abc;
            while ((line = rd.readLine()) != null) {
              result.append(line);
            }
            rd.close();
          }catch(MalformedURLException e){
            e.printStackTrace();
          }catch(ProtocolException e){
            e.printStackTrace();
          }catch(IOException e){
            e.printStackTrace();
          }
   }



		//Under construction
		@RequestMapping(value = "/jobHistory", method = RequestMethod.GET)
		public String showJobHistory(Model model, HttpServletRequest request) throws Exception{
			log("");
			//List<Job> jobs = jobHistoryRepository.findAll();

			List<Job> jobs = null;
			if(request.getSession().getAttribute("is_cilogon").toString()=="true")
			jobs = jobHistoryRepository.findByUserName(request.getSession().getAttribute("curusername").toString());
			else
			jobs = jobHistoryRepository.findByUserName(request.getUserPrincipal().getName());
			//List<MappedUser> jobs = jobHistoryRepository.findAll();
			//List<Job> jobs = new ArrayList<Job>();
			model.addAttribute("jobs", jobs);
			return "jobHistory";

		}
		//Under construction
		@RequestMapping(value = "/help", method = RequestMethod.GET)
		public String showHelp(Model model) throws Exception{
                        log("help");
			
			return "help";

		}
		
		@RequestMapping(value = "/aboutus", method = RequestMethod.GET)
		public String showAboutUs(Model model) throws Exception{
                        log("about us");
		
			return "about-us";

		}
		
		@RequestMapping(value = "/faq", method = RequestMethod.GET)
		public String showFaq(Model model) throws Exception{
                        log("faq");
			
			return "faq";

		}
		
		@RequestMapping(value = "/vdemos", method = RequestMethod.GET)
		public String showVDemo(Model model) throws Exception{
                        log("video demos");
			
			return "vdemos";

		}
		
		@RequestMapping(value = "/contactus", method = RequestMethod.GET)
		public String showContactUs(Model model) throws Exception{
                        log("contact us");
			
			return "contactus";

		}
		
		@RequestMapping(value = "/accessDenied", method = RequestMethod.GET)
		public String accessDenied(){
			return "accessDenied";
		}
	
		@RequestMapping(value = "/pagenotfound", method = RequestMethod.GET)
		public String pagenotfound(){
			return "pagenotfound";
		}
		
		@RequestMapping(value = "/forgotPassword", method = RequestMethod.GET)
		public String showForgotPassword(Model model) {
			return "forgotPassword";
		}

	

}
