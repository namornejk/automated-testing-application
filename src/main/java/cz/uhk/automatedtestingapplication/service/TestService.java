package cz.uhk.automatedtestingapplication.service;

import cz.uhk.automatedtestingapplication.dao.*;
import cz.uhk.automatedtestingapplication.model.Project;
import cz.uhk.automatedtestingapplication.model.testResult.*;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private TestcaseDao testcaseDao;
    @Autowired
    private FailureDao failureDao;

    public void testProjects(List<Project> projectList, long examID, long assignmentID, String assignmentName,
                             String testerUsername){
        for (Project project : projectList) {
            List<Testsuite> testsuiteList = testProject(examID, assignmentID, project.getUser().getUsername(),
                    assignmentName, testerUsername);
            saveAllParsedResults(testsuiteList, project);

            List<Testsuite> oldTestSuite = project.getTestsuiteList();
            oldTestSuite.addAll(testsuiteList);
            project.setTestsuiteList(oldTestSuite);
            projectDao.save(project);
        }
    }

    public List<Testsuite> testProject(long examID, long assignmentID, String username, String assignmentName,
                                       String testerUsername){
        String workplacePath = fileSystemManagementService.unzipProjectIntoWorkPlace(examID, assignmentID, username,
                assignmentName, testerUsername);

        File workplace = new File(workplacePath);
        String[] folders = workplace.list();

        String projectPath = workplacePath + File.separator + folders[0];

        runTestsInProject(projectPath);
        List<Testsuite> testResults = getTestsResults(projectPath);

        fileSystemManagementService.deleteDirectory(workplace);

        return testResults;
    }

    public void runTestsInProject(String projectPath){
        String homeDriveLetter = fileSystemManagementService.getHomeDriveLetter();

        try{
            ProcessBuilder builder = new ProcessBuilder(
                    "cmd.exe", "/c", homeDriveLetter + ": && cd \"" + projectPath + "\" && mvn clean test");

            builder.redirectErrorStream(true);
            Process process = builder.start();

            BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while (true) {
                line = r.readLine();
                if (line == null) { break; }
            }
        } catch (IOException e){e.printStackTrace();}
    }

    public List<Testsuite> getTestsResults(String projectPath){
        List<Testsuite> testsuiteList = new ArrayList<>();

        String reportsDirPath = projectPath + File.separator + "target" + File.separator + "surefire-reports";
        File reportsDir = new File(reportsDirPath);

        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File f, String name) {
                return name.endsWith(".xml");
            }
        };

        String[] reports = reportsDir.list(filter);

        for(String fileName : reports){
            File report = new File(reportsDirPath + File.separator + fileName);

            testsuiteList.add(xmlService.parseTestsuite(report));
        }

        return testsuiteList;
    }

    public void saveAllParsedResults(List<Testsuite> testsuiteList, Project project){
        List<Testsuite> oldTestsuiteList = project.getTestsuiteList();
        if(!oldTestsuiteList.isEmpty()){
            oldTestsuiteList.clear();
            project.setTestsuiteList(oldTestsuiteList);
            projectDao.save(project);
        }

        for(Testsuite testsuite : testsuiteList){
            testsuite.setProject(project);
            testsuiteDao.save(testsuite);

            for (Testcase testCase : testsuite.getTestcase()) {
                testCase.setTestsuite(testsuite);
                testcaseDao.save(testCase);

                if(testCase.getFailure() != null){
                    Failure failure = testCase.getFailure();
                    failure.setTestcase(testCase);
                    failureDao.save(failure);
                }
            }
        }
    }
}
