package cz.uhk.automatedtestingapplication.model;

import javax.persistence.*;
import java.util.List;

@Entity
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long assignment_id;

    @Column
    private String name;

    @ManyToOne
    @JoinColumn
    private User creator;

    @OneToMany
    private List<Project> projectList;

    @ManyToOne
    @JoinColumn
    private Exam exam;

    public Assignment(String name, User creator, List<Project> projectList, Exam exam) {
        this.name = name;
        this.creator = creator;
        this.projectList = projectList;
        this.exam = exam;
    }

    public Assignment() {
    }

    public long getAssignment_id() {
        return assignment_id;
    }

    public void setAssignment_id(long assignment_id) {
        this.assignment_id = assignment_id;
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
}
