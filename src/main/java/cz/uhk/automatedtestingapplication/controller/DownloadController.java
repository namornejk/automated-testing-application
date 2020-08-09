package cz.uhk.automatedtestingapplication.controller;

import cz.uhk.automatedtestingapplication.dao.AssignmentDao;
import cz.uhk.automatedtestingapplication.dao.ExamDao;
import cz.uhk.automatedtestingapplication.dao.ProjectDao;
import cz.uhk.automatedtestingapplication.dao.UserDao;
import cz.uhk.automatedtestingapplication.model.*;
import cz.uhk.automatedtestingapplication.service.FileSystemManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.Principal;

@Controller
public class DownloadController {

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private ExamDao examDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private FileSystemManagementService fileSystemManagementService;

    @RequestMapping("/downloadStudentProject/{projectId}")
    @ResponseBody
    public void downloadStudentProjectHandler(@PathVariable("projectId") Long projectId,
                                HttpServletResponse response, Principal principal){
        User user = userDao.findByUsername(principal.getName());
        Project project = projectDao.findById(projectId).get();
        Assignment assignment = project.getAssignment();
        Exam exam = project.getAssignment().getExam();

        boolean isAuthorized = false;

        for (Role r : user.getRoleList()) {
            if(r.getName().equals("TEACHER")) isAuthorized = true;
        }

        if(!isAuthorized){
            for (Assignment a : user.getAssignmentList()) {
                if(a.getId() == assignment.getId()) isAuthorized = true;
            }
        }

        if (isAuthorized){
            File projectFile = fileSystemManagementService.getTeacherProject(exam.getId(), assignment.getId());
            String examName = examDao.findById(exam.getId()).get().getName();
            downloadProject(response, projectFile, examName);
        }
    }

    @RequestMapping("/downloadTeacherProject/{examId}/{assignmentId}")
    @ResponseBody
    public void downloadTeacherProjectHandler(@PathVariable("examId") Long examId, @PathVariable("assignmentId") Long assignmentId,
                                       HttpServletResponse response, Principal principal){
        User user = userDao.findByUsername(principal.getName());
        boolean isAuthorized = false;
        boolean isAlreadySubmitted = false;

        for (Project p : user.getProjectList()) {
            if (p.getAssignment().getId() == assignmentId) isAlreadySubmitted = true;
        }

        for (Assignment a : user.getAssignmentList()) {
            if(a.getId() == assignmentId) isAuthorized = true;
        }

        if (isAuthorized && !isAlreadySubmitted){
            File projectFile = fileSystemManagementService.getTeacherProject(examId, assignmentId);
            String examName = examDao.findById(examId).get().getName();
            downloadProject(response, projectFile, examName);
        }
    }

    private void downloadProject(HttpServletResponse response, File project, String examName){
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", String.format("attachment; filename=\"" + examName + ".zip\""));
        response.setHeader("Content-Transfer-Encoding", "binary");

        try {
            BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
            FileInputStream fis = new FileInputStream(project);

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
}
