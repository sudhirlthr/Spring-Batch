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


public class FlowLast {
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Bean
    public Step lastFlow_myStep(){
        return stepBuilderFactory.get("lastFlow_myStep")
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("On last flow of last step");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Job lastFlow_lastJob(Flow flow){
        return jobBuilderFactory.get("lastFlow_lastJob")
                .start(lastFlow_myStep())
                .on("COMPLETED").to(flow)
                .end().build();
    }
}
