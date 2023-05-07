package dev.orion.users.ws;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import dev.orion.users.dto.UserQueryDto;
import dev.orion.users.model.User;
import dev.orion.users.usecase.UseCase;
import dev.orion.users.ws.exceptions.UserWSException;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;

import java.util.List;

@Path("api/users")

public class ListWS {

    @Inject
    private UseCase useCase;

    @GET
    @Path("/find")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @WithSession
    public Uni<List<User>> find(@BeanParam UserQueryDto query) {
        try {
            return useCase.findUserByQuery(query);
        } catch (Exception e) {
            throw new UserWSException(e.getMessage(),
                    Response.Status.BAD_REQUEST);
        }

    }

    @GET
    @Path("{hash}/roles")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    // @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response validateRoles(@BeanParam UserQueryDto query) {
        return null;

    }

}
