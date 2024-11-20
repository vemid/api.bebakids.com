package com.example.apibebakids.service;

import com.example.apibebakids.model.StockResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class StockService {

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

    public StockResponse getStockBySku(String sku, String system, String storeId) {

        StringBuilder sql = new StringBuilder();
        if (Objects.equals(storeId, "MAG")) {
            sql = new StringBuilder("""
                    select
                        trim(z.sif_rob) sku,
                        trim(r.naz_rob) name,
                        trim(e.bar_kod) barcode,
                        trim(z.sif_ent_rob) size,
                        round(z.kolic-z.rez_kol, 0) qty,
                        trim(zm.sif_mag) warehouse, zm.cen_zal price
                        from
                        zal_robe_mag_zon z
                        left join ean_kod e on e.sif_rob = z.sif_rob and e.sif_ent_rob = z.sif_ent_rob
                        left join roba r on r.sif_rob = z.sif_Rob
                        left join zal_robe_mag zm on zm.sif_rob = z.sif_rob and zm.sif_mag = z.sif_mag
                         where z.kolic-z.rez_kol > 0
                         and (z.sif_rob in (select distinct sif_rob from ean_kod where bar_kod like ?)\s
                         or z.sif_rob like ?)
                         and (z.sif_rob like ?
                         or z.sif_rob in (select distinct sif_rob from ean_kod where bar_kod like ?)\s
                         or r.kat_bro like ?)
                    """);
        } else {
            sql = new StringBuilder("""
                        select 
                            trim(z.sif_rob) sku,
                            trim(r.naz_rob) name,
                            trim(e.bar_kod) barcode, 
                            trim(z.sif_ent_rob) size,
                            round(z.kolic-z.rez_kol, 0) qty,
                            trim(o.naz_obj_mp) warehouse, zm.pro_cen price
                        from 
                            zal_robe_mp_zon z
                            left join obj_mp o on o.sif_obj_mp = z.sif_obj_mp 
                            left join ean_kod e on e.sif_rob = z.sif_rob and e.sif_ent_rob = z.sif_ent_rob
                            left join roba r on r.sif_rob = z.sif_Rob
                            left join zal_robe_mp zm on zm.sif_rob = z.sif_rob and zm.sif_obj_mp = z.sif_obj_mp
                        where z.kolic-z.rez_kol > 0 
                          and (z.sif_rob in (select distinct sif_rob from ean_kod where bar_kod like ?) 
                          or z.sif_rob like ?)
                          and (z.sif_rob like ? 
                          or z.sif_rob in (select distinct sif_rob from ean_kod where bar_kod like ?) 
                          or r.kat_bro like ?) 
                    """);

            // Conditionally add the storeId filter if provided
            if (storeId != null && !storeId.isEmpty()) {
                sql.append(" and z.sif_obj_mp = ? ");
            }

            sql.append(" order by z.sif_ent_rob, z.sif_obj_mp");
        }

        try {
            // Get the correct JdbcTemplate based on the system
            JdbcTemplate jdbcTemplate = getJdbcTemplate(system);

            String likePattern = "%" + sku + "%";
            List<Object> params = List.of(likePattern, likePattern, likePattern, likePattern, likePattern);

            // Add storeId to the parameters if it's provided
            if (storeId != null && !storeId.isEmpty()) {
                params = new ArrayList<>(params);
                if (!Objects.equals(storeId, "MAG")) {// Create a mutable list
                    params.add(storeId);
                }
            }

            List<StockResponse.StockItem> stockItems = jdbcTemplate.query(sql.toString(), params.toArray(), (rs, rowNum) -> {
                StockResponse.StockItem item = new StockResponse.StockItem();
                item.setSku(rs.getString("sku"));
                item.setName(rs.getString("name"));
                item.setBarcode(rs.getString("barcode"));
                item.setSize(rs.getString("size"));
                item.setQty(rs.getInt("qty"));
                item.setWarehouse(rs.getString("warehouse"));
                item.setPrice(rs.getDouble("price"));
                return item;
            });

            StockResponse response = new StockResponse();
            response.setResponseResult(!stockItems.isEmpty());
            response.setErrorMessage(stockItems.isEmpty() ? "No stock found for SKU: " + sku : null);
            response.setRespResultCount(stockItems.size());
            response.setData(stockItems);

            return response;
        } catch (Exception e) {
            StockResponse response = new StockResponse();
            response.setResponseResult(false);
            response.setErrorMessage("Error fetching stock data: " + e.getMessage());
            response.setRespResultCount(0);
            response.setData(null);
            return response;
        }
    }

    public StockResponse getStockByStore(String system, String storeId) {
        StringBuilder sql = new StringBuilder();
        if (Objects.equals(storeId, "MAG")) {
            sql = new StringBuilder("""
                        select 
                            trim(z.sif_rob) sku,
                            trim(r.naz_rob) name,
                            trim(e.bar_kod) barcode,
                            trim(z.sif_ent_rob) size,
                            sum(z.kolic-z.rez_kol) qty
                        from zal_robe_mag_zon z
                            left join ean_kod e on e.sif_rob = z.sif_rob and e.sif_ent_rob = z.sif_ent_rob
                            left join roba r on r.sif_rob = z.sif_Rob
                            where (z.sif_mag in ('MP','MR') or z.sif_mag= ? )
                            group by 1,2,3,4 having sum(z.kolic-z.rez_kol) > 0
                    """);

            //sql.append(" order by z.sif_ent_rob, z.sif_mag");
        } else {
            sql = new StringBuilder("""
                        select 
                            trim(z.sif_rob) sku,
                            trim(r.naz_rob) name,
                            trim(e.bar_kod) barcode, 
                            trim(z.sif_ent_rob) size,
                            round(z.kolic-z.rez_kol, 0) qty
                        from 
                            zal_robe_mp_zon z
                            left join ean_kod e on e.sif_rob = z.sif_rob and e.sif_ent_rob = z.sif_ent_rob
                            left join roba r on r.sif_rob = z.sif_Rob
                        where z.kolic-z.rez_kol > 0 
                            and z.sif_obj_mp = ? 
                    """);

            sql.append(" order by z.sif_ent_rob, z.sif_obj_mp");
        }

        try {
            // Get the correct JdbcTemplate based on the system
            JdbcTemplate jdbcTemplate = getJdbcTemplate(system);


            List<StockResponse.StockItem> stockItems = jdbcTemplate.query(sql.toString(), new Object[]{storeId}, (rs, rowNum) -> {
                StockResponse.StockItem item = new StockResponse.StockItem();
                item.setSku(rs.getString("sku"));
                item.setName(rs.getString("name"));
                item.setBarcode(rs.getString("barcode"));
                item.setSize(rs.getString("size"));
                item.setQty(rs.getInt("qty"));
                //item.setPrice(rs.getDouble("price"));
                return item;
            });

            StockResponse response = new StockResponse();
            response.setResponseResult(!stockItems.isEmpty());
            response.setErrorMessage(stockItems.isEmpty() ? "No stock found for Store: " + storeId : null);
            response.setRespResultCount(stockItems.size());
            response.setData(stockItems);

            return response;
        } catch (Exception e) {
            StockResponse response = new StockResponse();
            response.setResponseResult(false);
            response.setErrorMessage("Error fetching stock data: " + e.getMessage());
            response.setRespResultCount(0);
            response.setData(null);
            return response;
        }
    }
}
