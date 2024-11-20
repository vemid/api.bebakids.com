package com.example.apibebakids.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Qualifier;

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
    public double[] checkPrices(String system, String storeCode, String sku, String pricelist) {
        String sql = "select first 1 cen_zal cenZal,round(c.mal_cen*0.8333,2) mp_bpdv, c.mal_cen mp,c.vel_cen vp from zal_robe_mag z " +
                "left join proiz_cen_st c on c.sif_rob = z.sif_rob and c.sta_cen_st = 'A' and c.ozn_cen = ? " +
                "where c.ozn_cen = ? and z.sif_rob = ? and z.sif_mag = ?";

        try {
            JdbcTemplate jdbcTemplate = getJdbcTemplate(system);

            return jdbcTemplate.queryForObject(sql, new Object[]{pricelist, pricelist, sku,storeCode},
                    (rs, rowNum) -> new double[]{
                            rs.getDouble("cenZal"),
                            rs.getDouble("mp_bpdv"),
                            rs.getDouble("mp"),
                            rs.getDouble("vp")
                    });
        } catch (Exception e) {
            // Handle exception (e.g., no result found)
            return new double[]{0.0, 0.0, 0.0, 0.0};
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
}