package cz.uhk.automatedtestingapplication.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "Exams")
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private String fileName;

    @Column
    private Boolean isActivated;

    @Column
    private String password;

    @ManyToOne
    @JoinColumn
    private User creator;

    @OneToMany(mappedBy = "exam")
    private List<Assignment> assignmentList;

    public Exam(String name, String description, User creator){
        this.name = name;
        this.description = description;
        this.creator = creator;
        this.isActivated = false;
    }

    public Exam(String name, String description, User creator, String password){
        this(name, description, creator);
        this.password = password;
    }

    public Exam(String name, String description, String fileName, Boolean isActivated, String password, User creator, List<Assignment> assignmentList) {
        this.name = name;
        this.description = description;
        this.fileName = fileName;
        this.isActivated = isActivated;
        this.password = password;
        this.creator = creator;
        this.assignmentList = assignmentList;
    }

    public Exam() {
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Boolean getIsActivated(){
        return isActivated;
    }

    public void setIsActivated(Boolean isActivated){
        this.isActivated = isActivated;
    }

    public Boolean getActivated() {
        return isActivated;
    }

    public void setActivated(Boolean activated) {
        isActivated = activated;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public List<Assignment> getAssignmentList() {
        return assignmentList;
    }

    public void setAssignmentList(List<Assignment> assignmentList) {
        this.assignmentList = assignmentList;
    }
}
