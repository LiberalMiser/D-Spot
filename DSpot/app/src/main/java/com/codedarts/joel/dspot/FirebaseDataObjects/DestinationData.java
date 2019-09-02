package com.codedarts.joel.dspot.FirebaseDataObjects;

import java.util.ArrayList;

public class DestinationData {

    public String address, cover_photo, facebook_link, info, name, open_days, open_hours, twitter_link, website;
    public double latitude, longitude;
    //public ArrayList rating = new ArrayList();
    //public String[] tags;

    public DestinationData() {

    }

    public DestinationData (String _add, String photo, String fb_link, String _info, double lat,
                            double longi, String _name, String days, String hours, int[] rate, String[] _tags, String twitter, String web) {
        address = _add;
        cover_photo = photo;
        facebook_link = fb_link;
        info = _info;
        latitude = lat;
        longitude = longi;
        name = _name;
        open_days = days;
        open_hours = hours;
        //rating = rate;
        //tags = _tags;
        twitter_link = twitter;
        website = web;
    }

}
