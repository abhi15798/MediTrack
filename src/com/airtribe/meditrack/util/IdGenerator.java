package com.airtribe.meditrack.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class IdGenerator {
    private static final IdGenerator INSTANCE = new IdGenerator(); // eager singleton

    private final Map<String, AtomicInteger> counters = new ConcurrentHashMap<>();

    private IdGenerator() {} // private constructor — prevents external instantiation

    public static IdGenerator getInstance() {
        return INSTANCE;
    }

    public String generateId(String prefix) {
        int next = counters.computeIfAbsent(prefix, p -> new AtomicInteger(0))
                .incrementAndGet();
        return prefix + "-" + String.format("%04d", next);
    }
}
