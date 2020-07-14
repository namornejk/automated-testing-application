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
import org.springframework.security.core.parameters.P;
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

        Exam e1 = new Exam("PRO1", "První pondělní zápočet z PRO1 pro skupinu A.");
        Exam e2 = new Exam("PGRF", "První středeční zápočet z PGRF pro skupinu B.");

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
                                    @RequestParam("examPassword") String examPassword){

        Exam e = new Exam(name, description, examPassword.equals("") == true ? "123" : examPassword);

        examDao.save(e);

        return "redirect:assignmentList/" + e.getId();
    }

    @RequestMapping("/assignmentList/{examId}")
    public String showAssignmentList(@PathVariable("examId") Long examId, Model model){
        Exam exam = examDao.findById(examId).get();
        List<Assignment> assignments = exam.getAssignmentList();

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
        String fileName = file.getOriginalFilename();
        String extension = "";
        int i = fileName.lastIndexOf('.');

        if (i > 0) extension = fileName.substring(i+1);

        if(extension.contains("zip")){
            Assignment assignment = new Assignment(assignmentName, assignmentDescription, examDao.findById(examId).get(), new ArrayList<Project>());

            DateTime dt = new DateTime();
            DateTimeFormatter fmt = ISODateTimeFormat.dateTime();

            Project project = new Project(assignmentName, fmt.print(dt));

            assignmentDao.save(assignment);
            projectDao.save(project);

            Exam exam = examDao.findById(examId).get();
            exam.getAssignmentList().add(assignment);
            examDao.save(exam);

            fileSystemManagementService.uploadOriginalProject(examId, assignment.getId(), assignmentName, file);
        }
        return "redirect:/teacher/assignmentList/" + examId;
    }

    @GetMapping("/deleteAssignment/{examId}/{assignmentId}")
    public String deleteAssignmentHandler(@PathVariable("examId") Long examId,
                                          @PathVariable("assignmentId") Long assignmentId){
        deleteAssignment(examId, assignmentId);

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
    public String deleteExamHandler(@PathVariable("examId") Long examId){
        Exam exam = examDao.findById(examId).get();

        examDao.deleteById(examId);
        fileSystemManagementService.deleteExam(examId.toString());

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

    @RequestMapping("/projectList/{examId}")
    public String showProjectList(@PathVariable("examId") Long examId,
                                  Model model){
        // Testovací projekt
        User user = new User("karel", "123");
        user.setFirstName("Karel"); user.setLastName("Novak");
        Project project = new Project("Nazev", "12:00", user);

        List<Project> projectList = new ArrayList<>();
        projectList.add(project);

        int succesfullTests = 0;
        int allTests = 0;
        for (Project p : projectList) {
            succesfullTests += p.getNumberOfProjectListSuccessfullTests();
            allTests += p.getNumberOfProjectListTests();
        }

        float success = (succesfullTests / (float)allTests) * 100;

        model.addAttribute("projects", projectList);
        model.addAttribute("overallSuccess", success);

        return "project-list";
    }

    private void deleteAssignment(Long examId, Long assignmentId){
        assignmentDao.deleteById(assignmentId);

        fileSystemManagementService.deleteAssignment(examId.toString(), assignmentId.toString());

    }
}