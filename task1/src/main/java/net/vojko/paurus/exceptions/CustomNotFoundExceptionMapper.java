package net.vojko.paurus.exceptions;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CustomNotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

    @Override
    public Response toResponse(NotFoundException exception) {
        ErrorResponse errorResponse = new ErrorResponse(Response.Status.NOT_FOUND.getStatusCode(),
                exception.getMessage());

        return Response.status(Response.Status.NOT_FOUND).entity(errorResponse).build();
    }

}
