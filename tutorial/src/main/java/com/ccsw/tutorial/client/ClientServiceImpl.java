package com.ccsw.tutorial.client;


import com.ccsw.tutorial.client.model.Client;
import com.ccsw.tutorial.client.model.ClientDto;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@Transactional
public class ClientServiceImpl implements ClientService {

    @Autowired
    ClientRepository clientRepository;

    @Override
    public Client get(Long id) {
        return this.clientRepository.findById(id).orElse(null);
    }

    @Override
    public List<Client> findAll() {
        return (List<Client>) this.clientRepository.findAll();
    }

    @Override
    public void save(Long id, ClientDto dto) {

        Client existing = this.clientRepository.findByName(dto.getName());

        if (existing != null && (!existing.getId().equals(id))) {
            throw new RuntimeException("Client already exists");
        }

        Client client;

        if (id == null) {
            client = new Client();
        } else {
            client = this.clientRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Client not found"));
        }

        client.setName(dto.getName());

        this.clientRepository.save(client);
    }

    @Override
    public void delete(Long id) throws Exception {


        if (this.get(id) == null) {
            throw new Exception("Not exists");
        }
        this.clientRepository.deleteById(id);
    }
}
