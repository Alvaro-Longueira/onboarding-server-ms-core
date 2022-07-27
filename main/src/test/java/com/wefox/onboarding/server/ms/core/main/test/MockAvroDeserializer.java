package com.wefox.onboarding.server.ms.core.main.test;

import static com.wefox.onboarding.server.ms.core.infrastructure.messagebroker.adapters.kafka.Bindings.topic;

import com.wefox.onboarding.server.ms.core.infrastructure.messagebroker.adapters.kafka.Bindings;
import com.wefox.server.spec.avro.claims.entity.ClaimDTO;
import io.confluent.kafka.schemaregistry.ParsedSchema;
import io.confluent.kafka.schemaregistry.avro.AvroSchema;
import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import java.util.HashMap;
import java.util.Map;
import org.apache.avro.Schema;

public class MockAvroDeserializer extends KafkaAvroDeserializer {

  private static final Map<String, Schema> schemaByTopic =
      new HashMap<>() {
        {
          put(topic(Bindings.CLAIM_CHANGED_EVENT), ClaimDTO.SCHEMA$);
          put("topic-claim-status-changed-event", ClaimDTO.SCHEMA$);
        }
      };

  public MockAvroDeserializer() {
    this.useSpecificAvroReader = true; // this was the key :)
  }

  @Override
  public Object deserialize(String topic, byte[] bytes) {
    this.schemaRegistry = getMockClient(schemaByTopic.get(topic));
    return super.deserialize(topic, bytes);
  }

  private static SchemaRegistryClient getMockClient(final Schema schema$) {
    return new MockSchemaRegistryClient() {
      @Override
      public ParsedSchema getSchemaById(int id) {
        return new AvroSchema(schema$);
      }
    };
  }
}
