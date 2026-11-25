package com.corely.corely_backend.service;

import com.corely.corely_backend.dto.request.store.StoreUpdateRequest;
import com.corely.corely_backend.dto.response.store.StoreResponse;
import com.corely.corely_backend.entity.Store;
import com.corely.corely_backend.exception.AppException;
import com.corely.corely_backend.exception.ErrorCode;
import com.corely.corely_backend.mapper.StoreMapper;
import com.corely.corely_backend.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;

    private final StoreMapper storeMapper;

    public StoreResponse getStore() {

        Store store = storeRepository.findTopBy()
                .orElseThrow(() ->
                        new AppException(ErrorCode.STORE_NOT_FOUND));

        return storeMapper.toStoreResponse(store);
    }

    @Transactional
    public StoreResponse updateStore(StoreUpdateRequest request) {

        Store store = storeRepository.findTopBy()
                .orElseThrow(() ->
                        new AppException(ErrorCode.STORE_NOT_FOUND));

        storeMapper.updateStore(request, store);

        return storeMapper.toStoreResponse(
                storeRepository.save(store));
    }
}
