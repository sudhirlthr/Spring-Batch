package spring.web.webApp.writer.xml;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.H2PagingQueryProvider;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.validation.BindException;
import spring.web.webApp.pojo.Customer;

import javax.sql.DataSource;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

//@Configuration
public class XmlFlatFileWriter {
    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public DataSource dataSource;

    private String sql = "insert into customer2 (id, firstname, lastname, birthdate) values (:id, :firstname, :lastname, :birthdate)";


    // 1. To read data from CSV file
    @Bean
    public FlatFileItemReader<Customer> customerItemReaderDB(){
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames(new String[]{"id", "firstname", "lastname", "birthdate"});

        DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(new CustomFieldSetMapper2());
        lineMapper.afterPropertiesSet();

        FlatFileItemReader<Customer> reader = new FlatFileItemReader<>();
        reader.setLinesToSkip(1);
        reader.setResource(new ClassPathResource("/customer.csv"));
        reader.setLineMapper(lineMapper);
        return reader;
    }

    // 2. To write data into DB which we get from CSV from above step
    @Bean
    public JdbcBatchItemWriter<Customer> customerItemWriterDB(){
        JdbcBatchItemWriter<Customer> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(this.dataSource);
        writer.setSql(sql);
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider());
        writer.afterPropertiesSet();
        return writer;
    }

    // 3. Read data from DB
    @Bean
    public JdbcPagingItemReader<Customer> pagingItemReader(){
        Map<String, Order> sorKeys = new HashMap<>();
        sorKeys.put("id", Order.ASCENDING);

        H2PagingQueryProvider queryProvider = new H2PagingQueryProvider();
        queryProvider.setSortKeys(sorKeys);
        queryProvider.setSelectClause("id, firstname, lastname, birthdate");
        queryProvider.setFromClause("from customer2");

        JdbcPagingItemReader<Customer> reader = new JdbcPagingItemReader<>();
        reader.setDataSource(this.dataSource);
        reader.setFetchSize(10);
        reader.setRowMapper(new CustomRowMapper());
        reader.setQueryProvider(queryProvider);
        return reader;
    }

    // 4. Write data into XML file
    @Bean
    public StaxEventItemWriter<Customer> customerItemWriter() throws Exception {
        Map<String, Class> alias = new HashMap<>();
        alias.put("customer2", Customer.class);

        XStreamMarshaller marshaller = new XStreamMarshaller();
        marshaller.setAliases(alias);

        StaxEventItemWriter<Customer> writer = new StaxEventItemWriter<>();
        writer.setRootTagName("customers");
        writer.setMarshaller(marshaller);
        String absolutePath = File.createTempFile("customerXmlOutput", ".xml").getAbsolutePath();
        System.out.println("Absolute path: "+absolutePath);
        writer.setResource(new FileSystemResource(absolutePath));
        writer.afterPropertiesSet();
        return writer;
    }

    @Bean
    public Step step1(){
        return stepBuilderFactory
                .get("step1")
                .<Customer, Customer>chunk(10)
                .reader(customerItemReaderDB())
                .writer(customerItemWriterDB())
                .build();
    }

    @Bean
    public Step step2() throws Exception {
        return stepBuilderFactory
                .get("step2")
                .<Customer, Customer>chunk(10)
                .reader(pagingItemReader())
                .writer(customerItemWriter())
                .build();
    }

    @Bean
    public Job job() throws Exception {
        return jobBuilderFactory
                .get("job")
                .start(step1())
                .next(step2())
                .build();
    }


}

class CustomRowMapper implements RowMapper<Customer> {

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

class CustomFieldSetMapper2 implements FieldSetMapper<Customer> {

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