package dev.orion.users.domain.models;

public enum RoleEnum {
    USER,
    ADMIN;

    RoleEnum() {
    }

    @Override
    public String toString() {
        return "RoleEnum{}";
    }
}
