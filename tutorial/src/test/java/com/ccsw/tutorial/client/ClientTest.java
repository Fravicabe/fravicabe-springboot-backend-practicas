package com.ccsw.tutorial.client;

import com.ccsw.tutorial.client.model.Client;
import com.ccsw.tutorial.client.model.ClientDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientTest {

    public static final Long EXISTS_CLIENT_ID = 1L;
    public static final Long NOT_EXISTS_CLIENT_ID = 0L;
    public static final String CLIENT_NAME = "CLIENT1";

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientServiceImpl clientService;
    // Testeo de insert
    @Test
    public void saveNotExistsClientIdShouldInsert() {

        ClientDto dto = new ClientDto();
        dto.setName(CLIENT_NAME);

        when(clientRepository.findByName(CLIENT_NAME)).thenReturn(null);

        ArgumentCaptor<Client> clientCaptor = ArgumentCaptor.forClass(Client.class);

        clientService.save(null, dto);

        verify(clientRepository).save(clientCaptor.capture());

        assertEquals(CLIENT_NAME, clientCaptor.getValue().getName());
    }

    // Intento de validación, existe el nombre
    @Test
    public void saveWithExistingNameShouldThrowException() {

        ClientDto dto = new ClientDto();
        dto.setName(CLIENT_NAME);

        when(clientRepository.findByName(CLIENT_NAME))
                .thenReturn(new Client());

        assertThrows(RuntimeException.class, () -> {
            clientService.save(null, dto);
        });
    }

    // Get client que existe
    @Test
    public void getExistsClientIdShouldReturnClient() {

        Client client = mock(Client.class);
        when(client.getId()).thenReturn(EXISTS_CLIENT_ID);
        when(clientRepository.findById(EXISTS_CLIENT_ID)).thenReturn(Optional.of(client));

        Client response = clientService.get(EXISTS_CLIENT_ID);

        assertNotNull(response);
        assertEquals(EXISTS_CLIENT_ID, response.getId());
    }

    // Get client que no existe
    @Test
    public void getNotExistsClientIdShouldReturnNull() {

        when(clientRepository.findById(NOT_EXISTS_CLIENT_ID)).thenReturn(Optional.empty());

        Client response = clientService.get(NOT_EXISTS_CLIENT_ID);

        assertNull(response);
    }

    // Update de un cliente
    @Test
    public void updateSameClientShouldNotThrowException() {

        ClientDto dto = new ClientDto();
        dto.setName(CLIENT_NAME);

        Client existing = new Client();
        existing.setId(EXISTS_CLIENT_ID);

        when(clientRepository.findByName(CLIENT_NAME))
                .thenReturn(existing);

        when(clientRepository.findById(EXISTS_CLIENT_ID))
                .thenReturn(Optional.of(existing));

        assertDoesNotThrow(() -> {
            clientService.save(EXISTS_CLIENT_ID, dto);
        });
    }

    // Update de un cliente al nombre de otro cliente (Fallo)
    @Test
    public void updateWithAnotherClientNameShouldThrowException() {

        ClientDto dto = new ClientDto();
        dto.setName(CLIENT_NAME);

        Client another = new Client();
        another.setId(2L);

        when(clientRepository.findByName(CLIENT_NAME))
                .thenReturn(another);

        assertThrows(RuntimeException.class, () -> {
            clientService.save(EXISTS_CLIENT_ID, dto);
        });
    }
}