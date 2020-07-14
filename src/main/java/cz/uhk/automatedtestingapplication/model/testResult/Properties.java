package cz.uhk.automatedtestingapplication.model.testResult;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Entity
@Table(name = "Properties")
@XmlRootElement
public class Properties {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "testsuite_id")
    private Testsuite testsuite;

    @OneToMany(mappedBy = "properties", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Property> propertieList;

    public Properties(){}

    public Properties(Testsuite testsuite, List<Property> propertieList) {
        this.testsuite = testsuite;
        this.propertieList = propertieList;
    }

    @XmlElement
    public List<Property> getProperties() {
        return propertieList;
    }

    public void setProperties(List<Property> propertieList) {
        this.propertieList = propertieList;
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

    public List<Property> getPropertieList() {
        return propertieList;
    }

    public void setPropertieList(List<Property> propertieList) {
        this.propertieList = propertieList;
    }
}
