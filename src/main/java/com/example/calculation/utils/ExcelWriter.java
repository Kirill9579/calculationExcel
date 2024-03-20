package com.example.calculation.utils;

import com.example.calculation.dto.DirectoryDto;
import com.example.calculation.dto.ExcelGeneratorDto;
import com.example.calculation.dto.ResultCalcDto;
import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import static com.example.calculation.utils.ExcelReader.readFile;


public class ExcelWriter {
    public static void run(ExcelGeneratorDto dto) {
        List<DirectoryDto> dtoList = readFile(dto);
        Calculation calculation = new Calculation(dto);
        List<ResultCalcDto> result = calculation.calculate(dtoList);

        File file = new File(dto.getOutputFile(), "Result.xlsx");

        try (var fos = new FileOutputStream(file); var workbook = new Workbook(fos, "App", null)) {
            var sheet = workbook.newWorksheet("Result");
            createHeader(sheet);

            int rowNumber = 1;
            for (int i = 0; i < result.size(); i++) {
                sheet.value(rowNumber, 0, result.get(i).getCompany());
                sheet.value(rowNumber, 1, result.get(i).getJob());
                sheet.value(rowNumber, 2, result.get(i).getIqr());
                sheet.value(rowNumber, 3, result.get(i).getAverage());
                sheet.value(rowNumber, 4, result.get(i).getSize());
                rowNumber++;
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

    }

    private static void createHeader(Worksheet sheet) {
        sheet.value(0, 0, "Организация");
        sheet.value(0, 1, "Работа");
        sheet.value(0, 2, "IQR");
        sheet.value(0, 3, "Норма");
        sheet.value(0, 4, "Количество значений в диапазоне");
    }

}
