package com.wefox.onboarding.server.ms.core.main.test;

import com.wefox.server.lib.common.stream.kafka.test.MockAvroSerializer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

// not working
public class MockAvroSerde implements Serde<Object> {

  @Override
  public Serializer<Object> serializer() {
    return new MockAvroSerializer();
  }

  @Override
  public Deserializer<Object> deserializer() {
    return new MockAvroDeserializer();
  }
}
