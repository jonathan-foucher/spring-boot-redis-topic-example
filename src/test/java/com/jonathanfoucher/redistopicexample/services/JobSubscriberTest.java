package com.jonathanfoucher.redistopicexample.services;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jonathanfoucher.redistopicexample.data.JobDto;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static ch.qos.logback.classic.Level.INFO;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringJUnitConfig(JobSubscriber.class)
class JobSubscriberTest {
    @Autowired
    private JobSubscriber jobSubscriber;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String TOPIC_NAME = "job_queue";
    private static final Long JOB_ID = 15L;
    private static final String JOB_NAME = "some job name";

    @Test
    void onMessageReceived() {
        // GIVEN
        Logger log = (Logger) LoggerFactory.getLogger(JobSubscriber.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        log.addAppender(listAppender);

        JobDto job = initJobDto();
        Message message = initMessage(job);

        // WHEN
        jobSubscriber.onMessage(message, TOPIC_NAME.getBytes(StandardCharsets.UTF_8));

        // THEN
        List<ILoggingEvent> logs = listAppender.list;
        assertEquals(2, logs.size());

        assertEquals(INFO, logs.get(0).getLevel());
        assertEquals("starting to process job " + job, logs.get(0).getFormattedMessage());

        assertEquals(INFO, logs.get(1).getLevel());
        assertEquals("successfully processed job " + job, logs.get(1).getFormattedMessage());
    }

    private JobDto initJobDto() {
        JobDto job = new JobDto();
        job.setId(JOB_ID);
        job.setName(JOB_NAME);
        return job;
    }

    private Message initMessage(JobDto job) {
        return new Message() {
            @Override
            public byte[] getBody() {
                try {
                    return objectMapper.writeValueAsBytes(job);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public byte[] getChannel() {
                return TOPIC_NAME.getBytes(StandardCharsets.UTF_8);
            }
        };
    }
}
