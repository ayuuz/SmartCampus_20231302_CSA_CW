package com.mycompany.smartcampus_20231302.resources;

import com.mycompany.smartcampus_20231302.models.ErrorResponse;
import com.mycompany.smartcampus_20231302.models.SensorDevice;
import com.mycompany.smartcampus_20231302.stores.SystemDataStore;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * Resource for sensor collection operations and readings sub-resource routing.
 */
@Path("/sensors")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SensorEndpoint {

    private final SystemDataStore dataStore = SystemDataStore.getInstance();

    @Context
    private UriInfo uriInfo;

    /**
     * Creates a sensor linked to an existing room.
     *
     * @param request incoming sensor payload
     * @return created sensor with HTTP 201
     */
    @POST
    public Response createSensor(SensorDevice request) {
        // Validate required fields before applying linked-resource rules.
        if (request == null || isBlank(request.getName()) || isBlank(request.getType()) || request.getRoomId() == null) {
            throw badRequest("SensorDevice payload must include name, type, and roomId.");
        }

        // SystemDataStore validates that roomId exists and may throw ResourceNotFoundException.
        SensorDevice created = dataStore.createSensor(request);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    /**
     * Returns sensors, optionally filtered by query parameter "type".
     *
     * @param type optional sensor type filter
     * @return list of sensors
     */
    @GET
    public List<SensorDevice> getSensors(@QueryParam("type") String type) {
        return dataStore.getAllSensors(type);
    }

    /**
     * Sub-resource locator for sensor reading history and creation.
     *
     * @param sensorId parent sensor identifier
     * @return sensor reading sub-resource
     */
    @Path("/{sensorId}/readings")
    public SensorReadingEndpoint sensorReadings(@PathParam("sensorId") int sensorId) {
        SensorDevice sensor = dataStore.getSensorById(sensorId);
        // Fail fast if sensor does not exist before entering sub-resource logic.
        if (sensor == null) {
            throw notFound("SensorDevice " + sensorId + " not found.");
        }
        return new SensorReadingEndpoint(sensorId);
    }

    /**
     * Builds a 404 web exception with standard error body.
     *
     * @param message error message
     * @return web application exception
     */
    private WebApplicationException notFound(String message) {
        String path = uriInfo == null ? "/api/v1/sensors" : uriInfo.getRequestUri().getPath();
        ErrorResponse error = ErrorResponse.of(404, "Not Found", message, path);
        return buildWebException(Response.Status.NOT_FOUND, error);
    }

    /**
     * Builds a 400 web exception with standard error body.
     *
     * @param message error message
     * @return web application exception
     */
    private WebApplicationException badRequest(String message) {
        String path = uriInfo == null ? "/api/v1/sensors" : uriInfo.getRequestUri().getPath();
        ErrorResponse error = ErrorResponse.of(400, "Bad Request", message, path);
        return buildWebException(Response.Status.BAD_REQUEST, error);
    }

    /**
     * Builds a JSON web exception response.
     *
     * @param status HTTP status
     * @param error error payload
     * @return web application exception
     */
    private WebApplicationException buildWebException(Response.Status status, ErrorResponse error) {
        Response response = Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
        return new WebApplicationException(response);
    }

    /**
     * Returns true when a string is null/blank.
     *
     * @param value input value
     * @return true if blank
     */
    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
