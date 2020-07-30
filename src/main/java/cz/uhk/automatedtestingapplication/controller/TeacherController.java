package cz.uhk.automatedtestingapplication.controller;

import cz.uhk.automatedtestingapplication.dao.*;
import cz.uhk.automatedtestingapplication.model.*;
import cz.uhk.automatedtestingapplication.model.testResult.Testcase;
import cz.uhk.automatedtestingapplication.service.FileSystemManagementService;
import cz.uhk.automatedtestingapplication.service.TestService;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.security.Principal;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

    @Autowired
    private FileSystemManagementService fileSystemManagementService;

    @Autowired
    private TestService testService;

    @Autowired
    private ExamDao examDao;

    @Autowired
    private AssignmentDao assignmentDao;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

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

    @RequestMapping("/mainWindow")
    public String showMainWindow(){
        return "main-window";
    }

    @RequestMapping("/teacherTestList")
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

    @RequestMapping("/create-assignment")
    public String createAssignmentHandler(@RequestParam("examId") long examId,
                                          @RequestParam("name") String assignmentName,
                                          @RequestParam("description") String assignmentDescription,
                                          @RequestParam("file") MultipartFile file,
                                          Principal principal){
        List<User> userList = new ArrayList<>();

        String fileName = file.getOriginalFilename();
        String extension = "";
        int i = fileName.lastIndexOf('.');

        if (i > 0) extension = fileName.substring(i+1);

        if(extension.contains("zip")){
            Assignment assignment = new Assignment(assignmentName, assignmentDescription, examDao.findById(examId).get(), new ArrayList<Project>(), userList);

            DateTime dt = new DateTime();
            DateTimeFormatter fmt = ISODateTimeFormat.dateTime();

            User user = userDao.findByUsername(principal.getName());

            Project project = new Project(assignmentName, fmt.print(dt), user, assignment, true);

            assignmentDao.save(assignment);
            projectDao.save(project);

            Exam exam = examDao.findById(examId).get();
            exam.getAssignmentList().add(assignment);
            examDao.save(exam);

            fileSystemManagementService.uploadOriginalProject(examId, assignment.getId(), assignmentName, file);
        }
        return "redirect:/teacher/assignmentList/" + examId;
    }

    @RequestMapping("/deleteAssignment/{examId}/{assignmentId}")
    public String deleteAssignmentHandler(@PathVariable("examId") Long examId,
                                          @PathVariable("assignmentId") Long assignmentId){
        deleteAssignment(examId, assignmentId);

        return "redirect:/teacher/assignmentList/" + examId;
    }

    @RequestMapping("/examDetail/{examId}")
    public String showExamDetail(@PathVariable("examId") long examId, Model model){
        Exam exam = examDao.findById(examId).get();
        model.addAttribute("exam", exam);
        return "exam-detail";
    }

    @RequestMapping("/activateExam/{examId}")
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

    @RequestMapping("/deleteExam/{examId}")
    public String deleteExamHandler(@PathVariable("examId") Long examId){
        Exam exam = examDao.findById(examId).get();

        examDao.deleteById(examId);
        fileSystemManagementService.deleteExam(examId.toString());

        return "redirect:/teacher/teacherTestList";
    }

    @RequestMapping("/setExamPassword/{examId}")
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

        List<Assignment> assignmentList = examDao.findById(examId).get().getAssignmentList();
        List<Project> projectList = new ArrayList<>();

        for (Assignment a : assignmentList) {
            List<Project> aProjectList = a.getProjectList();
            for (Project p : aProjectList) {
                if(!p.isTeacherProject())
                    projectList.add(p);
            }
        }

        int succesfullTests = 0;
        int allTests = 0;
        for (Project p : projectList) {
            succesfullTests += p.getNumberOfProjectListSuccessfullTests();
            allTests += p.getNumberOfProjectListTests();
        }

        float success = 0;
        if(allTests != 0)
            success = (succesfullTests / (float)allTests) * 100;

        model.addAttribute("projects", projectList);
        model.addAttribute("overallSuccess", success);
        model.addAttribute("examId", examId);

        return "project-list";
    }

    @RequestMapping("/userManagement")
    public String showUserManagement(Model model, Principal principal){
        User user = userDao.findByUsername(principal.getName());
        List<User> userList = userDao.findAll();
        model.addAttribute("roleList", roleDao.findAll());
        model.addAttribute("userList", userList);
        return "user-management";
    }

    @RequestMapping("/userDetail/{userId}")
    public String showUserDetail(@PathVariable("userId") Long userId, Model model){
        User user = userDao.findById(userId).get();
        List<Role> completeRoleList = roleDao.findAll();
        List<Role> filtredRoleList = new ArrayList<>();

        for (Role r : completeRoleList) {
            filtredRoleList.add(r);
        }

        for (Role r1 : completeRoleList) {
            for (Role r2: user.getRoleList()) {
                if(r1.getId() == r2.getId())
                    filtredRoleList.remove(r1);
            }
        }
        model.addAttribute("user", user);
        model.addAttribute("roleList", filtredRoleList);

        return "user-detail";
    }

    @RequestMapping("/updateUser")
    public String updateUserHandler(@RequestParam("userId") Long userId, @RequestParam("firstName") String firstName,
                                          @RequestParam("lastName") String lastName, @RequestParam("rolesId") List<Long> rolesId,
                                          ModelMap model){
        User user = userDao.findById(userId).get();

        List<Role> roleList = new ArrayList<>();
        for(Long roleId : rolesId){
            roleList.add(roleDao.findById(roleId).get());
        }

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRoleList(roleList);

        userDao.save(user);

        return "redirect:userDetail/" + user.getId() + "?userUpdated";
    }

    @RequestMapping("/createUser")
    public String createUser(@RequestParam("firstName") String firstName, @RequestParam("lastName") String lastName,
                             @RequestParam("rolesId") List<Long> rolesId){

        List<User> userList = userDao.findAll();
        List<Role> roleList = new ArrayList<>();

        int i = 1;
        for (User u : userList){
            if(u.getLastName().equals(lastName)){
                i++;
            }
        }

        for (Long roleId : rolesId) {
            roleList.add(roleDao.findById(roleId).get());
        }

        String nfdNormalizedString = Normalizer.normalize(lastName, Normalizer.Form.NFD).toLowerCase();
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String userName = pattern.matcher(nfdNormalizedString).replaceAll("").replaceAll("[^a-zA-Z]", "");

        String password = "123";
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(password);
        User user = new User(userName + i, hashedPassword, firstName, lastName, roleList);
        userDao.save(user);
        return "redirect:userManagement";
    }

    @RequestMapping("/deleteUser/{userId}")
    public String deleteUserHandler(@PathVariable("userId") Long userId, Principal principal){
        User selectedUser = userDao.findById(userId).get();
        User loggedInUser = userDao.findByUsername(principal.getName());

        if(selectedUser.getId() != loggedInUser.getId()){
            User user = userDao.findById(userId).get();

            userDao.delete(user);
            return "redirect:/teacher/userManagement";
        } else {
            return "redirect:/teacher/userManagement?selfDelete";
        }
    }

    @RequestMapping("/evaluateProjects")
    public String evaluate(@RequestParam("projects") List<Long> projectIdList, @RequestParam("examId") Long examId){
        List<Project> projectList = projectDao.findAllById(projectIdList);

        Long assignmentId = projectList.get(0).getAssignment().getId();
        String assignmentName = projectList.get(0).getAssignment().getName();

        testService.testProjects(projectList, examId, assignmentId, assignmentName);

        return "redirect:projectList/" + examId;
    }

    @RequestMapping("/projectDetail/{projectId}")
    public String projectDetail(@PathVariable("projectId") Long projectId, Model model){
        Project project = projectDao.findById(projectId).get();
        User user = project.getUser();

        model.addAttribute("project", project);
        model.addAttribute("user", user);
        model.addAttribute("examId", project.getAssignment().getExam().getId());
        return "project-detail";
    }

    @RequestMapping("/downloadStudentProject/{projectId}")
    @ResponseBody
    public void downloadProject(@PathVariable("projectId") Long projectId,
                                HttpServletResponse response, Principal principal){
        User user = userDao.findByUsername(principal.getName());
        Project project = projectDao.findById(projectId).get();

        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", String.format("attachment; filename=\"" + project.getUser().getUsername() + ".zip\""));
        response.setHeader("Content-Transfer-Encoding", "binary");

        try {
            BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
            FileInputStream fis = new FileInputStream(fileSystemManagementService.getStudentProject(
                    project.getAssignment().getExam().getId(),
                    project.getAssignment().getId(),
                    project.getUser().getUsername()));

            int length;
            byte[] buf = new byte[1024];
            while ((length = fis.read(buf)) > 0){
                bos.write(buf, 0, length);
            }
            bos.close();
            response.flushBuffer();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void deleteAssignment(Long examId, Long assignmentId){
        assignmentDao.deleteById(assignmentId);

        fileSystemManagementService.deleteAssignment(examId.toString(), assignmentId.toString());
    }
}