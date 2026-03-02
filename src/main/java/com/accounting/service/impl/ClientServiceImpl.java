package com.accounting.service.impl;

import com.accounting.dto.ClientDTO;
import com.accounting.entity.Client;
import com.accounting.exception.BusinessException;
import com.accounting.mapper.ClientMapper;
import com.accounting.service.ClientService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final ClientMapper clientMapper;

    @Override
    public List<Client> listByUser(Long userId) {
        return clientMapper.selectList(
                new LambdaQueryWrapper<Client>().eq(Client::getUserId, userId)
                        .orderByDesc(Client::getId));
    }

    @Override
    public Client create(Long userId, ClientDTO dto) {
        Client client = new Client();
        BeanUtils.copyProperties(dto, client);
        client.setUserId(userId);
        clientMapper.insert(client);
        return client;
    }

    @Override
    public Client update(Long userId, Long id, ClientDTO dto) {
        Client client = getOwned(userId, id);
        BeanUtils.copyProperties(dto, client);
        clientMapper.updateById(client);
        return client;
    }

    @Override
    public void delete(Long userId, Long id) {
        clientMapper.deleteById(getOwned(userId, id).getId());
    }

    private Client getOwned(Long userId, Long id) {
        Client client = clientMapper.selectById(id);
        if (client == null || !client.getUserId().equals(userId))
            throw new BusinessException(404, "客户不存在");
        return client;
    }
}
