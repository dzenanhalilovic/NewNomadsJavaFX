package com.example.newnomads;

import bazneTabele.Ugovor;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class WordUgovorExporter {

    // PDF ekvivalenti
    private static final int KEY_COL_PX = 150;
    private static final int LOGO_PX = 80;

    public static void export(Ugovor u, File outFile) throws Exception {
        try (XWPFDocument doc = new XWPFDocument()) {

            // === HEADER TABLE (1 ROW, 3 COLS) ===
            // [logo] [razmak] [tekst]
            XWPFTable header = doc.createTable(1, 3);
            removeBorders(header);

            setColWidth(header, 0, 90);   // logo
            setColWidth(header, 1, 10);   // gap
            setColWidth(header, 2, 400);  // tekst

            // LOGO
            XWPFTableCell cLogo = header.getRow(0).getCell(0);
            clear(cLogo);
            XWPFRun logoRun = cLogo.addParagraph().createRun();

            try (InputStream is = WordUgovorExporter.class.getResourceAsStream("/images/Logo.png")) {
                if (is != null) {
                    logoRun.addPicture(is, Document.PICTURE_TYPE_PNG,
                            "Logo.png", Units.toEMU(LOGO_PX), Units.toEMU(LOGO_PX));
                }
            }

            // TEXT
            XWPFTableCell cText = header.getRow(0).getCell(2);
            clear(cText);

            XWPFRun title = cText.addParagraph().createRun();
            title.setBold(true);
            title.setFontFamily("Calibri");
            title.setFontSize(18);
            title.setText("UGOVOR O RADU");

            XWPFRun sub = cText.addParagraph().createRun();
            sub.setFontFamily("Calibri");
            sub.setFontSize(11);
            sub.setText("ID: " + u.getIdUgovora() + " | Status: " + ns(u.getStatusUgovora()));

            // === LINE (PDF stroke imitation) ===
            XWPFParagraph line = doc.createParagraph();
            addBottomBorder(line);

            // === BODY TABLE (KEY / VALUE) ===
            XWPFTable body = doc.createTable(5, 2);
            removeBorders(body);

            setColWidth(body, 0, KEY_COL_PX);
            setColWidth(body, 1, 400);

            kv(body.getRow(0), "Firma:", u.getFirma());
            kv(body.getRow(1), "Radnik:", u.getRadnik());
            kv(body.getRow(2), "Datum početka:", nsDate(u.getDatumPocetkaRada()));
            kv(body.getRow(3), "Datum kraja:", nsDate(u.getDatumKrajaRada()));
            kv(body.getRow(4), "Opis:", u.getOpis());

            // === FOOTER ===
            XWPFParagraph foot = doc.createParagraph();
            XWPFRun fr = foot.createRun();
            fr.setFontFamily("Calibri");
            fr.setFontSize(9);
            fr.setText("Dokument je generisan iz NewNomads sistema.");

            try (FileOutputStream fos = new FileOutputStream(outFile)) {
                doc.write(fos);
            }
        }
    }

    // ================= helpers =================

    private static void kv(XWPFTableRow row, String k, String v) {
        clear(row.getCell(0));
        clear(row.getCell(1));

        XWPFRun rk = row.getCell(0).addParagraph().createRun();
        rk.setBold(true);
        rk.setFontFamily("Calibri");
        rk.setFontSize(11);
        rk.setText(k);

        XWPFRun rv = row.getCell(1).addParagraph().createRun();
        rv.setFontFamily("Calibri");
        rv.setFontSize(11);
        rv.setText(ns(v));
    }

    private static void clear(XWPFTableCell c) {
        while (c.getParagraphs().size() > 0) c.removeParagraph(0);
    }

    private static void removeBorders(XWPFTable t) {
        CTTblBorders b = t.getCTTbl().getTblPr().addNewTblBorders();
        b.addNewTop().setVal(STBorder.NONE);
        b.addNewBottom().setVal(STBorder.NONE);
        b.addNewLeft().setVal(STBorder.NONE);
        b.addNewRight().setVal(STBorder.NONE);
        b.addNewInsideH().setVal(STBorder.NONE);
        b.addNewInsideV().setVal(STBorder.NONE);
    }

    private static void setColWidth(XWPFTable t, int col, int px) {
        for (XWPFTableRow r : t.getRows()) {
            CTTcPr pr = r.getCell(col).getCTTc().addNewTcPr();
            CTTblWidth w = pr.addNewTcW();
            w.setType(STTblWidth.DXA);
            w.setW(java.math.BigInteger.valueOf(px * 15));
        }
    }

    private static void addBottomBorder(XWPFParagraph p) {
        CTPBdr b = p.getCTP().addNewPPr().addNewPBdr();
        b.addNewBottom().setVal(STBorder.SINGLE);
    }

    private static String ns(String s) {
        return (s == null || s.isBlank()) ? "-" : s;
    }

    private static String nsDate(java.util.Date d) {
        return d == null ? "-" : d.toString();
    }
}
