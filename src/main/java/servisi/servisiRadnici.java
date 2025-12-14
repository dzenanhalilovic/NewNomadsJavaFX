package servisi;

import bazneTabele.Radnik;
import com.example.newnomads.DB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class servisiRadnici {


    private static Radnik mapRadnik(ResultSet rs) throws Exception {
        return new Radnik(
                rs.getInt("idRadnika"),
                rs.getString("brojPasosa"),
                rs.getString("ime"),
                rs.getString("prezime"),
                rs.getInt("drzavaId"),
                rs.getInt("idGraneRada"),
                rs.getString("spol"),
                rs.getDate("doKadTrajePasos"),
                rs.getDate("doKadTrajeViza"),
                rs.getDate("datumRodjenja"),
                rs.getString("nazivStatusa")
        );
    }


    public static Task<Boolean> kreirajRadnikaAsync(
            String brojPasosa,
            String ime,
            String prezime,
            int drzavaId,
            int idGraneRada,
            String spol,
            Date doKadTrajePasos,
            Date doKadTrajeViza,
            Date datumRodjenja,
            String nazivStatusa
    ) {
        return new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                String query = """
                        INSERT INTO radnici
                        (brojPasosa, ime, prezime, drzavaId, idGraneRada, spol,
                         doKadTrajePasos, doKadTrajeViza, datumRodjenja, nazivStatusa)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                        """;

                try (Connection conn = DB.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(query)) {

                    stmt.setString(1, brojPasosa);
                    stmt.setString(2, ime);
                    stmt.setString(3, prezime);
                    stmt.setInt(4, drzavaId);
                    stmt.setInt(5, idGraneRada);
                    stmt.setString(6, spol);
                    stmt.setDate(7, doKadTrajePasos);
                    stmt.setDate(8, doKadTrajeViza);
                    stmt.setDate(9, datumRodjenja);
                    stmt.setString(10, nazivStatusa);

                    return stmt.executeUpdate() > 0;
                }
            }
        };
    }


    public static Task<Radnik> getRadnikByIdAsync(int idRadnika) {
        return new Task<>() {
            @Override
            protected Radnik call() throws Exception {
                String query = "SELECT * FROM radnici WHERE idRadnika = ?";

                try (Connection conn = DB.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(query)) {

                    stmt.setInt(1, idRadnika);

                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            return mapRadnik(rs);
                        }
                    }
                }

                return null; // ako ne postoji
            }
        };
    }


    public static Task<Radnik> getRadnikByBrojPasosaAsync(String brojPasosa) {
        return new Task<>() {
            @Override
            protected Radnik call() throws Exception {
                String query = "SELECT * FROM radnici WHERE brojPasosa = ?";

                try (Connection conn = DB.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(query)) {

                    stmt.setString(1, brojPasosa);

                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            return mapRadnik(rs);
                        }
                    }
                }

                return null;
            }
        };
    }


    public static Task<ObservableList<Radnik>> getRadniciByPrezimeAsync(String prezime) {
        return new Task<>() {
            @Override
            protected ObservableList<Radnik> call() throws Exception {
                ObservableList<Radnik> lista = FXCollections.observableArrayList();

                String query = "SELECT * FROM radnici WHERE prezime = ?";

                try (Connection conn = DB.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(query)) {

                    stmt.setString(1, prezime);

                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            lista.add(mapRadnik(rs));
                        }
                    }
                }

                return lista;
            }
        };
    }


    private static Task<ObservableList<Radnik>> getRadniciByStatusAsyncInternal(String status) {
        return new Task<>() {
            @Override
            protected ObservableList<Radnik> call() throws Exception {
                ObservableList<Radnik> lista = FXCollections.observableArrayList();

                String query = "SELECT * FROM radnici WHERE nazivStatusa = ?";

                try (Connection conn = DB.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(query)) {

                    stmt.setString(1, status);

                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            lista.add(mapRadnik(rs));
                        }
                    }
                }

                return lista;
            }
        };
    }


    public static Task<ObservableList<Radnik>> getRadniciZaposleniAsync() {
        return getRadniciByStatusAsyncInternal("zaposlen");
    }

    public static Task<ObservableList<Radnik>> getRadniciSlobodniAsync() {
        return getRadniciByStatusAsyncInternal("slobodan");
    }


    public static Task<ObservableList<Radnik>> getRadniciUskoroIsticeVizaAsync() {
        return getRadniciByStatusAsyncInternal("uskoro istice viza");
    }


    public static Task<ObservableList<Radnik>> getRadniciUskoroIsticeUgovorRadaAsync() {
        return getRadniciByStatusAsyncInternal("uskoro istice ugovor rada");
    }


    public static Task<Boolean> promijeniIdGraneRadaAsync(int idRadnika, int noviIdGraneRada) {
        return new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                String query = "UPDATE radnici SET idGraneRada = ? WHERE idRadnika = ?";

                try (Connection conn = DB.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(query)) {

                    stmt.setInt(1, noviIdGraneRada);
                    stmt.setInt(2, idRadnika);

                    return stmt.executeUpdate() > 0;
                }
            }
        };
    }


    public static Task<Boolean> promijeniDrzavaIdAsync(int idRadnika, int novaDrzavaId) {
        return new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                String query = "UPDATE radnici SET drzavaId = ? WHERE idRadnika = ?";

                try (Connection conn = DB.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(query)) {

                    stmt.setInt(1, novaDrzavaId);
                    stmt.setInt(2, idRadnika);

                    return stmt.executeUpdate() > 0;
                }
            }
        };
    }


    public static Task<Boolean> promijeniDoKadTrajePasosAsync(int idRadnika, Date noviDatum) {
        return new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                String query = "UPDATE radnici SET doKadTrajePasos = ? WHERE idRadnika = ?";

                try (Connection conn = DB.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(query)) {

                    stmt.setDate(1, noviDatum);
                    stmt.setInt(2, idRadnika);

                    return stmt.executeUpdate() > 0;
                }
            }
        };
    }


    public static Task<Boolean> promijeniDoKadTrajeVizaAsync(int idRadnika, Date noviDatum) {
        return new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                String query = "UPDATE radnici SET doKadTrajeViza = ? WHERE idRadnika = ?";

                try (Connection conn = DB.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(query)) {

                    stmt.setDate(1, noviDatum);
                    stmt.setInt(2, idRadnika);

                    return stmt.executeUpdate() > 0;
                }
            }
        };
    }


    public static Task<Boolean> promijeniBrojPasosaAsync(int idRadnika, String noviBrojPasosa) {
        return new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                String query = "UPDATE radnici SET brojPasosa = ? WHERE idRadnika = ?";

                try (Connection conn = DB.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(query)) {

                    stmt.setString(1, noviBrojPasosa);
                    stmt.setInt(2, idRadnika);

                    return stmt.executeUpdate() > 0;
                }
            }
        };
    }

    public static Task<Boolean> promijeniImeAsync(int idRadnika, String novoIme) {
        return new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                String query = "UPDATE radnici SET ime = ? WHERE idRadnika = ?";

                try (Connection conn = DB.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(query)) {

                    stmt.setString(1, novoIme);
                    stmt.setInt(2, idRadnika);

                    return stmt.executeUpdate() > 0;
                }
            }
        };
    }


    public static Task<Boolean> promijeniPrezimeAsync(int idRadnika, String novoPrezime) {
        return new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                String query = "UPDATE radnici SET prezime = ? WHERE idRadnika = ?";

                try (Connection conn = DB.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(query)) {

                    stmt.setString(1, novoPrezime);
                    stmt.setInt(2, idRadnika);

                    return stmt.executeUpdate() > 0;
                }
            }
        };
    }

    public static Task<Boolean> promijeniStatusAsync(int idRadnika, String noviStatus) {
        return new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                String query = "UPDATE radnici SET nazivStatusa = ? WHERE idRadnika = ?";

                try (Connection conn = DB.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(query)) {

                    stmt.setString(1, noviStatus);
                    stmt.setInt(2, idRadnika);

                    return stmt.executeUpdate() > 0;
                }
            }
        };
    }


    public static Task<Boolean> obrisiRadnikaAsync(int idRadnika) {
        return new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                String query = "DELETE FROM radnici WHERE idRadnika = ?";

                try (Connection conn = DB.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(query)) {

                    stmt.setInt(1, idRadnika);

                    return stmt.executeUpdate() > 0;
                }
            }
        };
    }

}
