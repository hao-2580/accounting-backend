package com.accounting.exception;

public class ResourceNotFoundException extends BusinessException {
    public ResourceNotFoundException(String resource, Long id) {
        super(404, resource + " 不存在，ID: " + id);
    }
}
