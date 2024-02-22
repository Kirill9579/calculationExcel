package com.example.calculation.utils;

import com.example.calculation.dto.DirectoryDto;
import com.example.calculation.dto.ExcelGeneratorDto;
import com.example.calculation.dto.ResultCalcDto;
import javafx.scene.control.Alert;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
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
        List<ResultCalcDto> result = Calculation.calculate(dtoList);

        File file = new File(dto.getOutputFile(), "Result.xlsx");

        try (var workbook = new XSSFWorkbook(); var fos = new FileOutputStream(file)) {
            Sheet sheet = workbook.createSheet("result");
            int rowCount = 1;
            for (ResultCalcDto calcDto : result) {
                Row row = sheet.createRow(rowCount);

                createCell(row, 0).setCellValue(calcDto.getCompany());
                createCell(row, 1).setCellValue(calcDto.getJob());
                createCell(row, 2).setCellValue(calcDto.getRange());

                rowCount++;
            }
            workbook.write(fos);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

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
                String companyName = row.getCell(companyColumn).getStringCellValue();
                String jobsName = row.getCell(jobsColumn).getStringCellValue();
                double value = Double.parseDouble(row.getCell(valueColumn).toString());

                dtoList.add(new DirectoryDto(companyName, jobsName, value));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return dtoList;
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
