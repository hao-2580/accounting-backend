package com.accounting.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CustomerDTO {

    @NotBlank(message = "客户名称不能为空")
    private String name;

    private String contact;
    private String phone;

    @Email(message = "邮箱格式不正确")
    private String email;

    private String taxId;
    private String address;
}
