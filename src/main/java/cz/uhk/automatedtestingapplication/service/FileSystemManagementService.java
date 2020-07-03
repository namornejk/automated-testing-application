package cz.uhk.automatedtestingapplication.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileSystemManagementService {

    @Value("${app.upload.dir:${user.home}}")
    private String homeDir;
    private static final String applicationDirName = "automated_testing";
    private String applicationDir;

    /*
        Path to original assignment project
            - user.home/automated_testing/exam_id/assignment_id/teacher/project_id.zip

        Path to student projects
            - user.home/automated_testing/exam_id/assignment_id/students/project_id.zip

        Path to student testing dir
            - user.home/automated_testing/exam_id/assignment_id/students/workplace/
     */

    public FileSystemManagementService(){}

    public String firstStartInit(){
        this.applicationDir = homeDir + File.separator + this.applicationDirName;
        System.out.println(this.applicationDir);

        File file = new File(this.applicationDir);
        if (!file.exists()) {
            if (file.mkdirs()) {
                return "Success";
            }
            return "File creation faild";
        }
        return "File already exists";
    }

    private void uploadFile(MultipartFile file, Path copyLocation){

        try{
            Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e){ e.printStackTrace(); }

    }

    public void uploadOriginalProject(long examID, long assignmentID, String assignmentName, MultipartFile file){
        String filePath = applicationDir + File.separator + examID + File.separator + assignmentID + File.separator + "teacher" + File.separator;
        File dirs = new File(filePath);
        if (!dirs.exists()) {
            dirs.mkdirs();
        }

        Path copyLocation = Paths
                .get(filePath + StringUtils.cleanPath(assignmentName + ".zip"));

        this.uploadFile(file, copyLocation);
    }

    public String getHomeDir() {
        return homeDir;
    }

    public void setHomeDir(String homeDir) {
        this.homeDir = homeDir;
    }

    public static String getApplicationDirName() {
        return applicationDirName;
    }

    public String getApplicationDir() {
        return applicationDir;
    }

    public void setApplicationDir(String applicationDir) {
        this.applicationDir = applicationDir;
    }

}
