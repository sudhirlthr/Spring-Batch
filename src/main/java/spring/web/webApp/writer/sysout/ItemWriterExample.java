package spring.web.webApp.writer.sysout;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

//@Configuration
public class ItemWriterExample {

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Bean
    public ListItemReader<String> itemReader(){
        List<String> list = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            list.add(String.valueOf(i));
        }
        return new ListItemReader<>(list);
    }

    @Bean
    public CustomItemWriter itemWriter(){
        return new CustomItemWriter();
    }

    @Bean
    public Step step1(){
        return stepBuilderFactory
                .get("step1")
                .<String, String>chunk(10)
                .reader(itemReader())
                .writer(itemWriter())
                .build();
    }

    @Bean
    public Job job(){
        return jobBuilderFactory
                .get("job")
                .start(step1())
                .build();
    }
}
class  CustomItemWriter implements ItemWriter<String> {

    @Override
    public void write(List<? extends String> list) throws Exception {
        System.out.println("\t Chunk size is: "+list.size());
        list.forEach(x -> System.out.println(">> "+x));
    }
}