package dev.orion.users.domain.usecases;

import dev.orion.users.domain.models.User;

public interface BlockUser {
    public User block(String hash);
}
