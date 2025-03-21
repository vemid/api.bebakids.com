package com.example.apibebakids.service;

import com.example.apibebakids.model.*;
import com.example.apibebakids.service.PriceCheckService;
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
 * Servis za rad sa otpremnicama maloprodaje
 */
@Service
public class OtpremnicaMpService {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yy");

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

    @Autowired
    private PriceCheckService priceCheckService;

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
     * Dodaje otpremnicu u maloprodaju
     */
    public ResponseEntity<OtpremnicaMpResponse> dodajOtpremnicu(
            String sifraMagacina, String sifraObjektaMaloprodaje, String system, String items,
            String oznakaCenovnika, String logname, String napomena) {

        OtpremnicaMpResponse response = new OtpremnicaMpResponse();
        Connection conn = null;
        PreparedStatement stmSelect = null;
        PreparedStatement stmInsert = null;
        PreparedStatement stmInsertStavke = null;
        ResultSet rSet = null;

        try {
            // Validacija obaveznih polja
            if (sifraMagacina == null || sifraObjektaMaloprodaje == null || system == null || items == null ||
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

            // Dobavi DataSource za odgovarajući sistem
            DataSource dataSource = getDataSourceForSystem(system);

            // Sačuvaj u bazu
            conn = dataSource.getConnection();

            // Kreiraj OtpremnicaMpDTO objekat
            OtpremnicaMpDTO otpremnica = new OtpremnicaMpDTO();
            otpremnica.setDatumDokumenta(new Date());
            otpremnica.setSifraMagacina(sifraMagacina);
            otpremnica.setSifraObjektaMaloprodaje(sifraObjektaMaloprodaje);
            otpremnica.setStorno("N");
            otpremnica.setLogname(logname);
            otpremnica.setNapomena(napomena);
            otpremnica.setOznakaCenovnika(oznakaCenovnika);
            otpremnica.setVrstaKnjizenja("3"); // Default UlazIzlaz
            otpremnica.setSystem(system);

            // Generisanje oznake dokumenta
            String sifraOrganizacije = getSifraOrganizacije(conn, sifraObjektaMaloprodaje);
            String poslovnaGodina = sdf.format(otpremnica.getDatumDokumenta());
            String prefix = sifraOrganizacije + "/" + poslovnaGodina + sifraObjektaMaloprodaje.trim();
            String oznakaDokumenta = documentUtils.generateDocumentNumber("OM", system, sifraMagacina, "");

            otpremnica.setOznakaDokumenta(oznakaDokumenta);

            // Parse items
            List<OtpremnicaMpDTO.Stavka> stavke = new ArrayList<>();
            for (int i = 0; i < itemsArray.length(); i++) {
                JSONObject itemJson = itemsArray.getJSONObject(i);
                OtpremnicaMpDTO.Stavka stavka = new OtpremnicaMpDTO.Stavka();

                // Obavezna polja
                stavka.setSifraRobe(itemJson.getString("sku"));
                stavka.setSifraObelezja(itemJson.getString("size"));
                stavka.setKolicina(new BigDecimal(itemJson.getString("qty")));

                // Podrazumevane i opcione vrednosti
                stavka.setSifraTarifneGrupePoreza("1"); // Default tarifna grupa PDV-a
                stavka.setSifraTarifneGrupeTakse("");
                stavka.setStopaPDV(new BigDecimal("20")); // Default stopa PDV-a
                stavka.setIznosAkcize(BigDecimal.ZERO);
                stavka.setIznosTakse(BigDecimal.ZERO);
                stavka.setBrojPakovanja(BigDecimal.ONE);
                stavka.setStopaRabata(BigDecimal.ZERO);
                stavka.setPosebnaStopaRabata(BigDecimal.ZERO);
                stavka.setAkcijskaStopaRabata(BigDecimal.ZERO);

                // Opciona polja iz JSON-a
                if (itemJson.has("zoneMagacina")) {
                    stavka.setSifraZoneMagacina(itemJson.getString("zoneMagacina"));
                }

                if (itemJson.has("sifraPakovanja")) {
                    stavka.setSifraPakovanja(itemJson.getString("sifraPakovanja"));
                }

                if (itemJson.has("brojPakovanja")) {
                    stavka.setBrojPakovanja(new BigDecimal(itemJson.getString("brojPakovanja")));
                }

                if (itemJson.has("stopaPDV")) {
                    stavka.setStopaPDV(new BigDecimal(itemJson.getString("stopaPDV")));
                }

                // Cene
                if (itemJson.has("cenaZalihe")) {
                    stavka.setCenaZalihe(new BigDecimal(itemJson.getString("cenaZalihe")));
                }

                if (itemJson.has("osnovnaCena")) {
                    stavka.setOsnovnaCena(new BigDecimal(itemJson.getString("osnovnaCena")));
                }

                if (itemJson.has("prodajnaCena")) {
                    stavka.setProdajnaCena(new BigDecimal(itemJson.getString("prodajnaCena")));
                }

                if (itemJson.has("prodajnaCenaBezPoreza")) {
                    stavka.setProdajnaCenaBezPoreza(new BigDecimal(itemJson.getString("prodajnaCenaBezPoreza")));
                }

                if (itemJson.has("maloprodajnaMarza")) {
                    stavka.setMaloprodajnaMarza(new BigDecimal(itemJson.getString("maloprodajnaMarza")));
                }

                // Proveri da li su cene dostupne, ako nisu, učitaj ih
                boolean potrebnoUcitatiCene = stavka.getCenaZalihe() == null ||
                        stavka.getOsnovnaCena() == null ||
                        stavka.getProdajnaCena() == null ||
                        stavka.getProdajnaCenaBezPoreza() == null;

                if (potrebnoUcitatiCene) {
                    // Učitaj cene iz baze
                    ucitajCeneZaStavku(stavka, sifraMagacina, system, oznakaCenovnika);
                }

                // Ako su cene i dalje null, postavi default vrednosti
                if (stavka.getCenaZalihe() == null) {
                    stavka.setCenaZalihe(BigDecimal.ZERO);
                }
                if (stavka.getOsnovnaCena() == null) {
                    stavka.setOsnovnaCena(BigDecimal.ZERO);
                }
                if (stavka.getProdajnaCena() == null) {
                    stavka.setProdajnaCena(BigDecimal.ZERO);
                }
                if (stavka.getProdajnaCenaBezPoreza() == null) {
                    stavka.setProdajnaCenaBezPoreza(BigDecimal.ZERO);
                }
                if (stavka.getMaloprodajnaMarza() == null) {
                    stavka.setMaloprodajnaMarza(BigDecimal.ZERO);
                }

                // Prodajnu cenu sa rabatom postaviti na prodajnu cenu bez poreza ako nije specificirana
                if (stavka.getProdajnaCenaSaRabatom() == null) {
                    stavka.setProdajnaCenaSaRabatom(stavka.getProdajnaCenaBezPoreza());
                }

                stavke.add(stavka);
            }
            otpremnica.setStavke(stavke);

            // Provera da li dokument već postoji
            stmSelect = conn.prepareStatement("SELECT * FROM otprem_mp WHERE ozn_otp_mal = ?");
            stmSelect.setString(1, otpremnica.getOznakaDokumenta());
            rSet = stmSelect.executeQuery();

            if (rSet.next()) {
                response.setResponseResult(false);
                response.setErrorMessage("Dokument sa oznakom " + otpremnica.getOznakaDokumenta() + " već postoji");
                response.setRespResultCount(0);
                return ResponseEntity.badRequest().body(response);
            }

            // Sačuvaj otpremnicu
            return sacuvajOtpremnicuMaloprodaje(conn, otpremnica);

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
     * Učitaj cene za stavku iz baze
     */
    private void ucitajCeneZaStavku(OtpremnicaMpDTO.Stavka stavka, String sifraMagacina, String system, String pricelist) {
        try {
            // Koristimo postojeći priceCheckService
            PriceInfoDTO prices = priceCheckService.checkPrices(system, sifraMagacina, stavka.getSifraRobe(), pricelist);

            // Postavi cene na osnovu rezultata
            stavka.setCenaZalihe(BigDecimal.valueOf(prices.getCenZal()));
            stavka.setProdajnaCenaBezPoreza(BigDecimal.valueOf(prices.getMpBezPdv()));
            stavka.setOsnovnaCena(BigDecimal.valueOf(prices.getMpSaPdv()));
            stavka.setProdajnaCena(BigDecimal.valueOf(prices.getMpSaPdv()));
            stavka.setProdajnaCenaSaRabatom(BigDecimal.valueOf(prices.getMpSaPdv()));
            stavka.setSifraTarifneGrupePoreza(String.valueOf((int)prices.getTarifnaGrupa()));
            stavka.setStopaPDV(BigDecimal.valueOf(prices.getStopaPoreza()));

            // Izračunaj maloprodajnu maržu ako imamo nabavnu cenu
            if (stavka.getCenaZalihe() != null && !stavka.getCenaZalihe().equals(BigDecimal.ZERO)) {
                BigDecimal razlika = stavka.getProdajnaCenaBezPoreza().subtract(stavka.getCenaZalihe());
                BigDecimal marza = razlika.multiply(new BigDecimal(100)).divide(stavka.getCenaZalihe(), 2, BigDecimal.ROUND_HALF_UP);
                stavka.setMaloprodajnaMarza(marza);
            }
        } catch (Exception e) {
            // Loguj grešku, ali nastavi dalje
            System.err.println("Greška prilikom dobavljanja cena: " + e.getMessage());
        }
    }
    /**
     * Sačuvaj otpremnicu maloprodaje
     */
    private ResponseEntity<OtpremnicaMpResponse> sacuvajOtpremnicuMaloprodaje(
            Connection conn, OtpremnicaMpDTO otpremnica) throws Exception {

        OtpremnicaMpResponse response = new OtpremnicaMpResponse();
        PreparedStatement stmInsert = null;
        PreparedStatement stmInsertStavke = null;

        try {
            conn.setAutoCommit(false);

            // Insert otpremnica header
            stmInsert = conn.prepareStatement(
                    "INSERT INTO otprem_mp (ozn_otp_mal, dat_otp_mal, sif_mag, sif_obj_mp, storno, logname, " +
                            "status, ozn_cen, vrs_por, vrs_knj, datum_promene, napomena) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

            stmInsert.setString(1, otpremnica.getOznakaDokumenta());
            stmInsert.setDate(2, new java.sql.Date(otpremnica.getDatumDokumenta().getTime()));
            stmInsert.setString(3, otpremnica.getSifraMagacina());
            stmInsert.setString(4, otpremnica.getSifraObjektaMaloprodaje());
            stmInsert.setString(5, "N"); // Nije stornirano
            stmInsert.setString(6, otpremnica.getLogname());
            stmInsert.setString(7, "0"); // Status
            stmInsert.setString(8, otpremnica.getOznakaCenovnika());
            stmInsert.setString(9, "1"); // Vrsta poreza
            stmInsert.setString(10, otpremnica.getVrstaKnjizenja() != null ? otpremnica.getVrstaKnjizenja() : "2"); // Default UlazIzlaz
            stmInsert.setDate(11, new java.sql.Date(new Date().getTime())); // Datum promene
            stmInsert.setString(12, otpremnica.getNapomena());

            stmInsert.executeUpdate();

            // Insert stavke
            stmInsertStavke = conn.prepareStatement(
                    "INSERT INTO otprem_mp_st (ozn_otp_mal, rbr, sif_zon_mag, sif_rob, sif_ent_rob, kolic, kolic1, " +
                            "cen_zal, cen_zal1, cen_osn, cen_pro, tar_gru_pp, zbi_sto_pp, izn_akc, izn_tak, " +  // Uklonjeno tar_gru_ta
                            "cen_osn1, cen_pro1, mal_mar, cen_pro_bp, sif_pak, bro_pak, sto_rab, cen_rab, cen_rab1, kol_nal, " +
                            "pos_sto_rab, akc_sto_rab, napomena, spec_tezina, sif_ode, vp_sto_rab, vp_pos_sto_rab, vp_akc_sto_rab, " +
                            "vp_dod_rab, cen_rab_vp, nov_nab_cen, min_mal_mar, nem_rab) VALUES " +
                            "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");  // Uklonjeno jedno ?

            int rbr = 1;
            for (OtpremnicaMpDTO.Stavka stavka : otpremnica.getStavke()) {
                stmInsertStavke.setString(1, otpremnica.getOznakaDokumenta());
                stmInsertStavke.setInt(2, rbr++);
                stmInsertStavke.setString(3, stavka.getSifraZoneMagacina());
                stmInsertStavke.setString(4, stavka.getSifraRobe());
                stmInsertStavke.setString(5, stavka.getSifraObelezja());
                stmInsertStavke.setBigDecimal(6, stavka.getKolicina());
                stmInsertStavke.setBigDecimal(7, null); // kolic1
                stmInsertStavke.setBigDecimal(8, stavka.getCenaZalihe());
                stmInsertStavke.setBigDecimal(9, null); // cen_zal1
                stmInsertStavke.setBigDecimal(10, stavka.getOsnovnaCena());
                stmInsertStavke.setBigDecimal(11, stavka.getProdajnaCena());
                stmInsertStavke.setString(12, stavka.getSifraTarifneGrupePoreza()); // tar_gru_pp
                stmInsertStavke.setBigDecimal(13, stavka.getStopaPDV());
                // Uklonjena linija za tar_gru_ta koja je bila indeks 14
                stmInsertStavke.setBigDecimal(14, stavka.getIznosAkcize() != null ? stavka.getIznosAkcize() : BigDecimal.ZERO);
                stmInsertStavke.setBigDecimal(15, stavka.getIznosTakse() != null ? stavka.getIznosTakse() : BigDecimal.ZERO);
                stmInsertStavke.setBigDecimal(16, null); // cen_osn1
                stmInsertStavke.setBigDecimal(17, null); // cen_pro1
                stmInsertStavke.setBigDecimal(18, stavka.getMaloprodajnaMarza());
                stmInsertStavke.setBigDecimal(19, stavka.getProdajnaCenaBezPoreza());
                stmInsertStavke.setString(20, stavka.getSifraPakovanja());
                stmInsertStavke.setBigDecimal(21, stavka.getBrojPakovanja());
                stmInsertStavke.setBigDecimal(22, stavka.getStopaRabata());
                stmInsertStavke.setBigDecimal(23, stavka.getProdajnaCenaSaRabatom());
                stmInsertStavke.setBigDecimal(24, null); // cen_rab1
                stmInsertStavke.setBigDecimal(25, BigDecimal.ZERO); // kol_nal
                stmInsertStavke.setBigDecimal(26, stavka.getPosebnaStopaRabata());
                stmInsertStavke.setBigDecimal(27, stavka.getAkcijskaStopaRabata());
                stmInsertStavke.setString(28, stavka.getNapomena());
                stmInsertStavke.setBigDecimal(29, BigDecimal.ZERO); // spec_tezina
                stmInsertStavke.setString(30, stavka.getSifraOdeljka());
                stmInsertStavke.setBigDecimal(31, BigDecimal.ZERO); // vp_sto_rab
                stmInsertStavke.setBigDecimal(32, BigDecimal.ZERO); // vp_pos_sto_rab
                stmInsertStavke.setBigDecimal(33, BigDecimal.ZERO); // vp_akc_sto_rab
                stmInsertStavke.setBigDecimal(34, BigDecimal.ZERO); // vp_dod_rab
                stmInsertStavke.setBigDecimal(35, stavka.getProdajnaCenaBezPoreza()); // cen_rab_vp
                stmInsertStavke.setBigDecimal(36, BigDecimal.ZERO); // nov_nab_cen
                stmInsertStavke.setBigDecimal(37, BigDecimal.ZERO); // min_mal_mar
                stmInsertStavke.setString(38, "0"); // nem_rab

                stmInsertStavke.executeUpdate();
            }

            conn.commit();
            response.setResponseResult(true);
            response.setRespResultCount(1);

            // Dodaj oznaku dokumenta u odgovor
            List<OtpremnicaMpDTO> otpremnice = new ArrayList<>();
            otpremnice.add(otpremnica);
            response.setOtpremnice(otpremnice);

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
     * Nabavlja šifru organizacije za objekat maloprodaje
     */
    private String getSifraOrganizacije(Connection conn, String sifraObjektaMaloprodaje) throws Exception {
        PreparedStatement stm = null;
        ResultSet rs = null;
        String sifraOrganizacije = "00";

        try {
            stm = conn.prepareStatement(
                    "SELECT oj.sif_norg FROM obj_mp o, org_jed oj WHERE o.sif_org_jed = oj.sif_org_jed AND o.sif_obj_mp = ?");
            stm.setString(1, sifraObjektaMaloprodaje);
            rs = stm.executeQuery();

            if (rs.next()) {
                sifraOrganizacije = rs.getString("sif_norg");
                if (sifraOrganizacije == null || sifraOrganizacije.trim().isEmpty()) {
                    sifraOrganizacije = "00";
                }
            }

            return sifraOrganizacije;
        } finally {
            closeResultSet(rs);
            closePreparedStatement(stm);
        }
    }

    /**
     * Nabavlja brojač za oznaku dokumenta
     */
    private String getBrojac(Connection conn, String vrsta, String prefix) throws Exception {
        PreparedStatement stmSelect = null;
        PreparedStatement stmUpdate = null;
        ResultSet rs = null;
        String brojac = "1";

        try {
            stmSelect = conn.prepareStatement(
                    "SELECT vrednost FROM brojaci WHERE vrsta = ? AND prefix = ?");
            stmSelect.setString(1, vrsta);
            stmSelect.setString(2, prefix);
            rs = stmSelect.executeQuery();

            if (rs.next()) {
                brojac = rs.getString("vrednost");
                // Inkrementiranje brojača
                stmUpdate = conn.prepareStatement(
                        "UPDATE brojaci SET vrednost = ? WHERE vrsta = ? AND prefix = ?");
                int novaBrojacVrednost = Integer.parseInt(brojac) + 1;
                stmUpdate.setString(1, String.valueOf(novaBrojacVrednost));
                stmUpdate.setString(2, vrsta);
                stmUpdate.setString(3, prefix);
                stmUpdate.executeUpdate();
            } else {
                // Kreiraj novi brojač
                stmUpdate = conn.prepareStatement(
                        "INSERT INTO brojaci (vrsta, prefix, vrednost) VALUES (?, ?, ?)");
                stmUpdate.setString(1, vrsta);
                stmUpdate.setString(2, prefix);
                stmUpdate.setString(3, "2"); // Postavi sledeću vrednost
                stmUpdate.executeUpdate();
            }

            return brojac;
        } finally {
            closeResultSet(rs);
            closePreparedStatement(stmSelect);
            closePreparedStatement(stmUpdate);
        }
    }

    /**
     * Dobavlja otpremnice
     */
    public ResponseEntity<OtpremnicaMpResponse> getOtpremnice(String datumOdStr, String datumDoStr, String system) {
        OtpremnicaMpResponse response = new OtpremnicaMpResponse();
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

            // SQL za selekciju otpremnica
            StringBuilder sql = new StringBuilder(
                    "SELECT ozn_otp_mal, dat_otp_mal, sif_mag, sif_obj_mp, storno, logname, " +
                            "status, ozn_cen, vrs_por, vrs_knj, napomena FROM otprem_mp WHERE 1=1");

            if (datumOd != null && datumDo != null) {
                sql.append(" AND dat_otp_mal BETWEEN ? AND ?");
            }

            stmSelect = conn.prepareStatement(sql.toString());

            int paramIndex = 1;
            if (datumOd != null && datumDo != null) {
                stmSelect.setDate(paramIndex++, new java.sql.Date(datumOd.getTime()));
                stmSelect.setDate(paramIndex++, new java.sql.Date(datumDo.getTime()));
            }

            rs = stmSelect.executeQuery();

            List<OtpremnicaMpDTO> otpremnice = new ArrayList<>();
            int recordCount = 0;

            while (rs.next()) {
                recordCount++;

                OtpremnicaMpDTO otpremnica = new OtpremnicaMpDTO();
                otpremnica.setOznakaDokumenta(rs.getString("ozn_otp_mal"));
                otpremnica.setDatumDokumenta(rs.getDate("dat_otp_mal"));
                otpremnica.setSifraMagacina(rs.getString("sif_mag"));
                otpremnica.setSifraObjektaMaloprodaje(rs.getString("sif_obj_mp"));
                otpremnica.setStorno(rs.getString("storno"));
                otpremnica.setLogname(rs.getString("logname"));
                otpremnica.setOznakaCenovnika(rs.getString("ozn_cen"));
                otpremnica.setVrstaKnjizenja(rs.getString("vrs_knj"));
                otpremnica.setNapomena(rs.getString("napomena"));
                otpremnica.setSystem(system);

                // Učitaj stavke otpremnice
                stmSelectStavke = conn.prepareStatement(
                        "SELECT sif_zon_mag, sif_rob, sif_ent_rob, kolic, cen_zal, cen_osn, cen_pro, " +
                                "tar_gru_pp, zbi_sto_pp, tar_gru_ta, izn_akc, izn_tak, mal_mar, cen_pro_bp, " +
                                "sif_pak, bro_pak, sto_rab, cen_rab, pos_sto_rab, akc_sto_rab, napomena, sif_ode " +
                                "FROM otprem_mp_st WHERE ozn_otp_mal = ?");
                stmSelectStavke.setString(1, otpremnica.getOznakaDokumenta());
                rsStavke = stmSelectStavke.executeQuery();

                List<OtpremnicaMpDTO.Stavka> stavke = new ArrayList<>();
                while (rsStavke.next()) {
                    OtpremnicaMpDTO.Stavka stavka = new OtpremnicaMpDTO.Stavka();
                    stavka.setSifraZoneMagacina(rsStavke.getString("sif_zon_mag"));
                    stavka.setSifraRobe(rsStavke.getString("sif_rob"));
                    stavka.setSifraObelezja(rsStavke.getString("sif_ent_rob"));
                    stavka.setKolicina(rsStavke.getBigDecimal("kolic"));
                    stavka.setCenaZalihe(rsStavke.getBigDecimal("cen_zal"));
                    stavka.setOsnovnaCena(rsStavke.getBigDecimal("cen_osn"));
                    stavka.setProdajnaCena(rsStavke.getBigDecimal("cen_pro"));
                    stavka.setSifraTarifneGrupePoreza(rsStavke.getString("tar_gru_pp"));
                    stavka.setStopaPDV(rsStavke.getBigDecimal("zbi_sto_pp"));
                    stavka.setSifraTarifneGrupeTakse(rsStavke.getString("tar_gru_ta"));
                    stavka.setIznosAkcize(rsStavke.getBigDecimal("izn_akc"));
                    stavka.setIznosTakse(rsStavke.getBigDecimal("izn_tak"));
                    stavka.setMaloprodajnaMarza(rsStavke.getBigDecimal("mal_mar"));
                    stavka.setProdajnaCenaBezPoreza(rsStavke.getBigDecimal("cen_pro_bp"));
                    stavka.setSifraPakovanja(rsStavke.getString("sif_pak"));
                    stavka.setBrojPakovanja(rsStavke.getBigDecimal("bro_pak"));
                    stavka.setStopaRabata(rsStavke.getBigDecimal("sto_rab"));
                    stavka.setProdajnaCenaSaRabatom(rsStavke.getBigDecimal("cen_rab"));
                    stavka.setPosebnaStopaRabata(rsStavke.getBigDecimal("pos_sto_rab"));
                    stavka.setAkcijskaStopaRabata(rsStavke.getBigDecimal("akc_sto_rab"));
                    stavka.setNapomena(rsStavke.getString("napomena"));
                    stavka.setSifraOdeljka(rsStavke.getString("sif_ode"));

                    stavke.add(stavka);
                }

                otpremnica.setStavke(stavke);
                otpremnice.add(otpremnica);

                closeResultSet(rsStavke);
                closePreparedStatement(stmSelectStavke);
            }

            response.setOtpremnice(otpremnice);
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