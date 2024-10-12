package com.jonathanfoucher.redistopicexample.services;

import com.jonathanfoucher.redistopicexample.data.JobDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic channelTopic;

    public void publish(JobDto jobMessage) {
        redisTemplate.convertAndSend(channelTopic.getTopic(), jobMessage);
        log.info("job {} was added to the queue", jobMessage);
    }
}
