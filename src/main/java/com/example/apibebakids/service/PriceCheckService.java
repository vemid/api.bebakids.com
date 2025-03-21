package com.example.apibebakids.service;

import com.example.apibebakids.model.PriceInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;


@Service
public class PriceCheckService {

    @Autowired
    @Qualifier("jdbcTemplateBebaKids")
    private JdbcTemplate jdbcTemplateBebaKids;

    @Autowired
    @Qualifier("jdbcTemplateWatch")
    private JdbcTemplate jdbcTemplateWatch;

    @Autowired
    @Qualifier("jdbcTemplateGeox")
    private JdbcTemplate jdbcTemplateGeox;
    @Qualifier("jdbcTemplateBebaKidsBih")
    @Autowired
    private JdbcTemplate jdbcTemplateBebaKidsBih;

    private JdbcTemplate getJdbcTemplate(String system) {
        switch (system.toLowerCase()) {
            case "bebakids":
                return jdbcTemplateBebaKids;
            case "watch":
                return jdbcTemplateWatch;
            case "geox":
                return jdbcTemplateGeox;
            case "bebakidsbih":
                return jdbcTemplateBebaKidsBih;
            default:
                throw new IllegalArgumentException("Invalid system: " + system);
        }
    }

    // New method to check multiple prices
    public PriceInfoDTO checkPrices(String system, String storeCode, String sku, String pricelist) {
        String sql = "select first 1 cen_zal cenZal, c.mal_cen mp, c.vel_cen vp, r.tar_gru1, t.sto_pp1 " +
                "from zal_robe_mag z " +
                "left join proiz_cen_st c on c.sif_rob = z.sif_rob and c.sta_cen_st = 'A' and c.ozn_cen = ? " +
                "left join roba r on r.sif_rob = z.sif_rob " +
                "left join (SELECT tar_gru_pp, sif_pod, sto_pp1 " +
                "FROM tarifa_pp order by tar_gru_pp, sif_pod, datum desc) t " +
                "on t.tar_gru_pp = r.tar_gru1 and t.sif_pod = ? " +
                "where c.ozn_cen = ? and z.sif_rob = ? and z.sif_mag = ?";
        int taxArea = getTaxArea(system);
        try {
            JdbcTemplate jdbcTemplate = getJdbcTemplate(system);


            return jdbcTemplate.queryForObject(sql,
                    new Object[]{pricelist, taxArea, pricelist, sku, storeCode},
                    (rs, rowNum) -> {
                        double mp = rs.getDouble("mp");
                        double stopp1 = rs.getDouble("sto_pp1");
                        // Izračunaj cenu bez PDV-a na osnovu procenta poreza
                        double mp_bpdv = stopp1 > 0 ? mp / (1 + (stopp1/100)) : mp;
                        mp_bpdv = Math.round(mp_bpdv * 100) / 100.0; // Zaokruživanje na 2 decimale

                        return new PriceInfoDTO(
                                rs.getDouble("cenZal"),
                                mp_bpdv,
                                mp,
                                rs.getDouble("vp"),
                                rs.getDouble("tar_gru1"),
                                stopp1
                        );
                    });
        } catch (EmptyResultDataAccessException e) {
            // Specifično hvatamo slučaj kada nema rezultata
            return new PriceInfoDTO(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
        } catch (Exception e) {
            // Hvatamo ostale greške
            System.err.println("Error in checkPrices: " + e.getMessage());
            e.printStackTrace();
            return new PriceInfoDTO(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
        }
    }

    // Existing method (kept for backward compatibility)
    public Double checkPrice(String system, String sifMag, String sifRob) {
        String sql = "SELECT cen_zal nab_cen FROM zal_robe_mag WHERE sif_mag = ? AND sif_rob = ?";

        try {
            JdbcTemplate jdbcTemplate = getJdbcTemplate(system);
            return jdbcTemplate.queryForObject(sql, new Object[]{sifMag, sifRob}, Double.class);
        } catch (Exception e) {
            // Handle exception (e.g., no result found)
            return null;
        }
    }

    public String getFransizeData(String system, String storeCodeTo) {
        String sql = "SELECT NVL(sif_par_obj, '000000') AS pos_par FROM obj_mp WHERE sif_obj_mp = ?";

        try {
            JdbcTemplate jdbcTemplate = getJdbcTemplate(system);

            return jdbcTemplate.queryForObject(sql, new Object[]{storeCodeTo},
                    (rs, rowNum) -> rs.getString("pos_par"));
        } catch (Exception e) {
            // Handle exception (e.g., no result found)
            return "000000";
        }
    }

    public int getTaxArea(String system) {
        switch (system.toLowerCase()) {
            case "bebakids":
                return 1;
            case "watch":
                return 1;
            case "geox":
                return 1;
            case "bebakidsbih":
                return 7;
            case "bebakidsmne":
                return 3;
            default:
                throw new IllegalArgumentException("Invalid system: " + system);
        }
    }
}