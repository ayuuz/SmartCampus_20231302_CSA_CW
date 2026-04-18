package com.mycompany.smartcampus_20231302.mappers;

import com.mycompany.smartcampus_20231302.exceptions.RoomIsNotEmptyException;
import com.mycompany.smartcampus_20231302.models.ErrorResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Maps RoomIsNotEmptyException to HTTP 409 with structured JSON error body.
 */
@Provider
public class RoomIsNotEmptyExceptionHandler implements ExceptionMapper<RoomIsNotEmptyException> {

    @Context
    private UriInfo uriInfo;

    /**
     * Converts exception to API response.
     *
     * @param exception thrown exception
     * @return HTTP 409 response
     */
    @Override
    public Response toResponse(RoomIsNotEmptyException exception) {
        // Capture current request path for client troubleshooting.
        String path = uriInfo == null ? "/api/v1/rooms" : uriInfo.getRequestUri().getPath();
        ErrorResponse error = ErrorResponse.of(409, "Conflict", exception.getMessage(), path);
        return Response.status(Response.Status.CONFLICT)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }
}
