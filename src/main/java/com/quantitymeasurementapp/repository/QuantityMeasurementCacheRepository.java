package com.quantitymeasurementapp.repository;

import com.quantitymeasurementapp.model.QuantityMeasurementEntity;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuantityMeasurementCacheRepository implements IQuantityMeasurementRepository {

    private static final String CACHE_FILE = "quantity_cache.ser";
    private static QuantityMeasurementCacheRepository instance;

    private final List<QuantityMeasurementEntity> cache = new ArrayList<>();

    private QuantityMeasurementCacheRepository() {
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
    }

    @Override
    public List<QuantityMeasurementEntity> getAllMeasurements() {
        return Collections.unmodifiableList(new ArrayList<>(cache));
    }

    @Override
    public void clear() {
        cache.clear();
        File f = new File(CACHE_FILE);
        if (f.exists()) f.delete();
    }

    private void saveToDisk(QuantityMeasurementEntity entity) {
        File file = new File(CACHE_FILE);
        try {
            if (!file.exists()) {
                try (ObjectOutputStream oos =
                             new ObjectOutputStream(new FileOutputStream(file))) {
                    oos.writeObject(entity);
                }
            } else {
                try (ObjectOutputStream oos =
                             new AppendableObjectOutputStream(
                                     new FileOutputStream(file, true))) {
                    oos.writeObject(entity);
                }
            }
        } catch (IOException e) {
            System.err.println("[Cache] Warning: could not save entity to disk — " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadFromDisk() {
        File file = new File(CACHE_FILE);
        if (!file.exists()) return;

        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(file))) {
            while (true) {
                try {
                    QuantityMeasurementEntity entity =
                            (QuantityMeasurementEntity) ois.readObject();
                    cache.add(entity);
                } catch (EOFException eof) {
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[Cache] Warning: could not load existing cache — " + e.getMessage());
        }
    }

    private static class AppendableObjectOutputStream extends ObjectOutputStream {

        public AppendableObjectOutputStream(OutputStream out) throws IOException {
            super(out);
        }

        @Override
        protected void writeStreamHeader() throws IOException {
            reset();
        }
    }
}
