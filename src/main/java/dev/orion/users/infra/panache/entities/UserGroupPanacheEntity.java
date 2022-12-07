package dev.orion.users.infra.panache.entities;

import dev.orion.users.domain.models.User;
import dev.orion.users.domain.models.UserGroup;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity(name = "userGroup")
@Table(name="userGroup")
public class UserGroupPanacheEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public Long id;

    @Column(name = "name")
    public String name;
    @Column(name = "userGroupHash")
    public String userGroupHash;

    @Column(name = "usersList")
    @ElementCollection(fetch = FetchType.LAZY)
    public Set<String> usersList;

    public UserGroup toUserGroup(){
        UserGroup userGroup = new UserGroup();
        userGroup.setUserGroupHash(this.userGroupHash);
        userGroup.setUsers(this.usersList);
        userGroup.setName(this.name);
        return  userGroup;
    }
}
