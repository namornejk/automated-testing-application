package cz.uhk.automatedtestingapplication.model;

import javax.persistence.*;

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

    @ManyToOne
    @JoinColumn
    private User user;

    @Column
    private String result;

    @ManyToOne
    @JoinColumn
    private Assignment assignment;

    public Project() {
    }

    public Project(String name, String dateTime, User user, Assignment assignment) {
        this.name = name;
        this.dateTime = dateTime;
        this.user = user;
        this.assignment = assignment;
    }

    public Project(String name, String dateTime, User user, String result, Assignment assignment) {
        this.name = name;
        this.dateTime = dateTime;
        this.user = user;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
