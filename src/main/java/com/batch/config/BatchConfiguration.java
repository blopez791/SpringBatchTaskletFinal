package com.batch.config;

import com.batch.entities.Person;
import com.batch.steps.ItemDescompressStep;
import com.batch.steps.ItemProcessorStep;
import com.batch.steps.ItemReaderStep;
import com.batch.steps.ItemWriterStep;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.core.repository.ExecutionContextSerializer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.dao.Jackson2ExecutionContextStringSerializer;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.PlatformTransactionManager;


@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory; //construir el job

    @Autowired
    public StepBuilderFactory stepBuilderFactory; //construir los step

    @Bean
    @JobScope
    public ItemDescompressStep itemDescompressStep(){
        return new ItemDescompressStep();
    }

    @Bean
    @JobScope
    public ItemReaderStep itemReaderStep(){
        return new ItemReaderStep();
    }

    @Bean
    @JobScope
    public ItemProcessorStep itemProcessorStep(){
        return new ItemProcessorStep();
    }

    @Bean
    @JobScope
    public ItemWriterStep itemWriterStep(){
        return new ItemWriterStep();
    }


    //CONSTRUACCION PASOS
    @Bean
    public Step descompressFileStep(){
        return stepBuilderFactory.get("descompressFileStep")
                .tasklet(itemDescompressStep())
                .build();
    }

    @Bean
    public Step readFileStep(){
        return stepBuilderFactory.get("readPersonStep")
                .tasklet(itemReaderStep())
                .build();
    }

    @Bean
    public Step processDataStep(){
        return stepBuilderFactory.get("processPerson")
                .tasklet(itemProcessorStep())
                .build();
    }

    @Bean
    public Step writerDataStep(){
        return  stepBuilderFactory.get("writePersonStep")
                .tasklet(itemWriterStep())
                .build();
    }

    //CREACION DEL JOB
    @Bean
    public Job readCSVJob(){
        return jobBuilderFactory.get("readCSVJob")
                .start(descompressFileStep())
                .next(readFileStep())
                .next(processDataStep())
                .next(writerDataStep())
                .build();
    }
}
