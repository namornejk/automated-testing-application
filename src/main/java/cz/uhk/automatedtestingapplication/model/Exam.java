package cz.uhk.automatedtestingapplication.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "Exams")
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long exam_id;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private String path;

    @ManyToOne
    @JoinColumn
    private User creator;

    @OneToMany(mappedBy = "exam")
    private List<Assignment> assignmentList;

    public Exam(String name, String description, User creator){
        this.name = name;
        this.description = description;
        this.creator = creator;
    }

    public Exam(String name, String description, String path, User creator, List<Assignment> assignmentList) {
        this.name = name;
        this.path = path;
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

    public long getExam_id() {
        return exam_id;
    }

    public void setExam_id(long exam_id) {
        this.exam_id = exam_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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
