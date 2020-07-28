package cz.uhk.automatedtestingapplication.controller;

import cz.uhk.automatedtestingapplication.dao.RoleDao;
import cz.uhk.automatedtestingapplication.dao.UserDao;
import cz.uhk.automatedtestingapplication.model.Role;
import cz.uhk.automatedtestingapplication.model.User;
import cz.uhk.automatedtestingapplication.service.FileSystemManagementService;
import cz.uhk.automatedtestingapplication.service.RolesFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Controller
public class LoginController {

    @Autowired
    private FileSystemManagementService fileSystemManagementService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @RequestMapping("/login")
    public String login(){
        return "log-in";
    }

    @RequestMapping("/createUsers")
    public String createUsers(){
        //password: aaa
        User u1 = new User("bruno1", "$2y$12$KJyTJr0X1btaLzq1BQmTtebN.HmSd5BCJHmt9Ecqg0E5xTJmNAbjy", "Bruno", "Ježek");
        Role r1 = new Role("TEACHER");
        //password: aaa
        User u2 = new User("tomas1", "$2y$12$FQ2ej666Ce94SuCPslmD6u/ipxyOcYSRK4LRp7334UHlm9ZC6./JG", "Tomáš", "Kudrna");
        Role r2 = new Role("STUDENT");

        List<Role> roles1 = new ArrayList<>();
        roles1.add(r1);

        List<Role> roles2 = new ArrayList<>();
        roles2.add(r2);

        u1.setRoleList(roles1);
        u2.setRoleList(roles2);

        roleDao.save(r1);
        userDao.save(u1);

        roleDao.save(r2);
        userDao.save(u2);

        return "redirect:login";
    }

    @RequestMapping("/roleBasedSite")
    public String loginHandler(Model model, Principal principal){
        //Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //Collection<GrantedAuthority> authorityList = (Collection<GrantedAuthority>) authentication.getAuthorities();

        User user = userDao.findByUsername(principal.getName());
        fileSystemManagementService.firstStartInit();

        if(user.getRoleList().size() == 1){
            for (Role role : user.getRoleList()) {
                if(role.getName().equals("STUDENT")){
                    return "redirect:/student/studentTestList";
                }
                else if(role.getName().equals("TEACHER")) {
                    return "redirect:/teacher/mainWindow";
                }
            }
        } else if(user.getRoleList().size() > 1){
            model.addAttribute("roles", user.getRoleList());
            return "redirect:/roleMenu";
        }

        return "log-in";
    }

    @RequestMapping("/roleMenu")
    public String showRoleMenu(){
        return "roles-menu";
    }

    @RequestMapping("/chooseRole")
    public String rolesMenuHandler(@PathVariable("roleId") Long roleId){
        Role role = roleDao.findById(roleId).get();
        if(role.getName().equals("STUDENT")){
            return "redirect:/student/studentTestList";
        }
        else if(role.getName().equals("TEACHER")) {
            return "redirect:/teacher/mainWindow";
        }
        else {
            return "redirect:roleBasedSite";
        }
    }

}
