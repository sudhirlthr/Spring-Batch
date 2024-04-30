package spring.web.webApp.job_flow;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


public class FlowFoo {
    @Autowired
    StepBuilderFactory stepBuilderFactory;

    @Bean
    public Step step1(){
        return stepBuilderFactory.get("step1")
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("On step1");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Step step2(){
        return stepBuilderFactory.get("step2")
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("On step2");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Flow flow(){
        FlowBuilder<Flow> fooFlowBuilder = new FlowBuilder<>("FooFlowBuilder");
        fooFlowBuilder.start(step1())
                .next(step2()).end();
        return fooFlowBuilder.build();

    }
}
