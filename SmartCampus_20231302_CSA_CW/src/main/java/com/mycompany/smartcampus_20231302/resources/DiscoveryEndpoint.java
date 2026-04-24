package com.mycompany.smartcampus_20231302.resources;

import com.mycompany.smartcampus_20231302.models.AdminContact;
import com.mycompany.smartcampus_20231302.models.DiscoveryResponse;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Root discovery endpoint exposing API metadata and top-level links.
 */
@Path("")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryEndpoint {

    /**
     * Returns version, admin details, and discoverable collection links.
     *
     * @return discovery response payload
     */
    @GET
    public DiscoveryResponse discover() {
        // Keep deterministic link ordering for readable output.
        Map<String, String> resources = new LinkedHashMap<String, String>();
        resources.put("rooms", "/api/v1/rooms");
        resources.put("sensors", "/api/v1/sensors");

        // Basic contact block for administration/support.
        AdminContact admin = new AdminContact("Ayumi Dissanayake", "admin@example.com");
        return new DiscoveryResponse("v1", admin, resources);
    }

    /**
     * Artificial endpoint designed ONLY to prove the Global Exception Mapper works for the video demo.
     */
    @GET
    @Path("/crash")
    public Response triggerCrash() {
        throw new RuntimeException("This is a simulated internal server error to demonstrate the Catch-All Exception Mapper!");
    }
}
