package spring.web.webApp.dat;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.Objects;

@Configuration
public class DatFileWriterExample {

    @Autowired
    StepBuilderFactory stepBuilderFactory;

    @Autowired
    JobBuilderFactory jobBuilderFactory;

    private String header = "id~firstname~lastname~birthdate";

    @Bean
    public JsonItemReader<Customer2> itemReader(){
        return new JsonItemReaderBuilder<Customer2>()
                .jsonObjectReader(new JacksonJsonObjectReader<>(Customer2.class))
                .name("itemReader")
                .resource(new ClassPathResource("/Customer.json"))
                .build();
    }

    @Bean
    public FlatFileItemWriter<Customer2> itemWriter() throws IOException {

        FlatFileItemWriter<Customer2> itemWriter = new FlatFileItemWriter<>();
        itemWriter.setAppendAllowed(true);
        itemWriter.setHeaderCallback(new CustomerFlatFileItemHeadercallback(header));
        itemWriter.setLineAggregator(new DelimitedLineAggregator<>(){
            {
                setDelimiter("~");
                setFieldExtractor(new BeanWrapperFieldExtractor<>(){
                    {
                        setNames(new String[]{"id","firstname","lastname","birthdate"});
                    }
                });
            }
        });
        String absolutePath = File.createTempFile("CustomerDatFileOutput", ".dat").getAbsolutePath();
        System.out.println("\n\tAbsolute file path: "+absolutePath);
        itemWriter.setResource(new FileSystemResource(absolutePath));
        return itemWriter;
    }
    @Bean
    public Step step1() throws IOException {
        return stepBuilderFactory
                .get("step1")
                .<Customer2, Customer2>chunk(5)
                .reader(itemReader())
                .writer(itemWriter())
                .build();
    }

    @Bean
    public Job job() throws IOException {
        return jobBuilderFactory
                .get("job")
                .start(step1())
                .build();
    }

}

class CustomerFlatFileItemHeadercallback implements FlatFileHeaderCallback{

    private String header;

    public CustomerFlatFileItemHeadercallback(String header) {
        this.header = header;
    }

    @Override
    public void writeHeader(Writer writer) throws IOException {
        writer.write(header);
    }
}
///
class Customer2 {

    private Long id;
    private String firstname;
    private String lastname;
    private String birthdate;

    public Customer2() {
    }

    public Customer2(Long id, String firstname, String lastname, String birthdate) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.birthdate = birthdate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", birthdate=" + birthdate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer2 customer)) return false;
        return Objects.equals(getId(), customer.getId()) && Objects.equals(getFirstname(), customer.getFirstname()) && Objects.equals(getLastname(), customer.getLastname()) && Objects.equals(getBirthdate(), customer.getBirthdate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getFirstname(), getLastname(), getBirthdate());
    }
}