package com.example.calculation.utils;

import com.example.calculation.dto.ExcelGeneratorDto;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

import static javafx.stage.FileChooser.*;

public class ViewBuilder {
    private final Stage stage;
    private final ExcelGeneratorDto excelGeneratorDto;
    private final Label inputField = buildLabel("Выбери файл...", 10, 20);
    private final Label outputField = buildLabel("Укажите папку для результата...", 10 , 50);
    private final TextField sheetName = buildTextField("Сводки (люди чел.час)", 255 , 80);
    private final TextField rangePercent = buildTextField("75", 255 , 110);
    private final TextField stepPercent = buildTextField("70", 255 , 140);
    private final TextField company = buildTextField("E", 255 , 170);
    private final TextField jobs = buildTextField("K", 255 , 200);
    private final TextField value = buildTextField("R", 255 , 230);

    public ViewBuilder(Stage stage) {
        this.stage = stage;
        this.excelGeneratorDto = new ExcelGeneratorDto();
    }

    public void buildView(Group root) {
        ObservableList<Node> children = root.getChildren();

        children.add(inputField);
        children.add(buildInputButton(255, 20));

        children.add(buildOutputButton(255, 50));
        children.add(outputField);

        children.add(buildLabel("Имя листа в Excel", 10, 80));
        children.add(sheetName);

        children.add(buildLabel("Сравнение диапазонов по количеству значений ", 10, 110));
        children.add(rangePercent);

        children.add(buildLabel("Шаг деления на диапазоны ", 10, 140));
        children.add(stepPercent);

        children.add(buildLabel("Введите столбец Организация", 10, 170));
        children.add(company);

        children.add(buildLabel("Введите столбец Вид работ", 10, 200));
        children.add(jobs);

        children.add(buildLabel("Введите столбец значений", 10, 230));
        children.add(value);


        children.add(buildButton("Начать!", 136, 290));


    }

    private TextField buildTextField(String prompt, int x, int y) {
        TextField textField = new TextField();
        textField.setLayoutY(y);
        textField.setLayoutX(x);
        textField.setPrefWidth(73);
        textField.setPrefHeight(26);
        textField.setText(prompt);
        return textField;
    }

    private static Label buildLabel(String prompt, int x, int y) {
        Label label = new Label();
        label.setLayoutY(y);
        label.setLayoutX(x);
        label.setPrefWidth(225);
        label.setPrefHeight(25);
        label.setText(prompt);
        return label;
    }
    private Button buildOutputButton(int x, int y) {
        Button button = new Button("Обзор");
        button.setLayoutY(y);
        button.setLayoutX(x);
        button.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select directory");
            File file = directoryChooser.showDialog(stage);
            if (file != null) {
                excelGeneratorDto.setOutputFile(file);
                outputField.setText(excelGeneratorDto.getOutputFile().getName());
            }
        });
        return button;
    }

    private Button buildInputButton(int x, int y) {
        Button button = new Button("Обзор");
        button.setLayoutY(y);
        button.setLayoutX(x);
        button.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Excel File");
            ExtensionFilter filter = new ExtensionFilter("Excel File", "*.xls", "*.xlsx");
            fileChooser.getExtensionFilters().add(filter);
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                excelGeneratorDto.setInputFile(file);
                inputField.setText(excelGeneratorDto.getInputFile().getName());
            }
        });
        return button;
    }

    private Button buildButton(String name, int x, int y) {
        Button button = new Button(name);
        button.setLayoutY(y);
        button.setLayoutX(x);
        button.setOnAction(event -> {
            excelGeneratorDto.setRangePercent(rangePercent.getText());
            excelGeneratorDto.setStepPercent(stepPercent.getText());
            excelGeneratorDto.setCompany(company.getText());
            excelGeneratorDto.setJobs(jobs.getText());
            excelGeneratorDto.setValue(value.getText());
            excelGeneratorDto.setSheetName(sheetName.getText());
            ExcelWriter.run(excelGeneratorDto);
        });
        return button;
    }
}
