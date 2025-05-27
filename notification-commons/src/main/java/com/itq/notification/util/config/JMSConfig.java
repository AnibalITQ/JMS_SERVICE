package com.itq.notification.util.config;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.Queue;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;

@Configuration
public class JMSConfig {
    
    @Value("${jms.broker.url:tcp://localhost:61616}")
    private String brokerUrl;
    
    public static final String NOTIFICATION_QUEUE_IN = "notification.queue.in";
    public static final String NOTIFICATION_QUEUE_OUT = "notification.queue.out";
    public static final String PRIORITY_QUEUE = "notification.queue.priority";
    public static final String GUARD_QUEUE = "notification.queue.guard";
    
    @Bean
    public ConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
        factory.setUser("admin");
        factory.setPassword("admin");
        return factory;
    }
    
    @Bean
    public JmsTemplate jmsTemplate() {
        JmsTemplate template = new JmsTemplate();
        template.setConnectionFactory(connectionFactory());
        template.setDeliveryPersistent(true);
        template.setExplicitQosEnabled(true);
        return template;
    }
    
    @Bean(name = "notificationQueueIn")
    public Queue notificationQueueIn() {
        return new ActiveMQQueue(NOTIFICATION_QUEUE_IN);
    }
    
    @Bean(name = "notificationQueueOut")
    public Queue notificationQueueOut() {
        return new ActiveMQQueue(NOTIFICATION_QUEUE_OUT);
    }
    
    @Bean(name = "priorityQueue")
    public Queue priorityQueue() {
        return new ActiveMQQueue(PRIORITY_QUEUE);
    }
    
    @Bean(name = "guardQueue")
    public Queue guardQueue() {
        return new ActiveMQQueue(GUARD_QUEUE);
    }
}