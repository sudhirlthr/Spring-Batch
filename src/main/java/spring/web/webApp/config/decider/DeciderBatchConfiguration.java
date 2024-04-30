package spring.web.webApp.config.decider;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class DeciderBatchConfiguration {
    @Autowired
    StepBuilderFactory stepBuilderFactory;

    @Autowired
    JobBuilderFactory jobBuilderFactory;

    @Bean
    public Step startStep(){
        return stepBuilderFactory.get("startStep()")
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("In starter step");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Step oddStep(){
        return stepBuilderFactory
                .get("oddStep")
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("In oddStep");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step evenStep(){
        return stepBuilderFactory
                .get("evenStep()")
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("In EvenStep");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public JobExecutionDecider jobExecutionDecider(){
        return new OddEvenDecider();
    }

    @Bean
    public Job job(){
        System.out.println("\n\nStarting Decider\n\n");
        return jobBuilderFactory
                .get("job()")
                .start(startStep())
                .next(jobExecutionDecider())
                .from(jobExecutionDecider()).on("ODD").to(oddStep())
                .from(jobExecutionDecider()).on("EVEN").to(evenStep())
                .from(oddStep()).on("*").to(jobExecutionDecider())
                .end()
                .build();
    }

    public static class OddEvenDecider implements JobExecutionDecider{

        private int count=0;

        @Override
        public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
            count++;
            if (count %2 == 0)
                return new FlowExecutionStatus("EVEN");
            else
                return new FlowExecutionStatus("ODD");
        }
    }
}
