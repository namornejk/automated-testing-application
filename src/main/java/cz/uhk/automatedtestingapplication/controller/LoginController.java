package cz.uhk.automatedtestingapplication.controller;

import cz.uhk.automatedtestingapplication.dao.RoleDao;
import cz.uhk.automatedtestingapplication.dao.UserDao;
import cz.uhk.automatedtestingapplication.model.Role;
import cz.uhk.automatedtestingapplication.model.User;
import cz.uhk.automatedtestingapplication.service.RolesFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Controller
public class LoginController {

    @Autowired
    private RolesFactory rolesFactory;

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @RequestMapping(value = "/db")
    public String databaseInit(){

        User user = new User("test", "test");
        Role role = new Role("TEACHER");

        List<Role> roles = new ArrayList<>();
        roles.add(role);
        List<User> users = new ArrayList<>();
        users.add(user);

        role.setUsers(users);
        user.setRoles(roles);

        userDao.save(user);
        roleDao.save(role);

        return "index";
    }

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
                return "redirect:/student/studentTestList";
            }
            else if(role.equals(rolesFactory.getTEACHER())) {
                return "redirect:/teacher/teacherTestList";
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
