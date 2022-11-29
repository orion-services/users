package dev.orion.users.presentation.services;

import dev.orion.users.data.handlers.AuthorizationCodeHandler;
import dev.orion.users.domain.dto.AuthenticateUserDto;
import dev.orion.users.domain.dto.CreateUserDto;
import dev.orion.users.domain.models.User;
import dev.orion.users.domain.usecases.AuthenticateUser;
import dev.orion.users.domain.usecases.CreateUser;
import dev.orion.users.presentation.dto.Authentication;
import dev.orion.users.presentation.mappers.ResponseMapper;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/user")
public class AuthenticantionService {
    @Inject
    protected AuthorizationCodeHandler authorizationCodeHandler;

    @Inject
    protected AuthenticateUser authUser;

    @Inject
    protected CreateUser createUser;
    /**
     * Creates a user and authenticate.
     *
     *
     * @return The Authentication DTO
     * @throws ServiceException Returns a HTTP 409 if the e-mail already
     *                          exists in the database or if the password is lower
     *                          than eight
     *                          characters
     */
    @POST
    @Path("/createAuthenticate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Retry(maxRetries = 1, delay = 2000)
    @Transactional
    public Authentication createAuthenticate(@RequestBody CreateUserDto createUserDto) {

        try {
            User user = createUser.create(createUserDto);
            String token = this.authorizationCodeHandler.getAccessToken(user);
            String refreshToken = this.authorizationCodeHandler.getRefreshToken(user);
            Authentication auth = new Authentication();
            auth.setToken(token);
            auth.setRefreshToken(refreshToken);
            auth.setUser(ResponseMapper.toResponse(user));
            return auth;
        } catch (Exception e) {
            String message = e.getMessage();
            throw new ServiceException(
                    message,
                    Response.Status.BAD_REQUEST);
        }
    }

    /**
     * Authenticates the user.
     *
     * @param email    : The e-mail of the user
     * @param password : The password of the user
     *
     * @return A JWT (JSON Web Token)
     * @throws ServiceException Returns an HTTP 401 if the services is not
     *                          able to find the user in the database
     */
    @POST
    @Path("/authenticate")
    @PermitAll
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Retry(maxRetries = 1, delay = 2000)
    @Transactional
    public Authentication authenticate(@RequestBody AuthenticateUserDto authDto) {
        try {
            User user = authUser.authenticate(authDto);
            String token = this.authorizationCodeHandler.getAccessToken(user);
            String refreshToken = this.authorizationCodeHandler.getRefreshToken(user);
            Authentication auth = new Authentication();
            auth.setUser(ResponseMapper.toResponse(user));
            auth.setToken(token);
            auth.setRefreshToken(refreshToken);
            return auth;
        } catch (Exception e) {
            throw new ServiceException(e.getMessage(), Response.Status.BAD_REQUEST);
        }

    }
}
