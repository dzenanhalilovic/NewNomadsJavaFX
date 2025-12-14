package com.example.newnomads;

import bazneTabele.Ugovor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UgovorDAO {
    public static void updateStatusUgovora(int idUgovora, String status) {
        String sql = "UPDATE ugovor SET statusUgovora = ? WHERE idUgovora = ?";
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, idUgovora);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static ObservableList<Ugovor> getUgovoriByFirmaId(int idFirme) {
        ObservableList<Ugovor> lista = FXCollections.observableArrayList();

        String sql = """
            SELECT u.*, r.ime AS imeRadnika, r.prezime AS prezimeRadnika, f.imeFirme AS nazivFirme
            FROM ugovor u
            JOIN radnici r ON u.idRadnika = r.idRadnika
            JOIN firmeKlijenti f ON u.idFirme = f.idFirme
            WHERE u.idFirme = ?
            """;

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idFirme);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Ugovor ugovor = new Ugovor(
                            rs.getInt("idUgovora"),
                            rs.getInt("idFirme"),
                            rs.getInt("idRadnika"),
                            rs.getTimestamp("datumKreiranja"),
                            rs.getInt("drzavaRadaId"),
                            rs.getDate("datumPocetkaRada"),
                            rs.getDate("datumKrajaRada"),
                            rs.getString("statusUgovora"),
                            rs.getString("opis"),
                            rs.getString("imeRadnika") + " " + rs.getString("prezimeRadnika"),
                            rs.getString("nazivFirme")
                    );
                    lista.add(ugovor);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }
}
