package cz.uhk.automatedtestingapplication.dao;

import cz.uhk.automatedtestingapplication.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentDao extends JpaRepository<Assignment, Long> {
}
