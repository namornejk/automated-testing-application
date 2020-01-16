package cz.uhk.automatedtestingapplication.controller;

import ch.qos.logback.core.CoreConstants;
import cz.uhk.automatedtestingapplication.dao.ProjectDao;
import cz.uhk.automatedtestingapplication.service.RolesFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Collection;

@Controller
public class LoginController {

    @Autowired
    private RolesFactory rolesFactory;

    @RequestMapping(value = "/login")
    public String login(){
        return "log-in";
    }

    @RequestMapping(value = "/roleBasedSite")
    public String loginHandler(){
        System.out.println("logInHandler");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<GrantedAuthority> authorityList = (Collection<GrantedAuthority>) authentication.getAuthorities();

        for (GrantedAuthority authority : authorityList) {

            String role = authority.getAuthority();

            System.out.println(role);

            if(role.equals(rolesFactory.getSTUDENT())){
                System.out.println("student");
                return "test-list";
            }
            else if(role.equals(rolesFactory.getTEACHER())) {
                System.out.println("teacher");
                return "test-list";
            }

        }


        System.out.println("else");
        return "log-in";

    }

    @RequestMapping(value = "/testList")
    public String testList(){
        return "test-list";
    }

    @RequestMapping(value = "/createProject")
    public String createProject(){
        return "create-project";
    }

    @RequestMapping(value = "/createProject", method = RequestMethod.POST)
    public String createProjectHandler(){
        return "test-list";
    }

}
