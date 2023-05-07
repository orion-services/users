package dev.orion.users.ws;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.hibernate.service.spi.ServiceException;

import dev.orion.users.dto.UserQueryDto;
import dev.orion.users.usecase.UseCase;
import dev.orion.users.ws.exceptions.UserWSException;

@Path("api/users")
public class ListWS {

    @Inject
    private UseCase useCase;

    @GET
    @Path("/find")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    // @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response find(@BeanParam UserQueryDto query) {
        try {
            return Response
                    .status(Response.Status.OK)
                    .entity(useCase.findUserByQuery(query))
                    .build();
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
