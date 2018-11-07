package com.codedarts.joel.dspot.FirebaseDataObjects;

public class CategoryObject {

    public String preference_id, preference_name, preference_photo_url;

    public CategoryObject () {

    }

    public CategoryObject (String id, String name, String url) {
        preference_id = id;
        preference_name = name;
        preference_photo_url = url;
    }

}
