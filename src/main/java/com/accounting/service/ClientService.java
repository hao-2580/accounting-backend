package com.accounting.service;

import com.accounting.dto.ClientDTO;
import com.accounting.entity.Client;
import java.util.List;

public interface ClientService {
    List<Client> listByUser(Long userId);
    Client create(Long userId, ClientDTO dto);
    Client update(Long userId, Long id, ClientDTO dto);
    void delete(Long userId, Long id);
}
