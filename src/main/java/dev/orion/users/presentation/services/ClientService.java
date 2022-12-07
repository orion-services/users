//package dev.orion.users.presentation.services;
//
//import dev.orion.users.domain.dto.client.CreateClientDto;
//import dev.orion.users.domain.usecases.client.CreateClient;
//import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
//
//import javax.inject.Inject;
//import javax.transaction.Transactional;
//import javax.ws.rs.*;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;
//
//@Path("/api/client")
//public class ClientService {
//
//    @Inject
//    protected CreateClient createClient;
//
//
//    @POST
//    @Path("/create")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    @Transactional
//    public Response create(@RequestBody CreateClientDto createClientDto){
//        try {
//            return Response.status(Response.Status.CREATED)
//                    .entity(createClient.create(createClientDto))
//                    .build();
//        }catch (Exception e){
//            throw new ServiceException(e.getMessage(),Response.Status.BAD_REQUEST);
//        }
//
//    }
//
//    @GET
//    public Response get(){
//        return  null;
//    }
//
//}
