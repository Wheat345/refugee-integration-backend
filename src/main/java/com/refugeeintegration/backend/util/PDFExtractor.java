package com.refugeeintegration.backend.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;

public class PDFExtractor {
    public static String extractText(String filePath) {
        try (PDDocument document = PDDocument.load(new File(filePath))) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void main(String[] args) {
        String extractedText = extractText("/Users/difeng/Desktop/technovation2025/RefugeeAndIntegeration/rag/source/imm5925e.pdf");
        System.out.println(extractedText);
    }
}