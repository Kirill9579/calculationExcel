package com.example.calculation.utils;

import com.example.calculation.dto.DirectoryDto;
import com.example.calculation.dto.ExcelGeneratorDto;
import com.example.calculation.dto.ResultCalcDto;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.Collections.max;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.*;

public class Calculation {
    public final ExcelGeneratorDto generatorDto;

    public Calculation(ExcelGeneratorDto generatorDto) {
        this.generatorDto = generatorDto;
    }


    public List<ResultCalcDto> calculate(List<DirectoryDto> dtoList) {
        Map<String, Map<String, List<Double>>> collect = dtoList.stream().collect(
                groupingBy(DirectoryDto::getCompany, groupingBy(DirectoryDto::getJob, mapping(DirectoryDto::getValue, toList())))
        );

        List<ResultCalcDto> result = new ArrayList<>();
        for (Map.Entry<String, Map<String, List<Double>>> entry : collect.entrySet()) {
            for (Map<String, List<Double>> map : collect.values()) {
                for (Map.Entry<String, List<Double>> listEntry : map.entrySet()) {
                    ResultCalcDto calcDto = new ResultCalcDto(entry.getKey(), listEntry.getKey());
                    calc(listEntry.getValue(), calcDto);
                    result.add(calcDto);
                }
            }
        }
        return result;
    }


    private void calc(List<Double> values, ResultCalcDto dto) {
        values.sort(Double::compareTo); // сортируем данные по возрастанию

        DescriptiveStatistics stat = new DescriptiveStatistics();
        values.forEach(stat::addValue);

        double q1 = stat.getPercentile(25);
        double q3 = stat.getPercentile(75);

        DescriptiveStatistics statistics = new DescriptiveStatistics();
        List<Double> collect = values.stream().filter(val -> val >= q1 && val <= q3)
                .peek(statistics::addValue)
                .collect(toList());

        double median = statistics.getPercentile(50);
        var range = median * generatorDto.getStepPercent()/100;

        List<List<Double>> lists = divideIntoRanges(collect, range);

        int maxList = max(lists, comparingInt(List::size)).size();

        List<Double> result = lists.stream()
                .filter(list -> list.size() >= (maxList * generatorDto.getRangePercent())/100)
                .flatMap(Collection::stream)
                .collect(toList());

        double average = result.stream().mapToDouble(Double::doubleValue).average().orElse(0);

        dto.setAverage(average);
        dto.setSize(result.size());
        dto.setIqr(q3 - q1);
    }


    public static List<List<Double>> divideIntoRanges(List<Double> values, double rangeStep) {
        List<List<Double>> ranges = new ArrayList<>();

        // Рассчитываем минимальное и максимальное значение
        double minValue = values.get(0);
        double maxValue = values.get(values.size() - 1);

        // Рассчитываем количество диапазонов
        int numRanges = (int) Math.ceil((maxValue - minValue + 1) / rangeStep);

        // Создаем диапазоны
        for (int i = 0; i < numRanges; i++) {
            double startValue = minValue + (i * rangeStep);
            double endValue = startValue + rangeStep;

            List<Double> range = new ArrayList<>();
            ranges.add(range);

            // Добавляем значения в диапазон
            for (Double value : values) {
                if (value >= startValue && value < endValue) {
                    range.add(value);
                }
            }
        }

        return ranges;
    }

}

