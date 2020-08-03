package cz.uhk.automatedtestingapplication.controller;

import cz.uhk.automatedtestingapplication.dao.RoleDao;
import cz.uhk.automatedtestingapplication.dao.UserDao;
import cz.uhk.automatedtestingapplication.model.Role;
import cz.uhk.automatedtestingapplication.model.User;
import cz.uhk.automatedtestingapplication.service.FileSystemManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.ArrayList;
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
        if(userDao.count() == 0){
            createStartingUser();
        }
        return "log-in";
    }

    private void createStartingUser(){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode("root");

        User rootUser = new User("root", hashedPassword, "Root", "Root");
        Role r1 = new Role("TEACHER");
        Role r2 = new Role("STUDENT");

        List<Role> roles = new ArrayList<>();
        roles.add(r1);
        roles.add(r2);

        rootUser.addRole(r1);

        roleDao.saveAll(roles);
        userDao.save(rootUser);
    }

    @RequestMapping("/roleBasedSite")
    public String loginHandler(Model model, Principal principal){
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
