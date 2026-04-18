package com.mycompany.smartcampus_20231302.stores;

import com.mycompany.smartcampus_20231302.exceptions.ResourceNotFoundException;
import com.mycompany.smartcampus_20231302.exceptions.RoomIsNotEmptyException;
import com.mycompany.smartcampus_20231302.exceptions.SensorNotAvailableException;
import com.mycompany.smartcampus_20231302.models.CampusRoom;
import com.mycompany.smartcampus_20231302.models.SensorDevice;
import com.mycompany.smartcampus_20231302.models.SensorData;
import com.mycompany.smartcampus_20231302.models.SensorStatus;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Central in-memory store for rooms, sensors, and sensor readings.
 * Uses thread-safe collections plus synchronized write operations.
 */
public class SystemDataStore {

    private static final SystemDataStore INSTANCE = new SystemDataStore();

    private final AtomicInteger roomIdGenerator = new AtomicInteger(0);
    private final AtomicInteger sensorIdGenerator = new AtomicInteger(0);
    private final AtomicInteger readingIdGenerator = new AtomicInteger(0);

    private final Map<Integer, CampusRoom> rooms = new ConcurrentHashMap<Integer, CampusRoom>();
    private final Map<Integer, SensorDevice> sensors = new ConcurrentHashMap<Integer, SensorDevice>();
    private final Map<Integer, List<SensorData>> sensorReadings = new ConcurrentHashMap<Integer, List<SensorData>>();

    /**
     * Private constructor for singleton pattern.
     */
    private SystemDataStore() {
    }

    /**
     * Returns the shared singleton store instance.
     *
     * @return datastore singleton
     */
    public static SystemDataStore getInstance() {
        return INSTANCE;
    }

    /**
     * Returns all rooms sorted by id.
     *
     * @return room list
     */
    public List<CampusRoom> getAllRooms() {
        List<CampusRoom> results = new ArrayList<CampusRoom>(rooms.values());
        // Keep output deterministic for clients and tests.
        Collections.sort(results, Comparator.comparing(CampusRoom::getId));
        return results;
    }

    /**
     * Finds one room by id.
     *
     * @param roomId room identifier
     * @return room or null
     */
    public CampusRoom getRoomById(int roomId) {
        return rooms.get(roomId);
    }

    /**
     * Creates and stores a new room with generated id.
     *
     * @param request requested room payload
     * @return created room
     */
    public synchronized CampusRoom createRoom(CampusRoom request) {
        // Generate monotonic id in a thread-safe manner.
        int id = roomIdGenerator.incrementAndGet();
        CampusRoom room = new CampusRoom(id, request.getName(), request.getBuilding(), request.getFloor(), request.getDescription());
        rooms.put(id, room);
        return room;
    }

    /**
     * Deletes a room when there are no linked sensors.
     *
     * @param roomId room identifier
     * @return true if deleted, false when room does not exist
     */
    public synchronized boolean deleteRoom(int roomId) {
        CampusRoom room = rooms.get(roomId);
        if (room == null) {
            return false;
        }

        // Enforce business rule: room cannot be removed while sensors are assigned.
        for (SensorDevice sensor : sensors.values()) {
            if (sensor.getRoomId() != null && sensor.getRoomId() == roomId) {
                throw new RoomIsNotEmptyException("CampusRoom " + roomId + " still has active sensors assigned.");
            }
        }

        // Safe to remove once no sensor linkage is found.
        rooms.remove(roomId);
        return true;
    }

    /**
     * Returns sensors optionally filtered by type (case-insensitive).
     *
     * @param type optional sensor type
     * @return sensor list
     */
    public List<SensorDevice> getAllSensors(String type) {
        List<SensorDevice> results = new ArrayList<SensorDevice>();
        for (SensorDevice sensor : sensors.values()) {
            if (type == null || type.trim().isEmpty()) {
                results.add(sensor);
            } else if (sensor.getType() != null && sensor.getType().equalsIgnoreCase(type.trim())) {
                results.add(sensor);
            }
        }
        // Keep output deterministic for clients and tests.
        Collections.sort(results, Comparator.comparing(SensorDevice::getId));
        return results;
    }

    /**
     * Finds one sensor by id.
     *
     * @param sensorId sensor identifier
     * @return sensor or null
     */
    public SensorDevice getSensorById(int sensorId) {
        return sensors.get(sensorId);
    }

    /**
     * Creates and stores a new sensor after validating linked room existence.
     *
     * @param request requested sensor payload
     * @return created sensor
     */
    public synchronized SensorDevice createSensor(SensorDevice request) {
        Integer roomId = request.getRoomId();
        // Semantic validation for linked resource references.
        if (roomId == null || !rooms.containsKey(roomId)) {
            throw new ResourceNotFoundException("CampusRoom " + roomId + " does not exist.");
        }

        // Generate id and assign default status when absent.
        int id = sensorIdGenerator.incrementAndGet();
        SensorStatus status = request.getStatus() == null ? SensorStatus.ACTIVE : request.getStatus();
        SensorDevice sensor = new SensorDevice(id, request.getName(), request.getType(), status, request.getRoomId(), request.getCurrentValue());
        sensors.put(id, sensor);
        // Initialize reading history list for this sensor.
        sensorReadings.put(id, Collections.synchronizedList(new ArrayList<SensorData>()));
        return sensor;
    }

    /**
     * Returns reading history for a sensor.
     *
     * @param sensorId sensor identifier
     * @return reading list (defensive copy)
     */
    public List<SensorData> getReadingsForSensor(int sensorId) {
        List<SensorData> readings = sensorReadings.get(sensorId);
        if (readings == null) {
            return Collections.emptyList();
        }
        // Synchronize around list copy because list is shared mutable state.
        synchronized (readings) {
            return new ArrayList<SensorData>(readings);
        }
    }

    /**
     * Adds a new sensor reading and updates sensor current value.
     *
     * @param sensorId sensor identifier
     * @param request reading payload
     * @return created reading or null when sensor is missing
     */
    public synchronized SensorData addReading(int sensorId, SensorData request) {
        SensorDevice sensor = sensors.get(sensorId);
        if (sensor == null) {
            return null;
        }
        // Enforce MAINTENANCE state rule before accepting readings.
        if (sensor.getStatus() == SensorStatus.MAINTENANCE) {
            throw new SensorNotAvailableException("SensorDevice " + sensorId + " is in MAINTENANCE mode and cannot accept readings.");
        }

        List<SensorData> readings = sensorReadings.get(sensorId);
        if (readings == null) {
            // Backfill list when missing unexpectedly.
            readings = Collections.synchronizedList(new ArrayList<SensorData>());
            sensorReadings.put(sensorId, readings);
        }

        // Generate id and normalize timestamp.
        int readingId = readingIdGenerator.incrementAndGet();
        long timestamp = request.getTimestamp() == null ? System.currentTimeMillis() : request.getTimestamp();
        SensorData reading = new SensorData(readingId, sensorId, request.getValue(), timestamp);
        readings.add(reading);

        // Side effect required by coursework: update parent sensor current value.
        sensor.setCurrentValue(reading.getValue());
        sensors.put(sensorId, sensor);
        return reading;
    }
}
