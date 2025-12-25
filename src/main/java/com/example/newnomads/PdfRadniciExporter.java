package com.example.newnomads;

import bazneTabele.Radnik;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.*;
import java.util.List;

public class PdfRadniciExporter {

    public static void export(List<Radnik> radnici, File outFile) throws Exception {

        try (PDDocument doc = new PDDocument()) {

            PDType0Font font = loadFont(doc, "/fonts/DejaVuSans.ttf");
            PDType0Font fontBold = loadFont(doc, "/fonts/DejaVuSans-Bold.ttf");

            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            PDPageContentStream cs = new PDPageContentStream(doc, page);
            PDImageXObject logo = loadLogo(doc);

            float margin = 40;
            float y = 780;
            float rowH = 18;

            // ===== HEADER =====
            if (logo != null) {
                cs.drawImage(logo, margin, y - 60, 60, 60);
            }

            write(cs, fontBold, 18, margin + 80, y - 25, "LISTA RADNIKA");
            write(cs, font, 10, margin + 80, y - 45,
                    "Ukupno: " + radnici.size() + " | NewNomads");

            y -= 90;

            // ===== TABELA HEADER =====
            String[] headers = {
                    "Pasoš", "Spol", "Rođenje", "Pasoš do", "Viza do", "Status", "Ime i prezime"
            };

            float[] colX = {
                    margin,
                    margin + 70,
                    margin + 120,
                    margin + 190,
                    margin + 270,
                    margin + 350,
                    margin + 430
            };

            for (int i = 0; i < headers.length; i++) {
                write(cs, fontBold, 9, colX[i], y, headers[i]);
            }

            y -= rowH;
            cs.moveTo(margin, y);
            cs.lineTo(555, y);
            cs.stroke();
            y -= 25;

            // ===== REDOVI =====
            for (Radnik r : radnici) {

                if (y < 60) {
                    cs.close();
                    page = new PDPage(PDRectangle.A4);
                    doc.addPage(page);
                    cs = new PDPageContentStream(doc, page);
                    y = 780;
                }

                String[] statusSplit = ns(r.getStatus()).split(" \\| ");
                String status = statusSplit.length > 2 ? statusSplit[2] : "";

                write(cs, font, 9, colX[0], y, ns(r.getBrojPasosa()));
                write(cs, font, 9, colX[1], y, ns(r.getSpol()));
                write(cs, font, 9, colX[2], y, nsDate(r.getDatumRodjenja()));
                write(cs, font, 9, colX[3], y, nsDate(r.getDoKadTrajePasos()));
                write(cs, font, 9, colX[4], y, nsDate(r.getDoKadTrajeViza()));
                write(cs, font, 9, colX[5], y, status);
                write(cs, fontBold, 9, colX[6], y,
                        ns(r.getIme()) + " " + ns(r.getPrezime()));

                y -= rowH;
            }

            // ===== FOOTER =====
            write(cs, font, 8, margin, 40,
                    "Dokument automatski generisan iz NewNomads sistema.");

            cs.close();
            doc.save(outFile);
        }
    }

    // ===== HELPERS =====

    private static void write(PDPageContentStream cs, PDType0Font f, int size,
                              float x, float y, String text) throws IOException {
        cs.beginText();
        cs.setFont(f, size);
        cs.newLineAtOffset(x, y);
        cs.showText(text);
        cs.endText();
    }

    private static PDType0Font loadFont(PDDocument doc, String path) throws IOException {
        try (InputStream is = PdfRadniciExporter.class.getResourceAsStream(path)) {
            if (is == null) throw new FileNotFoundException("Font not found: " + path);
            return PDType0Font.load(doc, is);
        }
    }

    private static PDImageXObject loadLogo(PDDocument doc) {
        try (InputStream is = PdfRadniciExporter.class.getResourceAsStream("/images/Logo.png")) {
            if (is == null) return null;
            return PDImageXObject.createFromByteArray(doc, is.readAllBytes(), "Logo");
        } catch (Exception e) {
            return null;
        }
    }

    private static String ns(String s) {
        return (s == null || s.isBlank()) ? "-" : s;
    }

    private static String nsDate(java.sql.Date d) {
        return d == null ? "-" : d.toString();
    }
}
