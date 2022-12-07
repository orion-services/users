package dev.orion.users.data.usecases.user;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

import com.google.inject.Inject;

import dev.orion.users.data.interfaces.UserRepository;
import dev.orion.users.domain.models.User;
import dev.orion.users.domain.usecases.user.BlockUser;

@ApplicationScoped
public class BlockUserImpl implements BlockUser {
    @Inject
    private UserRepository repository;

    /**
     * @param hash
     * @return
     */
    @Override
    @Transactional
    public User block(String hash) {


        return repository.blockUser(hash);
    }
}
