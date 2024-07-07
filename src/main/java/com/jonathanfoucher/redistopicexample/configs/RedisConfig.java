package com.jonathanfoucher.redistopicexample.configs;

import com.jonathanfoucher.redistopicexample.data.JobDto;
import com.jonathanfoucher.redistopicexample.services.JobSubscriber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@Configuration
public class RedisConfig {
    @Value("${redis-topic-example.topic-name}")
    private String topicName;

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(JobDto.class));
        return template;
    }

    @Bean
    public ChannelTopic channelTopic() {
        return new ChannelTopic(topicName);
    }

    @Bean
    public MessageListenerAdapter messageListener() {
        return new MessageListenerAdapter(new JobSubscriber());
    }

    @Bean
    public RedisMessageListenerContainer messageListenerContainer(RedisConnectionFactory connectionFactory,
                                                                  MessageListenerAdapter messageListener,
                                                                  ChannelTopic channelTopic) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(messageListener, channelTopic);
        return container;
    }
}
