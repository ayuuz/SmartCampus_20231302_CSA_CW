package com.mycompany.smartcampus_20231302.configs;

import com.mycompany.smartcampus_20231302.filters.RequestLoggingFilter;
import com.mycompany.smartcampus_20231302.mappers.GlobalExceptionHandler;
import com.mycompany.smartcampus_20231302.mappers.ResourceNotFoundExceptionHandler;
import com.mycompany.smartcampus_20231302.mappers.RoomIsNotEmptyExceptionHandler;
import com.mycompany.smartcampus_20231302.mappers.SensorNotAvailableExceptionHandler;
import com.mycompany.smartcampus_20231302.resources.DiscoveryEndpoint;
import com.mycompany.smartcampus_20231302.resources.SensorEndpoint;
import com.mycompany.smartcampus_20231302.resources.SensorRoomEndpoint;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * JAX-RS bootstrap class that defines API base path and registers components.
 */
@ApplicationPath("/api/v1")
public class RestAppConfig extends Application {

    /**
     * Registers all resources, exception mappers, and filters explicitly.
     *
     * @return registered JAX-RS classes
     */
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        // Core resources.
        classes.add(DiscoveryEndpoint.class);
        classes.add(SensorRoomEndpoint.class);
        classes.add(SensorEndpoint.class);
        // Exception mapping strategy.
        classes.add(RoomIsNotEmptyExceptionHandler.class);
        classes.add(ResourceNotFoundExceptionHandler.class);
        classes.add(SensorNotAvailableExceptionHandler.class);
        classes.add(GlobalExceptionHandler.class);
        // Cross-cutting request/response logging.
        classes.add(RequestLoggingFilter.class);
        return classes;
    }
}
