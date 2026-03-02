package com.accounting.mapper;

import com.accounting.entity.InvoiceItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InvoiceItemMapper extends BaseMapper<InvoiceItem> {
}
