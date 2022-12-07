package dev.orion.users.infra.panache.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.orion.users.domain.models.RoleEnum;
import dev.orion.users.domain.models.User;

import dev.orion.users.domain.vo.Email;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import javax.persistence.*;
import java.util.Set;

@Entity(name = "user")
@Table(name = "user")
public class UserPanacheEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public Long id;

    @Column(name = "userHash")
    public String userHash;

    @Column(name = "name")
    public String name;

    @Column(name = "email")
    public String email;

    @Column(name = "password")
    @JsonIgnore
    public String password;

    @Column(name = "status")
    public String status;

    @Column(name = "type")
    public String type;

    @Column(name = "roles")
    @ElementCollection
    public Set<RoleEnum> roles;

    public User toUser() {
        User user = new User();
        user.setName(this.name);
        user.setEmail(new Email(this.email));
        user.setStatus(this.status);
        user.setUserHash(this.userHash);
        user.setPassword(this.password);
        user.setType(this.type);
        user.setRoles(roles);
        return user;
    }
}
