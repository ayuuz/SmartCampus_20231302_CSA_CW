package com.mycompany.smartcampus_20231302.mappers;

import com.mycompany.smartcampus_20231302.exceptions.ResourceNotFoundException;
import com.mycompany.smartcampus_20231302.models.ErrorResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Maps linked-resource validation failures to HTTP 422 JSON errors.
 */
@Provider
public class ResourceNotFoundExceptionHandler implements ExceptionMapper<ResourceNotFoundException> {

    @Context
    private UriInfo uriInfo;

    /**
     * Converts exception to API response.
     *
     * @param exception thrown exception
     * @return HTTP 422 response
     */
    @Override
    public Response toResponse(ResourceNotFoundException exception) {
        // Keep response body informative but safe.
        String path = uriInfo == null ? "/api/v1/sensors" : uriInfo.getRequestUri().getPath();
        ErrorResponse error = ErrorResponse.of(422, "Unprocessable Entity", exception.getMessage(), path);
        return Response.status(422)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }
}
