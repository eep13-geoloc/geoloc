package com.hsbc.eep.geoloc.model;

import java.util.Objects;

public class Place {
    private String name;
    private float likehood;

    public Place(String name, float likehood) {
        this.name = name;
        this.likehood = likehood;
    }

    public String getName() {
        return name;
    }

    public float getLikehood() {
        return likehood;
    }

    @Override
    public String toString() {
        return "Place{" +
                "name='" + name + '\'' +
                ", likehood=" + likehood +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Place place = (Place) o;
        return Float.compare(place.likehood, likehood) == 0 &&
                Objects.equals(name, place.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, likehood);
    }
}
