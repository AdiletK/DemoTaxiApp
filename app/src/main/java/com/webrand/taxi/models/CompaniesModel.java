package com.webrand.taxi.models;

import java.util.List;

public class CompaniesModel {

    public String name;
    public String icon;
    public List<ContactsModel> contacts;
    public List<DriversModel> drivers;

    @Override
    public String toString() {
        return "CompaniesModel{" +
                "name='" + name + '\'' +
                '}';
    }
}
