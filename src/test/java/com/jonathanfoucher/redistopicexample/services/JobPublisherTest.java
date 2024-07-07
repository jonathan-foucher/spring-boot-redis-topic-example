package com.jonathanfoucher.redistopicexample.services;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.jonathanfoucher.redistopicexample.data.JobDto;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static ch.qos.logback.classic.Level.INFO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@SpringJUnitConfig(JobPublisher.class)
class JobPublisherTest {
    @Autowired
    private JobPublisher jobPublisher;
    @MockBean
    private RedisTemplate<String, Object> redisTemplate;
    @MockBean
    private ChannelTopic channelTopic;

    private static final String TOPIC_NAME = "job_queue";
    private static final Long JOB_ID = 15L;
    private static final String JOB_NAME = "some job name";

    @Test
    void publishJobToTheQueue() {
        // GIVEN
        Logger log = (Logger) LoggerFactory.getLogger(JobPublisher.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        log.addAppender(listAppender);

        JobDto job = initJobDto();

        when(channelTopic.getTopic())
                .thenReturn(TOPIC_NAME);

        // WHEN
        jobPublisher.publish(job);

        // THEN
        ArgumentCaptor<JobDto> capturedJob = ArgumentCaptor.forClass(JobDto.class);
        verify(redisTemplate, times(1))
                .convertAndSend(eq(TOPIC_NAME), capturedJob.capture());

        assertEquals(1, capturedJob.getAllValues().size());
        checkJob(capturedJob.getValue());

        List<ILoggingEvent> logs = listAppender.list;
        assertEquals(1, logs.size());
        assertEquals(INFO, logs.getFirst().getLevel());
        assertEquals("job " + job + " was added to the queue", logs.getFirst().getFormattedMessage());
    }

    private JobDto initJobDto() {
        JobDto job = new JobDto();
        job.setId(JOB_ID);
        job.setName(JOB_NAME);
        return job;
    }

    private void checkJob(JobDto job) {
        assertNotNull(job);
        assertEquals(JOB_ID, job.getId());
        assertEquals(JOB_NAME, job.getName());
    }
}
