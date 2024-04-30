package spring.web.webApp.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
//@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    private Integer i= 1;

    @Bean
    public Integer getInteger(){
        return i++;
    }

    @Bean
    public Tasklet myTasklet( Integer i) {
        return (contribution, chunkContext) -> {
            System.out.println("Hello from tasklet: "+i);
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Step step1(){
        return stepBuilderFactory.get("step1")
                .tasklet(myTasklet(1))
                .build();
    }
    @Bean
    public Step step2(){
        return stepBuilderFactory.get("step2")
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("From tasklet but step 2");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }
    @Bean
    public Step step3(){
        return stepBuilderFactory.get("step3")
                .tasklet(myTasklet(getInteger()))
                .build();
    }

    @Bean
    public Job helloWorldJob(){
        return jobBuilderFactory.get("helloWorldJob")
                .start(step1())
                .build();
    }

    @Bean
    public Job transitionJobExecution(){
        return jobBuilderFactory.get("transitionJob")
                .start(step1())
                .next(step2())
                .next(step3())
                .build();
    }

    @Bean
    public Job transitionJobExecution_OnCompletionStepByStep(){
        return jobBuilderFactory.get("transitionJobExecution_OnCompletionStepByStep")
                .start(step1())
                .on("COMPLETED").to(step3())
                .from(step3()).on("COMPLETED").to(step2())
                .from(step2()).end()
                .build();
    }

    @Bean
    public Job transitionJobExecution_OnFailStep(){
        return jobBuilderFactory.get("transitionJobExecution_OnFailStep")
                .start(step1())
                .on("COMPLETED").to(step2())
                .from(step2()).on("COMPLETED").fail()
                .from(step3()).end()
                .build();
    }
}