package com.bibliotheque.universitaire.serviceemprunts.batch;

import com.bibliotheque.universitaire.serviceemprunts.model.Emprunt;
import com.bibliotheque.universitaire.serviceemprunts.repository.EmpruntRepository;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.adapter.ItemWriterAdapter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Bean
    public JpaPagingItemReader<Emprunt> reader(EntityManagerFactory entityManagerFactory) {
        return new JpaPagingItemReaderBuilder<Emprunt>()
                .name("empruntReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT e FROM Emprunt e WHERE e.dateRetour < CURRENT_DATE")
                .pageSize(10)
                .build();
    }

    @Bean
    public ItemWriterAdapter<Emprunt> writer(EmpruntRepository empruntRepository) {
        ItemWriterAdapter<Emprunt> writer = new ItemWriterAdapter<>();
        writer.setTargetObject(empruntRepository);
        writer.setTargetMethod("save");
        return writer;
    }

    @Bean
    public Step penaliteStep(JobRepository jobRepository,
                             PlatformTransactionManager transactionManager,
                             JpaPagingItemReader<Emprunt> reader,
                             PenaliteProcessor processor,
                             ItemWriterAdapter<Emprunt> writer) {
        return new StepBuilder("penaliteStep", jobRepository)
                .<Emprunt, Emprunt>chunk(10, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job penaliteJob(JobRepository jobRepository, Step penaliteStep) {
        return new JobBuilder("penaliteJob", jobRepository)
                .start(penaliteStep)
                .build();
    }
}
