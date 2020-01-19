package cz.uhk.automatedtestingapplication.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Secured("ROLE_TEACHER")
public class TeacherController {

    @GetMapping("teacherTestList")
    public String testList(){
        System.out.println("Teacher test list.");
        return "test-list";
    }

}
