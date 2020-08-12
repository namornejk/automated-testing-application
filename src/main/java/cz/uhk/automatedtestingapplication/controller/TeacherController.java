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

    @RequestMapping("/createExam")
    public String createExamHandler(@RequestParam("name") String name,
                                    @RequestParam("description") String description,
                                    @RequestParam("examPassword") String examPassword){

        if((name == null || description == null)
        || (name.equals("") || description.equals(""))){
            return "redirect:/teacher/teacherTestList?inputMissing";
        } else if(name.length() > 254){
            return "redirect:/teacher/teacherTestList?tooLongName";
        }
        Exam e;
        if(examPassword.equals(""))
            e = new Exam(name, description);
        else
            e = new Exam(name, description, examPassword);


        examDao.save(e);

        return "redirect:/teacher/teacherTestList";
    }

    @RequestMapping("/assignmentList/{examId}")
    public String showAssignmentList(@PathVariable("examId") Long examId, Model model){
        Exam exam = examDao.findById(examId).get();
        List<Assignment> assignments = exam.getAssignmentList();

        model.addAttribute("assignments", assignments);
        model.addAttribute("examId", examId);

        return "assignment-list";
    }

    @RequestMapping("/createAssignment")
    public String createAssignmentHandler(@RequestParam("examId") long examId,
                                          @RequestParam("name") String assignmentName,
                                          @RequestParam("description") String assignmentDescription,
                                          @RequestParam("projectFile") MultipartFile projectFile,
                                          @RequestParam("testFile") MultipartFile testFile,
                                          Principal principal){

        if((assignmentName == null || assignmentDescription == null || projectFile == null || testFile == null)
                || (assignmentName.equals("") || assignmentDescription.equals(""))){
            return "redirect:/teacher/assignmentList/" + examId + "?inputMissing";
        } else if(assignmentName.length() > 254){
            return "redirect:/teacher/assignmentList/" + examId + "?tooLongName";
        }

        List<User> userList = new ArrayList<>();

        if(isZipFile(projectFile.getOriginalFilename()) && isZipFile(testFile.getOriginalFilename())){
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

            fileSystemManagementService.uploadOriginalProject(examId, assignment.getId(), assignmentName, projectFile, testFile);

            return "redirect:/teacher/assignmentList/" + examId + "?successfulUpload";
        } else {
            return "redirect:/teacher/assignmentList/" + examId + "?wrongArchiveFormat";
        }
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
        if((firstName == null || lastName == null || rolesId == null)
                || (firstName.equals("") || lastName.equals("") || rolesId.isEmpty())){
            return "redirect:/teacher/userDetail/" + userId + "?inputMissing";
        } else if (firstName.length() > 254 || lastName.length() > 254){
            return "redirect:/teacher/userDetail/" + userId + "?tooLongUserName";
        }

        User user = userDao.findById(userId).get();

        List<Role> roleList = new ArrayList<>();
        for(Long roleId : rolesId){
            roleList.add(roleDao.findById(roleId).get());
        }

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRoleList(roleList);

        userDao.save(user);

        return "redirect:/teacher/userDetail/" + user.getId() + "?userUpdated";
    }

    @RequestMapping("/createUser")
    public String createUserHandler(@RequestParam("firstName") String firstName,
                             @RequestParam("lastName") String lastName,
                             @RequestParam("rolesId") List<Long> rolesId){

        if((firstName == null || lastName == null || rolesId == null)
        || (firstName.equals("") || lastName.equals("") || rolesId.isEmpty())){
            return "redirect:/teacher/userManagement?inputMissing";
        } else if (firstName.length() > 254 || lastName.length() > 254){
            return "redirect:/teacher/userManagement?tooLongUserName";
        }

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

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode("123");
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
    public String testProjectsHandler(@RequestParam("projects") List<Long> projectIdList,
                           @RequestParam("examId") Long examId, Principal principal){
        if(projectIdList != null){
            if(!projectIdList.isEmpty()){
                List<Project> projectList = projectDao.findAllById(projectIdList);

                Long assignmentId = projectList.get(0).getAssignment().getId();
                String assignmentName = projectList.get(0).getAssignment().getName();

                testService.testProjects(projectList, examId, assignmentId, assignmentName, principal.getName());

                return "redirect:projectList/" + examId;
            }
        }
        return "redirect:projectList/" + examId + "?noProjectSelected";

    }

    @RequestMapping("/projectDetail/{projectId}")
    public String showProjectDetail(@PathVariable("projectId") Long projectId, Model model){
        Project project = projectDao.findById(projectId).get();
        User user = project.getUser();

        model.addAttribute("project", project);
        model.addAttribute("user", user);
        model.addAttribute("examId", project.getAssignment().getExam().getId());
        return "project-detail";
    }

    private void deleteAssignment(Long examId, Long assignmentId){
        assignmentDao.deleteById(assignmentId);

        fileSystemManagementService.deleteAssignment(examId.toString(), assignmentId.toString());
    }

    private boolean isZipFile(String fileName){
        String extension = "";
        int i = fileName.lastIndexOf('.');

        if (i > 0) extension = fileName.substring(i+1);

        if(extension.contains("zip"))
            return true;
        else
            return false;
    }
}