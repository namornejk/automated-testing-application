package cz.uhk.automatedtestingapplication.dao;

import cz.uhk.automatedtestingapplication.model.testResult.Testcase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestcaseDao extends JpaRepository<Testcase, Long> {
}
