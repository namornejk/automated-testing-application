package cz.uhk.automatedtestingapplication.controller;

import cz.uhk.automatedtestingapplication.dao.UserDao;
import cz.uhk.automatedtestingapplication.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserDao userDao;

    @GetMapping("/showProfile")
    public String showProfile(Principal principal, Model model){
        String username = principal.getName();
        User user = userDao.findByUsername(username);

        model.addAttribute("user", user);

        return "profile";
    }

    @PostMapping("/editProfile")
    public String editProfile(@RequestParam("firstName") String firstName, @RequestParam("lastName") String lastName,
                              @RequestParam("password") String password, @RequestParam("passwordConf") String passwordConf,
                              Principal principal){

        if(password.equals(passwordConf)){

            String username = principal.getName();
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = passwordEncoder.encode(password);

            if(!password.equals("")){
                userDao.updateUserWithPassword(firstName, lastName, hashedPassword, username);
            } else {
                userDao.updateUserWithNoPassword(firstName, lastName, username);
            }

            return "redirect:/roleBasedSite";

        } else {
            return "redirect:showProfile?error";
        }
    }
}