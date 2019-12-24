package cz.uhk.automatedtestingapplication.dao;

import cz.uhk.automatedtestingapplication.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectDao extends JpaRepository<Project, Long> {
}
