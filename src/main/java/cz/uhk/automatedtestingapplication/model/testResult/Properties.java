package cz.uhk.automatedtestingapplication.model.testResult;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Entity
@Table(name = "Properties")
public class Properties {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "testsuite_id")
    private Testsuite testsuite;

    @OneToMany(mappedBy = "properties", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Property> property;

    public Properties(){}

    public Properties(Testsuite testsuite, List<Property> property) {
        this.testsuite = testsuite;
        this.property = property;
    }

    @XmlElement
    public List<Property> getProperty() {
        return property;
    }

    public void setProperty(List<Property> propertyList) {
        this.property = property;
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
