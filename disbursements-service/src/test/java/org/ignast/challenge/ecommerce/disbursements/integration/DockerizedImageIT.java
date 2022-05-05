package org.ignast.challenge.ecommerce.disbursements.integration;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public class DockerizedImageIT {

    private static final int APP_PORT = 8080;

    private static final int BAD_REQUEST = 400;

    @Container
    @SuppressWarnings("rawtypes")
    private static final GenericContainer APP = new GenericContainer(
        DockerImageName.parse(System.getProperty("app.docker.image"))
    )
        .withExposedPorts(APP_PORT);

    @Test
    public void shouldProvideRootResource() throws IOException, InterruptedException {
        final val client = HttpClient.newHttpClient();
        final val port = APP.getMappedPort(APP_PORT);
        final val uri = format(
            "http://localhost:%d/disbursements?timeFrameEndingBefore=2022-01-01&timeFrameLength=1week",
            port
        );
        final val request = HttpRequest
            .newBuilder()
            .uri(URI.create(uri))
            .header("Accept", "application/json")
            .build();

        final val response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST);
    }
}
