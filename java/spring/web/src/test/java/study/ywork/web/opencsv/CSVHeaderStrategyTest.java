package study.ywork.web.opencsv;

import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

class CSVHeaderStrategyTest {
    @Test
    void beanToCSVWithDefault() throws Exception {
        // 测试自定义列名
        List<Employee> employees = List.of(
                new Employee(123L, "dev", 30),
                new Employee(456L, "test", 40),
                new Employee(890L, "admin", 50)
        );
        try (FileWriter writer = new FileWriter("employees.csv")) {
            var mappingStrategy = new CustomColumnNameStrategy<Employee>();
            mappingStrategy.setType(Employee.class);
            var builder = new StatefulBeanToCsvBuilder<Employee>(writer)
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .withMappingStrategy(mappingStrategy)
                    .build();
            builder.write(employees);
        }
    }

    @Test
    void beanToCSVWithDefault2() throws Exception {
        // 测试自定义列的位置
        List<Employee> employees = List.of(
                new Employee(123L, "dev", 30),
                new Employee(456L, "test", 40),
                new Employee(890L, "admin", 50)
        );
        try (FileWriter writer = new FileWriter("employees2.csv")) {
            var builder = new StatefulBeanToCsvBuilder<Employee>(writer)
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .build();
            builder.write(employees);
        }
    }

    @Test
    void beanToCSVWithCustomHeaderAndPositionStrategy()
            throws IOException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {
        // 测试自定义列名和位置
        List<Employee> employees = List.of(
                new Employee(123L, "dev", 30),
                new Employee(456L, "test", 40),
                new Employee(890L, "admin", 50)
        );
        try (FileWriter writer = new FileWriter("employees3.csv")) {
            var mappingStrategy = new MixedColumnNamePositionStrategy<Employee>();
            mappingStrategy.setType(Employee.class);

            var builder = new StatefulBeanToCsvBuilder<Employee>(writer)
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .withMappingStrategy(mappingStrategy)
                    .build();
            builder.write(employees);
        }
    }

    @Test
    void useReadMappingStrategy() throws Exception {
        // 使用读文件的MappingStrategy对象给写文件的
        // StatefulBeanToCsvBuilder.withMappingStrategy方法
        // 那么会保持原有的CSV列名和位置
        Country[] countries = new Country[]{
                new Country("HK", "852"),
                new Country("US", "1"),
                new Country("JP", "81"),
        };

        HeaderColumnNameMappingStrategy<Country> strategy = new HeaderColumnNameMappingStrategy<>();
        strategy.setType(Country.class);
        // 构造CsvBindByName的列名，按照定义顺序构造
        String headerLine = Arrays.stream(Country.class.getDeclaredFields())
                .map(field -> field.getAnnotation(CsvBindByName.class))
                .filter(Objects::nonNull)
                .map(CsvBindByName::column)
                .collect(Collectors.joining(","));

        try (StringReader reader = new StringReader(headerLine)) {
            new CsvToBeanBuilder<Country>(reader)
                    .withType(Country.class)
                    .withMappingStrategy(strategy)
                    .build().parse();
        }

        Path outputPath = Path.of("countries.csv");
        try (var writer = Files.newBufferedWriter(outputPath)) {
            StatefulBeanToCsv<Country> csv = new StatefulBeanToCsvBuilder<Country>(writer)
                    .withMappingStrategy(strategy)
                    .withApplyQuotesToAll(false)
                    .build();
            csv.write(Arrays.asList(countries));
        }
    }
}
