package cz.uhk.automatedtestingapplication.controller;

import cz.uhk.automatedtestingapplication.dao.ProjectDao;
import cz.uhk.automatedtestingapplication.service.RolesFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;

@Controller
public class LoginController {

    @Autowired
    private RolesFactory rolesFactory;

    @Autowired
    private ProjectDao projectDao;

    private String currentRole;

    @RequestMapping(value = "/")
    public String home(){
        return "index";
    }

    @RequestMapping(value = "/login")
    public String login(){
        return "log-in";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String loginHandler(@RequestParam String role){
        System.out.println("logInHandler");
        if(role.equals(rolesFactory.getSTUDENT())){
            System.out.println("student");
            this.currentRole = rolesFactory.getSTUDENT();
            return "test-list";
        }
        else if(role.equals(rolesFactory.getTEACHER())) {
            System.out.println("teacher");

            this.currentRole = rolesFactory.getTEACHER();

            return "test-list";
        }
        System.out.println("else");
        return "log-in";

    }

    @RequestMapping(value = "/test-list")
    public String testList(){
        return "test-list";
    }

    @RequestMapping(value = "/create-project")
    public String createProject(){
        return "create-project";
    }

    @RequestMapping(value = "/create-project", method = RequestMethod.POST)
    public String createProjectHandler(){
        return "test-list";
    }

}
