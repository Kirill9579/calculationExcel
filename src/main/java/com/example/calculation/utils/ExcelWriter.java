package com.example.calculation.utils;

import com.example.calculation.dto.DirectoryDto;
import com.example.calculation.dto.ExcelGeneratorDto;
import com.example.calculation.dto.ResultCalcDto;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import static com.example.calculation.utils.ExcelReader.readFile;


public class ExcelWriter {
    public static void run(ExcelGeneratorDto dto) {
        ExcelReader.checkParams(dto);
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
}
