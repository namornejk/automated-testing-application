package cz.uhk.automatedtestingapplication.controller;

import cz.uhk.automatedtestingapplication.service.RolesFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<GrantedAuthority> authorityList = (Collection<GrantedAuthority>) authentication.getAuthorities();

        for (GrantedAuthority authority : authorityList) {

            String role = authority.getAuthority();

            if(role.equals(rolesFactory.getSTUDENT())){
                return "redirect:/studentTestList";
            }
            else if(role.equals(rolesFactory.getTEACHER())) {
                return "redirect:/teacherTestList";
            }
        }

        return "log-in";
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
