package com.smartcontactmanager.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.smartcontactmanager.dao.UserRepository;
import com.smartcontactmanager.entities.User;
import com.smartcontactmanager.helper.Message;

@Controller
public class HomeController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title","Home-Smart Contact Manager");
        return "home";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("title","About-Smart Contact Manager");
        return "about";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("title","Signup-Smart Contact Manager");
        model.addAttribute("user",new User());
        return "signup";
    }


    // handler for signup

    @PostMapping("/do_signup")
    public String signupUser(@Valid @ModelAttribute("user") User user,BindingResult bindingResult,@RequestParam(value = "agreement",defaultValue = "false") Boolean agreement,
    Model model,HttpSession session) {
       try{
        if(!agreement) {
            System.out.println("You have not agreed term and conditions");
            throw new Exception("You have not agreed term and conditions");
        }
        if(bindingResult.hasErrors())
        {
            System.out.print("ERROR"+bindingResult.toString());
            model.addAttribute("user", user);
            return "signup";
        }

        user.setRole("ROLE_USER");
        user.setEnabled(true);
        user.setPassword(passwordEncoder.encode(user.getPassword()));



        System.out.println(agreement);
        System.out.println(user);

        User result= this.userRepository.save(user);
        model.addAttribute("user", new User());
        session.setAttribute("message", new Message("Successfully Registered!!!!","alert-success"));

        System.out.println(result);
        return "signup";
       }
       catch(Exception e)
       {
        e.printStackTrace();
        model.addAttribute("user", user);
        session.setAttribute("message", new Message("Something Went wrong! "+e.getMessage(),"alert-danger"));
        return "signup";
       }
       
    }
    //handler for custum login
    @GetMapping("/signin")
    public String custumLogin(Model model)
    {
        model.addAttribute("title", "Login Page");
        return "login";
    }
    
}
