package com.example.calculation.utils;

import com.example.calculation.dto.DirectoryDto;
import com.example.calculation.dto.ExcelGeneratorDto;
import io.vavr.control.Try;
import javafx.scene.control.Alert;
import org.dhatim.fastexcel.reader.CellAddress;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;


public class ExcelReader {


    public static List<DirectoryDto> readFile(ExcelGeneratorDto dto) {
        checkParams(dto);

        int companyColumn = new CellAddress(dto.getCompany() + "1").getColumn();
        int jobsColumn = new CellAddress(dto.getJobs() + "1").getColumn();
        int valueColumn = new CellAddress(dto.getValue() + "1").getColumn();

        List<DirectoryDto> dtoList = new ArrayList<>();
        try (ReadableWorkbook workbook = new ReadableWorkbook(dto.getInputFile())) {
            workbook.getSheets()
                    .filter(sheet -> sheet.getName().equals(dto.getSheetName()))
                    .forEach(sheet -> {
                        try (Stream<Row> rows = sheet.openStream()) {
                            AtomicInteger count = new AtomicInteger(0);
                            long start = System.currentTimeMillis();
                            rows.skip(1).forEach(row -> {
                                String companyName =  row.getCellRawValue(companyColumn).orElse("").trim();
                                String jobsName = row.getCellRawValue(jobsColumn).orElse("").trim();
                                String valueString = row.getCellRawValue(valueColumn).orElse("0.0").trim();
                                Double value = Try.of(() -> Double.parseDouble(valueString)).getOrElse(Double.NaN);

                                dtoList.add(new DirectoryDto(companyName, jobsName, value));
                                count.incrementAndGet();
                            });
                            System.out.println("Прочитано " + count.get() + " записей");
                            System.out.println("Прочитано за " + (System.currentTimeMillis() - start) + " мс");
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                            e.printStackTrace();
                        }
                    });
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }
        return dtoList;
    }

    public static void checkParams(ExcelGeneratorDto dto) {
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
