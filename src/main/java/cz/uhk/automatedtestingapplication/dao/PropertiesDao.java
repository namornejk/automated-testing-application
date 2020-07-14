package cz.uhk.automatedtestingapplication.dao;

import cz.uhk.automatedtestingapplication.model.testResult.Properties;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertiesDao extends JpaRepository<Properties, Long> {
}
