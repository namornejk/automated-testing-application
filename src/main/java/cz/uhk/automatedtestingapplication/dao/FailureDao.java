package cz.uhk.automatedtestingapplication.dao;

import cz.uhk.automatedtestingapplication.model.testResult.Failure;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FailureDao extends JpaRepository<Failure, Long> {
}
