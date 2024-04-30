package spring.web.webApp.dbReader;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import spring.web.webApp.pojo.Employee;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

//@Configuration
public class DBReaderJobConfiguration {

    @Autowired
    StepBuilderFactory stepBuilderFactory;

    @Autowired
    JobBuilderFactory jobBuilderFactory;

    @Autowired
    DataSource dataSource;

    private String sql = "select eid, fname,lname from employee";


    @Bean
    public JdbcCursorItemReader<Employee> employeeItemReader() throws Exception {
        return new JdbcCursorItemReaderBuilder<Employee>()
                .name("employee")
                .sql(sql)
                .dataSource(this.dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(Employee.class))
                .build();


        /*JdbcCursorItemReader<Employee> itemReader = new JdbcCursorItemReader<>();

        itemReader.setDataSource(this.dataSource);
        itemReader.setSql(sql);
        itemReader.setRowMapper(new CustomRowMapper());
        itemReader.afterPropertiesSet();

        return itemReader;*/
    }

    @Bean
    public ItemWriter<Employee> employeeItemWriter(){
        return items -> {
            for (Employee emp: items){
                System.out.println("Employee: "+emp.toString());
            }
        };
    }

    @Bean
    public Step step1() throws Exception {
        return stepBuilderFactory
                .get("step1")
                .<Employee, Employee>chunk(5)
                .reader(employeeItemReader())
                .writer(employeeItemWriter())
                .build();
    }

    @Bean
    public Job jobToGetEmployeeFromDatabase() throws Exception {
        return jobBuilderFactory
                .get("jobToGetEmployeeFromDatabase")
                .start(step1())
                .build();
    }


}


class CustomRowMapper implements RowMapper<Employee> {

    @Override
    public Employee mapRow(ResultSet rs, int rowNum) throws SQLException {
        Employee employee = new Employee();
        employee.setEid(rs.getLong("eid"));
        employee.setFname(rs.getString("fname"));
        employee.setLname(rs.getString("lname"));

        return employee;
    }
}