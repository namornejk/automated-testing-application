package cz.uhk.automatedtestingapplication.model.testResult;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "Testcases")
public class Testcase {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column
    private String className;
    @Column
    private String name;
    @Column
    private String time;

    @ManyToOne
    @JoinColumn(name = "testsuite_id")
    private Testsuite testsuite;

    @OneToOne(mappedBy = "testcase", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Failure failure;

    public Testcase(){

    }

    public Testcase(String className, String name, String time, Testsuite testsuite, Failure failure) {
        this.className = className;
        this.name = name;
        this.time = time;
        this.testsuite = testsuite;
        this.failure = failure;
    }

    @XmlAttribute
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @XmlElement
    public Failure getFailure() {
        return failure;
    }

    public void setFailure(Failure failure) {
        this.failure = failure;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Testsuite getTestsuite() {
        return testsuite;
    }

    public void setTestsuite(Testsuite testsuite) {
        this.testsuite = testsuite;
    }
}
