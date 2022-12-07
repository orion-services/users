package dev.orion.users.data.usecases.user;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

import javax.inject.Inject;

import dev.orion.users.data.mappers.UserMapper;
import dev.orion.users.domain.models.User;
import dev.orion.users.data.interfaces.Encrypter;
import dev.orion.users.data.interfaces.UserRepository;
import dev.orion.users.domain.usecases.user.CreateUser;

import dev.orion.users.domain.dto.user.CreateUserDto;

@ApplicationScoped
public class CreateUserImpl implements CreateUser {

    @Inject
    protected UserRepository repository;
    @Inject
    protected Encrypter encrypter;

    @Override
    @Transactional
    public User create(CreateUserDto createUserDto) {
        try {

            User userToBeCreated = UserMapper.toEntity(createUserDto);
            User user = this.repository.findByEmail(userToBeCreated.getEmail().getAddress());

            if (user != null) {
                throw new IllegalArgumentException("User already exists with this email!");
            }

            userToBeCreated.setPassword(this.encrypter.hash(userToBeCreated.getPassword()));

            return this.repository.create(userToBeCreated);
        }catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }

    }
}