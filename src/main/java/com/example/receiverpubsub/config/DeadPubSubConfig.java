package com.example.receiverpubsub.config;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.AckMode;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
public class DeadPubSubConfig {

    @Bean
    public MessageChannel deadInputChannel(){
        return new DirectChannel();
    }

    @Bean
    public PubSubInboundChannelAdapter deadPubSubInboundChannelAdapter(@Qualifier("deadInputChannel") MessageChannel deadInputChannel, PubSubTemplate pubSubTemplate){
        PubSubInboundChannelAdapter pubSubInboundChannelAdapter =  new PubSubInboundChannelAdapter(pubSubTemplate, "deadSubscription");
        pubSubInboundChannelAdapter.setOutputChannel(deadInputChannel);
        pubSubInboundChannelAdapter.setAckMode(AckMode.MANUAL);
        return pubSubInboundChannelAdapter;
    }

    @Bean
    @ServiceActivator(inputChannel = "deadInputChannel")
    public MessageHandler receiveDeadPubSubMessage(){
        return message -> {
            String deadMessage = new String((byte[])message.getPayload());
            BasicAcknowledgeablePubsubMessage basicAcknowledgeablePubsubMessage = message.getHeaders().get(GcpPubSubHeaders.ORIGINAL_MESSAGE, BasicAcknowledgeablePubsubMessage.class);
            basicAcknowledgeablePubsubMessage.ack();
            System.out.println("Dead Message Received: "+deadMessage);
        };
    }
}
