package com.jonathanfoucher.redistopicexample.services;

import com.jonathanfoucher.redistopicexample.data.JobDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic channelTopic;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public void publish(JobDto jobMessage) {
        redisTemplate.convertAndSend(channelTopic.getTopic(), jobMessage);
        log.info("job {} was added to the queue", jobMessage);
    }
}
