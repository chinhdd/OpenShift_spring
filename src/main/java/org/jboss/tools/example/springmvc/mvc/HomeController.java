package org.jboss.tools.example.springmvc.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value="/")
public class HomeController {

   @RequestMapping(method=RequestMethod.GET)
   public String displayHomePage(Model model) {
      return "home";
   }
}
