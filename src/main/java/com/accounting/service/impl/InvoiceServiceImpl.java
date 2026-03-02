package com.accounting.service.impl;

import com.accounting.common.PageResult;
import com.accounting.dto.request.InvoiceQueryRequest;
import com.accounting.dto.request.InvoiceRequest;
import com.accounting.dto.response.InvoiceItemResponse;
import com.accounting.dto.response.InvoiceResponse;
import com.accounting.entity.Invoice;
import com.accounting.entity.InvoiceItem;
import com.accounting.exception.ResourceNotFoundException;
import com.accounting.mapper.InvoiceItemMapper;
import com.accounting.mapper.InvoiceMapper;
import com.accounting.service.InvoiceService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceMapper invoiceMapper;
    private final InvoiceItemMapper invoiceItemMapper;

    @Override
    public PageResult<InvoiceResponse> query(InvoiceQueryRequest req) {
        Page<Invoice> page = new Page<>(req.getPage() + 1, req.getSize());
        
        LambdaQueryWrapper<Invoice> wrapper = new LambdaQueryWrapper<>();
        if (req.getStatus() != null) {
            wrapper.eq(Invoice::getStatus, req.getStatus());
        }
        if (req.getQuery() != null && !req.getQuery().isEmpty()) {
            wrapper.and(w -> w.like(Invoice::getClientName, req.getQuery())
                    .or().like(Invoice::getInvoiceNo, req.getQuery()));
        }
        wrapper.orderByDesc(Invoice::getIssueDate, Invoice::getId);
        
        Page<Invoice> result = invoiceMapper.selectPage(page, wrapper);
        List<InvoiceResponse> content = result.getRecords().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        
        return new PageResult<>(content, (int) result.getTotal(), (int) result.getCurrent() - 1, (int) result.getSize());
    }

    @Override
    public InvoiceResponse getById(Long id) {
        return toResponse(findById(id));
    }

    @Override
    public List<InvoiceResponse> getOverdue() {
        LambdaQueryWrapper<Invoice> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Invoice::getStatus, "UNPAID", "PARTIAL")
                .lt(Invoice::getDueDate, LocalDate.now())
                .orderByAsc(Invoice::getDueDate);
        
        return invoiceMapper.selectList(wrapper).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public InvoiceResponse create(InvoiceRequest req) {
        Invoice invoice = new Invoice();
        invoice.setInvoiceNo("INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        invoice.setClientName(req.getClientName());
        invoice.setIssueDate(req.getIssueDate());
        invoice.setDueDate(req.getDueDate());
        invoice.setTaxRate(req.getTaxRate());
        invoice.setStatus(req.getStatus() != null ? req.getStatus().name() : "DRAFT");
        invoice.setRemark(req.getRemark());
        
        invoiceMapper.insert(invoice);

        // 保存明细
        if (req.getItems() != null) {
            req.getItems().forEach(item -> {
                InvoiceItem invoiceItem = new InvoiceItem();
                invoiceItem.setInvoiceId(invoice.getId());
                invoiceItem.setDescription(item.getDescription());
                invoiceItem.setQuantity(item.getQuantity());
                invoiceItem.setUnitPrice(item.getUnitPrice());
                invoiceItemMapper.insert(invoiceItem);
            });
        }

        return toResponse(invoice);
    }

    @Override
    @Transactional
    public InvoiceResponse update(Long id, InvoiceRequest req) {
        Invoice invoice = findById(id);
        invoice.setClientName(req.getClientName());
        invoice.setIssueDate(req.getIssueDate());
        invoice.setDueDate(req.getDueDate());
        invoice.setTaxRate(req.getTaxRate());
        invoice.setStatus(req.getStatus() != null ? req.getStatus().name() : invoice.getStatus());
        invoice.setRemark(req.getRemark());
        
        invoiceMapper.updateById(invoice);

        // 删除旧明细，插入新明细
        invoiceItemMapper.delete(new LambdaQueryWrapper<InvoiceItem>().eq(InvoiceItem::getInvoiceId, id));
        if (req.getItems() != null) {
            req.getItems().forEach(item -> {
                InvoiceItem invoiceItem = new InvoiceItem();
                invoiceItem.setInvoiceId(invoice.getId());
                invoiceItem.setDescription(item.getDescription());
                invoiceItem.setQuantity(item.getQuantity());
                invoiceItem.setUnitPrice(item.getUnitPrice());
                invoiceItemMapper.insert(invoiceItem);
            });
        }

        return toResponse(invoice);
    }

    @Override
    @Transactional
    public InvoiceResponse updateStatus(Long id, Invoice.InvoiceStatus status) {
        Invoice invoice = findById(id);
        invoice.setStatus(status.name());
        invoiceMapper.updateById(invoice);
        return toResponse(invoice);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        findById(id);
        invoiceItemMapper.delete(new LambdaQueryWrapper<InvoiceItem>().eq(InvoiceItem::getInvoiceId, id));
        invoiceMapper.deleteById(id);
    }

    private Invoice findById(Long id) {
        Invoice invoice = invoiceMapper.selectById(id);
        if (invoice == null) {
            throw new ResourceNotFoundException("发票", id);
        }
        return invoice;
    }

    private InvoiceResponse toResponse(Invoice inv) {
        List<InvoiceItem> items = invoiceItemMapper.selectList(
                new LambdaQueryWrapper<InvoiceItem>().eq(InvoiceItem::getInvoiceId, inv.getId()));
        
        BigDecimal subtotal = items.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal taxAmount = subtotal.multiply(inv.getTaxRate()).divide(BigDecimal.valueOf(100));
        BigDecimal totalAmount = subtotal.add(taxAmount);
        
        InvoiceResponse r = new InvoiceResponse();
        r.setId(inv.getId());
        r.setInvoiceNo(inv.getInvoiceNo());
        r.setClientName(inv.getClientName());
        r.setIssueDate(inv.getIssueDate());
        r.setDueDate(inv.getDueDate());
        r.setTaxRate(inv.getTaxRate());
        r.setStatus(Invoice.InvoiceStatus.valueOf(inv.getStatus()));
        r.setRemark(inv.getRemark());
        r.setSubtotal(subtotal);
        r.setTaxAmount(taxAmount);
        r.setTotalAmount(totalAmount);
        r.setOverdue(inv.getDueDate().isBefore(LocalDate.now()) && 
                     ("UNPAID".equals(inv.getStatus()) || "PARTIAL".equals(inv.getStatus())));
        r.setItems(items.stream().map(this::toItemResponse).collect(Collectors.toList()));
        r.setCreatedAt(inv.getCreatedAt());
        return r;
    }

    private InvoiceItemResponse toItemResponse(InvoiceItem item) {
        InvoiceItemResponse r = new InvoiceItemResponse();
        r.setId(item.getId());
        r.setDescription(item.getDescription());
        r.setQuantity(item.getQuantity());
        r.setUnitPrice(item.getUnitPrice());
        r.setSubtotal(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        return r;
    }
}
