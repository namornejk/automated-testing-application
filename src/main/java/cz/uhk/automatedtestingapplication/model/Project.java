package cz.uhk.automatedtestingapplication.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String name;

    @Column
    private Date timeSent;

    @ManyToOne
    @JoinColumn
    private User student;

    @Column
    private String result;

    @ManyToOne
    @JoinColumn
    private Assignment assignment;

    public Project() {
    }

    public Project(Long id, String name, Date timeSent, User student, String result, Assignment assignment) {
        this.id = id;
        this.name = name;
        this.timeSent = timeSent;
        this.student = student;
        this.result = result;
        this.assignment = assignment;
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

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    public Date getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(Date timeSent) {
        this.timeSent = timeSent;
    }
}
