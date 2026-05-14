package com.ccsw.tutorial.client;

import com.ccsw.tutorial.client.model.ClientDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ClientIT {

    public static final String LOCALHOST = "http://localhost:";
    public static final String SERVICE_PATH = "/client";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    ParameterizedTypeReference<List<ClientDto>> responseType =
            new ParameterizedTypeReference<>() {};

    // Read de FindAll
    @Test
    public void findAllShouldReturnAllClients() {

        ResponseEntity<List<ClientDto>> response =
                restTemplate.exchange(LOCALHOST + port + SERVICE_PATH,
                        HttpMethod.GET, null, responseType);

        assertNotNull(response);
        assertEquals(3, response.getBody().size());
    }

    public static final String NEW_CLIENT_NAME = "CLIENT_NEW";

    // Insertamos un cliente
    @Test
    public void saveWithoutIdShouldCreateNewClient() {

        ClientDto dto = new ClientDto();
        dto.setName(NEW_CLIENT_NAME);

        restTemplate.exchange(LOCALHOST + port + SERVICE_PATH,
                HttpMethod.PUT, new HttpEntity<>(dto), Void.class);

        ResponseEntity<List<ClientDto>> response =
                restTemplate.exchange(LOCALHOST + port + SERVICE_PATH,
                        HttpMethod.GET, null, responseType);

        assertEquals(4, response.getBody().size());
    }
    // Insertamos un duplicado esperando error
    @Test
    public void saveDuplicateClientShouldReturnError() {

        ClientDto dto = new ClientDto();
        dto.setName("CLIENT_DUP");

        restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH,
                HttpMethod.PUT,
                new HttpEntity<>(dto),
                Void.class
        );

        ResponseEntity<String> response =
                restTemplate.exchange(
                        LOCALHOST + port + SERVICE_PATH,
                        HttpMethod.PUT,
                        new HttpEntity<>(dto),
                        String.class
                );

        assertTrue(response.getStatusCode().is5xxServerError());
    }
    // Prueba de borrar un cliente
    @Test
    public void deleteExistingClientShouldRemoveClient() {

        ResponseEntity<List<ClientDto>> responseBefore =
                restTemplate.exchange(
                        LOCALHOST + port + SERVICE_PATH,
                        HttpMethod.GET,
                        null,
                        responseType
                );

        assertNotNull(responseBefore);
        assertEquals(3, responseBefore.getBody().size());

        try {
            restTemplate.exchange(
                    "http://localhost:" + port + "/loan/4", // Borra el préstamo de Manuel primero
                    HttpMethod.DELETE,
                    null,
                    Void.class
            );
        } catch (Exception e) {
        }

        restTemplate.exchange(
                LOCALHOST + port + SERVICE_PATH + "/3",
                HttpMethod.DELETE,
                null,
                Void.class
        );

        ResponseEntity<List<ClientDto>> responseAfter =
                restTemplate.exchange(
                        LOCALHOST + port + SERVICE_PATH,
                        HttpMethod.GET,
                        null,
                        responseType
                );

        assertNotNull(responseAfter);
        assertEquals(2, responseAfter.getBody().size()); // Ahora sí devolverá 2 de forma correcta

        ClientDto deletedClient = responseAfter.getBody().stream()
                .filter(c -> c.getId().equals(3L))
                .findFirst()
                .orElse(null);

        assertNull(deletedClient);
    }

    @Test
    public void deleteNotExistingClientShouldReturnError() {

        Long notExistingId = 67L;

        ResponseEntity<String> response =
                restTemplate.exchange(
                        LOCALHOST + port + SERVICE_PATH + "/" + notExistingId,
                        HttpMethod.DELETE,
                        null,
                        String.class
                );

        // Comprobación de error
        assertTrue(response.getStatusCode().is5xxServerError());
    }

}