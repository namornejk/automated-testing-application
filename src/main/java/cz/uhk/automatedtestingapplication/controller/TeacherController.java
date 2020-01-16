package cz.uhk.automatedtestingapplication.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller(value = "/teacher/*")
public class TeacherController {

    @RequestMapping(value = "teacherMenu")
    public String teacherMenu(){
        return "teacher-menu";
    }

}
