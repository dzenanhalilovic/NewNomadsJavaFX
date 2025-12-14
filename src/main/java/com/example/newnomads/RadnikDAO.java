package com.example.newnomads;

import bazneTabele.Radnik;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RadnikDAO {

    // Mapiranje ResultSet-a na objekat Radnik
    private static Radnik mapRadnik(ResultSet rs) throws Exception {
        return new Radnik(
                rs.getInt("idRadnika"),
                rs.getString("brojPasosa"),
                rs.getString("ime"),
                rs.getString("prezime"),
                rs.getInt("drzavaId"),
                rs.getInt("idGraneRada"),
                rs.getString("spol"),
                rs.getDate("datumRodjenja"),
                rs.getDate("doKadTrajePasos"),
                rs.getDate("doKadTrajeViza"),
                rs.getString("nazivStatusa")
        );
    }

    // Dohvati sve slobodne radnike
    public static ObservableList<Radnik> getSlobodniRadnici() {
        ObservableList<Radnik> lista = FXCollections.observableArrayList();
        String sql = "SELECT * FROM radnici WHERE nazivStatusa = 'slobodan'";

        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRadnik(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    // Dohvati radnika po ID-u
    public static Radnik getRadnikById(int idRadnika) {
        String sql = "SELECT * FROM radnici WHERE idRadnika = ?";
        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idRadnika);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRadnik(rs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Dodaj novog radnika
    public static boolean dodajRadnika(Radnik r) {
        String sql = """
                INSERT INTO radnici 
                (brojPasosa, ime, prezime, drzavaId, idGraneRada, spol, datumRodjenja, doKadTrajePasos, doKadTrajeViza, nazivStatusa)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, r.getBrojPasosa());
            ps.setString(2, r.getIme());
            ps.setString(3, r.getPrezime());
            ps.setInt(4, r.getDrzavaId());
            ps.setInt(5, r.getGrana());
            ps.setString(6, r.getSpol());
            ps.setDate(7, r.getDatumRodjenja());
            ps.setDate(8, r.getDoKadTrajePasos());
            ps.setDate(9, r.getDoKadTrajeViza());
            ps.setString(10, r.getStatus());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // Promijeni status radnika
    public static boolean promijeniStatusRadnika(int idRadnika, String noviStatus) {
        String sql = "UPDATE radnici SET nazivStatusa = ? WHERE idRadnika = ?";
        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, noviStatus);
            ps.setInt(2, idRadnika);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
