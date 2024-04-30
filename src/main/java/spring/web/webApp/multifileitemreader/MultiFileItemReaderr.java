package spring.web.webApp.multifileitemreader;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.ResourceAware;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.validation.BindException;

import java.util.Date;

//@Configuration
public class MultiFileItemReaderr {

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Value("classpath*:/data/customer*.csv")
    private Resource[] inputFiles;

    @Bean
    public MultiResourceItemReader<Customer> multiResourceItemReader(){
        MultiResourceItemReader<Customer> reader = new MultiResourceItemReader<>();
        reader.setDelegate(customerItemReader());
        reader.setResources(inputFiles);
        return reader;
    }

    @Bean
    public FlatFileItemReader<Customer> customerItemReader(){
        FlatFileItemReader<Customer> reader = new FlatFileItemReader<>();
        DefaultLineMapper<Customer> mapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames(new String[]{"id", "firstName", "lastName", "birthdate"});

        mapper.setLineTokenizer(tokenizer);
        mapper.setFieldSetMapper(new CustomerFieldSetMapper());
        mapper.afterPropertiesSet();
        reader.setLineMapper(mapper);
        return reader;
    }

    @Bean
    public ItemWriter<Customer> customerItemWriter(){
        return items -> {
          for (Customer cust:items){
              System.out.println("Customer: "+cust.toString());
          }
        };
    }

    @Bean
    public Step step1(){
        return stepBuilderFactory
                .get("step1")
                .<Customer, Customer>chunk(10)
                .reader(multiResourceItemReader())
                .writer(customerItemWriter())
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

class CustomerFieldSetMapper implements FieldSetMapper<Customer>{

    @Override
    public Customer mapFieldSet(FieldSet fieldSet) throws BindException {
        Customer customer = new Customer();
        customer.setId(fieldSet.readLong("id"));
        customer.setFirstName(fieldSet.readString("firstName"));
        customer.setLastName(fieldSet.readString("lastName"));
        customer.setBirthdate(fieldSet.readDate("birthdate", "yyyy-MM-dd HH:mm:ss"));
        return customer;
    }
}

class Customer implements ResourceAware{
    private Long id;
    private String firstName;
    private String lastName;

    @Override
    public void setResource(Resource resource) {
        this.resource = resource;
    }

    private Resource resource;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    private Date birthdate;

    public Customer(){}

    public Customer(Long id, String firstName, String lastName, Date birthdate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthdate = birthdate;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", resource=" + resource +
                ", birthdate=" + birthdate +
                '}';
    }
}