package cz.uhk.automatedtestingapplication.dao;

import cz.uhk.automatedtestingapplication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDao extends JpaRepository<User, Long> {

    User findByUsername(String username);

}
