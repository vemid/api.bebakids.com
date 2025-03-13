package com.example.apibebakids.service;

import com.example.apibebakids.model.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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

@Service
public class PrenosnicaMpService {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private PrenosnicaUtils prenosnicaUtils;

    /**
     * Dodaje prenosnicu maloprodaje
     */
    public ResponseEntity<PrenosnicaMpResponse> dodajPrenosnicu(
            String objekatIzlaza, String objekatUlaza, String system, String items,
            String oznakaCenovnika, String logname, Integer vrstaKnjizenja, String oznakaNarudzbenice) {

        PrenosnicaMpResponse response = new PrenosnicaMpResponse();
        Connection conn = null;
        PreparedStatement stmSelect = null;
        PreparedStatement stmInsert = null;
        PreparedStatement stmInsertStavke = null;
        PreparedStatement stmSelectOrganizacijaParametar = null;
        ResultSet rSet = null;
        ResultSet rsetRezervisi = null;

        try {
            // Validacija obaveznih polja
            if (objekatIzlaza == null || objekatUlaza == null || system == null || items == null || logname == null) {
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
            String oznakaDokumenta = prenosnicaUtils.generateDocumentNumber("P9", system, objekatIzlaza, "");

            // Kreiraj PrenosnicaMpDTO objekat
            PrenosnicaMpDTO prenosnica = new PrenosnicaMpDTO();
            prenosnica.setOznakaDokumenta(oznakaDokumenta);
            prenosnica.setDatumDokumenta(new Date());
            prenosnica.setObjekatIzlaza(objekatIzlaza);
            prenosnica.setObjekatUlaza(objekatUlaza);
            prenosnica.setSystem(system);
            prenosnica.setLogname(logname);
            prenosnica.setOznakaCenovnika(oznakaCenovnika);
            prenosnica.setOznakaNarudzbenice(oznakaNarudzbenice);

            // Postavi vrstu knjiženja
            switch (vrstaKnjizenja) {
                case 1:
                    prenosnica.setVrstaKnjizenja(VrstaKnjizenjaMp.SamoUlaz);
                    break;
                case 2:
                    prenosnica.setVrstaKnjizenja(VrstaKnjizenjaMp.SamoIzlaz);
                    break;
                case 3:
                    prenosnica.setVrstaKnjizenja(VrstaKnjizenjaMp.UlazIzlaz);
                    break;
                default:
                    prenosnica.setVrstaKnjizenja(VrstaKnjizenjaMp.UlazIzlaz);
            }

            // Podrazumevano je interni prevoz
            prenosnica.setVrstaPrevoza(VrstaPrevozaMp.interniPrevoz);

            // Parse items
            List<PrenosnicaMpDTO.Stavka> stavke = new ArrayList<>();
            for (int i = 0; i < itemsArray.length(); i++) {
                JSONObject itemJson = itemsArray.getJSONObject(i);
                PrenosnicaMpDTO.Stavka stavka = new PrenosnicaMpDTO.Stavka();
                stavka.setSifraRobe(itemJson.getString("sifraRobe"));
                stavka.setSifraObelezja(itemJson.getString("sifraObelezja"));
                stavka.setKolicina(new BigDecimal(itemJson.getString("kolicina")));

                // Opciona polja
                if (itemJson.has("stopaPoreza")) {
                    stavka.setStopaPoreza(new BigDecimal(itemJson.getString("stopaPoreza")));
                } else {
                    stavka.setStopaPoreza(new BigDecimal("20"));
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

                if (itemJson.has("barkod")) {
                    stavka.setBarkod(itemJson.getString("barkod"));
                }

                if (itemJson.has("brojPakovanja")) {
                    stavka.setBrojPakovanja(new BigDecimal(itemJson.getString("brojPakovanja")));
                } else {
                    stavka.setBrojPakovanja(BigDecimal.ZERO); // Podrazumevana vrednost 0
                }

                stavke.add(stavka);
            }
            prenosnica.setStavke(stavke);

            // Sačuvaj u bazu
            conn = dataSource.getConnection();

            // Provera da li dokument već postoji
            stmSelect = conn.prepareStatement("SELECT * FROM pren_mp WHERE ozn_pre_mp = ?");
            stmSelect.setString(1, oznakaDokumenta);
            rSet = stmSelect.executeQuery();

            if (rSet.next()) {
                response.setResponseResult(false);
                response.setErrorMessage("Dokument sa oznakom " + oznakaDokumenta + " već postoji");
                response.setRespResultCount(0);
                return ResponseEntity.badRequest().body(response);
            }

            // Provera parametra za rezervaciju zaliha
            Boolean rezervisiZalihe = false;
            stmSelectOrganizacijaParametar = conn.prepareStatement(
                    "SELECT par_val FROM dd_params WHERE par_nam='REZERV_NAL_PRE_MP' AND r_user='default' " +
                            "AND sif_norg = (SELECT MIN(sif_norg) FROM obj_mp ob, org_jed oj WHERE ob.sif_org_jed=oj.sif_org_jed AND ob.sif_obj_mp=?)");
            stmSelectOrganizacijaParametar.setString(1, objekatIzlaza);
            rsetRezervisi = stmSelectOrganizacijaParametar.executeQuery();
            if (rsetRezervisi.next()) {
                rezervisiZalihe = "1".equals(rsetRezervisi.getString("par_val").trim());
            }

            // Učitavanje cena iz izlaznog objekta ako je potrebno
            Boolean uzmiCeneIzIzlaznogObjekta = checkParameterValue(conn, "WS_PREN_CEN_OBJ_IZL", "default");

            // Odluči da li se kreira prenosnica ili nalog za prenos
            Boolean kreirajNalog = checkParameterValue(conn, "WS_PRENOS_MP_KREIRA_NALOG", "default");

            if (kreirajNalog) {
                return sacuvajNalogZaPrenosMaloprodaje(conn, prenosnica, rezervisiZalihe, uzmiCeneIzIzlaznogObjekta);
            } else {
                return sacuvajPrenosnicuMaloprodaje(conn, prenosnica, uzmiCeneIzIzlaznogObjekta);
            }

        } catch (Exception e) {
            response.setResponseResult(false);
            response.setErrorMessage("Greška: " + e.getMessage());
            response.setRespResultCount(-1);
            return ResponseEntity.status(500).body(response);
        } finally {
            closeResources(conn, stmSelect, stmInsert, stmInsertStavke, stmSelectOrganizacijaParametar, rSet, rsetRezervisi);
        }
    }

    /**
     * Dobavlja prenosnice
     */
    public ResponseEntity<PrenosnicaMpResponse> getPrenosnice(String datumOdStr, String datumDoStr, String system) {
        PrenosnicaMpResponse response = new PrenosnicaMpResponse();
        Connection conn = null;
        PreparedStatement stmSelect = null;
        PreparedStatement stmSelectStavke = null;
        ResultSet rs = null;
        ResultSet rsStavke = null;

        try {
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

            // SQL za selekciju prenosnica
            StringBuilder sql = new StringBuilder("SELECT ozn_pre_mp, dat_knj, sif_obj_ula, sif_obj_izl, vrs_knj, logname, vrs_pre_voz, mal_vre, ozn_nar, storno FROM pren_mp WHERE 1=1");

            if (datumOd != null && datumDo != null) {
                sql.append(" AND dat_knj BETWEEN ? AND ?");
            }

            stmSelect = conn.prepareStatement(sql.toString());

            int paramIndex = 1;
            if (datumOd != null && datumDo != null) {
                stmSelect.setDate(paramIndex++, new java.sql.Date(datumOd.getTime()));
                stmSelect.setDate(paramIndex++, new java.sql.Date(datumDo.getTime()));
            }

            rs = stmSelect.executeQuery();

            List<PrenosnicaMpDTO> prenosnice = new ArrayList<>();
            int recordCount = 0;

            while (rs.next()) {
                recordCount++;

                PrenosnicaMpDTO prenosnica = new PrenosnicaMpDTO();
                prenosnica.setOznakaDokumenta(rs.getString("ozn_pre_mp").trim());
                prenosnica.setDatumDokumenta(rs.getDate("dat_knj"));
                prenosnica.setObjekatUlaza(rs.getString("sif_obj_ula").trim());
                prenosnica.setObjekatIzlaza(rs.getString("sif_obj_izl").trim());
                prenosnica.setLogname(rs.getString("logname"));
                prenosnica.setSystem(system);

                // Postavi vrstu knjiženja
                String vrstaKnjizenja = rs.getString("vrs_knj");
                if ("1".equals(vrstaKnjizenja)) {
                    prenosnica.setVrstaKnjizenja(VrstaKnjizenjaMp.SamoUlaz);
                } else if ("2".equals(vrstaKnjizenja)) {
                    prenosnica.setVrstaKnjizenja(VrstaKnjizenjaMp.SamoIzlaz);
                } else {
                    prenosnica.setVrstaKnjizenja(VrstaKnjizenjaMp.UlazIzlaz);
                }

                // Postavi vrstu prevoza
                String vrstaPrevoza = rs.getString("vrs_pre_voz");
                if ("2".equals(vrstaPrevoza)) {
                    prenosnica.setVrstaPrevoza(VrstaPrevozaMp.externiPrevoz);
                } else {
                    prenosnica.setVrstaPrevoza(VrstaPrevozaMp.interniPrevoz);
                }

                prenosnica.setVrednostSaPorezom(rs.getBigDecimal("mal_vre"));
                prenosnica.setOznakaNarudzbenice(rs.getString("ozn_nar"));

                // Učitaj stavke prenosnice
                stmSelectStavke = conn.prepareStatement(
                        "SELECT sif_rob, kolic, nab_cen, pro_cen_bp, pro_cen, zbi_sto_pp, bar_kod, sif_ent_rob, bro_pak " +
                                "FROM pren_mp_st WHERE ozn_pre_mp = ?");
                stmSelectStavke.setString(1, prenosnica.getOznakaDokumenta());
                rsStavke = stmSelectStavke.executeQuery();

                List<PrenosnicaMpDTO.Stavka> stavke = new ArrayList<>();
                while (rsStavke.next()) {
                    PrenosnicaMpDTO.Stavka stavka = new PrenosnicaMpDTO.Stavka();
                    stavka.setSifraRobe(rsStavke.getString("sif_rob"));
                    stavka.setKolicina(rsStavke.getBigDecimal("kolic"));
                    stavka.setNabavnaCena(rsStavke.getBigDecimal("nab_cen"));
                    stavka.setProdajnaCenaBezPoreza(rsStavke.getBigDecimal("pro_cen_bp"));
                    stavka.setProdajnaCena(rsStavke.getBigDecimal("pro_cen"));
                    stavka.setStopaPoreza(rsStavke.getBigDecimal("zbi_sto_pp"));
                    stavka.setBarkod(rsStavke.getString("bar_kod"));
                    stavka.setSifraObelezja(rsStavke.getString("sif_ent_rob"));
                    stavka.setBrojPakovanja(rsStavke.getBigDecimal("bro_pak"));

                    stavke.add(stavka);
                }

                prenosnica.setStavke(stavke);
                prenosnice.add(prenosnica);

                closeResultSet(rsStavke);
                closePreparedStatement(stmSelectStavke);
            }

            response.setPrenosnice(prenosnice);
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
     * Sačuvaj prenosnicu maloprodaje
     */
    private ResponseEntity<PrenosnicaMpResponse> sacuvajPrenosnicuMaloprodaje(
            Connection conn, PrenosnicaMpDTO prenosnica, Boolean uzmiCeneIzIzlaznogObjekta) throws Exception {

        PrenosnicaMpResponse response = new PrenosnicaMpResponse();
        PreparedStatement stmInsert = null;
        PreparedStatement stmInsertStavke = null;

        try {
            conn.setAutoCommit(false);

            // Insert prenosnica header
            stmInsert = conn.prepareStatement(
                    "INSERT INTO pren_mp (ozn_pre_mp, dat_knj, sif_obj_ula, sif_obj_izl, vrs_knj, logname, " +
                            "vrs_pre_voz, mal_vre, ozn_nar, storno, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

            stmInsert.setString(1, prenosnica.getOznakaDokumenta());
            stmInsert.setDate(2, new java.sql.Date(prenosnica.getDatumDokumenta().getTime()));
            stmInsert.setString(3, prenosnica.getObjekatUlaza());
            stmInsert.setString(4, prenosnica.getObjekatIzlaza());

            // Vrsta knjiženja
            String vrstaKnjizenja = "3"; // Default UlazIzlaz
            if (prenosnica.getVrstaKnjizenja() != null) {
                switch (prenosnica.getVrstaKnjizenja()) {
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

            stmInsert.setString(6, prenosnica.getLogname());

            // Vrsta prevoza
            String vrstaPrevoza = "1"; // Default interni prevoz
            if (prenosnica.getVrstaPrevoza() != null) {
                switch (prenosnica.getVrstaPrevoza()) {
                    case interniPrevoz:
                        vrstaPrevoza = "1";
                        break;
                    case externiPrevoz:
                        vrstaPrevoza = "2";
                        break;
                }
            }
            stmInsert.setString(7, vrstaPrevoza);

            // Opciona polja
            stmInsert.setBigDecimal(8, prenosnica.getVrednostSaPorezom() != null ?
                    prenosnica.getVrednostSaPorezom() : BigDecimal.ZERO);
            stmInsert.setString(9, prenosnica.getOznakaNarudzbenice());
            stmInsert.setString(10, "N"); // Nije stornirano
            stmInsert.setString(11, "0"); // Status

            stmInsert.executeUpdate();

            // Insert stavke
            stmInsertStavke = conn.prepareStatement(
                    "INSERT INTO pren_mp_st (ozn_pre_mp, rbr, sif_rob, kolic, nab_cen, pro_cen_bp, pro_cen, " +
                            "zbi_sto_pp, bar_kod, sif_ent_rob, bro_pak) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

            int rbr = 1;
            for (PrenosnicaMpDTO.Stavka stavka : prenosnica.getStavke()) {
                stmInsertStavke.setString(1, prenosnica.getOznakaDokumenta());
                stmInsertStavke.setInt(2, rbr++);
                stmInsertStavke.setString(3, stavka.getSifraRobe());
                stmInsertStavke.setBigDecimal(4, stavka.getKolicina());

                // Nabavna cena i prodajne cene
                BigDecimal nabavnaCena = stavka.getNabavnaCena() != null ?
                        stavka.getNabavnaCena() : BigDecimal.ZERO;
                BigDecimal prodajnaCenaBezPoreza = stavka.getProdajnaCenaBezPoreza() != null ?
                        stavka.getProdajnaCenaBezPoreza() : BigDecimal.ZERO;
                BigDecimal prodajnaCena = stavka.getProdajnaCena() != null ?
                        stavka.getProdajnaCena() : BigDecimal.ZERO;

                // Ako je potrebno, učitaj cene iz izlaznog objekta
                if (uzmiCeneIzIzlaznogObjekta) {
                    ZaliheMpDTO zalihe = getZaliheObjekatMaloprodaje(
                            stavka.getSifraRobe(), prenosnica.getObjekatIzlaza(), conn);

                    if (zalihe != null && zalihe.getSifraRobe() != null) {
                        nabavnaCena = zalihe.getProsecnaNabavnaCena() != null ?
                                zalihe.getProsecnaNabavnaCena() : nabavnaCena;
                        prodajnaCenaBezPoreza = zalihe.getMaloprodajnaCenaBezPoreza() != null ?
                                zalihe.getMaloprodajnaCenaBezPoreza() : prodajnaCenaBezPoreza;
                        prodajnaCena = zalihe.getMaloprodajnaCena() != null ?
                                zalihe.getMaloprodajnaCena() : prodajnaCena;
                    }
                }

                stmInsertStavke.setBigDecimal(5, nabavnaCena);
                stmInsertStavke.setBigDecimal(6, prodajnaCenaBezPoreza);
                stmInsertStavke.setBigDecimal(7, prodajnaCena);
                stmInsertStavke.setBigDecimal(8, stavka.getStopaPoreza() != null ?
                        stavka.getStopaPoreza() : new BigDecimal("20"));
                stmInsertStavke.setString(9, stavka.getBarkod());
                stmInsertStavke.setString(10, stavka.getSifraObelezja());
                stmInsertStavke.setBigDecimal(11, stavka.getBrojPakovanja() != null ?
                        stavka.getBrojPakovanja() : BigDecimal.ZERO);

                stmInsertStavke.executeUpdate();
            }

            conn.commit();
            response.setResponseResult(true);
            response.setRespResultCount(1);

            // Dodaj oznaku dokumenta u odgovor
            List<PrenosnicaMpDTO> prenosnice = new ArrayList<>();
            prenosnice.add(prenosnica);
            response.setPrenosnice(prenosnice);

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
     * Sačuvaj nalog za prenos maloprodaje
     */
    private ResponseEntity<PrenosnicaMpResponse> sacuvajNalogZaPrenosMaloprodaje(
            Connection conn, PrenosnicaMpDTO prenosnica, Boolean rezervisiZalihe, Boolean uzmiCeneIzIzlaznogObjekta) throws Exception {

        PrenosnicaMpResponse response = new PrenosnicaMpResponse();
        PreparedStatement stmInsert = null;
        PreparedStatement stmInsertStavke = null;

        try {
            conn.setAutoCommit(false);

            // Insert header
            stmInsert = conn.prepareStatement(
                    "INSERT INTO nal_pren_mp (ozn_nal_pre_mp, dat_knj, sif_obj_ula, sif_obj_izl, vrs_knj, logname, " +
                            "vrs_pre_voz, ozn_nar, storno, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

            stmInsert.setString(1, prenosnica.getOznakaDokumenta());
            stmInsert.setDate(2, new java.sql.Date(prenosnica.getDatumDokumenta().getTime()));
            stmInsert.setString(3, prenosnica.getObjekatUlaza());
            stmInsert.setString(4, prenosnica.getObjekatIzlaza());

            // Vrsta knjiženja
            String vrstaKnjizenja = "3"; // Default UlazIzlaz
            if (prenosnica.getVrstaKnjizenja() != null) {
                switch (prenosnica.getVrstaKnjizenja()) {
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

            stmInsert.setString(6, prenosnica.getLogname());

            // Vrsta prevoza
            String vrstaPrevoza = "1"; // Default interni prevoz
            if (prenosnica.getVrstaPrevoza() != null) {
                switch (prenosnica.getVrstaPrevoza()) {
                    case interniPrevoz:
                        vrstaPrevoza = "1";
                        break;
                    case externiPrevoz:
                        vrstaPrevoza = "2";
                        break;
                }
            }
            stmInsert.setString(7, vrstaPrevoza);

            // Opciona polja
            stmInsert.setString(8, prenosnica.getOznakaNarudzbenice() != null && !prenosnica.getOznakaNarudzbenice().isEmpty() ?
                    prenosnica.getOznakaNarudzbenice() : null);
            stmInsert.setString(9, "N"); // Nije stornirano
            stmInsert.setString(10, "1"); // Status - aktivan

            stmInsert.executeUpdate();

            // Insert stavke
            stmInsertStavke = conn.prepareStatement(
                    "INSERT INTO nal_pren_mp_st (ozn_nal_pre_mp, rbr, sif_rob, kolic, nab_cen, pro_cen_bp, pro_cen, " +
                            "zbi_sto_pp, bar_kod, sif_ent_rob, bro_pak) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

            int rbr = 1;
            for (PrenosnicaMpDTO.Stavka stavka : prenosnica.getStavke()) {
                stmInsertStavke.setString(1, prenosnica.getOznakaDokumenta());
                stmInsertStavke.setInt(2, rbr++);
                stmInsertStavke.setString(3, stavka.getSifraRobe());
                stmInsertStavke.setBigDecimal(4, stavka.getKolicina());

                // Nabavna cena i prodajne cene
                BigDecimal nabavnaCena = stavka.getNabavnaCena() != null ?
                        stavka.getNabavnaCena() : BigDecimal.ZERO;
                BigDecimal prodajnaCenaBezPoreza = stavka.getProdajnaCenaBezPoreza() != null ?
                        stavka.getProdajnaCenaBezPoreza() : BigDecimal.ZERO;
                BigDecimal prodajnaCena = stavka.getProdajnaCena() != null ?
                        stavka.getProdajnaCena() : BigDecimal.ZERO;

                // Ako je potrebno, učitaj cene iz izlaznog objekta
                if (uzmiCeneIzIzlaznogObjekta) {
                    ZaliheMpDTO zalihe = getZaliheObjekatMaloprodaje(
                            stavka.getSifraRobe(), prenosnica.getObjekatIzlaza(), conn);

                    if (zalihe != null && zalihe.getSifraRobe() != null) {
                        nabavnaCena = zalihe.getProsecnaNabavnaCena() != null ?
                                zalihe.getProsecnaNabavnaCena() : nabavnaCena;
                        prodajnaCenaBezPoreza = zalihe.getMaloprodajnaCenaBezPoreza() != null ?
                                zalihe.getMaloprodajnaCenaBezPoreza() : prodajnaCenaBezPoreza;
                        prodajnaCena = zalihe.getMaloprodajnaCena() != null ?
                                zalihe.getMaloprodajnaCena() : prodajnaCena;
                    }
                }

                stmInsertStavke.setBigDecimal(5, nabavnaCena);
                stmInsertStavke.setBigDecimal(6, prodajnaCenaBezPoreza);
                stmInsertStavke.setBigDecimal(7, prodajnaCena);
                stmInsertStavke.setBigDecimal(8, stavka.getStopaPoreza() != null ?
                        stavka.getStopaPoreza() : new BigDecimal("20"));
                stmInsertStavke.setString(9, stavka.getBarkod());
                stmInsertStavke.setString(10, stavka.getSifraObelezja());
                stmInsertStavke.setBigDecimal(11, stavka.getBrojPakovanja() != null ?
                        stavka.getBrojPakovanja() : BigDecimal.ZERO);

                stmInsertStavke.executeUpdate();

                // Ako je potrebno, rezerviši zalihe
                if (rezervisiZalihe) {
                    rezervisiKolicinu(
                            stavka.getSifraRobe(),
                            prenosnica.getObjekatIzlaza(),
                            stavka.getKolicina(),
                            prenosnica.getOznakaDokumenta(),
                            prenosnica.getLogname(),
                            "Nalog za prenos u objekat: " + prenosnica.getObjekatUlaza(),
                            stavka.getSifraObelezja(),
                            conn
                    );
                }
            }

            conn.commit();
            response.setResponseResult(true);
            response.setRespResultCount(1);

            // Dodaj oznaku dokumenta u odgovor
            List<PrenosnicaMpDTO> prenosnice = new ArrayList<>();
            prenosnice.add(prenosnica);
            response.setPrenosnice(prenosnice);

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

    // Provera vrednosti parametra
    private Boolean checkParameterValue(Connection conn, String paramName, String user) throws Exception {
        PreparedStatement stm = null;
        ResultSet rs = null;
        try {
            stm = conn.prepareStatement("SELECT par_val FROM dd_params WHERE par_nam = ? AND r_user = ?");
            stm.setString(1, paramName);
            stm.setString(2, user);
            rs = stm.executeQuery();

            if (rs.next()) {
                return "1".equals(rs.getString("par_val"));
            }
            return false;
        } finally {
            closeResultSet(rs);
            closePreparedStatement(stm);
        }
    }

    // Dobavljanje zaliha iz objekta maloprodaje
    private ZaliheMpDTO getZaliheObjekatMaloprodaje(String sifraRobe, String objekatMaloprodaje, Connection conn) throws Exception {
        PreparedStatement stm = null;
        ResultSet rs = null;
        ZaliheMpDTO zalihe = new ZaliheMpDTO();

        try {
            stm = conn.prepareStatement(
                    "SELECT sif_rob, pro_nab_cen, pro_vp_cen, vel_cen, mal_cen, mal_cen_bp " +
                            "FROM zal_obj_mp WHERE sif_rob = ? AND sif_obj_mp = ?");
            stm.setString(1, sifraRobe);
            stm.setString(2, objekatMaloprodaje);
            rs = stm.executeQuery();

            if (rs.next()) {
                zalihe.setSifraRobe(rs.getString("sif_rob"));
                zalihe.setProsecnaNabavnaCena(rs.getBigDecimal("pro_nab_cen"));
                zalihe.setVeleprodajnaCena(rs.getBigDecimal("vel_cen"));
                zalihe.setMaloprodajnaCena(rs.getBigDecimal("mal_cen"));
                zalihe.setMaloprodajnaCenaBezPoreza(rs.getBigDecimal("mal_cen_bp"));
            }

            return zalihe;
        } finally {
            closeResultSet(rs);
            closePreparedStatement(stm);
        }
    }

    // Rezervacija količine
    private void rezervisiKolicinu(String sifraRobe, String objekatMaloprodaje, BigDecimal kolicina,
                                   String oznakaDokumenta, String logname, String napomena, String sifraObelezja,
                                   Connection conn) throws Exception {
        PreparedStatement stm = null;

        try {
            stm = conn.prepareStatement(
                    "INSERT INTO rez_mp (sif_obj_mp, sif_rob, dat_rez, kol_rez, ozn_dok, vrs_dok, logname, napomena, sif_ent_rob) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");

            stm.setString(1, objekatMaloprodaje);
            stm.setString(2, sifraRobe);
            stm.setDate(3, new java.sql.Date(new Date().getTime()));
            stm.setBigDecimal(4, kolicina);
            stm.setString(5, oznakaDokumenta);
            stm.setString(6, "N5"); // Oznaka za nalog za prenos
            stm.setString(7, logname);
            stm.setString(8, napomena);
            stm.setString(9, sifraObelezja);

            stm.executeUpdate();
        } finally {
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
                                PreparedStatement stm3, PreparedStatement stm4, ResultSet rs1, ResultSet rs2) {
        closeResultSet(rs1);
        closeResultSet(rs2);
        closePreparedStatement(stm1);
        closePreparedStatement(stm2);
        closePreparedStatement(stm3);
        closePreparedStatement(stm4);
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