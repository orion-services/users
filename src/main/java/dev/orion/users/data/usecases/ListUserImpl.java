package dev.orion.users.data.usecases;

import dev.orion.users.data.interfaces.UserRepository;
import dev.orion.users.domain.dto.UserQueryDto;
import dev.orion.users.domain.models.User;
import dev.orion.users.domain.usecases.ListUser;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.inject.Inject;

@ApplicationScoped
public class ListUserImpl implements ListUser {

    @Inject
    protected UserRepository repository;

    @Override
    @Transactional
    public List<User> list(UserQueryDto query) {
        List<User> users = this.repository.findByQuery(query);
        return users;
    }
}
