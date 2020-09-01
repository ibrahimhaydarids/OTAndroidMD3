package com.ids.fixot.model;

import java.util.ArrayList;

/**
 * Created by Amal on 4/4/2017.
 */

public class ChartData {


    private int Date, MaxY, MinY;
    private double EndTimeX, StartTimeX, StepX, StepY;
    private ArrayList<ChartValue> chartvalues;

    public ChartData() {
    }

    public ArrayList<ChartValue> getChartvalues() {
        return chartvalues;
    }

    public void setChartvalues(ArrayList<ChartValue> chartvalues) {
        this.chartvalues = chartvalues;
    }

    public int getDate() {
        return Date;
    }

    public void setDate(int date) {
        Date = date;
    }

    public int getMaxY() {
        return MaxY;
    }

    public void setMaxY(int maxY) {
        MaxY = maxY;
    }

    public int getMinY() {
        return MinY;
    }

    public void setMinY(int minY) {
        MinY = minY;
    }

    public double getEndTimeX() {
        return EndTimeX;
    }

    public void setEndTimeX(double endTimeX) {
        EndTimeX = endTimeX;
    }

    public double getStartTimeX() {
        return StartTimeX;
    }

    public void setStartTimeX(double startTimeX) {
        StartTimeX = startTimeX;
    }

    public double getStepX() {
        return StepX;
    }

    public void setStepX(double stepX) {
        StepX = stepX;
    }

    public double getStepY() {
        return StepY;
    }

    public void setStepY(double stepY) {
        StepY = stepY;
    }
}
