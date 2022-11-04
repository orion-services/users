package dev.orion.users.domain.usecases;

import javax.enterprise.context.ApplicationScoped;

public interface RemoveUser {
    public Boolean removeUser(String hash);
}
