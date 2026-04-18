package com.mycompany.smartcampus_20231302.resources;

import com.mycompany.smartcampus_20231302.models.ErrorResponse;
import com.mycompany.smartcampus_20231302.models.SensorDevice;
import com.mycompany.smartcampus_20231302.models.SensorData;
import com.mycompany.smartcampus_20231302.stores.SystemDataStore;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Sub-resource handling sensor reading history operations for one sensor.
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SensorReadingEndpoint {

    private final int sensorId;
    private final SystemDataStore dataStore = SystemDataStore.getInstance();

    /**
     * Creates a sub-resource bound to one parent sensor id.
     *
     * @param sensorId parent sensor identifier
     */
    public SensorReadingEndpoint(int sensorId) {
        this.sensorId = sensorId;
    }

    /**
     * Returns historical readings for the bound sensor.
     *
     * @return list of readings
     */
    @GET
    public List<SensorData> getReadingHistory() {
        SensorDevice sensor = dataStore.getSensorById(sensorId);
        // Enforce parent existence for sub-resource endpoint.
        if (sensor == null) {
            throw notFound("SensorDevice " + sensorId + " not found.");
        }
        return dataStore.getReadingsForSensor(sensorId);
    }

    /**
     * Appends a reading and updates the parent sensor's current value.
     *
     * @param request incoming reading payload
     * @return created reading with HTTP 201
     */
    @POST
    public Response addReading(SensorData request) {
        SensorDevice sensor = dataStore.getSensorById(sensorId);
        if (sensor == null) {
            throw notFound("SensorDevice " + sensorId + " not found.");
        }
        // Validate required payload field before store update.
        if (request == null || request.getValue() == null) {
            throw badRequest("Reading payload must include a numeric value.");
        }

        // SystemDataStore enforces MAINTENANCE rule and updates sensor.currentValue.
        SensorData created = dataStore.addReading(sensorId, request);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    /**
     * Builds a 404 web exception with standard error body.
     *
     * @param message error message
     * @return web application exception
     */
    private WebApplicationException notFound(String message) {
        ErrorResponse error = ErrorResponse.of(404, "Not Found", message, "/api/v1/sensors/" + sensorId + "/readings");
        Response response = Response.status(Response.Status.NOT_FOUND)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
        return new WebApplicationException(response);
    }

    /**
     * Builds a 400 web exception with standard error body.
     *
     * @param message error message
     * @return web application exception
     */
    private WebApplicationException badRequest(String message) {
        ErrorResponse error = ErrorResponse.of(400, "Bad Request", message, "/api/v1/sensors/" + sensorId + "/readings");
        Response response = Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
        return new WebApplicationException(response);
    }
}
