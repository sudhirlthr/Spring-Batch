package spring.web.webApp.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.H2PagingQueryProvider;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.RowMapper;
import spring.web.webApp.pojo.Customer;

import javax.sql.DataSource;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

//@Configuration
public class processorExample {
    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public DataSource dataSource;

    @Bean
    JdbcPagingItemReader<Customer> itemReader(){
        Map<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("id", Order.DESCENDING);

        H2PagingQueryProvider queryProvider = new H2PagingQueryProvider();
        queryProvider.setSortKeys(sortKeys);
        queryProvider.setSelectClause("id, firstname, lastname, birthdate");
        queryProvider.setFromClause("from customer2");

        JdbcPagingItemReader<Customer> reader = new JdbcPagingItemReader<>();
        reader.setQueryProvider(queryProvider);
        reader.setFetchSize(10);
        reader.setDataSource(this.dataSource);
        reader.setRowMapper(new CustomerRowMapper());
        return reader;
    }

    @Bean
    public FlatFileItemWriter<Customer> customerItemWriter() throws Exception {
        FlatFileItemWriter<Customer> writer = new FlatFileItemWriter<>();
        writer.setLineAggregator(new CustomLineAggregator());
        String absolutePath = File.createTempFile("customerOutput", ".json").getAbsolutePath();
        System.out.println("File Path directory: "+absolutePath);
        writer.setResource(new FileSystemResource(absolutePath));
        writer.afterPropertiesSet();
        return writer;
    }

    @Bean
    public UppercaseItemProcessor itemProcessor(){
        return new UppercaseItemProcessor();
    }

    @Bean
    public Step step1() throws Exception {
        return stepBuilderFactory
                .get("step1")
                .<Customer, Customer>chunk(10)
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(customerItemWriter())
                .build();
    }
    @Bean
    public Job job() throws Exception {
        return jobBuilderFactory
                .get("job")
                .start(step1())
                .build();
    }

}

class UppercaseItemProcessor implements ItemProcessor<Customer, Customer> {

    @Override
    public Customer process(Customer customer) throws Exception {
        Customer customerWithUpperCase = new Customer();
        customerWithUpperCase.setBirthdate(customer.getBirthdate());
        customerWithUpperCase.setId(customer.getId());
        customerWithUpperCase.setFirstname(customer.getFirstname().toUpperCase());
        customerWithUpperCase.setLastname(customer.getLastname().toUpperCase());
        return customerWithUpperCase;
    }
}

class CustomLineAggregator implements LineAggregator<Customer> {

    private ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public String aggregate(Customer customer) {
        try{
            return objectMapper.writeValueAsString(customer);
        }catch (JsonProcessingException exception){
            System.err.println("Error in processing json file: "+exception.getMessage());
            throw new RuntimeException("Unable to serialize Customer data", exception);
        }
    }
}

class CustomerRowMapper implements RowMapper<Customer>{

    @Override
    public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
        Customer customer = new Customer();
        customer.setId(rs.getLong("id"));
        customer.setFirstname(rs.getString("firstname"));
        customer.setLastname(rs.getString("lastname"));
        customer.setBirthdate(rs.getDate("birthdate"));

        return customer;
    }
}
