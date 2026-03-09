package com.app.quantitymeasurementapp.repository;

import com.app.quantitymeasurementapp.entity.QuantityMeasurementEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class QuantityMeasurementCacheRepository implements IQuantityMeasurementRepository {

    private static final Logger logger   = LoggerFactory.getLogger(QuantityMeasurementCacheRepository.class);
    private static final String CACHE_FILE = "quantity_cache.ser";
    private static QuantityMeasurementCacheRepository instance;

    private final List<QuantityMeasurementEntity> cache = new ArrayList<>();

    private QuantityMeasurementCacheRepository() {
        logger.info("Initializing QuantityMeasurementCacheRepository");
        loadFromDisk();
    }

    public static synchronized QuantityMeasurementCacheRepository getInstance() {
        if (instance == null) {
            instance = new QuantityMeasurementCacheRepository();
        }
        return instance;
    }

    @Override
    public void save(QuantityMeasurementEntity entity) {
        cache.add(entity);
        saveToDisk(entity);
        logger.debug("Entity saved to cache. Total: {}", cache.size());
    }

    @Override
    public List<QuantityMeasurementEntity> getAllMeasurements() {
        return Collections.unmodifiableList(new ArrayList<>(cache));
    }

    @Override
    public List<QuantityMeasurementEntity> getMeasurementsByOperation(String operationType) {
        return cache.stream()
            .filter(e -> operationType.equalsIgnoreCase(e.getOperationType()))
            .collect(Collectors.toList());
    }

    @Override
    public List<QuantityMeasurementEntity> getMeasurementsByType(String measurementType) {
        return cache.stream()
            .filter(e -> e.getOperand1() != null
                && e.getOperand1().getUnit() != null
                && measurementType.equalsIgnoreCase(e.getOperand1().getUnit().getMeasurementType()))
            .collect(Collectors.toList());
    }

    @Override
    public int getTotalCount() {
        return cache.size();
    }

    @Override
    public void deleteAll() {
        cache.clear();
        File f = new File(CACHE_FILE);
        if (f.exists()) f.delete();
        logger.info("All measurements deleted from cache");
    }

    @Override
    public void clear() {
        deleteAll();
    }

    private void saveToDisk(QuantityMeasurementEntity entity) {
        File file = new File(CACHE_FILE);
        try {
            if (!file.exists()) {
                try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                    oos.writeObject(entity);
                }
            } else {
                try (ObjectOutputStream oos = new AppendableObjectOutputStream(new FileOutputStream(file, true))) {
                    oos.writeObject(entity);
                }
            }
        } catch (IOException e) {
            logger.warn("Could not save entity to disk: {}", e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadFromDisk() {
        File file = new File(CACHE_FILE);
        if (!file.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            while (true) {
                try {
                    cache.add((QuantityMeasurementEntity) ois.readObject());
                } catch (EOFException eof) {
                    break;
                }
            }
            logger.info("Loaded {} entities from disk cache", cache.size());
        } catch (IOException | ClassNotFoundException e) {
            logger.warn("Could not load existing cache: {}", e.getMessage());
        }
    }

    private static class AppendableObjectOutputStream extends ObjectOutputStream {
        public AppendableObjectOutputStream(OutputStream out) throws IOException { super(out); }
        @Override protected void writeStreamHeader() throws IOException { reset(); }
    }
}
