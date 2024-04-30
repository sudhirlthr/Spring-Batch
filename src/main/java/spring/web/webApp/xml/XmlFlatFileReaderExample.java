package spring.web.webApp.xml;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.xstream.XStreamMarshaller;
import spring.web.webApp.pojo.Employee;

import java.util.HashMap;
import java.util.Map;

//@Configuration
public class XmlFlatFileReaderExample {
    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    public StaxEventItemReader<Employee> employeeItemReader() throws ClassNotFoundException {
        XStreamMarshaller unMarshaller = new XStreamMarshaller();
        Map<String, Class> aliases = new HashMap<>();
        aliases.put("employee", Employee.class);

        unMarshaller.setAliases(aliases);

        StaxEventItemReader<Employee> reader = new StaxEventItemReader<>();
        reader.setResource(new ClassPathResource("/employee.xml"));
        reader.setFragmentRootElementName("employee");
        reader.setUnmarshaller(unMarshaller);
        return reader;
    }

    @Bean
    public ItemWriter<Employee> employeeItemWriter(){
        return items -> {
            for (Employee emp: items)
                System.out.println("Employee: "+emp.toString());
        };
    }

    @Bean
    public Step step1() throws ClassNotFoundException {
        return stepBuilderFactory
                .get("step1")
                .<Employee, Employee>chunk(10)
                .reader(employeeItemReader())
                .writer(employeeItemWriter())
                .build();
    }

    @Bean
    public Job job() throws ClassNotFoundException {
        return jobBuilderFactory
                .get("job")
                .start(step1())
                .build();
    }

}
