package com.hsbc.eep.geoloc.model;

import java.util.Objects;

public class Place {
    private CharSequence name;
    private float likehood;
    private CharSequence address;
    private CharSequence telephone;

    public Place(CharSequence name, float likehood, CharSequence address, CharSequence telephone) {
        this.name = name;
        this.likehood = likehood;
        this.address = address;
        this.telephone = telephone;
    }

    public CharSequence getName() {
        return name;
    }

    public CharSequence getAddress() {
        return address;
    }

    public CharSequence getTelephone() {
        return telephone;
    }

    public float getLikehood() {
        return likehood;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Place place = (Place) o;
        return Float.compare(place.likehood, likehood) == 0 &&
                Objects.equals(name, place.name) &&
                Objects.equals(address, place.address) &&
                Objects.equals(telephone, place.telephone);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, likehood, address, telephone);
    }

    @Override
    public String toString() {
        return "Place{" +
                "name=" + name +
                ", likehood=" + likehood +
                ", address=" + address +
                ", telephone=" + telephone +
                '}';
    }
}
