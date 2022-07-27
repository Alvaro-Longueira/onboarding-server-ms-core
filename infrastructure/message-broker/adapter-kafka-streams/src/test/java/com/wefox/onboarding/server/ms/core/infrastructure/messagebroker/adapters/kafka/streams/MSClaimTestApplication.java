package com.wefox.onboarding.server.ms.core.infrastructure.messagebroker.adapters.kafka.streams;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MSClaimTestApplication {

  //  TODO: workaround for this issue
  //
  // https://stackoverflow.com/questions/66881210/spring-cloud-stream-kafka-streams-binder-kafkaexception-could-not-start-stream
  @Bean
  public MeterRegistry meterRegistry() {
    return new SimpleMeterRegistry();
  }

  public static void main(String[] args) {
    SpringApplication.run(MSClaimTestApplication.class, args);
  }
}
