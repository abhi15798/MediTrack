package com.airtribe.meditrack.entity;

import com.airtribe.meditrack.util.Validator;

import java.util.Objects;

public abstract class Person {

    private final String id;
    private String name;
    private String contact;

    protected Person(String id, String name, String contact) {
        this.id = id;
        setName(name);       // route through validated setters even at construction
        setContact(contact); // so an invalid Person can never be constructed
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        Validator.validateName(name);
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        Validator.validateContact(contact);
        this.contact = contact;
    }

    // abstract → forces Doctor/Patient to define their own identity string;
    // this is what you call to demonstrate dynamic dispatch (Person ref, subclass behavior)
    public abstract String getRole();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        Person person = (Person) o;
        return id.equals(person.id); // identity equality is id-based, not field-based
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return getRole() + "{id='" + id + "', name='" + name + "', contact='" + contact + "'}";
    }
}