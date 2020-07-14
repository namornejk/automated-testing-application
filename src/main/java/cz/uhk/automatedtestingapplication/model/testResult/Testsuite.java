package cz.uhk.automatedtestingapplication.model.testResult;

import cz.uhk.automatedtestingapplication.model.Project;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Entity
@Table(name = "Testsuites")
@XmlRootElement
public class Testsuite {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column
    private Integer tests;
    @Column
    private Integer failures;
    @Column
    private String name;
    @Column
    private String time;
    @Column
    private Integer errors;
    @Column
    private Integer skipped;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @OneToOne(mappedBy = "testsuite", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Properties properties;

    @OneToMany(mappedBy = "testsuite", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Testcase> testcase;

    public Testsuite(){

    }

    public Testsuite(Integer tests, Integer failures, String name, String time, Integer errors, Integer skipped, Project project, Properties properties, List<Testcase> testcase) {
        this.tests = tests;
        this.failures = failures;
        this.name = name;
        this.time = time;
        this.errors = errors;
        this.skipped = skipped;
        this.project = project;
        this.properties = properties;
        this.testcase = testcase;
    }

    public int getSuccessfullTests(){
        return this.tests - this.failures - this.errors;
    }

    @XmlAttribute
    public Integer getTests() {
        return tests;
    }

    public void setTests(Integer tests) {
        this.tests = tests;
    }

    @XmlAttribute
    public Integer getFailures() {
        return failures;
    }

    public void setFailures(Integer failures) {
        this.failures = failures;
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

    @XmlAttribute
    public Integer getErrors() {
        return errors;
    }

    public void setErrors(Integer errors) {
        this.errors = errors;
    }

    @XmlAttribute
    public Integer getSkipped() {
        return skipped;
    }

    public void setSkipped(Integer skipped) {
        this.skipped = skipped;
    }

    @XmlElement
    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @XmlElement
    public List<Testcase> getTestcase() {
        return testcase;
    }

    public void setTestcase(List<Testcase> testcase) {
        this.testcase = testcase;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
