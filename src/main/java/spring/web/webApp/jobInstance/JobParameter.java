package spring.web.webApp.jobInstance;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class JobParameter {
    @Autowired
    JobBuilderFactory jobBuilderFactory;

    @Autowired
    StepBuilderFactory stepBuilderFactory;


    @Bean
    @StepScope
    public Tasklet helloWorldTasklet(@Value("#{jobParameters['message']}") String message){
        return (stepContribution, chunkContext) -> {
            System.out.println("\n\n\tIn Tasklet\t");
            System.out.println(message);
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Step step1(){
        return stepBuilderFactory
                .get("step1")
                .tasklet(helloWorldTasklet(null))
                .build();
    }
    public Job jobForJobParameters(){
        return jobBuilderFactory
                .get("jobForJobParameters")
                .start(step1())
                .build();
    }
}
