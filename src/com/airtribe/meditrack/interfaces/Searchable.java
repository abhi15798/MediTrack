package com.airtribe.meditrack.interfaces;

import java.util.List;

public interface Searchable<T> {
    T searchById(String id);
    List<T> search(String name);
}
