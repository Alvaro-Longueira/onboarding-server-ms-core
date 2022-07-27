package com.wefox.onboarding.server.ms.core.infrastructure.messagebroker.adapters.kafka.streams.config;

import com.wefox.onboarding.server.ms.core.infrastructure.messagebroker.adapters.kafka.streams.ClaimChangedProcessor;
import com.wefox.server.spec.avro.claims.entity.ClaimDTO;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class InfrastructureKafkaStreamsAutoConfiguration {

  @Bean("claim-changed-processor")
  public Function<KStream<String, ClaimDTO>, KStream<String, ClaimDTO>> claimChangedProcessor() {
    return new ClaimChangedProcessor();
  }
}
