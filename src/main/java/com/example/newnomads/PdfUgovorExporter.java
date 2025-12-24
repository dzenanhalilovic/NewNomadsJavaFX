package com.example.newnomads;

import bazneTabele.Ugovor;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.*;

public class PdfUgovorExporter {

    public static void export(Ugovor u, File outFile) throws Exception {

        try (PDDocument doc = new PDDocument()) {

            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            // ✅ Unicode fontovi
            PDType0Font font = loadFont(doc, "/fonts/DejaVuSans.ttf");
            PDType0Font fontBold = loadFont(doc, "/fonts/DejaVuSans-Bold.ttf");

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {

                float margin = 50;
                float y = 780;

                // --- LOGO ---
                PDImageXObject logo = loadLogo(doc);
                if (logo != null) {
                    cs.drawImage(logo, margin, y - 80, 80, 80);
                }

                // --- NASLOV ---
                cs.beginText();
                cs.setFont(fontBold, 18);
                cs.newLineAtOffset(margin + 100, y - 25);
                cs.showText("UGOVOR O RADU");
                cs.endText();

                cs.beginText();
                cs.setFont(font, 11);
                cs.newLineAtOffset(margin + 100, y - 45);
                cs.showText("ID: " + u.getIdUgovora() + " | Status: " + ns(u.getStatusUgovora()));
                cs.endText();

                // linija
                cs.moveTo(margin, y - 100);
                cs.lineTo(545, y - 100);
                cs.stroke();

                float yy = y - 140;
                float keyX = margin;
                float valX = margin + 150;
                float row = 20;

                write(cs, fontBold, 11, keyX, yy, "Firma:");
                write(cs, font, 11, valX, yy, ns(u.getFirma())); yy -= row;

                write(cs, fontBold, 11, keyX, yy, "Radnik:");
                write(cs, font, 11, valX, yy, ns(u.getRadnik())); yy -= row;

                write(cs, fontBold, 11, keyX, yy, "Datum početka:");
                write(cs, font, 11, valX, yy, nsDate(u.getDatumPocetkaRada())); yy -= row;

                write(cs, fontBold, 11, keyX, yy, "Datum kraja:");
                write(cs, font, 11, valX, yy, nsDate(u.getDatumKrajaRada())); yy -= row;

                write(cs, fontBold, 11, keyX, yy, "Opis:");
                write(cs, font, 11, valX, yy, ns(u.getOpis())); yy -= row;

                // footer
                write(cs, font, 9, margin, 40,
                        "Dokument je generisan iz NewNomads sistema.");

            }

            doc.save(outFile);
        }
    }

    // ---------- helpers ----------

    private static void write(PDPageContentStream cs, PDType0Font f, int size,
                              float x, float y, String text) throws IOException {
        cs.beginText();
        cs.setFont(f, size);
        cs.newLineAtOffset(x, y);
        cs.showText(text);
        cs.endText();
    }

    private static PDType0Font loadFont(PDDocument doc, String path) throws IOException {
        try (InputStream is = PdfUgovorExporter.class.getResourceAsStream(path)) {
            if (is == null) throw new FileNotFoundException("Font not found: " + path);
            return PDType0Font.load(doc, is);
        }
    }

    private static PDImageXObject loadLogo(PDDocument doc) {
        try (InputStream is = PdfUgovorExporter.class.getResourceAsStream("/images/Logo.png")) {
            if (is == null) return null;
            byte[] bytes = is.readAllBytes();
            return PDImageXObject.createFromByteArray(doc, bytes, "Logo.png");
        } catch (Exception e) {
            return null;
        }
    }

    private static String ns(String s) {
        return (s == null || s.isBlank()) ? "-" : s;
    }

    private static String nsDate(java.util.Date d) {
        return d == null ? "-" : d.toString();
    }
}
