package com.example.workPay.service;

import com.example.workPay.entities.BusinessInfo;
import com.example.workPay.entities.Invoice;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class InvoicePdfService {

    private static final DeviceRgb HEADER_BG = new DeviceRgb(44, 62, 80);
    private static final DeviceRgb HEADER_TEXT = new DeviceRgb(255, 255, 255);
    private static final DeviceRgb SECTION_BG = new DeviceRgb(236, 240, 241);
    private static final DeviceRgb ACCENT_COLOR = new DeviceRgb(41, 128, 185);
    private static final float FONT_SIZE_TITLE = 14f;
    private static final float FONT_SIZE_SECTION = 10f;
    private static final float FONT_SIZE_NORMAL = 9f;
    private static final float FONT_SIZE_SMALL = 8f;

    public byte[] generateSingleInvoicePdf(Invoice invoice, BusinessInfo business) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.setDefaultPageSize(PageSize.A4);
        Document document = new Document(pdfDoc, PageSize.A4);
        document.setMargins(15, 20, 15, 20);

        Div invoiceDiv = new Div();
        addInvoiceContent(invoiceDiv, invoice, business, PageSize.A4.getWidth() - 40);
        document.add(invoiceDiv);

        document.close();
        return baos.toByteArray();
    }
    public byte[] generateBulkInvoicePdf(List<Invoice> invoices, BusinessInfo business) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.setDefaultPageSize(PageSize.A4);
        Document document = new Document(pdfDoc, PageSize.A4);
        document.setMargins(15, 20, 15, 20);

        float contentWidth = PageSize.A4.getWidth() - 40;
        int total = invoices.size();

        for (int i = 0; i < total; i += 2) {
            Div slot1 = new Div();
            addCompactInvoiceContent(slot1, invoices.get(i), business, contentWidth);
            document.add(slot1);

            if (i + 1 < total) {
                addSeparator(document, 8, 8);
                Div slot2 = new Div();
                addCompactInvoiceContent(slot2, invoices.get(i + 1), business, contentWidth);
                document.add(slot2);
            }

            boolean moreInvoicesRemain = (i + 2) < total;
            if (moreInvoicesRemain) {
                document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            }
        }

        document.close();
        return baos.toByteArray();
    }

    private void addSeparator(Document document, float marginTop, float marginBottom) {
        Table separator = new Table(1).useAllAvailableWidth();
        separator.addCell(new Cell()
                .add(new Paragraph(""))
                .setHeight(1)
                .setBorderTop(new SolidBorder(ColorConstants.DARK_GRAY, 1f))
                .setBorderBottom(Border.NO_BORDER)
                .setBorderLeft(Border.NO_BORDER)
                .setBorderRight(Border.NO_BORDER)
                .setMarginTop(marginTop)
                .setMarginBottom(marginBottom));
        document.add(separator);
    }

    private void addInvoiceContent(Div container, Invoice invoice, BusinessInfo business, float width) {
        // === HEADER: Tax Invoice Title ===
        Table headerTable = new Table(1).useAllAvailableWidth();
        headerTable.addCell(new Cell()
                .add(new Paragraph("TAX INVOICE")
                        .setFontSize(10)
                        .setBold()
                        .setFontColor(HEADER_TEXT)
                        .setTextAlignment(TextAlignment.CENTER))
                .setBackgroundColor(HEADER_BG)
                .setPadding(5)
                .setBorder(Border.NO_BORDER));
        container.add(headerTable);

        // === BUSINESS INFO SECTION ===
        if (business != null) {
            Table bizTable = new Table(1).useAllAvailableWidth();
            bizTable.addCell(new Cell()
                    .add(new Paragraph(safe(business.getBusinessName()))
                            .setFontSize(22).setBold().setTextAlignment(TextAlignment.CENTER)
                            .setFontColor(ACCENT_COLOR))
                    .add(new Paragraph(safe(business.getAddress()))
                            .setFontSize(FONT_SIZE_NORMAL).setTextAlignment(TextAlignment.CENTER))
                    .add(new Paragraph("GSTIN: " + safe(business.getGstin()) + "  |  State: " + safe(business.getState())
                            + "  |  Phone: " + safe(business.getPhoneNumber()))
                            .setFontSize(FONT_SIZE_NORMAL).setTextAlignment(TextAlignment.CENTER))
                    .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
                    .setPaddingTop(4).setPaddingBottom(4));
            container.add(bizTable);
        }

        // === INVOICE INFO & CUSTOMER INFO (side by side) ===
        Table infoTable = new Table(UnitValue.createPercentArray(new float[]{50, 50})).useAllAvailableWidth();

        Cell invoiceCell = new Cell()
                .add(new Paragraph("Invoice Details").setFontSize(FONT_SIZE_SECTION).setBold()
                        .setFontColor(ACCENT_COLOR))
                .add(new Paragraph("Invoice No: " + safe(invoice.getInvoiceNumber()))
                        .setFontSize(FONT_SIZE_NORMAL))
                .add(new Paragraph("Date: " + safe(invoice.getInvoiceDate()))
                        .setFontSize(FONT_SIZE_NORMAL))
                .setPadding(4)
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f));
        infoTable.addCell(invoiceCell);

        Cell customerCell = new Cell()
                .add(new Paragraph("Bill To").setFontSize(FONT_SIZE_SECTION).setBold()
                        .setFontColor(ACCENT_COLOR))
                .add(new Paragraph(safe(invoice.getCustomerName()))
                        .setFontSize(FONT_SIZE_NORMAL).setBold())
                .add(new Paragraph(safe(invoice.getCustomerAddress()))
                        .setFontSize(FONT_SIZE_NORMAL))
                .add(new Paragraph("Contact: " + safe(invoice.getCustomerContact()))
                        .setFontSize(FONT_SIZE_NORMAL))
                .add(new Paragraph("GSTIN: " + safe(invoice.getCustomerGstin()))
                        .setFontSize(FONT_SIZE_NORMAL))
                .add(new Paragraph("State: " + safe(invoice.getCustomerState()))
                        .setFontSize(FONT_SIZE_NORMAL))
                .setPadding(4)
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f));
        infoTable.addCell(customerCell);

        container.add(infoTable);

        // === FABRIC QUALITY DETAILS TABLE ===
        container.add(new Paragraph("Fabric Quality Details")
                .setFontSize(FONT_SIZE_SECTION).setBold().setFontColor(ACCENT_COLOR)
                .setMarginTop(4).setMarginBottom(2));

        Table qualityTable = new Table(UnitValue.createPercentArray(new float[]{25, 15, 15, 15, 15, 15}))
                .useAllAvailableWidth();
        addTableHeader(qualityTable, "Quality Name", "Width", "Reed", "Peak", "Warp", "Weft");
        addTableRow(qualityTable,
                safe(invoice.getQualityName()),
                safe(invoice.getWidth()),
                safe(invoice.getFani()),
                safe(invoice.getPeak()),
                safe(invoice.getWarp()),
                safe(invoice.getWeft()));
        container.add(qualityTable);

        // === QUANTITY & RATE TABLE ===
        container.add(new Paragraph("Quantity & Rate")
                .setFontSize(FONT_SIZE_SECTION).setBold().setFontColor(ACCENT_COLOR)
                .setMarginTop(4).setMarginBottom(2));

        Table qtyTable = new Table(UnitValue.createPercentArray(new float[]{20, 20, 20, 20, 20}))
                .useAllAvailableWidth();
        addTableHeader(qtyTable, "Sr. No.", "Rolls", "Meters", "Rate (₹)", "Amount (₹)");
        addTableRow(qtyTable, "1",
                String.valueOf(invoice.getRolls() != null ? invoice.getRolls() : 0),
                String.format("%.2f", invoice.getMeters() != null ? invoice.getMeters() : 0.0),
                String.format("%.2f", invoice.getRate() != null ? invoice.getRate() : 0.0),
                String.format("%.2f", invoice.getTotalAmount() != null ? invoice.getTotalAmount() : 0.0));
        container.add(qtyTable);

        // === AMOUNT SUMMARY (grouped with the words/footer below so an overflow,
        //     if it happens, forces a break BEFORE this section instead of mid-table
        //     at the Net Payable row) ===
        Div summaryGroup = new Div();
        summaryGroup.setKeepTogether(true);

        summaryGroup.add(new Paragraph("Amount Summary")
                .setFontSize(FONT_SIZE_SECTION).setBold().setFontColor(ACCENT_COLOR)
                .setMarginTop(4).setMarginBottom(2));

        Table amountTable = new Table(UnitValue.createPercentArray(new float[]{60, 40}))
                .useAllAvailableWidth();

        addAmountRow(amountTable, "Total Amount", formatAmount(invoice.getTotalAmount()));
        addAmountRow(amountTable, "CGST", formatAmount(invoice.getCgstAmount()));
        addAmountRow(amountTable, "SGST", formatAmount(invoice.getSgstAmount()));

        Cell labelCell = new Cell()
                .add(new Paragraph("Net Payable").setFontSize(FONT_SIZE_SECTION).setBold()
                        .setFontColor(HEADER_TEXT))
                .setBackgroundColor(HEADER_BG)
                .setTextAlignment(TextAlignment.RIGHT)
                .setPadding(3)
                .setBorder(Border.NO_BORDER);
        Cell valueCell = new Cell()
                .add(new Paragraph("₹ " + formatAmount(invoice.getNetPayable()))
                        .setFontSize(FONT_SIZE_SECTION).setBold().setFontColor(HEADER_TEXT))
                .setBackgroundColor(HEADER_BG)
                .setTextAlignment(TextAlignment.RIGHT)
                .setPadding(3)
                .setBorder(Border.NO_BORDER);
        amountTable.addCell(labelCell);
        amountTable.addCell(valueCell);

        summaryGroup.add(amountTable);

        // === AMOUNT IN WORDS ===
        Table wordsTable = new Table(1).useAllAvailableWidth();
        wordsTable.addCell(new Cell()
                .add(new Paragraph("Amount in Words: " + safe(invoice.getAmountInWords()))
                        .setFontSize(FONT_SIZE_NORMAL).setItalic())
                .setBackgroundColor(SECTION_BG)
                .setPadding(4)
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f)));
        summaryGroup.add(wordsTable);

        // === FOOTER ===
        Table footerTable = new Table(UnitValue.createPercentArray(new float[]{50, 50})).useAllAvailableWidth();
        footerTable.addCell(new Cell()
                .add(new Paragraph("Terms & Conditions:")
                        .setFontSize(FONT_SIZE_SMALL).setBold())
                .add(new Paragraph("1. Goods once sold will not be taken back.\n2. Subject to local jurisdiction.")
                        .setFontSize(FONT_SIZE_SMALL))
                .setPadding(4)
                .setBorder(Border.NO_BORDER));
        footerTable.addCell(new Cell()
                .add(new Paragraph("For " + (business != null ? safe(business.getBusinessName()) : ""))
                        .setFontSize(FONT_SIZE_SMALL).setBold().setTextAlignment(TextAlignment.RIGHT))
                .add(new Paragraph("\nAuthorized Signatory")
                        .setFontSize(FONT_SIZE_SMALL).setTextAlignment(TextAlignment.RIGHT))
                .setPadding(4)
                .setBorder(Border.NO_BORDER));
        summaryGroup.add(footerTable);

        container.add(summaryGroup);
    }

    /**
     * Compact renderer used for bulk PDFs — sized so two of these plus one
     * separator fit inside a single A4 page with the 15/20/15/20 margins set above.
     */
    private void addCompactInvoiceContent(Div container, Invoice invoice, BusinessInfo business, float width) {
        // === HEADER ===
        Table headerTable = new Table(1).useAllAvailableWidth();
        headerTable.addCell(new Cell()
                .add(new Paragraph("TAX INVOICE")
                        .setFontSize(FONT_SIZE_TITLE - 3)
                        .setBold()
                        .setFontColor(HEADER_TEXT)
                        .setTextAlignment(TextAlignment.CENTER))
                .setBackgroundColor(HEADER_BG)
                .setPadding(4)
                .setBorder(Border.NO_BORDER));
        container.add(headerTable);

        // === BUSINESS INFO ===
        if (business != null) {
            Table bizTable = new Table(1).useAllAvailableWidth();
            bizTable.addCell(new Cell()
                    .add(new Paragraph(safe(business.getBusinessName()))
                            .setFontSize(13).setBold().setTextAlignment(TextAlignment.CENTER)
                            .setFontColor(ACCENT_COLOR))
                    .add(new Paragraph(safe(business.getAddress()) + "  |  GSTIN: " + safe(business.getGstin())
                            + "  |  Ph: " + safe(business.getPhoneNumber()))
                            .setFontSize(FONT_SIZE_SMALL).setTextAlignment(TextAlignment.CENTER))
                    .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
                    .setPaddingTop(3).setPaddingBottom(3));
            container.add(bizTable);
        }

        // === INVOICE + CUSTOMER INFO ===
        Table infoTable = new Table(UnitValue.createPercentArray(new float[]{50, 50})).useAllAvailableWidth();
        Cell invoiceCell = new Cell()
                .add(new Paragraph("Invoice No: " + safe(invoice.getInvoiceNumber()))
                        .setFontSize(FONT_SIZE_SMALL))
                .add(new Paragraph("Date: " + safe(invoice.getInvoiceDate()))
                        .setFontSize(FONT_SIZE_SMALL))
                .setPadding(3)
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f));
        infoTable.addCell(invoiceCell);

        Cell customerCell = new Cell()
                .add(new Paragraph("To: " + safe(invoice.getCustomerName()))
                        .setFontSize(FONT_SIZE_SMALL).setBold().setCharacterSpacing(1f))
                .add(new Paragraph(safe(invoice.getCustomerAddress()) + " | State: " + safe(invoice.getCustomerState()))
                        .setFontSize(FONT_SIZE_SMALL))
                .add(new Paragraph("GSTIN: " + safe(invoice.getCustomerGstin()))
                        .setFontSize(FONT_SIZE_SMALL))
                .setPadding(3)
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f));
        infoTable.addCell(customerCell);
        container.add(infoTable);

        // === FABRIC QUALITY TABLE ===
        container.add(new Paragraph("Fabric Quality Details")
                .setFontSize(FONT_SIZE_SECTION).setBold().setFontColor(ACCENT_COLOR)
                .setMarginTop(4).setMarginBottom(2));
        Table qualityTable = new Table(UnitValue.createPercentArray(new float[]{25, 15, 15, 15, 15, 15}))
                .useAllAvailableWidth();
        addCompactTableHeader(qualityTable, "Quality", "Width", "Reed", "Peak", "Warp", "Weft");
        addCompactTableRow(qualityTable,
                safe(invoice.getQualityName()), safe(invoice.getWidth()), safe(invoice.getFani()),
                safe(invoice.getPeak()), safe(invoice.getWarp()), safe(invoice.getWeft()));
        container.add(qualityTable);

        // === QUANTITY TABLE ===
        container.add(new Paragraph("Quantity & Rate")
                .setFontSize(FONT_SIZE_SECTION).setBold().setFontColor(ACCENT_COLOR)
                .setMarginTop(4).setMarginBottom(2));
        Table qtyTable = new Table(UnitValue.createPercentArray(new float[]{15, 17, 17, 17, 17, 17}))
                .useAllAvailableWidth();
        addCompactTableHeader(qtyTable, "Sr.", "Rolls", "Meters", "Rate", "Amount", "");
        qtyTable.addCell(createCompactCell("1", TextAlignment.CENTER));
        qtyTable.addCell(createCompactCell(String.valueOf(invoice.getRolls() != null ? invoice.getRolls() : 0), TextAlignment.CENTER));
        qtyTable.addCell(createCompactCell(String.format("%.2f", invoice.getMeters() != null ? invoice.getMeters() : 0.0), TextAlignment.RIGHT));
        qtyTable.addCell(createCompactCell(String.format("%.2f", invoice.getRate() != null ? invoice.getRate() : 0.0), TextAlignment.RIGHT));
        qtyTable.addCell(createCompactCell(String.format("%.2f", invoice.getTotalAmount() != null ? invoice.getTotalAmount() : 0.0), TextAlignment.RIGHT));
        qtyTable.addCell(createCompactCell("", TextAlignment.CENTER));
        container.add(qtyTable);

        // === AMOUNT SUMMARY (compact) ===
        container.add(new Paragraph("Amount Summary")
                .setFontSize(FONT_SIZE_SECTION).setBold().setFontColor(ACCENT_COLOR)
                .setMarginTop(4).setMarginBottom(2));
        Table amountTable = new Table(UnitValue.createPercentArray(new float[]{50, 25, 25}))
                .useAllAvailableWidth();

        Cell wordsCell = new Cell(4, 1)
                .add(new Paragraph("Amount In Words: " + safe(invoice.getAmountInWords()))
                        .setFontSize(FONT_SIZE_SMALL).setItalic())
                .setBackgroundColor(SECTION_BG)
                .setPadding(3)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f));
        amountTable.addCell(wordsCell);

        addCompactAmountRow(amountTable, "Total", formatAmount(invoice.getTotalAmount()));
        addCompactAmountRow(amountTable, "CGST", formatAmount(invoice.getCgstAmount()));
        addCompactAmountRow(amountTable, "SGST", formatAmount(invoice.getSgstAmount()));

        Cell netLabel = new Cell()
                .add(new Paragraph("Net Payable").setFontSize(FONT_SIZE_SMALL).setBold().setFontColor(HEADER_TEXT))
                .setBackgroundColor(HEADER_BG).setTextAlignment(TextAlignment.RIGHT).setPadding(2)
                .setBorder(Border.NO_BORDER);
        Cell netValue = new Cell()
                .add(new Paragraph("₹ " + formatAmount(invoice.getNetPayable())).setFontSize(FONT_SIZE_SMALL).setBold().setFontColor(HEADER_TEXT))
                .setBackgroundColor(HEADER_BG).setTextAlignment(TextAlignment.RIGHT).setPadding(2)
                .setBorder(Border.NO_BORDER);
        amountTable.addCell(netLabel);
        amountTable.addCell(netValue);
        container.add(amountTable);

        // === FOOTER: same Terms & Conditions / Authorized Signatory structure as the full invoice ===
        Table footerTable = new Table(UnitValue.createPercentArray(new float[]{50, 50})).useAllAvailableWidth();
        footerTable.addCell(new Cell()
                .add(new Paragraph("Terms & Conditions:")
                        .setFontSize(FONT_SIZE_SMALL).setBold())
                .add(new Paragraph("1. Goods once sold will not be taken back.\n2. Subject to local jurisdiction.")
                        .setFontSize(FONT_SIZE_SMALL))
                .setPadding(2)
                .setBorder(Border.NO_BORDER));
        footerTable.addCell(new Cell()
                .add(new Paragraph("For " + (business != null ? safe(business.getBusinessName()) : ""))
                        .setFontSize(FONT_SIZE_SMALL).setBold().setTextAlignment(TextAlignment.RIGHT))
                .add(new Paragraph("\nAuthorized Signatory")
                        .setFontSize(FONT_SIZE_SMALL).setTextAlignment(TextAlignment.RIGHT))
                .setPadding(2).setBorder(Border.NO_BORDER));
        container.add(footerTable);
    }

    // === Helper methods ===

    private void addTableHeader(Table table, String... headers) {
        for (String header : headers) {
            table.addHeaderCell(new Cell()
                    .add(new Paragraph(header).setFontSize(FONT_SIZE_NORMAL).setBold().setFontColor(HEADER_TEXT))
                    .setBackgroundColor(ACCENT_COLOR)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setPadding(3)
                    .setBorder(new SolidBorder(ColorConstants.WHITE, 0.5f)));
        }
    }

    private void addTableRow(Table table, String... values) {
        for (String value : values) {
            table.addCell(new Cell()
                    .add(new Paragraph(value).setFontSize(FONT_SIZE_NORMAL))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setPadding(3)
                    .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f)));
        }
    }

    private void addCompactTableHeader(Table table, String... headers) {
        for (String header : headers) {
            if (header.isEmpty()) {
                table.addHeaderCell(new Cell().add(new Paragraph(""))
                        .setBackgroundColor(ACCENT_COLOR).setPadding(2)
                        .setBorder(new SolidBorder(ColorConstants.WHITE, 0.5f)));
                continue;
            }
            table.addHeaderCell(new Cell()
                    .add(new Paragraph(header).setFontSize(FONT_SIZE_SMALL).setBold().setFontColor(HEADER_TEXT))
                    .setBackgroundColor(ACCENT_COLOR)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setPadding(2)
                    .setBorder(new SolidBorder(ColorConstants.WHITE, 0.5f)));
        }
    }

    private void addCompactTableRow(Table table, String... values) {
        for (String value : values) {
            table.addCell(new Cell()
                    .add(new Paragraph(value).setFontSize(FONT_SIZE_SMALL))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setPadding(2)
                    .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f)));
        }
    }

    private Cell createCompactCell(String text, TextAlignment align) {
        return new Cell()
                .add(new Paragraph(text).setFontSize(FONT_SIZE_SMALL))
                .setTextAlignment(align)
                .setPadding(2)
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f));
    }

    private void addAmountRow(Table table, String label, String value) {
        table.addCell(new Cell()
                .add(new Paragraph(label).setFontSize(FONT_SIZE_NORMAL))
                .setTextAlignment(TextAlignment.RIGHT)
                .setPadding(3)
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f)));
        table.addCell(new Cell()
                .add(new Paragraph("₹ " + value).setFontSize(FONT_SIZE_NORMAL))
                .setTextAlignment(TextAlignment.RIGHT)
                .setPadding(3)
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f)));
    }

    private void addCompactAmountRow(Table table, String label, String value) {
        table.addCell(new Cell()
                .add(new Paragraph(label).setFontSize(FONT_SIZE_SMALL))
                .setTextAlignment(TextAlignment.RIGHT).setPadding(2)
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f)));
        table.addCell(new Cell()
                .add(new Paragraph("₹ " + value).setFontSize(FONT_SIZE_SMALL))
                .setTextAlignment(TextAlignment.RIGHT).setPadding(2)
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f)));
    }

    private String safe(String value) {
        return value != null ? value : "";
    }

    private String formatAmount(Double amount) {
        return amount != null ? String.format("%.2f", amount) : "0.00";
    }
}