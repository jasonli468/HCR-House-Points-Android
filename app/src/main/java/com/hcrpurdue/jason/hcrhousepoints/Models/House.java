package com.hcrpurdue.jason.hcrhousepoints.Models;

import androidx.annotation.NonNull;

public class House implements Comparable<House> {

    public static String COLOR_KEY = "Color";
    public static String NUMBER_OF_RESIDENTS_KEY = "NumberOfResidents";
    public static String TOTAL_POINTS_KEY = "TotalPoints";


    private int numResidents;
    private int totalPoints;
    private String name;

    public House(String n, int residents, int points) {
        name = n;
        numResidents = residents;
        totalPoints = points;
    }

    public String getName() {
        return name;
    }

    public int getNumResidents() {
        return numResidents;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    @Override
    public int compareTo(@NonNull House house) {
        return Float.compare(house.totalPoints, totalPoints);
    }
}
