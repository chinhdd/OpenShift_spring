package org.jboss.tools.example.springmvc.mvc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

//import javax.servlet.ServletContext;

import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value="/test")
public class TestController {

	//@Autowired
	//ServletContext context;
	
	@RequestMapping(method=RequestMethod.GET)
	public String displayTestPage(Model model) {
		return "test";
	}
	
	@RequestMapping(value="/getNumber", method=RequestMethod.GET, produces="application/json")
	public @ResponseBody Integer getDataFromFile() {
		
		FileReader fileReader = null;
		BufferedReader bufferReader = null;
		String content = "";
		int num = 0;
		
		try {
			//String filePath = Thread.currentThread().getContextClassLoader().getResource("files/data.txt").getFile();
			//File file = new File(filePath);
			fileReader = new FileReader("files/data.txt");
			//fileReader = new FileReader(file);
			bufferReader = new BufferedReader(fileReader);
			content = bufferReader.readLine();
			
			num = Integer.parseInt(content);
						
			bufferReader.close();
			return new Integer(num);
		} catch (FileNotFoundException e) {
			
		} catch (IOException e) {
			
		}
		
		return new Integer(9999);
	}
}
