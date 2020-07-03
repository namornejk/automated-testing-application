package cz.uhk.automatedtestingapplication.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "Assignments")
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String name;

    @Column
    private String description;

    @ManyToOne
    @JoinColumn
    private User creator;

    @OneToMany
    private List<Project> projectList;

    @ManyToOne
    @JoinColumn
    private Exam exam;

    public Assignment() {
    }

    public Assignment(String name, String description, User creator, Exam exam){
        this.name = name;
        this.description = description;
        this.creator = creator;
        this.exam = exam;
    }

    public Assignment(String name, String description, User creator, List<Project> projectList, Exam exam) {
        this.name = name;
        this.description = description;
        this.creator = creator;
        this.projectList = projectList;
        this.exam = exam;
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

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public List<Project> getProjectList() {
        return projectList;
    }

    public void setProjectList(List<Project> projectList) {
        this.projectList = projectList;
    }

    public Exam getExam() {
        return exam;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
