package com.udacity.jwdnd.course1.cloudstorage.controllers;

import com.udacity.jwdnd.course1.cloudstorage.model.User;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.http.HttpClient;

@Controller
public class SignupController {
    private UserService userService;

    public SignupController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/signup")
    public String getSignup(@ModelAttribute("user") User user, Model model){
        return "/signup";
    }

    @PostMapping("/signup")
    public String registerUser(@ModelAttribute("user") User user, Model model, RedirectAttributes redirectAttributes){
        if(!userService.userExists(user.getUsername())){
            //        model.addAttribute("")
            // if successful
            model.addAttribute("signuperror", "User already exists, naviagte to login to login");
        }else {
            // Try to create the user
            int userid = userService.createUser(user);
            if(userid > 0){
                redirectAttributes.addFlashAttribute("signpsuccess", true);
                return "redirect:/login";
            }else {
                model.addAttribute("signuperror", "User creation failed");
            }

        }
        return "/signup";
    }
}

