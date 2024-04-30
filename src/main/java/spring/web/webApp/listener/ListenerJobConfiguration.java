package spring.web.webApp.listener;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import spring.web.webApp.mail.EmailUtil;

import java.util.Arrays;
import java.util.List;

//@Configuration
public class ListenerJobConfiguration {

    @Autowired
    JobBuilderFactory jobBuilderFactory;

    @Autowired
    StepBuilderFactory stepBuilderFactory;

    @Bean
    public ItemReader<String> reader(){
        return new ListItemReader<>(Arrays.asList("one", "two", "three", "four", "five"));
    }

    @Bean
    public ItemWriter<String> writer(){
        return new ItemWriter<String>() {
            @Override
            public void write(List<? extends String> list) throws Exception {
                for (String l:list){
                    System.out.println("Writing item: "+l);
                }
            }
        };
    }

    @Bean
    public Step step1(){
        return stepBuilderFactory
                .get("step1")
                .<String, String>chunk(2)
                .faultTolerant()
                .listener(new ChunkListenerImpl())
                .reader(reader())
                .writer(writer())
                .build();
    }

    @Bean
    public Job listenerJob(EmailUtil emailUtil){
        return jobBuilderFactory
                .get("listenerJob")
                .start(step1())
                .listener(new JobListener(emailUtil))
                .build();
    }
}
