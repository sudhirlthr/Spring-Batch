package spring.web.webApp.flatfile;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.validation.BindException;
import spring.web.webApp.pojo.Employee;

//@Configuration
public class FlatFileExampleUsingCsv {

    @Autowired
    public StepBuilderFactory  stepBuilderFactory;

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Bean
    public FlatFileItemReader<Employee> employeeItemReader(){
        FlatFileItemReader<Employee> reader = new FlatFileItemReader<>();
        reader.setLinesToSkip(1);
        reader.setResource(new ClassPathResource("/Employee.csv"));

        DefaultLineMapper<Employee> defaultLineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames(new String[]{"eid", "fname", "lname"});

        defaultLineMapper.setLineTokenizer(tokenizer);
        defaultLineMapper.setFieldSetMapper(new CustomFieldSetMapper());
        defaultLineMapper.afterPropertiesSet();

        reader.setLineMapper(defaultLineMapper);
        return reader;
    }
    @Bean
    public ItemWriter<Employee> employeeItemWriter(){
        return items -> {
            for(Employee e: items){
                System.out.println("\nEmpl: "+e.toString());
            }
        };
    }
    @Bean
    public Step step1(){
        return stepBuilderFactory
                .get("step1")
                .<Employee, Employee>chunk(5)
                .reader(employeeItemReader())
                .writer(employeeItemWriter())
                .build();
    }

    @Bean
    public Job job(){
        return jobBuilderFactory
                .get("job")
                .start(step1())
                .build();
    }

}

class CustomFieldSetMapper implements FieldSetMapper<Employee> {

    @Override
    public Employee mapFieldSet(FieldSet fieldSet) throws BindException {
        Employee employee = new Employee();
        employee.setEid(fieldSet.readLong("eid"));
        employee.setFname(fieldSet.readString("fname"));
        employee.setLname(fieldSet.readString("lname"));
        return employee;
    }
}
