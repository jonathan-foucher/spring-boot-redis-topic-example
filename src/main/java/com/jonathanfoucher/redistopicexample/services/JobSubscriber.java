package com.jonathanfoucher.redistopicexample.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jonathanfoucher.redistopicexample.data.JobDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobSubscriber implements MessageListener {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            JobDto jobMessage = objectMapper.readValue(message.getBody(), JobDto.class);
            processJob(jobMessage);
        } catch (IOException e) {
            log.error("failed to retrieve a job from the queue");
            log.error(e.getMessage());
        }
    }

    private void processJob(JobDto job) {
        log.info("starting to process job {}", job);
        try {
            // simulate running job
            TimeUnit.SECONDS.sleep(10);
            log.info("successfully processed job {}", job);
        } catch (InterruptedException e) {
            log.error("failed to process job {}", job);
            log.error(e.getMessage());
        }
    }
}
