package dev.orion.users.data.usecases;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

import dev.orion.users.data.mappers.UserMapper;
import dev.orion.users.domain.models.User;
import dev.orion.users.data.interfaces.UserRepository;
import dev.orion.users.domain.usecases.CreateUser;

import dev.orion.users.domain.dto.CreateUserDto;
import org.apache.commons.codec.digest.DigestUtils;

@ApplicationScoped
public class CreateUserImpl implements CreateUser {
    private UserRepository repository;
    /** User repository. */
    public CreateUserImpl(UserRepository repository){
        this.repository = repository;
    }

    @Override
    @Transactional
    public User create(CreateUserDto createUserDto) {
            try {
                User user = this.repository.findByEmail(createUserDto.email);

                if(user != null){
                    throw new IllegalArgumentException("User already exists with this email!");
                }

                User userToBeCreated =  UserMapper.toEntity(createUserDto);
                userToBeCreated.setPassword(DigestUtils.sha256Hex(userToBeCreated.getPassword()));

                return this.repository.create(UserMapper.toEntity(createUserDto));
            }catch (Exception exception){
                throw  exception;
            }
    }
}