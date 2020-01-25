package cz.uhk.automatedtestingapplication.controller;

import cz.uhk.automatedtestingapplication.dao.ExamDao;
import cz.uhk.automatedtestingapplication.dao.UserDao;
import cz.uhk.automatedtestingapplication.model.Exam;
import cz.uhk.automatedtestingapplication.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

    @Autowired
    private ExamDao examDao;

    @Autowired
    private UserDao userDao;

    @RequestMapping("/db")
    public String db(Principal principal){
        String username = principal.getName();
        User user = userDao.findByUsername(username);

        Exam e1 = new Exam("PRO1", "První pondělní zápočet z PRO1 pro skupinu A.");

        Exam e2 = new Exam("PGRF", "První středeční zápočet z PGRF pro skupinu B.");


        examDao.save(e1);
        examDao.save(e2);

        return "redirect:/teacher/teacherTestList";
    }

    @GetMapping("/teacherTestList")
    public String showTestList(Model model){
        List<Exam> exams = examDao.findAll();

        for (Exam e: exams) {
            System.out.println(e.getName());
        }

        model.addAttribute("exams", exams);

        return "test-list";
    }

    @RequestMapping("/create-exam")
    public String createExamHandler(@RequestParam("name") String name, @RequestParam("description") String description){
        Exam e = new Exam(name, description);
        examDao.save(e);

        return "redirect:/teacher/teacherTestList";
        //return "redirect:teacherAssignmentList";
    }

    @RequestMapping("/teacherAssignmentList")
    public String showTeacherAssignmentList(){
        return "teacher-assignment-list";
    }

    @RequestMapping("/examDetail")
    public String showExamDetail(){
        return "exam-detail";
    }
}