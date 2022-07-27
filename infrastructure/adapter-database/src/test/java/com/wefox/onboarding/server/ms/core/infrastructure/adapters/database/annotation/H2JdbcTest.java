package com.wefox.onboarding.server.ms.core.infrastructure.adapters.database.annotation;

import com.wefox.onboarding.server.ms.core.infrastructure.adapters.database.config.InfrastructureDatabaseAutoConfiguration;
import com.wefox.server.lib.common.data.core.config.DataCoreAutoConfiguration;
import com.wefox.server.lib.common.data.jdbc.config.DataJdbcAutoConfiguration;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ActiveProfiles("h2")
@DirtiesContext
@ContextConfiguration(
    classes = {
      InfrastructureDatabaseAutoConfiguration.class,
      DataCoreAutoConfiguration.class,
      DataJdbcAutoConfiguration.class
    })
@DataJdbcTest(
    properties = {
      "logging.level.org.springframework.jdbc=debug",
      "logging.level.com.wefox.onboarding.server.ms.core.infrastructure.adapters.database=debug"
    })
@AutoConfigureTestDatabase(replace = Replace.NONE)
public @interface H2JdbcTest {}
