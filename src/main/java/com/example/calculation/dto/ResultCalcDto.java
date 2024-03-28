package com.example.calculation.dto;

public class ResultCalcDto {
    private String company;
    private String job;
    private double average;
    private int size;
    private double iqr;
    private double value = 0.0;
    private long countValue = 0;

    public ResultCalcDto(String company, String job) {
        this.company = company;
        this.job = job;
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public double getIqr() {
        return iqr;
    }

    public void setIqr(double iqr) {
        this.iqr = iqr;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }


    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public long getCountValue() {
        return countValue;
    }

    public void setCountValue(long countValue) {
        this.countValue = countValue;
    }

    @Override
    public String toString() {
        return company + " " + job;
    }

}
