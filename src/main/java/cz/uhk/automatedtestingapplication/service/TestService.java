package cz.uhk.automatedtestingapplication.service;

import cz.uhk.automatedtestingapplication.dao.*;
import cz.uhk.automatedtestingapplication.model.Assignment;
import cz.uhk.automatedtestingapplication.model.Exam;
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

    public void testProjects(List<Project> projectList, String testerUsername){
        for (Project project : projectList) {
            List<Testsuite> testsuiteList = testProject(project, testerUsername);
            saveAllParsedResults(testsuiteList, project);

            List<Testsuite> oldTestsuite = project.getTestsuiteList();
            oldTestsuite.addAll(testsuiteList);
            project.setTestsuiteList(oldTestsuite);
            projectDao.save(project);
        }
    }

    public List<Testsuite> testProject(Project project, String testerUsername){
        Assignment assignment = project.getAssignment();
        Exam exam = assignment.getExam();

        String workplacePath = fileSystemManagementService.unzipProjectIntoWorkPlace(exam.getId(), assignment.getId(), project.getUser().getUsername(),
                assignment.getName(), testerUsername);

        fileSystemManagementService.unzipTestsIntoProject(exam.getId(), assignment.getId(), workplacePath);

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
            String operatingSystem = System.getProperty("os.name");
            Process process;

            if(operatingSystem.toUpperCase().contains("WINDOWS")){
                ProcessBuilder builder = new ProcessBuilder(
                        "cmd.exe", "/c", homeDriveLetter + ": && cd \"" + projectPath + "\" && mvn clean test");

                builder.redirectErrorStream(true);
                process = builder.start();
            } else {
                // Unix systems go here
                process = Runtime.getRuntime().exec("cd " + projectPath + " && mvn clean test");
            }


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
