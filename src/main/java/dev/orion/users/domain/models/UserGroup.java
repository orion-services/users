package dev.orion.users.domain.models;

import java.util.Set;

public class UserGroup {
    public String name;
    public String userGroupHash;
    public Set<String> users;
//    public List<RoleEnum> roles;


    public UserGroup() {
    }

    public UserGroup(String name, String userGroupHash, Set<String> users) {
        this.name = name;
        this.userGroupHash = userGroupHash;
        this.users = users;
    }
    public UserGroup(String name, Set<String> users) {
        this.name = name;
        this.users = users;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserGroupHash() {
        return userGroupHash;
    }

    public void setUserGroupHash(String userGroupHash) {
        this.userGroupHash = userGroupHash;
    }

    public Set<String> getUsers() {
        return users;
    }

    public void setUsers(Set<String> users) {
        this.users = users;
    }
}
