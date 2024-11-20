package com.example.apibebakids.service;

import com.example.apibebakids.model.BarcodeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;

@Service
public class DatabaseService {

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

    // Method to select appropriate JdbcTemplate based on system
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

    public BarcodeResponse getBarcodes(String system) {
        String sql = """
            select r.sif_rob, r.naz_rob, e.sif_ent_rob, e.bar_kod, 
            nvl(get_nabavna(r.sif_rob), 0) as nab_cen, 
            nvl(get_cena(r.sif_rob, 'mp'), 0) as mal_cen 
            from ean_kod e
            left join roba r on r.sif_Rob = e.sif_rob
            where r.rok_tra is null
        """;

        BarcodeResponse response = new BarcodeResponse();
        try {
            JdbcTemplate jdbcTemplate = getJdbcTemplate(system);

            List<BarcodeResponse.BarcodeData> barcodeDataList = jdbcTemplate.query(sql, (rs, rowNum) -> {
                BarcodeResponse.BarcodeData barcodeData = new BarcodeResponse.BarcodeData();
                barcodeData.setSifRob(rs.getString("sif_rob"));
                barcodeData.setNazRob(rs.getString("naz_rob"));
                barcodeData.setSifEntRob(rs.getString("sif_ent_rob"));
                barcodeData.setBarKod(rs.getString("bar_kod"));
                barcodeData.setNabCena(rs.getDouble("nab_cen"));
                barcodeData.setMalCena(rs.getDouble("mal_cen"));
                return barcodeData;
            });

            response.setResponseResult(!barcodeDataList.isEmpty());
            response.setErrorMessage(null);
            response.setRespResultCount(barcodeDataList.size());
            response.setData(barcodeDataList);
        } catch (Exception e) {
            response.setResponseResult(false);
            response.setErrorMessage("Error fetching barcodes: " + e.getMessage());
            response.setRespResultCount(0);
            response.setData(new ArrayList<>());
        }
        return response;
    }

    public BarcodeResponse getSkuByBarcode(String system, String barcode) {
        String sql = """
            select r.sif_rob, r.naz_rob, e.sif_ent_rob, e.bar_kod, 
            nvl(get_nabavna(r.sif_rob), 0) as nab_cen, 
            nvl(get_cena(r.sif_rob, 'mp'), 0) as mal_cen 
            from ean_kod e
            left join roba r on r.sif_Rob = e.sif_rob
            where e.bar_kod = ?
        """;

        BarcodeResponse response = new BarcodeResponse();
        try {
            JdbcTemplate jdbcTemplate = getJdbcTemplate(system);

            List<BarcodeResponse.BarcodeData> barcodeDataList = jdbcTemplate.query(sql, new Object[]{barcode}, (rs, rowNum) -> {
                BarcodeResponse.BarcodeData barcodeData = new BarcodeResponse.BarcodeData();
                barcodeData.setSifRob(rs.getString("sif_rob"));
                barcodeData.setNazRob(rs.getString("naz_rob"));
                barcodeData.setSifEntRob(rs.getString("sif_ent_rob"));
                barcodeData.setBarKod(rs.getString("bar_kod"));
                barcodeData.setNabCena(rs.getDouble("nab_cen"));
                barcodeData.setMalCena(rs.getDouble("mal_cen"));
                return barcodeData;
            });

            response.setResponseResult(!barcodeDataList.isEmpty());
            response.setErrorMessage(barcodeDataList.isEmpty() ? "No barcode found for: " + barcode : null);
            response.setRespResultCount(barcodeDataList.size());
            response.setData(barcodeDataList);
        } catch (Exception e) {
            response.setResponseResult(false);
            response.setErrorMessage("Error fetching barcode: " + e.getMessage());
            response.setRespResultCount(0);
            response.setData(new ArrayList<>());
        }
        return response;
    }

    public BarcodeResponse getBarcodesById(String system, Integer id) {
        String sql = """
            select distinct e.id_ean_kod_arh id,r.sif_rob, r.naz_rob, e.sif_ent_rob, e.bar_kod, 
            nvl(get_nabavna(r.sif_rob), 0) as nab_cen, 
            nvl(get_cena(r.sif_rob, 'mp'), 0) as mal_cen 
            from mk_ean_kod_arh e
            left join roba r on r.sif_Rob = e.sif_rob
            where e.id_ean_kod_arh >= ?
        """;

        BarcodeResponse response = new BarcodeResponse();
        try {
            JdbcTemplate jdbcTemplate = getJdbcTemplate(system);

            List<BarcodeResponse.BarcodeData> barcodeDataList = jdbcTemplate.query(sql, new Object[]{id}, (rs, rowNum) -> {
                BarcodeResponse.BarcodeData barcodeData = new BarcodeResponse.BarcodeData();
                barcodeData.setId(rs.getInt("id"));
                barcodeData.setSifRob(rs.getString("sif_rob"));
                barcodeData.setNazRob(rs.getString("naz_rob"));
                barcodeData.setSifEntRob(rs.getString("sif_ent_rob"));
                barcodeData.setBarKod(rs.getString("bar_kod"));
                barcodeData.setNabCena(rs.getDouble("nab_cen"));
                barcodeData.setMalCena(rs.getDouble("mal_cen"));
                return barcodeData;
            });

            response.setResponseResult(!barcodeDataList.isEmpty());
            response.setErrorMessage(barcodeDataList.isEmpty() ? "No barcode found for: " + id : null);
            response.setRespResultCount(barcodeDataList.size());
            response.setData(barcodeDataList);
        } catch (Exception e) {
            response.setResponseResult(false);
            response.setErrorMessage("Error fetching barcode: " + e.getMessage());
            response.setRespResultCount(0);
            response.setData(new ArrayList<>());
        }
        return response;
    }
}