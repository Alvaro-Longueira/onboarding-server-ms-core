package com.wefox.onboarding.server.ms.core.infrastructure.messagebroker.adapters.kafka.streams;

import com.wefox.server.spec.avro.claims.entity.ClaimDTO;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KStream;

@Slf4j
public class ClaimChangedProcessor
    implements Function<KStream<String, ClaimDTO>, KStream<String, ClaimDTO>> {

  // https://github.com/spring-cloud/spring-cloud-stream-samples/tree/main/kafka-streams-samples
  @Override
  public KStream<String, ClaimDTO> apply(KStream<String, ClaimDTO> kStream) {
    return kStream
        .peek((k, v) -> log.debug("r.key={}, r.value.type={}", k, v.getType()))
        .leftJoin(kStream.toTable(), OldAndNew::new)
        .peek((k, v) -> log.debug("r.key={}, r.value.previous={}", k, v.previous()))
        .filter(
            (k, v) -> v.previous() == null || v.current().getStatus() != v.previous().getStatus())
        .mapValues(OldAndNew::current);
  }
}
