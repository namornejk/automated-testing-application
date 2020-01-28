package cz.uhk.automatedtestingapplication.controller;

import cz.uhk.automatedtestingapplication.dao.AssignmentDao;
import cz.uhk.automatedtestingapplication.dao.ExamDao;
import cz.uhk.automatedtestingapplication.dao.UserDao;
import cz.uhk.automatedtestingapplication.model.Assignment;
import cz.uhk.automatedtestingapplication.model.Exam;
import cz.uhk.automatedtestingapplication.model.User;
import cz.uhk.automatedtestingapplication.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

    @Autowired
    private FileService fileService;

    @Autowired
    private ExamDao examDao;

    @Autowired
    private AssignmentDao assignmentDao;

    @Autowired
    private UserDao userDao;

    @RequestMapping("/db")
    public String db(Principal principal){
        String username = principal.getName();
        User user = userDao.findByUsername(username);

        Exam e1 = new Exam("PRO1", "První pondělní zápočet z PRO1 pro skupinu A.", user);
        Exam e2 = new Exam("PGRF", "První středeční zápočet z PGRF pro skupinu B.", user);

        examDao.save(e1);
        examDao.save(e2);
        return "redirect:/teacher/teacherTestList";
    }

    @GetMapping("/teacherTestList")
    public String showTestList(Model model){
        List<Exam> exams = examDao.findAll();

        model.addAttribute("exams", exams);

        return "test-list";
    }

    @RequestMapping("/create-exam")
    public String createExamHandler(@RequestParam("name") String name, @RequestParam("description") String description,
                                    Principal principal){

        String username = principal.getName();
        User user = userDao.findByUsername(username);

        Exam e = new Exam(name, description, user);

        examDao.save(e);

        //return "redirect:/teacher/teacherTestList";
        return "redirect:teacherAssignmentList/" + e.getId();
    }

    @RequestMapping("/teacherAssignmentList/{examId}")
    public String showTeacherAssignmentList(@PathVariable("examId") long examId, Model model){
        ArrayList<Assignment> assignments = assignmentDao.findByExamId(examId);

        model.addAttribute("assignments", assignments);
        model.addAttribute("examId", examId);

        return "assignment-list";
    }

    @PostMapping("/create-assignment")
    public String createAssignmentHandler(@RequestParam("examId") long examId,
                                          @RequestParam("name") String name,
                                          @RequestParam("description") String description,
                                          @RequestParam("file") MultipartFile file,
                                          Principal principal){

        //TODO - Dodělat vytváření adresářové struktury

        String username = principal.getName();
        User user = userDao.findByUsername(username);

        Exam exam = (Exam)examDao.findById(examId).get();

        Assignment assignment = new Assignment(name, description, user, exam);

        assignmentDao.save(assignment);
        fileService.uploadFile(file);

        return "redirect:/teacher/teacherAssignmentList/" + examId;
    }

    @GetMapping("/deleteAssignment/{examId}/{assignmentId}")
    public String deleteAssignmentHandler(@PathVariable("examId") Long examId,
                                          @PathVariable("assignmentId") Long assignmentId){

        assignmentDao.deleteById(assignmentId);
        /*
                TODO - Dodělat aby se společně s daty v databázi mazaly i soubory
                ?? asi v AssignmentSerivice v metodě deleteById kde se bude volat metoda deleteRelatedFiles ??
         */

        return "redirect:/teacher/teacherAssignmentList/" + examId;
    }

    @GetMapping("/examDetail")
    public String showExamDetail(){
        return "exam-detail";
    }
}