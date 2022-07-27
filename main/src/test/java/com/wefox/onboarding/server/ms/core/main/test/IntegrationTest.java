package com.wefox.onboarding.server.ms.core.main.test;

import com.wefox.onboarding.server.ms.core.main.OnboardingMSCoreApplication;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootTest(
    classes = {OnboardingMSCoreApplication.class},
    webEnvironment = WebEnvironment.RANDOM_PORT,
    properties = {
      "spring.cloud.stream.kafka.binder.brokers=${spring.embedded.kafka.brokers}",
      "spring.cloud.stream.kafka.binder.replication-factor=1",
      "spring.cloud.stream.kafka.binder.configuration.security.protocol=PLAINTEXT",
      "spring.cloud.stream.kafka.streams.binder.brokers=${spring.embedded.kafka.brokers}",
      "spring.cloud.stream.kafka.streams.binder.replication-factor=1",
      "spring.cloud.stream.kafka.streams.binder.configuration.security.protocol=PLAINTEXT",
      "spring.cloud.stream.kafka.binder.producer-properties.schema.registry.url=mock://mock-registry",
      "spring.cloud.stream.kafka.binder.consumer-properties.schema.registry.url=mock://mock-registry",
      "spring.cloud.stream.binders.kafka.type=kafka",
      "spring.cloud.stream.binders.kstream.type=kstream",
      "spring.cloud.stream.binders.kstream.environment.spring.cloud.stream.kafka.streams.binder.brokers=${spring.embedded.kafka.brokers}",
      "spring.cloud.stream.binders.kstream.environment.spring.cloud.stream.kafka.streams.binder.replication-factor=1",
      "spring.cloud.stream.binders.kstream.environment.spring.cloud.stream.kafka.streams.binder.configuration.security.protocol=PLAINTEXT",
      "spring.cloud.stream.binders.kstream.environment.spring.cloud.stream.kafka.streams.binder.configuration.default.value.serde=io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde",
      "spring.cloud.stream.binders.kstream.environment.spring.cloud.stream.kafka.streams.binder.configuration.schema.registry.url=mock://mock-registry",
      "wefox.common.web.client.configs.account-service.base-url=http://localhost:${wiremock.server.port}/",
      "wefox.common.web.client.configs.account-service.security=NONE",
      "wefox.common.web.client.configs.contract-service.base-url=http://localhost:${wiremock.server.port}/",
      "wefox.common.web.client.configs.contract-service.security=NONE",
      // disable security on /v1 url. TODO enable security and adapt ClaimControllerIT to send token
      "wefox.common.web.security.public-urls=/**,/actuator/**",
      // logging
      "logging.level.com.wefox.onboarding.server.ms.core=debug",
      "logging.level.org.springframework.kafka=warn",
      "logging.level.org.springframework.cloud=debug",
      "logging.level.org.springframework.integration=debug",
      "logging.level.kafka=warn",
      // solve problem with Apple M1
      // https://stackoverflow.com/questions/60074168/java-lang-illegalstateexception-no-server-alpnprocessors-wiremock
      "wiremock.server.httpsPort=-1",
    })
@EmbeddedKafka(
    partitions = 1,
    controlledShutdown = true,
    topics = {"topic-claim-changed-event"},
    brokerProperties = {
      "transaction.state.log.replication.factor=1",
      "transaction.state.log.min.isr=1"
    })
@AutoConfigureWireMock(port = 0)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public @interface IntegrationTest {}
