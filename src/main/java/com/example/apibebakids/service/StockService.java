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
                    select trim(A.sif_rob) sku,trim(r.naz_rob) name,trim(e.bar_kod) barcode,trim(A.sif_ent_rob) size,sum(A.kolic) qty from (
                        select sif_rob,sif_ent_rob,sum(kolic-rez_kol) kolic from zal_robe_mag_zon z
                        where (z.sif_mag in ('MP','MR') or z.sif_mag= ? )
                        group by 1,2
                        having sum(z.kolic-z.rez_kol) > 0
                        union all
                        select os.sif_rob,os.sif_ent_rob,sum(os.kolic)*-1 kolic from otprem_mp_st os
                        left join otprem_mp o on o.ozn_otp_mal = os.ozn_otp_mal
                        where o.storno = 'N' and o.status = 0 and o.vrs_knj in ('2','3') 
                        and o.dat_otp_mal>=cast("01.01."||year(today) as date) and o.dat_otp_mal>=today-30 and o.sif_mag in ('MP','MR') group by 1,2) as A
                        left join roba r on r.sif_rob = A.sif_rob
                        left join ean_kod e on e.sif_rob = A.sif_rob and e.sif_ent_rob = A.sif_ent_rob
                        group by 1,2,3,4
                """);
        } else {
            sql = new StringBuilder("""
                    select trim(A.sif_rob) sku,trim(r.naz_rob) name,trim(e.bar_kod) barcode,trim(A.sif_ent_rob) size,sum(A.kolic) qty from (
                    select sif_rob,sif_ent_rob,kolic-rez_kol kolic from zal_robe_mp_zon where kolic-rez_kol>0 and sif_obj_mp = ?
                    union all
                    select ns.sif_rob,ns.sif_ent_rob,sum(ns.kolic)*-1 kolic from nal_povrat_mp_st ns
                    left join nal_povrat_mp n on n.ozn_nal_pov_mp = ns.ozn_nal_pov_mp 
                    left join povrat_mp p on p.sif_obj_mp = n.sif_obj_mp and p.ozn_nal_pov_mp = n.ozn_nal_pov_mp
                    where n.storno='N' and p.ozn_nal_pov_mp is null
                    and dat_nal_pov_mp>=cast("01.01."||year(today) as date) and dat_nal_pov_mp>=today-30 and n.sif_obj_mp = ? group by 1,2 
                    union all 
                    SELECT ps.sif_rob,ps.sif_ent_rob,sum(ps.kolic)*-1 kolic  from pren_mp_st ps
                    left join pren_mp p on p.ozn_pre_mp = ps.ozn_pre_mp and p.storno = 'N' and p.status =0
                    where
                    p.dat_knj>=cast("01.01."||year(today) as date) and p.dat_knj>=today-30 and p.sif_obj_izl = ? group by 1,2
                    ) as A 
                    left join roba r on r.sif_rob = A.sif_rob
                    left join ean_kod e on e.sif_rob = A.sif_rob and e.sif_ent_rob = A.sif_ent_rob
                    group by 1,2,3,4
                """);
        }

        try {
            // Get the correct JdbcTemplate based on the system
            JdbcTemplate jdbcTemplate = getJdbcTemplate(system);

            List<StockResponse.StockItem> stockItems;
            if (Objects.equals(storeId, "MAG")) {
                stockItems = jdbcTemplate.query(sql.toString(), new Object[]{storeId}, (rs, rowNum) -> {
                    StockResponse.StockItem item = new StockResponse.StockItem();
                    item.setSku(rs.getString("sku"));
                    item.setName(rs.getString("name"));
                    item.setBarcode(rs.getString("barcode"));
                    item.setSize(rs.getString("size"));
                    item.setQty(rs.getInt("qty"));
                    return item;
                });
            } else {
                // For the new query with three parameters, pass storeId three times
                stockItems = jdbcTemplate.query(sql.toString(), new Object[]{storeId, storeId, storeId}, (rs, rowNum) -> {
                    StockResponse.StockItem item = new StockResponse.StockItem();
                    item.setSku(rs.getString("sku"));
                    item.setName(rs.getString("name"));
                    item.setBarcode(rs.getString("barcode"));
                    item.setSize(rs.getString("size"));
                    item.setQty(rs.getInt("qty"));
                    return item;
                });
            }

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
