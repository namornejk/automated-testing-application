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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

   @GetMapping("/login")
    public String login(){
/*
        //password: aaa
        User u1 = new User("bruno", "$2y$12$KJyTJr0X1btaLzq1BQmTtebN.HmSd5BCJHmt9Ecqg0E5xTJmNAbjy");
        Role r1 = new Role("TEACHER");
        //password: aaa
        User u2 = new User("tomas", "$2y$12$FQ2ej666Ce94SuCPslmD6u/ipxyOcYSRK4LRp7334UHlm9ZC6./JG");
        Role r2 = new Role("STUDENT");

        List<Role> roles1 = new ArrayList<>();
        roles1.add(r1);
        List<User> users1 = new ArrayList<>();
        users1.add(u1);

        List<Role> roles2 = new ArrayList<>();
        roles2.add(r2);
        List<User> users2 = new ArrayList<>();
        users2.add(u2);

        r1.setUsers(users1);
        u1.setRoles(roles1);

        r2.setUsers(users2);
        u2.setRoles(roles2);

        userDao.save(u1);
        roleDao.save(r1);

        userDao.save(u2);
        roleDao.save(r2);*/

        return "log-in";
    }

    @RequestMapping("/roleBasedSite")
    public String loginHandler(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<GrantedAuthority> authorityList = (Collection<GrantedAuthority>) authentication.getAuthorities();

        for (GrantedAuthority authority : authorityList) {

            String role = authority.getAuthority();

            if(role.equals(rolesFactory.getSTUDENT())){
                return "redirect:/student/studentTestList";
            }
            else if(role.equals(rolesFactory.getTEACHER())) {
                return "redirect:/teacher/mainWindow";
            }
        }

        return "log-in";
    }

}
