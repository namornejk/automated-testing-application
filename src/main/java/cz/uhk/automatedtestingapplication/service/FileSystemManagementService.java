package cz.uhk.automatedtestingapplication.service;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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

    /*
        Path to original assignment project
            - user.home/automated_testing/exam_id/assignment_id/teacher/project_id.zip

        Path to student projects
            - user.home/automated_testing/exam_id/assignment_id/students/project_id.zip

        Path to student testing dir
            - user.home/automated_testing/exam_id/assignment_id/students/workplace/
     */

    public FileSystemManagementService(){

    }

    public String firstStartInit(){
        File file = new File(buildApplicationDir());
        if (!file.exists()) {
            if (file.mkdirs()) {
                return "Success";
            }
            return "File creation faild";
        }
        return "File already exists";
    }

    private String buildApplicationDir(){
        return this.homeDir + File.separator + this.applicationDirName;
    }

    private String buildTeacherDir(Long examID, Long assignmentID){
        return buildApplicationDir() + File.separator + examID + File.separator + assignmentID + File.separator + "teacher" + File.separator;
    }

    private void uploadFile(MultipartFile file, Path copyLocation){
        try{
            Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e){ e.printStackTrace(); }
    }

    public void uploadOriginalProject(long examID, long assignmentID, String assignmentName, MultipartFile file){
        String filePath = buildApplicationDir() + File.separator + examID + File.separator + assignmentID + File.separator + "teacher" + File.separator;
        createDirStructure(filePath);

        Path copyLocation = Paths
                .get(filePath + assignmentName + ".zip");

        this.uploadFile(file, copyLocation);
    }

    public void uploadStudentProject(long examID, long assignmentID, String username, String assignmentName, MultipartFile file){
        String filePath = buildApplicationDir() + File.separator + examID + File.separator + assignmentID + File.separator + "students" + File.separator;
        createDirStructure(filePath);

        String projectFileName = username + "_" + assignmentName;
        Path copyLocation = Paths
                .get(filePath + projectFileName + ".zip");

        this.uploadFile(file, copyLocation);
    }

    public String unzipProjectIntoWorkPlace(long examID, long assignmentID, String username, String assignmentName){
        String destinationFilePath = buildApplicationDir() + File.separator + examID + File.separator + assignmentID + File.separator + "workplace";
        createDirStructure(destinationFilePath);

        String sourceFilePath = buildApplicationDir() + File.separator + examID + File.separator + assignmentID + File.separator + "students" + File.separator;
        String projectFileName = username + "_" + assignmentName;
        unzip((sourceFilePath + projectFileName + ".zip"), destinationFilePath);

        return destinationFilePath;
    }

    public void copyFile(File source, File destination){
        Path sourcePath = source.toPath();
        Path destPath = destination.toPath();

        try {
            Files.copy(sourcePath, destPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteAssignment(String examID, String assignmentID){
        String assignmentPath = buildApplicationDir() + File.separator + examID + File.separator + assignmentID;
        deleteDirectory(new File(assignmentPath));
    }

    public void deleteExam(String examID){
        String examPath = buildApplicationDir() + File.separator + examID;
        deleteDirectory(new File(examPath));
    }

    public void deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        directoryToBeDeleted.delete();
    }

    private void createDirStructure(String path){
        File dirs = new File(path);
        if (!dirs.exists()) {
            dirs.mkdirs();
        }
    }

    public void unzip(String sourcePath, String destinationPath){
        try {
            ZipFile zipFile = new ZipFile(sourcePath);
            zipFile.extractAll(destinationPath);
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }

    public File getTeacherProject(Long examId, Long assignmentId){
        File file = new File(buildTeacherDir(examId, assignmentId));
        File[] fileArray = file.listFiles();
        return fileArray[0];
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
}
