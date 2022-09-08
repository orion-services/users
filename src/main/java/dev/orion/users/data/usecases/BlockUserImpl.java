package dev.orion.users.data.usecases;

import dev.orion.users.data.interfaces.UserRepository;
import dev.orion.users.domain.models.User;
import dev.orion.users.domain.usecases.BlockUser;

public class BlockUserImpl implements BlockUser {
    private UserRepository repository;

    public BlockUserImpl(UserRepository repository) {
        this.repository = repository;
    }
    /**
     * @param hash 
     * @return
     */
    @Override
    public User block(String hash) {
        return null;
    }
}
