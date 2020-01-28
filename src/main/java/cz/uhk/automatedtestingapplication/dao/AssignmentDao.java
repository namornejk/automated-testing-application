package cz.uhk.automatedtestingapplication.dao;

import cz.uhk.automatedtestingapplication.model.Assignment;
import cz.uhk.automatedtestingapplication.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.Optional;

public interface AssignmentDao extends JpaRepository<Assignment, Long> {

    @Query(nativeQuery = true, value = "select * from assignments where exam_id=:id")
    ArrayList<Assignment> findByExamId(@Param("id") Long id);

}
