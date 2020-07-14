package cz.uhk.automatedtestingapplication.dao;

import cz.uhk.automatedtestingapplication.model.testResult.Property;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyDao extends JpaRepository<Property, Long> {
}
