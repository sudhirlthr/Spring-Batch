package spring.web.webApp.writer.jdbc;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.validation.BindException;
import spring.web.webApp.pojo.Customer;

import javax.sql.DataSource;

//@Configuration
public class DataBaseWriteExample {
    @Autowired
    public StepBuilderFactory stepBuilderFactory;
    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    DataSource dataSource;

    private String sql = "insert into customer (id, firstname, lastname, birthdate) values (:id, :firstname, :lastname, :birthdate)";

    @Bean
    public FlatFileItemReader<Customer> customerItemReader(){
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames(new String[]{"id", "firstname", "lastname", "birthdate"});

        DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(new CustomFieldSetMapper());
        lineMapper.afterPropertiesSet();

        FlatFileItemReader<Customer> reader = new FlatFileItemReader<>();
        reader.setLinesToSkip(1);
        reader.setResource(new ClassPathResource("/customer.csv"));
        reader.setLineMapper(lineMapper);
        return reader;
    }

    @Bean
    public JdbcBatchItemWriter<Customer> customerItemWriter(){
        JdbcBatchItemWriter<Customer> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(this.dataSource);
        writer.setSql(sql);
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider());
        writer.afterPropertiesSet();
        return writer;
    }

    @Bean
    public Step step1(){
        return stepBuilderFactory
                .get("step1")
                .<Customer, Customer>chunk(10)
                .reader(customerItemReader())
                .writer(customerItemWriter())
                .build();
    }
    @Bean
    public Job job(){
        return jobBuilderFactory.get("job")
                .start(step1())
                .build();
    }


}

class CustomFieldSetMapper implements FieldSetMapper<Customer>{

    @Override
    public Customer mapFieldSet(FieldSet fieldSet) throws BindException {
        Customer customer = new Customer();
        customer.setId(fieldSet.readLong("id"));
        customer.setFirstname(fieldSet.readString("firstname"));
        customer.setLastname(fieldSet.readString("lastname"));
        customer.setBirthdate(fieldSet.readDate("birthdate", "yyyy-MM-dd HH:mm:ss"));
        return customer;
    }
}
