package spring.web.webApp.job_flow;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


public class FlowFirst {

    @Autowired
    StepBuilderFactory stepBuilderFactory;

    @Autowired
    JobBuilderFactory jobBuilderFactory;

    @Bean
    public Step myNewStep(){
        return stepBuilderFactory.get("myNewStep")
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("In myNewStep printing from tasklet lambda");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Job flowFirstJob(Flow flow){
        return jobBuilderFactory.get("flowFirstJob")
                .start(flow)// get the data from S3
                .next(myNewStep())// send a successful notification
                .end().build();
    }
}
