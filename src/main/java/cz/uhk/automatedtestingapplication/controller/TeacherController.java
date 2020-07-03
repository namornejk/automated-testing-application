package cz.uhk.automatedtestingapplication.controller;

import cz.uhk.automatedtestingapplication.dao.AssignmentDao;
import cz.uhk.automatedtestingapplication.dao.ExamDao;
import cz.uhk.automatedtestingapplication.dao.ProjectDao;
import cz.uhk.automatedtestingapplication.dao.UserDao;
import cz.uhk.automatedtestingapplication.model.Assignment;
import cz.uhk.automatedtestingapplication.model.Exam;
import cz.uhk.automatedtestingapplication.model.Project;
import cz.uhk.automatedtestingapplication.model.User;
import cz.uhk.automatedtestingapplication.service.FileSystemManagementService;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

    @Autowired
    private FileSystemManagementService fileSystemManagementService;

    @Autowired
    private ExamDao examDao;

    @Autowired
    private AssignmentDao assignmentDao;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private UserDao userDao;

    // Metoda vytvořená pouze pro vložení testovacích dat do databáze
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

    @GetMapping("/mainWindow")
    public String showMainWindow(){
        return "main-window";
    }

    @GetMapping("/teacherTestList")
    public String showTestList(Model model){
        List<Exam> exams = examDao.findAll();

        model.addAttribute("exams", exams);

        return "test-list";
    }

    @RequestMapping("/create-exam")
    public String createExamHandler(@RequestParam("name") String name, @RequestParam("description") String description,
                                    @RequestParam("examPassword") String examPassword, Principal principal){

        String username = principal.getName();
        User user = userDao.findByUsername(username);

        Exam e = new Exam(name, description, user, examPassword.equals("") == true ? "123" : examPassword);

        examDao.save(e);

        return "redirect:assignmentList/" + e.getId();
    }

    @RequestMapping("/assignmentList/{examId}")
    public String showAssignmentList(@PathVariable("examId") long examId, Model model){
        ArrayList<Assignment> assignments = assignmentDao.findByExamId(examId);

        model.addAttribute("assignments", assignments);
        model.addAttribute("examId", examId);

        return "assignment-list";
    }

    @PostMapping("/create-assignment")
    public String createAssignmentHandler(@RequestParam("examId") long examId,
                                          @RequestParam("name") String assignmentName,
                                          @RequestParam("description") String assignmentDescription,
                                          @RequestParam("file") MultipartFile file,
                                          Principal principal){

        //TODO - Dodělat vytváření adresářové struktury

        String fileName = file.getOriginalFilename();
        String extension = "";
        int i = fileName.lastIndexOf('.');

        if (i > 0) extension = fileName.substring(i+1);

        if(extension.contains("zip")){
            String username = principal.getName();
            User user = userDao.findByUsername(username);

            Exam exam = examDao.findById(examId).get();

            Assignment assignment = new Assignment(assignmentName, assignmentDescription, user, exam);

            DateTime dt = new DateTime();
            DateTimeFormatter fmt = ISODateTimeFormat.dateTime();

            Project project = new Project(assignmentName, fmt.print(dt), user, assignment);

            assignmentDao.save(assignment);
            projectDao.save(project);
            fileSystemManagementService.uploadOriginalProject(examId, assignment.getId(), assignmentName, file);
        }
        return "redirect:/teacher/assignmentList/" + examId;
    }

    @GetMapping("/deleteAssignment/{examId}/{assignmentId}")
    public String deleteAssignmentHandler(@PathVariable("examId") Long examId,
                                          @PathVariable("assignmentId") Long assignmentId){

        assignmentDao.deleteById(assignmentId);
        /*
                TODO - Dodělat aby se společně s daty v databázi mazaly i soubory
                ?? asi v AssignmentSerivice v metodě deleteById kde se bude volat metoda deleteRelatedFiles ??
         */

        return "redirect:/teacher/assignmentList/" + examId;
    }

    @GetMapping("/examDetail/{examId}")
    public String showExamDetail(@PathVariable("examId") long examId, Model model){
        Exam exam = examDao.findById(examId).get();
        model.addAttribute("exam", exam);
        return "exam-detail";
    }

    @GetMapping("/activateExam/{examId}")
    public String activateExamHandler(@PathVariable("examId") long examId){
        Exam exam = examDao.getOne(examId);

        if(exam.getIsActivated()){
            exam.setIsActivated(false);
        } else {
            exam.setIsActivated(true);
        }

        examDao.save(exam);

        return "redirect:/teacher/examDetail/" + examId;
    }

    @GetMapping("/deleteExam/{examId}")
    public String deleteExamHandler(@PathVariable("examId") long examId){
        examDao.deleteById(examId);

        return "redirect:/teacher/teacherTestList";
    }

    @PostMapping("/setExamPassword/{examId}")
    public String setExamPasswordHandler(@PathVariable("examId") long examId,
                                         @RequestParam("examPassword") String examPassword){
        Exam exam = examDao.getOne(examId);

        exam.setPassword(examPassword);

        examDao.save(exam);

        return "redirect:/teacher/examDetail/" + examId;
    }

}