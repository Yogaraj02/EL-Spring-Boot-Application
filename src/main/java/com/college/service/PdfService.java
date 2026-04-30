package com.college.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class PdfService {

    public byte[] generateCertificatePdf(String studentName, String eventName, String certType) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // Create a document with landscape orientation
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, out);

            document.open();

            // Fonts
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 36, Font.NORMAL);
            Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA, 20, Font.NORMAL);
            Font nameFont = FontFactory.getFont(FontFactory.TIMES_BOLDITALIC, 32, Font.NORMAL);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 16, Font.NORMAL);

            // Add empty lines for spacing
            document.add(new Paragraph("\n\n"));

            // Title
            Paragraph title = new Paragraph("CERTIFICATE OF " + certType.toUpperCase(), titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph("\n\n"));

            // Subtitle
            Paragraph subtitle = new Paragraph("This is to certify that", subTitleFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            document.add(subtitle);

            document.add(new Paragraph("\n"));

            // Student Name
            Paragraph name = new Paragraph(studentName, nameFont);
            name.setAlignment(Element.ALIGN_CENTER);
            document.add(name);

            document.add(new Paragraph("\n"));

            // Description
            Paragraph desc = new Paragraph("has successfully participated in the event", normalFont);
            desc.setAlignment(Element.ALIGN_CENTER);
            document.add(desc);

            document.add(new Paragraph("\n"));

            // Event Name
            Paragraph event = new Paragraph(eventName, titleFont);
            event.setAlignment(Element.ALIGN_CENTER);
            document.add(event);

            document.add(new Paragraph("\n\n\n"));

            // Date & Signature
            String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));
            Paragraph footer = new Paragraph("Date: " + currentDate + "                                          Authorized Signatory", normalFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            System.err.println("Error generating PDF: " + e.getMessage());
            return null;
        }
    }
}
