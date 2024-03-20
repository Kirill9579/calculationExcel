package com.example.calculation.utils;

import com.example.calculation.Main;
import com.example.calculation.dto.DirectoryDto;
import com.example.calculation.dto.ExcelGeneratorDto;
import io.vavr.control.Try;
import javafx.scene.control.Alert;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.poi.ss.util.CellReference;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static com.example.calculation.Main.*;

public class ExcelReader {


    public static List<DirectoryDto> readFile(ExcelGeneratorDto dto) {
        int companyColumn = CellReference.convertColStringToIndex(dto.getCompany());
        int jobsColumn = CellReference.convertColStringToIndex(dto.getJobs());
        int valueColumn = CellReference.convertColStringToIndex(dto.getValue());

        List<DirectoryDto> dtoList = new ArrayList<>();
        try (ReadableWorkbook workbook = new ReadableWorkbook(dto.getInputFile())) {
            workbook.getSheets()
                    .filter(sheet -> sheet.getName().equals(dto.getSheetName()))
                    .forEach(sheet -> {
                        try (Stream<Row> rows = sheet.openStream()) {
                            AtomicInteger count = new AtomicInteger(0);
                            long start = System.currentTimeMillis();
                            rows.skip(1).forEach(row -> {
                                String companyName =  row.getCellRawValue(companyColumn).orElse("");
                                String jobsName = row.getCellRawValue(jobsColumn).orElse("");
                                String valueString = row.getCellRawValue(valueColumn).orElse("0.0");
                                Double value = Try.of(() -> Double.parseDouble(valueString)).getOrElse(Double.NaN);

                                dtoList.add(new DirectoryDto(companyName, jobsName, value));
                                count.incrementAndGet();
                            });
                            log.info("Прочитано " + count.get() + " записей");
                            log.info("Прочитано за " + (System.currentTimeMillis() - start) + " мс");
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                    });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
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
