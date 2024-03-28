package com.example.calculation.utils;

import com.example.calculation.dto.DirectoryDto;
import com.example.calculation.dto.ExcelGeneratorDto;
import com.example.calculation.dto.ResultCalcDto;
import org.dhatim.fastexcel.BorderStyle;
import org.dhatim.fastexcel.Color;
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
                sheet.value(rowNumber, 5, result.get(i).getValue());
                sheet.value(rowNumber, 6, result.get(i).getCountValue());

                sheet.style(rowNumber,0).borderStyle(BorderStyle.THIN).horizontalAlignment("left").set();
                sheet.style(rowNumber,1).borderStyle(BorderStyle.THIN).horizontalAlignment("left").set();
                sheet.style(rowNumber,2).borderStyle(BorderStyle.THIN).horizontalAlignment("left").set();
                sheet.style(rowNumber,3).borderStyle(BorderStyle.THIN).horizontalAlignment("left").set();
                sheet.style(rowNumber,4).borderStyle(BorderStyle.THIN).horizontalAlignment("left").set();
                sheet.style(rowNumber,5).borderStyle(BorderStyle.THIN).horizontalAlignment("left").set();
                sheet.style(rowNumber,6).borderStyle(BorderStyle.THIN).horizontalAlignment("left").set();

                rowNumber++;
            }
            sheet.setAutoFilter(0,0, 6);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

    }

    private static void createHeader(Worksheet sheet) {
        sheet.rowHeight(0, 30);

        sheet.value(0, 0, "Организация");
        sheet.width(0, 15);

        sheet.value(0, 1, "Работа");
        sheet.width(1, 40);

        sheet.value(0, 2, "IQR");
        sheet.width(2, 10);

        sheet.value(0, 3, "Норма");
        sheet.width(3, 10);

        sheet.value(0, 4, "Количество значений в диапазоне");
        sheet.width(4, 35);

        sheet.value(0, 5, "Мода числового ряда");
        sheet.width(5, 25);

        sheet.value(0, 6, "Количество повторений");
        sheet.width(6, 25);

        sheet.style(0,0).borderStyle(BorderStyle.THIN).bold().fillColor("D2FAC0").horizontalAlignment("left").set();
        sheet.style(0,1).borderStyle(BorderStyle.THIN).bold().fillColor("D2FAC0").horizontalAlignment("left").set();
        sheet.style(0,2).borderStyle(BorderStyle.THIN).bold().fillColor("D2FAC0").horizontalAlignment("left").set();
        sheet.style(0,3).borderStyle(BorderStyle.THIN).bold().fillColor("D2FAC0").horizontalAlignment("left").set();
        sheet.style(0,4).borderStyle(BorderStyle.THIN).bold().fillColor("D2FAC0").horizontalAlignment("left").set();
        sheet.style(0,5).borderStyle(BorderStyle.THIN).bold().fillColor("D2FAC0").horizontalAlignment("left").set();
        sheet.style(0,6).borderStyle(BorderStyle.THIN).bold().fillColor("D2FAC0").horizontalAlignment("left").set();

    }

}
