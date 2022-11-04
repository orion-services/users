package dev.orion.users.data.usecases;

import dev.orion.users.data.interfaces.UserRepository;
import dev.orion.users.domain.dto.UserQueryDto;
import dev.orion.users.domain.models.User;
import dev.orion.users.domain.usecases.ListUser;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

import com.google.inject.Inject;

@ApplicationScoped
public class ListUserImpl implements ListUser {
    @Inject
    protected UserRepository repository;

    /**
     * @param params
     * @return
     */
    @Override
    @Transactional
    public List<User> list(UserQueryDto query) {
        return repository.findByQuery(query);
    }
}
