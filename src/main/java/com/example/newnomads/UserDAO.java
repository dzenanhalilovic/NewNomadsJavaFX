package com.example.newnomads;

import java.sql.*;

public class UserDAO {

    // REGISTRACIJA
    public static boolean registerUser(User user) {
        String insertUser = "INSERT INTO users (email, password, role) VALUES (?, ?, ?)";

        try (Connection conn = DB.getConnection();
             PreparedStatement psUser = conn.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS)) {

            // 1) Upis u USERS
            psUser.setString(1, user.getEmail());
            psUser.setString(2, user.getPassword());
            psUser.setString(3, user.getRole());  // "regruter" ili "firma"
            psUser.executeUpdate();

            // Dobijamo userId koji je FK
            ResultSet rs = psUser.getGeneratedKeys();
            int userId = -1;
            if (rs.next()) {
                userId = rs.getInt(1);
            }

            if (userId == -1) return false;

            // 2) Upis u REGRUTER tabelu
            if (user.getRole().equalsIgnoreCase("regruter")) {
                String sqlReg = "INSERT INTO regruter (ime, mail, lozinka, potvrdjen, userId) VALUES (?, ?, ?, 0, ?)";
                PreparedStatement ps = conn.prepareStatement(sqlReg);
                ps.setString(1, user.getIme());
                ps.setString(2, user.getEmail());
                ps.setString(3, user.getPassword());
                ps.setInt(4, userId);
                ps.executeUpdate();
            }

            // 3) Upis u FIRMEKLIJENTI
            else if (user.getRole().equalsIgnoreCase("firma")) {
                String sqlFirma = "INSERT INTO firmeKlijenti (imeFirme, mail, lozinka, idGraneRada, brojTelefona, drzavaId, vlasnikFirme, oFirmi, userId) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sqlFirma);

                ps.setString(1, user.getIme());              // imeFirme
                ps.setString(2, user.getEmail());
                ps.setString(3, user.getPassword());
                ps.setInt(4, 1);                             // idGraneRada placeholder
                ps.setString(5, user.getBrojTelefona());    // brojTelefona
                ps.setInt(6, 1);                             // drzavaId placeholder
                ps.setString(7, user.getVlasnikFirme());    // vlasnikFirme
                ps.setString(8, user.getoFirmi());          // oFirmi
                ps.setInt(9, userId);
                ps.executeUpdate();
            }

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // LOGIN
    public static User login(String email, String password) {
        String sql = "SELECT * FROM users WHERE email=? AND password=?";
        try (Connection conn = DB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String role = rs.getString("role");
                int userId = rs.getInt("userId"); // <-- koristimo userId iz users tabele

                // Ako je firma, učitaj dodatne podatke
                if (role.equalsIgnoreCase("firma")) {
                    String sqlFirma = "SELECT * FROM firmeKlijenti WHERE userId = ?";
                    PreparedStatement psFirma = conn.prepareStatement(sqlFirma);
                    psFirma.setInt(1, userId); // userId iz users tabele
                    ResultSet rsFirma = psFirma.executeQuery();
                    if (rsFirma.next()) {
                        User user = new User(
                                rsFirma.getString("imeFirme"),
                                rs.getString("email"),
                                rs.getString("password"),
                                role
                        );
                        user.setBrojTelefona(rsFirma.getString("brojTelefona"));
                        user.setVlasnikFirme(rsFirma.getString("vlasnikFirme"));
                        user.setoFirmi(rsFirma.getString("oFirmi"));
                        return user;
                    }
                }

                // Ako je regruter
                else if (role.equalsIgnoreCase("regruter")) {
                    String sqlReg = "SELECT * FROM regruter WHERE userId = ?";
                    PreparedStatement psReg = conn.prepareStatement(sqlReg);
                    psReg.setInt(1, userId);
                    ResultSet rsReg = psReg.executeQuery();
                    if (rsReg.next()) {
                        User user = new User(
                                rsReg.getString("ime"),
                                rs.getString("email"),
                                rs.getString("password"),
                                role
                        );
                        return user;
                    }
                }

                // default fallback
                return new User("", rs.getString("email"), rs.getString("password"), role);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
