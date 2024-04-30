package spring.web.webApp.config.nested_job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.JobStepBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

public class ParentJob {
    @Autowired
    JobBuilderFactory jobBuilderFactory;

    @Autowired
    StepBuilderFactory stepBuilderFactory;

    @Autowired
    Job childJobFromChildClass;

    @Autowired
    JobLauncher jobLauncher;

    @Bean
    public Step step1FromParent(){
        return stepBuilderFactory
                .get("step1FromParent")
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("tasklet from Parent step");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Job parentJobFromParentClass(JobRepository jobRepository, PlatformTransactionManager transactionManager){
        Step childJobStep = new JobStepBuilder(new StepBuilder("childJob"))
                .job(childJobFromChildClass)
                .launcher(jobLauncher)
                .repository(jobRepository)
                .transactionManager(transactionManager)
                .build();
        return jobBuilderFactory
                .get("parentJobFromParentClass")
                .start(step1FromParent())
                .next(childJobStep)
                .build();
    }
}
