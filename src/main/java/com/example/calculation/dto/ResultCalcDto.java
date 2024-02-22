package com.example.calculation.dto;

public class ResultCalcDto {
    private String company;
    private String job;
    private Double range;

    public ResultCalcDto(String company, String job, Double range) {
        this.company = company;
        this.job = job;
        this.range = range;
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

    public Double getRange() {
        return range;
    }

    public void setRange(Double range) {
        this.range = range;
    }

    @Override
    public String toString() {
        return "ResultCalcDto{" +
                "company='" + company + '\'' +
                ", job='" + job + '\'' +
                ", range=" + range +
                '}' + "\n";
    }
}
