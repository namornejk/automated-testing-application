package cz.uhk.automatedtestingapplication.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    @NotNull
    private String username;

    @Column
    @NotNull
    private String password;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @ManyToMany(mappedBy="users", fetch = FetchType.EAGER, cascade=CascadeType.ALL, targetEntity = Role.class)
    @NotNull
    private List<Role> roles;

    @OneToMany
    private List<Project> projectList;

    @OneToMany
    private List<Assignment> assignmentList;

    @OneToMany
    private List<Exam> examList;

    public User() {
    }

    public User(String username, String password){
        this.username = username;
        this.password = password;
    }

    public User(Long id, String username, String password, String firstName, String lastName, List<Role> roles, List<Project> projectList, List<Assignment> assignmentList, List<Exam> examList) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roles = roles;
        this.projectList = projectList;
        this.assignmentList = assignmentList;
        this.examList = examList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id){
        this.id = id;
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

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String userName) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
