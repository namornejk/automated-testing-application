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
import cz.uhk.automatedtestingapplication.service.AssignmentService;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private FileSystemManagementService fileSystemManagementService;

    @Autowired
    private ExamDao examDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private AssignmentDao assignmentDao;

    @Autowired
    private ProjectDao projectDao;

    @RequestMapping("/studentTestList")
    public String showTestList(Model model){
        List<Exam> exams = new ArrayList<>();
        for (Exam e : examDao.findAllActivatedExams()) {
            if (!e.getAssignmentList().isEmpty())
                exams.add(e);
        }

        model.addAttribute("exams", exams);
        return "student-test-list";
    }

    @RequestMapping("/uploadStudentProject")
    public String uploadStudentProjectHandler(@RequestParam("examId") Long examId, @RequestParam("assignmentId") Long assignmentId,
                                  @RequestParam("file") MultipartFile file, Principal principal){
        User user = userDao.findByUsername(principal.getName());
        Assignment assignment = assignmentDao.findById(assignmentId).get();

        String fileName = file.getOriginalFilename();
        String extension = "";
        int i = fileName.lastIndexOf('.');

        if (i > 0) extension = fileName.substring(i+1);

        if(extension.contains("zip")){
            DateTime dt = new DateTime();
            DateTimeFormatter fmt = ISODateTimeFormat.dateTime();

            Project project = new Project(user.getUsername() + "_" + assignment.getName(), fmt.print(dt), user, assignment, false);
            projectDao.save(project);

            fileSystemManagementService.uploadStudentProject(examId, assignmentId, user.getUsername(), assignment.getName(), file);

            return "redirect:studentTestList?successfulUpload";
        } else {
            return "redirect:studentTestList?wrongArchiveFormat";
        }

    }

    @RequestMapping("/assignmentDetail")
    public String showAssignmentDetail(@RequestParam("examId") Long examId, @RequestParam("examPassword") String userExamPassword,
                                       Model model, Principal principal){

        Exam userExam = examDao.findById(examId).get();
        if(!userExamPassword.equals(userExam.getPassword())){
            return "redirect:studentTestList?wrongPassword";
        } else if(!userExam.getIsActivated()){
            return "redirect:studentTestList?wrongPassword";
        }else {
            User user = userDao.findByUsername(principal.getName());
            Assignment userAssignment = null;
            boolean isAlreadySubmitted = false;

            for (Assignment a : userExam.getAssignmentList()) {
                for (Project p : a.getProjectList()) {
                    if (p.getUser().getUsername().equals(user.getUsername())) {
                        isAlreadySubmitted = true;
                    }
                }
            }

            if (!isAlreadySubmitted) {
                userAssignment = assignmentService.getRandomAssignment(userExam.getAssignmentList());
                userAssignment.getUserList().add(user);
                assignmentDao.save(userAssignment);
            } else {
                return "redirect:studentTestList?alreadySubmited";
            }

            model.addAttribute("exam", userExam);
            model.addAttribute("assignment", userAssignment);
            return "assignment-detail";
        }
    }

    @RequestMapping("/projectDetail/{examId}")
    public String showProjectDetail(@PathVariable("examId") Long examId, Model model, Principal principal){
        List<Assignment> assignmentList = examDao.findById(examId).get().getAssignmentList();

        for (Assignment a : assignmentList) {
            for (Project p : a.getProjectList()) {
                if(p.getUser().getUsername().equals(principal.getName())){
                    Project project = projectDao.findById(p.getId()).get();

                    User user = project.getUser();

                    model.addAttribute("project", project);
                    model.addAttribute("user", user);
                    model.addAttribute("examId", project.getAssignment().getExam().getId());
                    return "student-project-detail";
                }
            }
        }
        return "redirect:/student/studentTestList?examNotSubmitted";
    }
}
