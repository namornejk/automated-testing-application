package cz.uhk.automatedtestingapplication.service;

import cz.uhk.automatedtestingapplication.model.testResult.Testsuite;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;

@Service
public class XmlService {
    private JAXBContext jaxbContext;
    private Unmarshaller jaxbUnmarshaller;

    public Testsuite parseTestsuite(File file){
        try{
            JAXBContext jaxbContext = JAXBContext.newInstance(Testsuite.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            return (Testsuite)jaxbUnmarshaller.unmarshal(file);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return new Testsuite();
    }

    public JAXBContext getJaxbContext() {
        return jaxbContext;
    }

    public void setJaxbContext(JAXBContext jaxbContext) {
        this.jaxbContext = jaxbContext;
    }

    public Unmarshaller getJaxbUnmarshaller() {
        return jaxbUnmarshaller;
    }

    public void setJaxbUnmarshaller(Unmarshaller jaxbUnmarshaller) {
        this.jaxbUnmarshaller = jaxbUnmarshaller;
    }
}
