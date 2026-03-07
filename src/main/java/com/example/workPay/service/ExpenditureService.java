package com.example.workPay.service;

import com.example.workPay.Repository.ExpenseReceiptRepository;
import com.example.workPay.Repository.ExpenditureRepo;
import com.example.workPay.entities.ExpenseReceipt;
import com.example.workPay.entities.Expenditure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
public class ExpenditureService {
    @Autowired
    private ExpenditureRepo expenditureRepo;

    @Autowired
    private ExpenseReceiptRepository expenseReceiptRepository;

    @Transactional
    public Optional<Expenditure> save(Expenditure expenditure, MultipartFile image) throws IOException {
        Expenditure saved = expenditureRepo.save(expenditure);

        if (image != null && !image.isEmpty()) {
            byte[] compressedImage = compressImage(image);

            ExpenseReceipt receipt = ExpenseReceipt.builder()
                    .expenseId(saved.getId())
                    .fileName(image.getOriginalFilename())
                    .fileType(image.getContentType())
                    .imageData(compressedImage)
                    .build();

            expenseReceiptRepository.save(receipt);
        }

        return Optional.of(saved);
    }

    public Optional<Expenditure> save(Expenditure expenditure) {
        return Optional.ofNullable(expenditure)
                .map(expend -> expenditureRepo.save(expend));
    }

    public List<Expenditure> findAll(Integer branchId) {
        if (branchId != null) {
            return expenditureRepo.findByBranchIdOrderByDateDesc(branchId);
        }
        return expenditureRepo.findAllByOrderByDateDesc();
    }

    public boolean deleteByIdAndExpenseType(String id, String expenseType) {
        Optional<Expenditure> expenditure = expenditureRepo.findByIdAndExpenseType(id, expenseType);
        if (expenditure.isPresent()) {
            expenditureRepo.delete(expenditure.get());
            return true;
        }
        return false;
    }

    public Optional<ExpenseReceipt> getReceiptByExpenseId(String expenseId) {
        return expenseReceiptRepository.findByExpenseId(expenseId);
    }

    private byte[] compressImage(MultipartFile file) throws IOException {
        String contentType = file.getContentType();

        // Only compress JPEG/PNG images; store others as-is
        if (contentType == null || (!contentType.contains("jpeg") && !contentType.contains("jpg") && !contentType.contains("png"))) {
            return file.getBytes();
        }

        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
        if (bufferedImage == null) {
            return file.getBytes();
        }

        // For JPEG: use compression quality of 0.7
        if (contentType.contains("jpeg") || contentType.contains("jpg")) {
            return compressJpeg(bufferedImage, 0.7f);
        }

        // For PNG: convert to JPEG with compression for smaller size
        if (contentType.contains("png")) {
            // Convert to RGB (remove alpha channel) for JPEG compression
            BufferedImage rgbImage = new BufferedImage(
                    bufferedImage.getWidth(),
                    bufferedImage.getHeight(),
                    BufferedImage.TYPE_INT_RGB);
            rgbImage.createGraphics().drawImage(bufferedImage, 0, 0, java.awt.Color.WHITE, null);
            return compressJpeg(rgbImage, 0.75f);
        }

        return file.getBytes();
    }

    private byte[] compressJpeg(BufferedImage image, float quality) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");

        if (!writers.hasNext()) {
            throw new IOException("No JPEG writer found");
        }

        ImageWriter writer = writers.next();
        ImageWriteParam params = writer.getDefaultWriteParam();
        params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        params.setCompressionQuality(quality);

        try (ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(outputStream)) {
            writer.setOutput(imageOutputStream);
            writer.write(null, new IIOImage(image, null, null), params);
        } finally {
            writer.dispose();
        }

        return outputStream.toByteArray();
    }
}
