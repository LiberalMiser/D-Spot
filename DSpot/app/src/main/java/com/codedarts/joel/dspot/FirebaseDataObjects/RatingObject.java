package com.codedarts.joel.dspot.FirebaseDataObjects;

public class RatingObject {

    public int star_rating5,star_rating4, star_rating3, star_rating2, star_rating1;
    public float weighted_mean;

    public RatingObject () {

    }

    public RatingObject (int star5, int star4, int star3, int star2, int star1, float wm) {
        star_rating1 = star1;
        star_rating2 = star2;
        star_rating3 = star3;
        star_rating4 = star4;
        star_rating5 = star5;
        weighted_mean = wm;
    }

}
