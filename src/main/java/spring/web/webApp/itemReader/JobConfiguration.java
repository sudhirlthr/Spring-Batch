package spring.web.webApp.itemReader;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;

//@Configuration
public class JobConfiguration {
    @Autowired
    StepBuilderFactory stepBuilderFactory;

    @Autowired
    JobBuilderFactory jobBuilderFactory;

    @Bean
    public StatelessReader statelessReader(){
        ArrayList<String> list = new ArrayList<>(Arrays.asList("one", "two", "Three", "four", "five", "six"));
        return new StatelessReader(list.iterator());
    }

    @Bean
    public Step step1(){
        return stepBuilderFactory
                .get("step1")
                .<String,String>chunk(2)
                .reader(statelessReader())
                .writer(list -> {
                    list.forEach(System.out::println);
                }).build();
    }

    @Bean
    public Job jobForItemReaderWriter(){
        return jobBuilderFactory
                .get("jobForItemReaderWriter")
                .start(step1())
                .build();
    }
}
