package oracle.ge.manualgrid.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IndexController {


    @GetMapping("/login")
    public String login(Model model, @RequestParam(value = "error", required = false) String error) {
        System.out.println("~~~~~~~~~~~~~~~~~~");
        return "forward:login.html";
    }

}
