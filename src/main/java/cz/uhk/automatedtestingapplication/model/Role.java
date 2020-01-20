package cz.uhk.automatedtestingapplication.model;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GeneratorType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "Roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String rolename;

    @ManyToMany(targetEntity = User.class)
    private List<User> users;

    public Role() {
    }

    public Role(String rolename){
        this.rolename = rolename;
    }

    public Role(Long id, String rolename, List<User> users) {
        this.id = id;
        this.rolename = rolename;
        this.users = users;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoleame() {
        return rolename;
    }

    public void setRolename(String rolename) {
        this.rolename = rolename;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
