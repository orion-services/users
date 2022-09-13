package dev.orion.users.infra.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.orion.users.domain.models.User;

import dev.orion.users.domain.vo.Email;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import javax.persistence.*;
import java.util.UUID;

@Entity(name = "user")
@Table(name = "user")
public class UserPanacheEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public Long id;

    @Column(name = "hash")
    public String hash;

    @Column(name = "name")
    public String name;

    @Column(name = "email")
    public String email;

    @Column(name = "password")
    @JsonIgnore
    public String password;

    @Column(name = "status")
    public String status;

    public User toUser() {
        User user = new User();
        user.setName(this.name);
        user.setEmail(new Email(this.email));
        user.setStatus(this.status);
        user.setHash(this.hash);
        return user;
    }
}
