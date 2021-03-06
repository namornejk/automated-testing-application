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

    private String buildStudentsDir(Long examID, Long assignmentID){
        return buildApplicationDir() + File.separator + examID + File.separator + assignmentID + File.separator + "students" + File.separator;
    }

    private void uploadFile(MultipartFile file, Path copyLocation){
        try{
            Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e){ e.printStackTrace(); }
    }

    public void uploadOriginalProject(long examID, long assignmentID, String assignmentName, MultipartFile projectFile, MultipartFile testFile){
        String filePath = buildApplicationDir() + File.separator + examID + File.separator + assignmentID + File.separator + "teacher" + File.separator;
        createDirStructure(filePath);

        Path projectCopyLocation = Paths
                .get(filePath + assignmentName + "_project.zip");
        Path testCopyLocation = Paths
                .get(filePath + "tests.zip");

        this.uploadFile(projectFile, projectCopyLocation);
        this.uploadFile(testFile, testCopyLocation);
    }

    public void uploadStudentProject(long examID, long assignmentID, String username, String assignmentName, MultipartFile file){
        String filePath = buildApplicationDir() + File.separator + examID + File.separator + assignmentID + File.separator + "students" + File.separator;
        createDirStructure(filePath);

        String projectFileName = username + "_" + assignmentName;
        Path copyLocation = Paths
                .get(filePath + projectFileName + ".zip");

        this.uploadFile(file, copyLocation);
    }

    public String unzipProjectIntoWorkPlace(long examID, long assignmentID, String ownerUsername, String assignmentName,
                                            String testerUsername){
        String destinationFilePath = buildApplicationDir() + File.separator
                + examID + File.separator
                + assignmentID + File.separator
                + "workplace" + File.separator
                + testerUsername;

        createDirStructure(destinationFilePath);

        String sourceFilePath = buildApplicationDir() + File.separator + examID + File.separator + assignmentID + File.separator + "students" + File.separator;
        String projectFileName = ownerUsername + "_" + assignmentName;
        unzip((sourceFilePath + projectFileName + ".zip"), destinationFilePath);

        return destinationFilePath;
    }

    public String unzipTestsIntoProject(long examID, long assignmentID, String projectPath){
        File workplaceUserDir = new File(projectPath);
        String[] folders = workplaceUserDir.list();
        String projectName = folders[0];

        String destinationPath = projectPath + File.separator + projectName + File.separator + "src" + File.separator + "test";

        String sourceFilePath = buildApplicationDir() + File.separator + examID + File.separator + assignmentID + File.separator + "teacher" + File.separator;
        unzip((sourceFilePath + "tests.zip"), destinationPath);

        return destinationPath;
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
        File teacherDir = new File(buildTeacherDir(examId, assignmentId));
        File[] fileArray = teacherDir.listFiles();

        File teacherProjectFile = null;
        for (File f : fileArray) {
            if(f.getName().contains("_project.zip")){
                teacherProjectFile = f;
            }
        }

        return teacherProjectFile;
    }

    public File getTestFile(Long examId, Long assignmentId){
        File teacherDir = new File(buildTeacherDir(examId, assignmentId));
        File[] fileArray = teacherDir.listFiles();

        File testFile = null;
        for (File f : fileArray) {
            if(f.getName().contains("_tests.zip")){
                testFile = f;
            }
        }

        return testFile;
    }

    public File getStudentProject(Long examId, Long assignmentId, String username){
        File studentDir = new File(buildStudentsDir(examId, assignmentId));
        for (File f : studentDir.listFiles()) {
            if(f.getName().contains(username)){
                return f;
            }
        }
        return null;
    }

    public String getHomeDir() {
        return homeDir;
    }

    public String getHomeDriveLetter(){
        String[] driveAndOther = homeDir.split(":");

        return driveAndOther[0];
    }

    public void setHomeDir(String homeDir) {
        this.homeDir = homeDir;
    }

    public static String getApplicationDirName() {
        return applicationDirName;
    }
}
