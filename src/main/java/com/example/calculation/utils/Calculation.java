package com.example.calculation.utils;

import com.example.calculation.dto.DirectoryDto;
import com.example.calculation.dto.ResultCalcDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.*;

public class Calculation {

    public static List<ResultCalcDto> calculate(List<DirectoryDto> dtoList) {
        Map<String, Map<String, List<Double>>> collect = dtoList.stream()
                .collect(
                        groupingBy(DirectoryDto::getCompany,
                                groupingBy(DirectoryDto::getJob, mapping(DirectoryDto::getValue, toList())))
                );

        List<ResultCalcDto> result = new ArrayList<>();
        for (Map.Entry<String, Map<String, List<Double>>> entry : collect.entrySet()) {
            for (Map<String, List<Double>> map : collect.values()) {
                for (Map.Entry<String, List<Double>> listEntry : map.entrySet()) {
                    ResultCalcDto calcDto = new ResultCalcDto(entry.getKey(), listEntry.getKey(), calc(listEntry.getValue()));
                    result.add(calcDto);
                }
            }
        }
        return result;
    }


    private static double calc(List<Double> values) {
        values.sort(Double::compareTo); // сортируем данные по возрастанию

        double q1 = quartile(values, 0.25); // находим Q1
        double q3 = quartile(values, 0.75); // находим Q3

        return q3 - q1;
    }

    private static double quartile(List<Double> data, double percentile) {
        if (data.size() == 1) return data.get(0);

        double n = (data.size() - 1) * percentile + 1;
        int k = (int) n;
        double d = n - k;

        return data.get(k - 1) + d * (data.get(k) - data.get(k - 1));
    }
}

