package cz.uhk.automatedtestingapplication.controller;

import cz.uhk.automatedtestingapplication.dao.ProjectDao;
import cz.uhk.automatedtestingapplication.model.Project;
import cz.uhk.automatedtestingapplication.service.FileSystemManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.security.Principal;

@Controller
public class DownloadController {

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private FileSystemManagementService fileSystemManagementService;

    @RequestMapping("/downloadStudentProject/{projectId}")
    @ResponseBody
    public void downloadProjectHandler(@PathVariable("projectId") Long projectId,
                                HttpServletResponse response, Principal principal){
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
}
