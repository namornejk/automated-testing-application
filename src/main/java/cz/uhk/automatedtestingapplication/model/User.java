package cz.uhk.automatedtestingapplication.model;

import javax.persistence.*;
import java.util.List;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long user_id;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private String role;

    @OneToMany
    private List<Project> projectList;

    @OneToMany
    private List<Assignment> assignmentList;

    @OneToMany
    private List<Exam> examList;

    public User(String firstName, String lastName, String role, List<Project> projectList, List<Assignment> assignmentList, List<Exam> examList) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.projectList = projectList;
        this.assignmentList = assignmentList;
        this.examList = examList;
    }

    public User() {
    }


    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<Project> getProjectList() {
        return projectList;
    }

    public void setProjectList(List<Project> projectList) {
        this.projectList = projectList;
    }

    public List<Assignment> getAssignmentList() {
        return assignmentList;
    }

    public void setAssignmentList(List<Assignment> assignmentList) {
        this.assignmentList = assignmentList;
    }

    public List<Exam> getExamList() {
        return examList;
    }

    public void setExamList(List<Exam> examList) {
        this.examList = examList;
    }
}
