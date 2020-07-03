package cz.uhk.automatedtestingapplication.dao;

import cz.uhk.automatedtestingapplication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserDao extends JpaRepository<User, Long> {

    User findByUsername(String username);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE users " +
                                        "SET first_name=:firstName, last_name=:lastName, password=:password " +
                                        "WHERE username=:username")
    void updateUserWithPassword(@Param("firstName") String firstName, @Param("lastName")String lastName,
                                @Param("password") String password, @Param("username") String username);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE users " +
                                        "SET first_name=:firstName, last_name=:lastName " +
                                        "WHERE username=:username")
    void updateUserWithNoPassword(@Param("firstName") String firstName,
                                  @Param("lastName") String lastName, @Param("username") String username);

}
