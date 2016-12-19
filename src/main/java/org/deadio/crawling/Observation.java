package org.deadio.crawling;

import java.util.Date;

/**
 * Created by yoni on 16/12/16.
 */
public class Observation {
    private final String countryCode;
    private final int year;
    private final int minAge;
    private final int maxAge;
    private final String gender;
    private final double lifeExpectancy;

    public Observation(String countryCode, int year, int minAge, int maxAge, String gender, double lifeExpectancy) {
        this.countryCode = countryCode;
        this.year = year;
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.gender = gender;
        this.lifeExpectancy = lifeExpectancy;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public int getYear() {
        return year;
    }

    public int getMinAge() {
        return minAge;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public String getGender() {
        return gender;
    }

    public double getLifeExpectancy() {
        return lifeExpectancy;
    }
}
