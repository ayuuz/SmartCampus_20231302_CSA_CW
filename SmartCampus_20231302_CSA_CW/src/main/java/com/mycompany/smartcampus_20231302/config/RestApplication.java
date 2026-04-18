package com.mycompany.smartcampus_20231302.config;

import com.mycompany.smartcampus_20231302.filter.LoggingFilter;
import com.mycompany.smartcampus_20231302.mapper.GlobalExceptionMapper;
import com.mycompany.smartcampus_20231302.mapper.LinkedResourceNotFoundExceptionMapper;
import com.mycompany.smartcampus_20231302.mapper.RoomNotEmptyExceptionMapper;
import com.mycompany.smartcampus_20231302.mapper.SensorUnavailableExceptionMapper;
import com.mycompany.smartcampus_20231302.resource.DiscoveryResource;
import com.mycompany.smartcampus_20231302.resource.SensorResource;
import com.mycompany.smartcampus_20231302.resource.SensorRoomResource;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * JAX-RS bootstrap class that defines API base path and registers components.
 */
@ApplicationPath("/api/v1")
public class RestApplication extends Application {

    /**
     * Registers all resources, exception mappers, and filters explicitly.
     *
     * @return registered JAX-RS classes
     */
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        // Core resources.
        classes.add(DiscoveryResource.class);
        classes.add(SensorRoomResource.class);
        classes.add(SensorResource.class);
        // Exception mapping strategy.
        classes.add(RoomNotEmptyExceptionMapper.class);
        classes.add(LinkedResourceNotFoundExceptionMapper.class);
        classes.add(SensorUnavailableExceptionMapper.class);
        classes.add(GlobalExceptionMapper.class);
        // Cross-cutting request/response logging.
        classes.add(LoggingFilter.class);
        return classes;
    }
}
