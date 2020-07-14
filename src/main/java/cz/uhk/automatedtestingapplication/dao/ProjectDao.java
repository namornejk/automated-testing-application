package cz.uhk.automatedtestingapplication.dao;

import cz.uhk.automatedtestingapplication.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;

public interface ProjectDao extends JpaRepository<Project, Long> {
}