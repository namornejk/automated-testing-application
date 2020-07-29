package cz.uhk.automatedtestingapplication.model.testResult;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "Failure")
public class Failure {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column
    private String message;
    @Column
    private String type;
    @Column
    private String failure;

    @ManyToOne
    @JoinColumn(name = "tescase_id")
    private Testcase testcase;

    public Failure(){

    }

    public Failure(String message, String type, String failure, Testcase testcase) {
        this.message = message;
        this.type = type;
        this.failure = failure;
        this.testcase = testcase;
    }

    @XmlAttribute
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @XmlAttribute
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlAttribute
    public String getFailure() {
        return failure;
    }

    public void setFailure(String failure) {
        this.failure = failure;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Testcase getTestcase() {
        return testcase;
    }

    public void setTestcase(Testcase testcase) {
        this.testcase = testcase;
    }
}
