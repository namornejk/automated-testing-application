package cz.uhk.automatedtestingapplication.controller;

import cz.uhk.automatedtestingapplication.dao.ProjectDao;
import cz.uhk.automatedtestingapplication.service.RolesFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Autowired
    private RolesFactory rolesFactory;

    @Autowired
    private ProjectDao projectDao;

    private String currentRole;

    @RequestMapping("/login")
    public String roleLogIn(){
        System.out.println(projectDao.findAll().toString());
        return "role-log-in";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String roleLogInHandler(@RequestParam String role){

        if(role.equals(rolesFactory.getSTUDENT())){
            this.currentRole = rolesFactory.getSTUDENT();
            return "test-list";
        }
        else if(role.equals(rolesFactory.getTEACHER())) {
            this.currentRole = rolesFactory.getTEACHER();
            return "teacher-menu";
        }
        else
            return "role-log-in";

    }

    @RequestMapping("/test-list")
    public String testList(){
        return "test-list";
    }

    @RequestMapping("/create-project")
    public String createProject(){
        return "create-project";
    }

    @RequestMapping(value = "/create-project", method = RequestMethod.POST)
    public String createProjectHandler(){
        return "test-list";
    }

}
