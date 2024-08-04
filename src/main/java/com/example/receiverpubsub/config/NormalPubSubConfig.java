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
public class NormalPubSubConfig {

    @Bean
    public MessageChannel outputChannel() {
        return new DirectChannel();
    }

    @Bean
    public PubSubInboundChannelAdapter pubSubInboundChannelAdapter(@Qualifier("outputChannel") MessageChannel outputChannel,
                                                                   PubSubTemplate pubSubTemplate) {
        PubSubInboundChannelAdapter pubSubInboundChannelAdapter = new PubSubInboundChannelAdapter(pubSubTemplate,"newSubscription");
        pubSubInboundChannelAdapter.setOutputChannel(outputChannel);
        pubSubInboundChannelAdapter.setAckMode(AckMode.MANUAL);
        return pubSubInboundChannelAdapter;
    }

    @Bean
    @ServiceActivator(inputChannel = "outputChannel")
    public MessageHandler receiveMessage() {
        return message -> {
            System.out.println("New Message Received = "+new String((byte[])message.getPayload()));
            BasicAcknowledgeablePubsubMessage basicAcknowledgeablePubsubMessage = message.getHeaders().get(GcpPubSubHeaders.ORIGINAL_MESSAGE, BasicAcknowledgeablePubsubMessage.class);
            basicAcknowledgeablePubsubMessage.ack();
        };
    }
}
