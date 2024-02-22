package com.example.calculation.dto;

import java.util.ArrayList;
import java.util.List;

public class DirectoryDto {
    private String company;
    private String job;
    private Double value;

    public DirectoryDto(String company, String job, Double value) {
        this.company = company;
        this.job = job;
        this.value = value;
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

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "DirectoryDto{" +
                "company='" + company + '\'' +
                ", job='" + job + '\'' +
                ", value=" + value +
                '}';
    }
}
