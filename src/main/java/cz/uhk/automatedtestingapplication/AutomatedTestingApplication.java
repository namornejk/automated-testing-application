package cz.uhk.automatedtestingapplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//@ComponentScan("cz.uhk.automatedtestingapplication")
public class AutomatedTestingApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutomatedTestingApplication.class, args);
    }

}
