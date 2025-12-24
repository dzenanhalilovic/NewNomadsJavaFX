package com.example.newnomads;

import bazneTabele.Ugovor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
                            rs.getString("imeRadnika") + " " + rs.getString("prezimeRadnika"), // radnik
                            rs.getString("nazivFirme"),                                         // firma
                            rs.getInt("idPotraznjeRadnika")                                     // sada stvarni idPotraznjeRadnika
                    );
                    lista.add(ugovor);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    public static Ugovor getUgovorById(int idUgovora) {
        String sql = """
            SELECT u.*,
                   r.ime AS imeRadnika, r.prezime AS prezimeRadnika,
                   f.imeFirme AS nazivFirme
            FROM ugovor u
            JOIN radnici r ON u.idRadnika = r.idRadnika
            JOIN firmeKlijenti f ON u.idFirme = f.idFirme
            WHERE u.idUgovora = ?
            """;

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUgovora);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUgovor(rs);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static Ugovor mapRowToUgovor(ResultSet rs) throws SQLException {
        return new Ugovor(
                rs.getInt("idUgovora"),
                rs.getInt("idFirme"),
                rs.getInt("idRadnika"),
                rs.getTimestamp("datumKreiranja"),
                rs.getInt("drzavaRadaId"),
                rs.getDate("datumPocetkaRada"),
                rs.getDate("datumKrajaRada"),
                rs.getString("statusUgovora"),
                rs.getString("opis"),
                rs.getString("imeRadnika") + " " + rs.getString("prezimeRadnika"), // radnik (alias)
                rs.getString("nazivFirme"),                                         // firma (alias)
                rs.getInt("idPotraznjeRadnika")
        );
    }




}
