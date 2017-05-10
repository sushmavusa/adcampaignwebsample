package com.adcampaign.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;


@WebServlet("/AdCampaignServlet")
public class AdCampaignServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	HttpClient client = new DefaultHttpClient();

	/*
	 * create ad
	 * doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpPost post = new HttpPost("http://localhost:8080/ad/");
		try {
			//get content from html page
			String partnerId=request.getParameter("partnerId");
			String duration=request.getParameter("duration");
			String adContent=request.getParameter("adContent");

			//prepare json string object for request body
			StringBuffer jsonStringBuffer=new StringBuffer("{\"partnerId\":");
			jsonStringBuffer.append(partnerId);
			jsonStringBuffer.append(",\"duration\":");
			jsonStringBuffer.append(duration);
			jsonStringBuffer.append(",\"adContent\":\"");
			jsonStringBuffer.append(adContent);
			jsonStringBuffer.append("\"}");

			StringEntity requestEntity = new StringEntity( jsonStringBuffer.toString(), ContentType.APPLICATION_JSON);
			post.setEntity(requestEntity);

			//call service to add ad
			HttpResponse res = client.execute(post);
			// process response
			processOutput(request, response, res);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/*
	 * create ad or ads
	 * doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		//check the input and prepare url
		String partnerId=request.getParameter("partnerId");
		String url=" ";
		if(partnerId!=null){
			url="http://localhost:8080/ad/"+request.getParameter("partnerId");
		}else{
			url="http://localhost:8080/ads/";
		}
		HttpGet get = new HttpGet(url);
		try {
			//call service to retrieve data
			HttpResponse res = client.execute(get);

			// process response
			processOutput(request, response, res);
		}catch (Exception e) {
			e.printStackTrace();
		}	
	}

	private void processOutput(HttpServletRequest request,
			HttpServletResponse response, HttpResponse res) throws IOException,
			ServletException {
		BufferedReader br = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
		String output = br.readLine();
		RequestDispatcher rd = request.getRequestDispatcher("index.html");
		response.setContentType("text/html");  
		PrintWriter out = response.getWriter();
		if(output==null || output.isEmpty() || output.equalsIgnoreCase("{}")){
			output="no active ad campaigns exist for the specified partner";
		}
		out.println("<font color=red>"+output+"</font>");  
		rd.include(request,response);
	}
}
