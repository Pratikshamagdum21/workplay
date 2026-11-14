package com.example.workPay.service;

import com.example.workPay.Repository.InvoiceRepository;
import com.example.workPay.entities.Invoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAllByOrderByCreatedAtDesc();
    }

    public Invoice createInvoice(Invoice invoice) {
        return invoiceRepository.save(invoice);
    }

    public Invoice updateInvoice(Long id, Invoice invoiceDetails) {
        Optional<Invoice> existingOpt = invoiceRepository.findById(id);
        if (existingOpt.isEmpty()) {
            return null;
        }

        Invoice existing = existingOpt.get();

        existing.setInvoiceNumber(invoiceDetails.getInvoiceNumber());
        existing.setInvoiceDate(invoiceDetails.getInvoiceDate());
        existing.setCustomerName(invoiceDetails.getCustomerName());
        existing.setCustomerAddress(invoiceDetails.getCustomerAddress());
        existing.setCustomerContact(invoiceDetails.getCustomerContact());
        existing.setCustomerGstin(invoiceDetails.getCustomerGstin());
        existing.setCustomerState(invoiceDetails.getCustomerState());
        existing.setQualityName(invoiceDetails.getQualityName());
        existing.setWidth(invoiceDetails.getWidth());
        existing.setFani(invoiceDetails.getFani());
        existing.setPeak(invoiceDetails.getPeak());
        existing.setWarp(invoiceDetails.getWarp());
        existing.setWeft(invoiceDetails.getWeft());
        existing.setRolls(invoiceDetails.getRolls());
        existing.setMeters(invoiceDetails.getMeters());
        existing.setRate(invoiceDetails.getRate());
        existing.setTotalAmount(invoiceDetails.getTotalAmount());
        existing.setCgstAmount(invoiceDetails.getCgstAmount());
        existing.setSgstAmount(invoiceDetails.getSgstAmount());
        existing.setNetPayable(invoiceDetails.getNetPayable());
        existing.setAmountInWords(invoiceDetails.getAmountInWords());

        return invoiceRepository.save(existing);
    }

    public Invoice getInvoiceById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found with id: " + id));
    }

    public List<Invoice> getInvoicesByIds(List<Long> ids) {
        return invoiceRepository.findAllById(ids);
    }

    public boolean deleteInvoice(Long id) {
        if (invoiceRepository.existsById(id)) {
            invoiceRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
