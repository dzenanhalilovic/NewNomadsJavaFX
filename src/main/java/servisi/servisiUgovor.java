package servisi;

import com.example.newnomads.DB;
import bazneTabele.Ugovor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class servisiUgovor {


    private static Ugovor mapUgovor(ResultSet rs) throws Exception {
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
                rs.getString("imeRadnika") + " " + rs.getString("prezimeRadnika"), // radnik
                rs.getString("imeFirme"),                                         // firma
                rs.getInt("idPotraznjeRadnika")                                   // <--- DODATO
        );
    }




    public static Task<Boolean> kreirajUgovorAsync(
            int idFirme,
            int idRadnika,
            int drzavaRadaId,
            Date datumPocetkaRada,
            Date datumKrajaRada,   // može biti null
            String opis            // može biti null
    ) {
        return new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                String query = """
                        INSERT INTO ugovor
                        (idFirme, idRadnika, drzavaRadaId, datumPocetkaRada, datumKrajaRada, opis)
                        VALUES (?, ?, ?, ?, ?, ?)
                        """;

                try (Connection conn = DB.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(query)) {

                    stmt.setInt(1, idFirme);
                    stmt.setInt(2, idRadnika);
                    stmt.setInt(3, drzavaRadaId);
                    stmt.setDate(4, datumPocetkaRada);
                    stmt.setDate(5, datumKrajaRada);
                    stmt.setString(6, opis);

                    return stmt.executeUpdate() > 0;
                }
            }
        };
    }


    private static Task<ObservableList<Ugovor>> getUgovoriListaAsyncInternal(String query, Object... params) {
        return new Task<>() {
            @Override
            protected ObservableList<Ugovor> call() throws Exception {
                ObservableList<Ugovor> lista = FXCollections.observableArrayList();

                try (Connection conn = DB.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(query)) {

                    for (int i = 0; i < params.length; i++) {
                        Object p = params[i];
                        int index = i + 1;

                        if (p instanceof Integer) {
                            stmt.setInt(index, (Integer)p);
                        } else if (p instanceof String) {
                            stmt.setString(index, (String)p);
                        } else {
                            stmt.setObject(index, p);
                        }
                    }

                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            lista.add(mapUgovor(rs));
                        }
                    }
                }

                return lista;
            }
        };
    }


    public static Task<ObservableList<Ugovor>> getUgovoriByFirmaAsync(int idFirme) {
        String query = """
                SELECT * FROM ugovor
                WHERE idFirme = ?
                ORDER BY datumKreiranja DESC
                """;
        return getUgovoriListaAsyncInternal(query, idFirme);
    }


    public static Task<ObservableList<Ugovor>> getUgovoriByRadnikAsync(int idRadnika) {
        String query = """
                SELECT * FROM ugovor
                WHERE idRadnika = ?
                ORDER BY datumKreiranja DESC
                """;
        return getUgovoriListaAsyncInternal(query, idRadnika);
    }


    public static Task<ObservableList<Ugovor>> getUgovoriAktivniAsync() {
        String query = """
                SELECT * FROM ugovor
                WHERE statusUgovora = 'aktivan'
                ORDER BY datumKreiranja DESC
                """;
        return getUgovoriListaAsyncInternal(query);
    }


    public static Task<ObservableList<Ugovor>> getUgovoriNijeAktivniAsync() {
        String query = """
                SELECT * FROM ugovor
                WHERE statusUgovora = 'nijeaktivan'
                ORDER BY datumKreiranja DESC
                """;
        return getUgovoriListaAsyncInternal(query);
    }


    public static Task<ObservableList<Ugovor>> getUgovoriNaCekanjuAsync() {
        String query = """
                SELECT * FROM ugovor
                WHERE statusUgovora = 'naCekanju'
                ORDER BY datumKreiranja DESC
                """;
        return getUgovoriListaAsyncInternal(query);
    }
}
