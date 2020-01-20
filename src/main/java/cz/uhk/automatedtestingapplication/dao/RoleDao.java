package cz.uhk.automatedtestingapplication.dao;

import cz.uhk.automatedtestingapplication.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleDao  extends JpaRepository<Role, Long> {
}
