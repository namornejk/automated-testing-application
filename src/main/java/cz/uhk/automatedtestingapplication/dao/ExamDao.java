package cz.uhk.automatedtestingapplication.dao;

import cz.uhk.automatedtestingapplication.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ExamDao extends JpaRepository<Exam, Long> {
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "SELECT * FROM exams WHERE exams.is_activated = true")
    List<Exam> findAllActivatedExams();
}
