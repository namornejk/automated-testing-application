package cz.uhk.automatedtestingapplication.service;

import org.springframework.stereotype.Service;

@Service
public class RolesFactory {

    public String getSTUDENT(){
        return "ROLE_STUDENT";
    }

    public String getTEACHER(){
        return "ROLE_TEACHER";
    }

}
