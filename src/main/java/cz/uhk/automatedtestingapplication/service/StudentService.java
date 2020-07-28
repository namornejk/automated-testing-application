package cz.uhk.automatedtestingapplication.service;

import cz.uhk.automatedtestingapplication.model.Assignment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class StudentService {

    public Assignment getRandomAssignment(List<Assignment> assignmentList){
        Random random = new Random();
        Assignment randomAssignment = assignmentList.get(random.nextInt(assignmentList.size()));

        return randomAssignment;
    }
}
