package cz.uhk.automatedtestingapplication.model;

import cz.uhk.automatedtestingapplication.model.testResult.Testsuite;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "Projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String name;

    @Column
    private String dateTime;

    @Column
    private boolean isTeacherProject;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "assignment_id")
    private Assignment assignment;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Testsuite> testsuiteList;

    public Project() {
    }

    public Project(String name, String dateTime) {
        this.name = name;
        this.dateTime = dateTime;
    }

    public Project(String name, String dateTime, User user) {
        this.name = name;
        this.dateTime = dateTime;
        this.user = user;
    }

    public Project(String name, String dateTime, User user, Assignment assignment, boolean isTeacherProject) {
        this.name = name;
        this.dateTime = dateTime;
        this.user = user;
        this.assignment = assignment;
        this.isTeacherProject = isTeacherProject;
    }

    public int getNumberOfProjectListSuccessfullTests(){
        if(this.testsuiteList != null) {
            int successfullTests = 0;
            for (Testsuite t : this.getTestsuiteList()) {
                successfullTests += t.getSuccessfullTests();
            }
            return successfullTests;
        }
        return 9;
    }

    public int getNumberOfProjectListTests(){
        if(this.testsuiteList != null) {
            int count = 0;
            for (Testsuite t : this.getTestsuiteList()) {
                count += t.getTests();
            }
            return count;
        }
        return 10;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Testsuite> getTestsuiteList() {
        return testsuiteList;
    }

    public void setTestsuiteList(List<Testsuite> testsuiteList) {
        this.testsuiteList = testsuiteList;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getFormatedDateTime(){
        DateTimeFormatter parser    = ISODateTimeFormat.dateTimeParser();
        DateTime dateTimeHere     = parser.parseDateTime(dateTime);
        DateTimeFormatter fmt = DateTimeFormat.forPattern("HH:mm:ss dd.MM.yyyy");
        return fmt.print(dateTimeHere);
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    public boolean isTeacherProject() {
        return isTeacherProject;
    }

    public void setTeacherProject(boolean teacherProject) {
        isTeacherProject = teacherProject;
    }

    public String getSuccessRate(){
        int allTests = 0, successfulTests = 0;

        for (Testsuite t : testsuiteList) {
            allTests += t.getTests();
            successfulTests += t.getSuccessfullTests();
        }

        return successfulTests + "/" + allTests;
    }

    public Integer getNumberOfAllTests(){
        int numberOfAllTests = 0;

        for (Testsuite t : testsuiteList) {
            numberOfAllTests += t.getTests();
        }

        return numberOfAllTests;
    }

    public Integer getNumberOfAllSuccessfulTests(){
        int numberOfAllSuccessfulTests = 0;

        for (Testsuite t : testsuiteList) {
            numberOfAllSuccessfulTests += t.getSuccessfullTests();
        }

        return numberOfAllSuccessfulTests;
    }

    public Integer getNumberOfAllFailedTests(){
        int numberOfAllFailedTests = 0;

        for (Testsuite t : testsuiteList) {
            numberOfAllFailedTests += t.getFailures();
        }

        return numberOfAllFailedTests;
    }

    public Integer getNumberOfAllSkippedTests(){
        int numberOfAllSkippedTests = 0;

        for (Testsuite t : testsuiteList) {
            numberOfAllSkippedTests += t.getSkipped();
        }

        return numberOfAllSkippedTests;
    }

    public Integer getNumberOfAllErrors(){
        int numberOfAllErrors = 0;

        for (Testsuite t : testsuiteList) {
            numberOfAllErrors += t.getErrors();
        }

        return numberOfAllErrors;
    }
}
