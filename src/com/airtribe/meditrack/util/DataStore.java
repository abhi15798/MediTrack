package com.airtribe.meditrack.util;

import java.util.*;

public class DataStore<T> {
    private final Map<String, T> store = new HashMap<>();

    public void save(String id, T item) {
        store.put(id, item);
    }

    public Optional<T> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<T> findAll() {
        return new ArrayList<>(store.values());
    }

    public void delete(String id) {
    store.remove(id);
    }
}
