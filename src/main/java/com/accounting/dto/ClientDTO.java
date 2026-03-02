package com.accounting.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ClientDTO {
    @NotBlank(message = "客户名称不能为空")
    private String name;
    private String contact;
    private String phone;
    private String email;
    private String taxNo;
    private String address;
}
