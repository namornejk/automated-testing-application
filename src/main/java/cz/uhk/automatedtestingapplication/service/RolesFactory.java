package cz.uhk.automatedtestingapplication.service;

import org.springframework.stereotype.Service;

@Service
public class RolesFactory {

    public String getSTUDENT(){
        return "student";
    }

    public String getTEACHER(){
        return "teacher";
    }

}
