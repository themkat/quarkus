package io.quarkus.opentelemetry.deployment;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.opentelemetry.runtime.config.build.OTelBuildConfig;
import io.quarkus.opentelemetry.runtime.config.build.exporter.OtlpExporterBuildConfig;
import io.quarkus.opentelemetry.runtime.config.runtime.OTelRuntimeConfig;
import io.quarkus.opentelemetry.runtime.config.runtime.exporter.OtlpExporterRuntimeConfig;
import io.quarkus.test.QuarkusUnitTest;

class OpenTelemetryLegacyConfigurationTest {
    @RegisterExtension
    static final QuarkusUnitTest TEST = new QuarkusUnitTest()
            .overrideConfigKey("quarkus.opentelemetry.enabled", "false")
            .overrideConfigKey("quarkus.opentelemetry.tracer.enabled", "false")
            .overrideConfigKey("quarkus.opentelemetry.tracer.suppress-non-application-uris", "false")
            .overrideConfigKey("quarkus.opentelemetry.tracer.include-static-resources", "true")
            .overrideConfigKey("quarkus.opentelemetry.tracer.sampler", "off")
            .overrideConfigKey("quarkus.opentelemetry.tracer.sampler.ratio", "2.0d")
            .overrideConfigKey("quarkus.opentelemetry.tracer.exporter.otlp.headers", "header=value")
            .overrideConfigKey("quarkus.opentelemetry.tracer.exporter.otlp.enabled", "false")
            .overrideConfigKey("quarkus.opentelemetry.tracer.exporter.otlp.endpoint", "http://localhost:4318/");

    @Inject
    OTelBuildConfig oTelBuildConfig;
    @Inject
    OTelRuntimeConfig oTelRuntimeConfig;
    @Inject
    OtlpExporterBuildConfig otlpExporterBuildConfig;
    @Inject
    OtlpExporterRuntimeConfig otlpExporterRuntimeConfig;

    @Test
    void config() {
        assertEquals(FALSE, oTelBuildConfig.enabled());
        assertTrue(oTelBuildConfig.traces().enabled().isPresent());
        assertEquals(FALSE, oTelBuildConfig.traces().enabled().get());
        assertEquals(FALSE, oTelRuntimeConfig.traces().suppressNonApplicationUris());
        assertEquals(TRUE, oTelRuntimeConfig.traces().includeStaticResources());
        assertEquals("always_off", oTelBuildConfig.traces().sampler());
        assertTrue(oTelRuntimeConfig.traces().samplerArg().isPresent());
        assertEquals(2.0d, oTelRuntimeConfig.traces().samplerArg().get());
        assertEquals(FALSE, otlpExporterBuildConfig.enabled());
        assertTrue(otlpExporterRuntimeConfig.traces().legacyEndpoint().isPresent());
        assertTrue(otlpExporterRuntimeConfig.traces().headers().isPresent());
        assertEquals("header=value", otlpExporterRuntimeConfig.traces().headers().get().get(0));
        assertEquals("http://localhost:4318/", otlpExporterRuntimeConfig.traces().legacyEndpoint().get());
    }
}
