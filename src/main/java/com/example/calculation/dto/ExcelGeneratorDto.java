package com.example.calculation.dto;

import javafx.scene.control.Alert;

import java.io.File;

public class ExcelGeneratorDto {
    private File inputFile;
    private File outputFile;
    private int rangePercent;
    private int stepPercent;
    private String sheetName;
    private String company;
    private String jobs;
    private String value;



    public File getInputFile() {
        return inputFile;
    }

    public void setInputFile(File inputFile) {
        this.inputFile = inputFile;
    }

    public File getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }

    public int getRangePercent() {
        return rangePercent;
    }

    public void setRangePercent(String rangePercent) {
        try {
            this.rangePercent =Integer.parseInt(rangePercent);
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("В поле Сравнение диапазонов не число");
            alert.showAndWait();
        }
    }

    public int getStepPercent() {
        return stepPercent;
    }

    public void setStepPercent(String stepPercent) {
        try {
            this.stepPercent =Integer.parseInt(stepPercent);
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("В поле Шаг деления не число");
            alert.showAndWait();
        }
    }

    public void setRangePercent(int rangePercent) {
        this.rangePercent = rangePercent;
    }

    public void setStepPercent(int stepPercent) {
        this.stepPercent = stepPercent;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getJobs() {
        return jobs;
    }

    public void setJobs(String jobs) {
        this.jobs = jobs;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }
}
