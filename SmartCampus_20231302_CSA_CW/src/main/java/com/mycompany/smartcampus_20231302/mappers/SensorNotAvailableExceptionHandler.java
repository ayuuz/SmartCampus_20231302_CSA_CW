package com.mycompany.smartcampus_20231302.mappers;

import com.mycompany.smartcampus_20231302.exceptions.SensorNotAvailableException;
import com.mycompany.smartcampus_20231302.models.ErrorResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Maps SensorNotAvailableException to HTTP 403 with JSON error body.
 */
@Provider
public class SensorNotAvailableExceptionHandler implements ExceptionMapper<SensorNotAvailableException> {

    @Context
    private UriInfo uriInfo;

    /**
     * Converts exception to API response.
     *
     * @param exception thrown exception
     * @return HTTP 403 response
     */
    @Override
    public Response toResponse(SensorNotAvailableException exception) {
        // Include request path so clients can locate failing operation.
        String path = uriInfo == null ? "/api/v1/sensors" : uriInfo.getRequestUri().getPath();
        ErrorResponse error = ErrorResponse.of(403, "Forbidden", exception.getMessage(), path);
        return Response.status(Response.Status.FORBIDDEN)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }
}
