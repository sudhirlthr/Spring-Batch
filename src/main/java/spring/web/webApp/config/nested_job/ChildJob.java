package spring.web.webApp.config.nested_job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


public class ChildJob {
    @Autowired
    StepBuilderFactory stepBuilderFactory;

    @Autowired
    JobBuilderFactory jobBuilderFactory;

    @Bean
    public Step step1a(){
        return stepBuilderFactory
                .get("step1a")
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("This is from step1a:Child");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Job childJobFromChildClass(){
        return jobBuilderFactory
                .get("childJob")
                .start(step1a())
                .build();
    }
}
