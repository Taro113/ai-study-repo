package com.gao.demo32.service;

import lombok.AllArgsConstructor;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.reader.JsonReader;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.ai.reader.pdf.ParagraphPdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DocumentReaderService {
    private final PdfDocumentReaderConfig pdfDocumentReaderConfig;
    private final MarkdownDocumentReaderConfig markdownDocumentReaderConfig;

    public DocumentReader getDocumentReader(String filename, Resource resource) {
        String lowerFilename = filename.toLowerCase();

        if (lowerFilename.endsWith(".json")) {
            return new JsonReader(resource);
        }
        if (lowerFilename.endsWith(".md")) {
            return new MarkdownDocumentReader(resource, markdownDocumentReaderConfig);
        }
        if (lowerFilename.endsWith(".pdf")) {
            return new ParagraphPdfDocumentReader(resource, pdfDocumentReaderConfig);
        }
        if (lowerFilename.endsWith(".docx") || lowerFilename.endsWith(".doc")) {
            return new TikaDocumentReader(resource);
        }
        return new TextReader(resource);
    }
}
