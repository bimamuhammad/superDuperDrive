package com.udacity.jwdnd.course1.cloudstorage.controllers;

import com.udacity.jwdnd.course1.cloudstorage.services.AuthenticationService;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String getLogin(){
        return "/login";
    }
    @GetMapping("/login-logout")
    public String getLogout(Model model){
        model.addAttribute("logoutsuccess", true);
        return "/login";
    }
}
