package cz.uhk.automatedtestingapplication.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.security.RolesAllowed;

@Controller
@Secured("ROLE_STUDENT")
public class StudentController {

    @GetMapping("studentTestList")
    public String testList(){
        System.out.println("Student test list.");
        return "test-list";
    }

}
