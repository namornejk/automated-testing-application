package cz.uhk.automatedtestingapplication.dao;

import cz.uhk.automatedtestingapplication.model.testResult.Testsuite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestsuiteDao extends JpaRepository<Testsuite, Long> {
}
