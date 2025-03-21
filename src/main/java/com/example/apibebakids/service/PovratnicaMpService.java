package com.example.apibebakids.service;

import com.example.apibebakids.model.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Servis za rad sa povratnicama maloprodaje
 */
@Service
public class PovratnicaMpService {

    @Autowired
    @Qualifier("dataSourceBebaKids")
    private DataSource dataSourceBebaKids;

    @Autowired
    @Qualifier("dataSourceWatch")
    private DataSource dataSourceWatch;

    @Autowired
    @Qualifier("dataSourceGeox")
    private DataSource dataSourceGeox;

    @Autowired
    @Qualifier("dataSourceBebaKidsBih")
    private DataSource dataSourceBebaKidsBih;

    @Autowired
    private DocumentUtils documentUtils;

    /**
     * Vraća odgovarajući DataSource na osnovu naziva sistema
     */
    private DataSource getDataSourceForSystem(String system) {
        switch (system.toLowerCase()) {
            case "bebakids":
                return dataSourceBebaKids;
            case "watch":
                return dataSourceWatch;
            case "geox":
                return dataSourceGeox;
            case "bebakidsbih":
                return dataSourceBebaKidsBih;
            default:
                throw new IllegalArgumentException("Nepoznat sistem: " + system);
        }
    }

    /**
     * Dodaje povratnicu maloprodaje
     */
    public ResponseEntity<PovratnicaMpResponse> dodajPovratnicu(
            String sifraMagacina, String sifraMaloprodajnogObjekta, String system, String items,
            String oznakaCenovnika, String logname, String razlogVracanja) {

        PovratnicaMpResponse response = new PovratnicaMpResponse();
        Connection conn = null;
        PreparedStatement stmSelect = null;
        PreparedStatement stmInsert = null;
        PreparedStatement stmInsertStavke = null;
        ResultSet rSet = null;

        try {
            // Validacija obaveznih polja
            if (sifraMagacina == null || sifraMaloprodajnogObjekta == null || system == null || items == null ||
                    oznakaCenovnika == null || logname == null) {
                response.setResponseResult(false);
                response.setErrorMessage("Nedostaju obavezna polja");
                response.setRespResultCount(0);
                return ResponseEntity.badRequest().body(response);
            }

            // Parse JSON items
            JSONArray itemsArray = new JSONArray(items);
            if (itemsArray.length() == 0) {
                response.setResponseResult(false);
                response.setErrorMessage("Lista stavki je prazna");
                response.setRespResultCount(0);
                return ResponseEntity.badRequest().body(response);
            }

            // Generisanje oznake dokumenta
            String oznakaDokumenta = documentUtils.generateDocumentNumber("MP", system, sifraMaloprodajnogObjekta, "");

            // Kreiraj PovratnicaMpDTO objekat
            PovratnicaMpDTO povratnica = new PovratnicaMpDTO();
            povratnica.setOznakaDokumenta(oznakaDokumenta);
            povratnica.setDatumDokumenta(new Date());
            povratnica.setSifraMagacina(sifraMagacina);
            povratnica.setSifraMaloprodajnogObjekta(sifraMaloprodajnogObjekta);
            povratnica.setSystem(system);
            povratnica.setLogname(logname);
            povratnica.setOznakaCenovnika(oznakaCenovnika);
            povratnica.setRazlogVracanja(razlogVracanja);
            povratnica.setVrstaKnjizenja(VrstaKnjizenjaMp.SamoIzlaz); // Podrazumevana vrednost
            povratnica.setSmena("1"); // Podrazumevana vrednost
            povratnica.setVrstaPovrataMP("1"); // Podrazumevana vrednost za povrat iz maloprodaje

            // Parse items
            List<PovratnicaMpDTO.Stavka> stavke = new ArrayList<>();
            for (int i = 0; i < itemsArray.length(); i++) {
                JSONObject itemJson = itemsArray.getJSONObject(i);
                PovratnicaMpDTO.Stavka stavka = new PovratnicaMpDTO.Stavka();
                stavka.setSifraRobe(itemJson.getString("sku"));
                stavka.setSifraObelezja(itemJson.getString("size"));
                stavka.setKolicina(new BigDecimal(itemJson.getString("qty")));

                // Opciona polja
                if (itemJson.has("zone")) {
                    stavka.setSifraZone(itemJson.getString("zone"));
                }

                if (itemJson.has("nabavnaCena")) {
                    stavka.setNabavnaCena(new BigDecimal(itemJson.getString("nabavnaCena")));
                }

                if (itemJson.has("prodajnaCena")) {
                    stavka.setProdajnaCena(new BigDecimal(itemJson.getString("prodajnaCena")));
                }

                if (itemJson.has("prodajnaCenaBezPoreza")) {
                    stavka.setProdajnaCenaBezPoreza(new BigDecimal(itemJson.getString("prodajnaCenaBezPoreza")));
                }

                stavke.add(stavka);
            }
            povratnica.setStavke(stavke);

            // Dobavi DataSource za odgovarajući sistem
            DataSource dataSource = getDataSourceForSystem(system);

            // Sačuvaj u bazu
            conn = dataSource.getConnection();

            // Provera da li dokument već postoji
            stmSelect = conn.prepareStatement("SELECT * FROM povrat_mp WHERE ozn_pov_mp = ?");
            stmSelect.setString(1, oznakaDokumenta);
            rSet = stmSelect.executeQuery();

            if (rSet.next()) {
                response.setResponseResult(false);
                response.setErrorMessage("Dokument sa oznakom " + oznakaDokumenta + " već postoji");
                response.setRespResultCount(0);
                return ResponseEntity.badRequest().body(response);
            }

            // Sačuvaj povratnicu
            return sacuvajPovratnicuMaloprodaje(conn, povratnica);

        } catch (Exception e) {
            response.setResponseResult(false);
            response.setErrorMessage("Greška: " + e.getMessage());
            response.setRespResultCount(-1);
            return ResponseEntity.status(500).body(response);
        } finally {
            closeResources(conn, stmSelect, stmInsert, stmInsertStavke, rSet);
        }
    }

    /**
     * Dobavlja povratnice
     */
    public ResponseEntity<PovratnicaMpResponse> getPovratnice(String datumOdStr, String datumDoStr, String system) {
        PovratnicaMpResponse response = new PovratnicaMpResponse();
        Connection conn = null;
        PreparedStatement stmSelect = null;
        PreparedStatement stmSelectStavke = null;
        ResultSet rs = null;
        ResultSet rsStavke = null;

        try {
            // Dobavi DataSource za odgovarajući sistem
            DataSource dataSource = getDataSourceForSystem(system);

            conn = dataSource.getConnection();

            // Parse datuma ako su prosleđeni
            Date datumOd = null;
            Date datumDo = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            if (datumOdStr != null && !datumOdStr.isEmpty()) {
                datumOd = sdf.parse(datumOdStr);
            }

            if (datumDoStr != null && !datumDoStr.isEmpty()) {
                datumDo = sdf.parse(datumDoStr);
            }

            // SQL za selekciju povratnica
            StringBuilder sql = new StringBuilder("SELECT ozn_pov_mp, dat_pov_mp, sif_obj_mp, sif_mag, vrs_knj, logname, status, smena, vrs_pov, storno FROM povrat_mp WHERE 1=1");

            if (datumOd != null && datumDo != null) {
                sql.append(" AND dat_pov_mp BETWEEN ? AND ?");
            }

            stmSelect = conn.prepareStatement(sql.toString());

            int paramIndex = 1;
            if (datumOd != null && datumDo != null) {
                stmSelect.setDate(paramIndex++, new java.sql.Date(datumOd.getTime()));
                stmSelect.setDate(paramIndex++, new java.sql.Date(datumDo.getTime()));
            }

            rs = stmSelect.executeQuery();

            List<PovratnicaMpDTO> povratnice = new ArrayList<>();
            int recordCount = 0;

            while (rs.next()) {
                recordCount++;

                PovratnicaMpDTO povratnica = new PovratnicaMpDTO();
                povratnica.setOznakaDokumenta(rs.getString("ozn_pov_mp").trim());
                povratnica.setDatumDokumenta(rs.getDate("dat_pov_mp"));
                povratnica.setSifraMaloprodajnogObjekta(rs.getString("sif_obj_mp").trim());
                povratnica.setSifraMagacina(rs.getString("sif_mag").trim());
                povratnica.setLogname(rs.getString("logname"));
                povratnica.setSystem(system);
                povratnica.setSmena(rs.getString("smena"));
                povratnica.setVrstaPovrataMP(rs.getString("vrs_pov"));

                // Postavi vrstu knjiženja
                String vrstaKnjizenja = rs.getString("vrs_knj");
                if ("1".equals(vrstaKnjizenja)) {
                    povratnica.setVrstaKnjizenja(VrstaKnjizenjaMp.SamoUlaz);
                } else if ("2".equals(vrstaKnjizenja)) {
                    povratnica.setVrstaKnjizenja(VrstaKnjizenjaMp.SamoIzlaz);
                } else {
                    povratnica.setVrstaKnjizenja(VrstaKnjizenjaMp.UlazIzlaz);
                }

                // Učitaj stavke povratnice
                stmSelectStavke = conn.prepareStatement(
                        "SELECT sif_rob, kolic, nab_cen, pro_cen_bp, pro_cen, sif_ent_rob " +
                                "FROM povrat_mp_st WHERE ozn_pov_mp = ?");
                stmSelectStavke.setString(1, povratnica.getOznakaDokumenta());
                rsStavke = stmSelectStavke.executeQuery();

                List<PovratnicaMpDTO.Stavka> stavke = new ArrayList<>();
                while (rsStavke.next()) {
                    PovratnicaMpDTO.Stavka stavka = new PovratnicaMpDTO.Stavka();
                    stavka.setSifraRobe(rsStavke.getString("sif_rob"));
                    stavka.setKolicina(rsStavke.getBigDecimal("kolic"));
                    stavka.setNabavnaCena(rsStavke.getBigDecimal("nab_cen"));
                    stavka.setProdajnaCenaBezPoreza(rsStavke.getBigDecimal("pro_cen_bp"));
                    stavka.setProdajnaCena(rsStavke.getBigDecimal("pro_cen"));
                    stavka.setSifraObelezja(rsStavke.getString("sif_ent_rob"));

                    stavke.add(stavka);
                }

                povratnica.setStavke(stavke);
                povratnice.add(povratnica);

                closeResultSet(rsStavke);
                closePreparedStatement(stmSelectStavke);
            }

            response.setPovratnice(povratnice);
            response.setResponseResult(true);
            response.setRespResultCount(recordCount);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.setResponseResult(false);
            response.setErrorMessage("Greška: " + e.getMessage());
            response.setRespResultCount(-1);
            return ResponseEntity.status(500).body(response);
        } finally {
            closeResultSet(rs);
            closeResultSet(rsStavke);
            closePreparedStatement(stmSelect);
            closePreparedStatement(stmSelectStavke);
            closeConnection(conn);
        }
    }

    /**
     * Sačuvaj povratnicu maloprodaje
     */
    private ResponseEntity<PovratnicaMpResponse> sacuvajPovratnicuMaloprodaje(
            Connection conn, PovratnicaMpDTO povratnica) throws Exception {

        PovratnicaMpResponse response = new PovratnicaMpResponse();
        PreparedStatement stmInsert = null;
        PreparedStatement stmInsertStavke = null;

        try {
            conn.setAutoCommit(false);

            // Insert povratnica header
            stmInsert = conn.prepareStatement(
                    "INSERT INTO povrat_mp (ozn_pov_mp, dat_pov_mp, sif_obj_mp, sif_mag, vrs_knj, logname, " +
                            "status, smena, vrs_pov, storno, ucit_asort,ozn_cen) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)");

            stmInsert.setString(1, povratnica.getOznakaDokumenta());
            stmInsert.setDate(2, new java.sql.Date(povratnica.getDatumDokumenta().getTime()));
            stmInsert.setString(3, povratnica.getSifraMaloprodajnogObjekta());
            stmInsert.setString(4, povratnica.getSifraMagacina());

            // Vrsta knjiženja
            String vrstaKnjizenja = "3"; // Default UlazIzlaz
            if (povratnica.getVrstaKnjizenja() != null) {
                switch (povratnica.getVrstaKnjizenja()) {
                    case SamoUlaz:
                        vrstaKnjizenja = "1";
                        break;
                    case SamoIzlaz:
                        vrstaKnjizenja = "2";
                        break;
                    case UlazIzlaz:
                        vrstaKnjizenja = "3";
                        break;
                }
            }
            stmInsert.setString(5, vrstaKnjizenja);

            stmInsert.setString(6, povratnica.getLogname());
            stmInsert.setString(7, "0"); // Status
            stmInsert.setString(8, povratnica.getSmena() != null ? povratnica.getSmena() : "1"); // Smena
            stmInsert.setString(9, povratnica.getVrstaPovrataMP() != null ? povratnica.getVrstaPovrataMP() : "1"); // Vrsta povrata
            stmInsert.setString(10, "N"); // Nije stornirano
            stmInsert.setString(11, "0"); // Učitaj asortiman - N
            stmInsert.setString(12, povratnica.getOznakaCenovnika()); // Učitaj asortiman - N

            stmInsert.executeUpdate();

            // Insert stavke
            stmInsertStavke = conn.prepareStatement(
                    "INSERT INTO povrat_mp_st (ozn_pov_mp, rbr, sif_rob, kolic, nab_cen, pro_cen_bp, pro_cen, " +
                            "sif_ent_rob,kolic1) VALUES (?, ?, ?, ?, ?, ?, ?, ?,?)");

            int rbr = 1;
            for (PovratnicaMpDTO.Stavka stavka : povratnica.getStavke()) {
                stmInsertStavke.setString(1, povratnica.getOznakaDokumenta());
                stmInsertStavke.setInt(2, rbr++);
                stmInsertStavke.setString(3, stavka.getSifraRobe());
                stmInsertStavke.setBigDecimal(4, stavka.getKolicina());

                // Cene
                BigDecimal nabavnaCena = stavka.getNabavnaCena() != null ?
                        stavka.getNabavnaCena() : BigDecimal.ZERO;
                BigDecimal prodajnaCenaBezPoreza = stavka.getProdajnaCenaBezPoreza() != null ?
                        stavka.getProdajnaCenaBezPoreza() : BigDecimal.ZERO;
                BigDecimal prodajnaCena = stavka.getProdajnaCena() != null ?
                        stavka.getProdajnaCena() : BigDecimal.ZERO;

                // Ako nemamo cene, možemo pokušati da ih dobavimo iz baze
                if (nabavnaCena.equals(BigDecimal.ZERO) || prodajnaCena.equals(BigDecimal.ZERO)) {
                    ZaliheMpDTO zalihe = getZaliheObjekatMaloprodaje(
                            stavka.getSifraRobe(), povratnica.getSifraMaloprodajnogObjekta(), conn);

                    if (zalihe != null && zalihe.getSifraRobe() != null) {
                        if (nabavnaCena.equals(BigDecimal.ZERO) && zalihe.getProsecnaNabavnaCena() != null) {
                            nabavnaCena = zalihe.getProsecnaNabavnaCena();
                        }
                        if (prodajnaCenaBezPoreza.equals(BigDecimal.ZERO) && zalihe.getMaloprodajnaCenaBezPoreza() != null) {
                            prodajnaCenaBezPoreza = zalihe.getMaloprodajnaCenaBezPoreza();
                        }
                        if (prodajnaCena.equals(BigDecimal.ZERO) && zalihe.getMaloprodajnaCena() != null) {
                            prodajnaCena = zalihe.getMaloprodajnaCena();
                        }
                    }
                }

                stmInsertStavke.setBigDecimal(5, nabavnaCena);
                stmInsertStavke.setBigDecimal(6, prodajnaCenaBezPoreza);
                stmInsertStavke.setBigDecimal(7, prodajnaCena);
                stmInsertStavke.setString(8, stavka.getSifraObelezja());
                stmInsertStavke.setBigDecimal(9, stavka.getKolicina());

                stmInsertStavke.executeUpdate();
            }

            conn.commit();
            response.setResponseResult(true);
            response.setRespResultCount(1);

            // Dodaj oznaku dokumenta u odgovor
            List<PovratnicaMpDTO> povratnice = new ArrayList<>();
            povratnice.add(povratnica);
            response.setPovratnice(povratnice);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (Exception ex) {
                    // Ignoriši
                }
            }
            throw e;
        } finally {
            closePreparedStatement(stmInsert);
            closePreparedStatement(stmInsertStavke);
        }
    }

    /**
     * Pomoćne metode
     */

    // Dobavljanje zaliha iz objekta maloprodaje
    private ZaliheMpDTO getZaliheObjekatMaloprodaje(String sifraRobe, String objekatMaloprodaje, Connection conn) throws Exception {
        PreparedStatement stm = null;
        ResultSet rs = null;
        ZaliheMpDTO zalihe = new ZaliheMpDTO();

        try {
            stm = conn.prepareStatement(
                    "SELECT sif_rob, nab_cen,pro_cen_bp,pro_cen " +
                            "FROM zal_robe_mp WHERE sif_rob = ? AND sif_obj_mp = ?");
            stm.setString(1, sifraRobe);
            stm.setString(2, objekatMaloprodaje);
            rs = stm.executeQuery();

            if (rs.next()) {
                zalihe.setSifraRobe(rs.getString("sif_rob"));
                zalihe.setProsecnaNabavnaCena(rs.getBigDecimal("nab_cen"));
//                zalihe.setVeleprodajnaCena(rs.getBigDecimal("vel_cen"));
                zalihe.setMaloprodajnaCena(rs.getBigDecimal("pro_cen"));
                zalihe.setMaloprodajnaCenaBezPoreza(rs.getBigDecimal("pro_cen_bp"));
            }

            return zalihe;
        } finally {
            closeResultSet(rs);
            closePreparedStatement(stm);
        }
    }

    // Zatvaranje resursa
    private void closeResources(Connection conn, PreparedStatement... statements) {
        for (PreparedStatement stm : statements) {
            closePreparedStatement(stm);
        }
        closeConnection(conn);
    }

    private void closeResources(Connection conn, PreparedStatement stm1, PreparedStatement stm2,
                                PreparedStatement stm3, ResultSet rs) {
        closeResultSet(rs);
        closePreparedStatement(stm1);
        closePreparedStatement(stm2);
        closePreparedStatement(stm3);
        closeConnection(conn);
    }

    private void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (Exception e) {
                // Ignoriši
            }
        }
    }

    private void closePreparedStatement(PreparedStatement stm) {
        if (stm != null) {
            try {
                stm.close();
            } catch (Exception e) {
                // Ignoriši
            }
        }
    }

    private void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (Exception e) {
                // Ignoriši
            }
        }
    }
}