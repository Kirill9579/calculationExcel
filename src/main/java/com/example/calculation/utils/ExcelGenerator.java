package com.example.calculation.utils;

import com.example.calculation.dto.DirectoryDto;
import com.example.calculation.dto.ExcelGeneratorDto;
import com.example.calculation.dto.ResultCalcDto;
import io.vavr.control.Try;
import javafx.scene.control.Alert;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ExcelGenerator {

    public static void run(ExcelGeneratorDto dto) {
        checkParams(dto);
        List<DirectoryDto> dtoList = readFile(dto);
        Calculation calculation = new Calculation(dto);
        List<ResultCalcDto> result = calculation.calculate(dtoList);

        File file = new File(dto.getOutputFile(), "Result.xlsx");

        try (var workbook = new XSSFWorkbook(); var fos = new FileOutputStream(file)) {
            Sheet sheet = workbook.createSheet("Result");
            createHeader(sheet);
            int rowCount = 1;
            for (ResultCalcDto calcDto : result) {
                Row row = sheet.createRow(rowCount);

                createCell(row, 0).setCellValue(calcDto.getCompany());
                createCell(row, 1).setCellValue(calcDto.getJob());
                createCell(row, 2).setCellValue(calcDto.getIqr());
                createCell(row, 3).setCellValue(calcDto.getAverage());
                createCell(row, 4).setCellValue(calcDto.getSize());

                rowCount++;
            }
            workbook.write(fos);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private static void createHeader(Sheet sheet) {
        Row row = sheet.createRow(0);
        createCell(row, 0).setCellValue("Организация");
        createCell(row, 1).setCellValue("Работа");
        createCell(row, 2).setCellValue("IQR");
        createCell(row, 3).setCellValue("Норма");
        createCell(row, 4).setCellValue("Количество значений в диапазоне");
    }

    public static Cell createCell(Row row, int cellIndex) {
        Cell cell = row.createCell(cellIndex);
        return cell;
    }

    private static List<DirectoryDto> readFile(ExcelGeneratorDto dto) {
        int companyColumn = CellReference.convertColStringToIndex(dto.getCompany());
        int jobsColumn = CellReference.convertColStringToIndex(dto.getJobs());
        int valueColumn = CellReference.convertColStringToIndex(dto.getValue());

        List<DirectoryDto> dtoList = new ArrayList<>();
        try (Workbook workbook = getWorkbook(dto.getInputFile())) {
            Sheet sheet = workbook.getSheet(dto.getSheetName());
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                String companyName = Try.of(() -> row.getCell(companyColumn).toString().trim()).getOrElse("");
                String jobsName = Try.of(() -> row.getCell(jobsColumn).toString().trim()).getOrElse("");
                Double value = Try.of(() ->parseCell(row.getCell(valueColumn), workbook)).getOrElse(Double.NaN);

                dtoList.add(new DirectoryDto(companyName, jobsName, value));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return dtoList;
    }

    private static double parseCell(Cell cell, Workbook workbook) {
        double value = 0;
        if (cell.getCellType() == CellType.FORMULA) {
            String formula = cell.getCellFormula();
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            CellValue cellValue = evaluator.evaluate(cell);
            if (cellValue.getCellType() == CellType.NUMERIC) {
                value = cellValue.getNumberValue();
            } else value = 0;
        } else if (cell.getCellType() == CellType.NUMERIC) {
            value = Double.parseDouble(cell.toString());
        }
        return value;
    }

    private static Workbook getWorkbook(File inputFile) throws Exception {
        if (inputFile.getName().endsWith("xlsx")) {
            return new XSSFWorkbook(inputFile);
        }
        return new HSSFWorkbook(new FileInputStream(inputFile));
    }

    private static void checkParams(ExcelGeneratorDto dto) {
        if (dto.getInputFile() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Укажите файл Справочник");
            alert.showAndWait();
        }
        if (dto.getOutputFile() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Укажите директорию для результатов вычисления");
            alert.showAndWait();
        }
    }
}
