package cz.uhk.automatedtestingapplication.service;

import cz.uhk.automatedtestingapplication.dao.ProjectDao;
import cz.uhk.automatedtestingapplication.dao.TestsuiteDao;
import cz.uhk.automatedtestingapplication.model.Project;
import cz.uhk.automatedtestingapplication.model.testResult.Testsuite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class TestService {

    @Autowired
    private FileSystemManagementService fileSystemManagementService;

    @Autowired
    private XmlService xmlService;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private TestsuiteDao testsuiteDao;

    public void testProjects(List<Project> projectList, long examID, long assignmentID, String assignmentName){
        System.out.println("Starting testProjects() loop -------------");
        for (Project project : projectList) {
            List<Testsuite> testsuiteList = testProject(examID, assignmentID, project.getUser().getUsername(), assignmentName);
            for(Testsuite t : testsuiteList){
                t.setProject(project);
                testsuiteDao.save(t);
            }
            project.setTestsuiteList(testsuiteList);

            System.out.println("Saving Project to DB");
            projectDao.save(project);
        }
        System.out.println("End testProjects() loop -------------");
    }

    public List<Testsuite> testProject(long examID, long assignmentID, String username, String assignmentName){
        System.out.println("Unzipping file into workspace");
        String projectPath = fileSystemManagementService.unzipProjectIntoWorkPlace(examID, assignmentID, username, assignmentName);
        runTestsInProject(projectPath);

        return getTestsResults(projectPath);
    }

    public void runTestsInProject(String projectPath){
        System.out.println("Starting runTestsInProject() ------");
        try{
            ProcessBuilder builder = new ProcessBuilder(
                    "cmd.exe", "/c", "cd \"" + projectPath + "\" && mvn clean test");

            builder.redirectErrorStream(true);
            System.out.println("Starting process in cmd");
            Process process = builder.start();

            BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while (true) {
                line = r.readLine();
                if (line == null) { break; }
                System.out.println(line);
            }
            process.destroy();
        } catch (IOException e){e.printStackTrace();}
        System.out.println("End runTestsInProject() ------");
    }

    public List<Testsuite> getTestsResults(String projectPath){
        List<Testsuite> testsuiteList = new ArrayList<>();

        File projectFile = new File(projectPath);
        String[] projectFileName = projectFile.list();

        String reportsDirPath = projectPath + projectFileName[0] + File.separator + "target" + File.separator + "surefire-reports";
        File reportsDir = new File(reportsDirPath);

        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File f, String name) {
                return name.endsWith(".xml");
            }
        };

        String[] reports = reportsDir.list(filter);

        System.out.println("Starting xml parsing loop ---");
        for(String fileName : reports){
            File report = new File(reportsDirPath + File.separator + fileName);

            testsuiteList.add(xmlService.parseTestsuite(report));
        }
        System.out.println("End xml parsing loop ---");
        return testsuiteList;
    }
}
