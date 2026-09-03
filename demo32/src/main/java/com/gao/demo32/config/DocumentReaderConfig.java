package com.gao.demo32.config;

import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DocumentReaderConfig {
    @Bean
    public PdfDocumentReaderConfig pdfDocumentReaderConfig() {
        return PdfDocumentReaderConfig.builder()
                .withPageTopMargin(50)
                .withPageBottomMargin(30)
                .build();
    }

    @Bean
    public MarkdownDocumentReaderConfig markdownDocumentReaderConfig() {
        return MarkdownDocumentReaderConfig.builder()
                .withHorizontalRuleCreateDocument(true)
                .withIncludeCodeBlock(true)
                .withIncludeBlockquote(true)
                .build();
    }
}
