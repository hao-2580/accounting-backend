package com.accounting.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("client")
public class Client {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String name;
    private String contact;
    private String phone;
    private String email;
    private String taxNo;
    private String address;
    @TableLogic
    private Integer deleted;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
