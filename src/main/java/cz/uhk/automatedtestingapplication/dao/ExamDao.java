package cz.uhk.automatedtestingapplication.dao;

import cz.uhk.automatedtestingapplication.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamDao extends JpaRepository<Exam, Long> {
}
