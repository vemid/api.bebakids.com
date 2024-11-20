package com.example.apibebakids.service;

import com.example.apibebakids.model.WarehouseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Qualifier;
import java.util.List;

@Service
public class WarehouseService {

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

    // Method to check if the system is valid
    private boolean isValidSystem(String system) {
        return "bebakids".equalsIgnoreCase(system) ||
                "watch".equalsIgnoreCase(system) ||
                "geox".equalsIgnoreCase(system);
    }

    public WarehouseResponse getWarehouses(String system) {
        // Check if the system is valid
        if (!isValidSystem(system)) {
            WarehouseResponse errorResponse = new WarehouseResponse();
            errorResponse.setResponseResult(false);
            errorResponse.setErrorMessage("Invalid system: " + system);
            errorResponse.setRespResultCount(0);
            return errorResponse;
        }

        String sql = """
                    select "MP" type,trim(upper(o.sif_obj_mp)) code, trim(upper(o.naz_obj_mp)) name, o.e_mail,
                    case when left(o.sif_org_jed,2) = '01' then "BK" when left(o.sif_org_jed,2) in ('03','62') then "CG" else "FR" end as retail_type, nvl(c.ozn_cen,"01/140000001") pricelist,
                    case when o.vrs_obj = 1 then 1 else 0 end as active from obj_mp o
                    left join proiz_cen_obj_mp c on c.sif_obj_mp = o.sif_obj_mp where left(sif_org_jed,2) not in ('03','64','62')
                    union all
                    select "WH" type, trim(sif_mag) code,trim(upper(naz_mag)) name,trim(nvl(napomena,"magacin@bebakids.com")) e_mail,
                    case when left(sif_org_jed,2) = '01' then "BK" when left(sif_org_jed,2) in ('03','62') then "CG" else "FR" end as retail_type, "01/140000001" pricelist,1 active
                    from magacin where left(sif_org_jed,2) in ('01') and sif_mag not in ('MM','MT','RX','DF')
                    order by code
                """;

        try {
            JdbcTemplate jdbcTemplate = getJdbcTemplate(system);

            List<WarehouseResponse.WarehouseData> warehouseDataList = jdbcTemplate.query(sql, (rs, rowNum) -> {
                WarehouseResponse.WarehouseData item = new WarehouseResponse.WarehouseData();
                item.setPricelist(rs.getString("pricelist"));
                item.setCode(rs.getString("code"));
                item.setEmail(rs.getString("e_mail"));
                item.setRetailType(rs.getString("retail_type"));
                item.setName(rs.getString("name"));
                item.setActive(rs.getInt("active"));
                item.setType(rs.getString("type"));
                return item;
            });

            WarehouseResponse response = new WarehouseResponse();
            response.setResponseResult(!warehouseDataList.isEmpty());
            response.setErrorMessage(warehouseDataList.isEmpty() ? "No warehouses found for system: " + system : null);
            response.setRespResultCount(warehouseDataList.size());
            response.setData(warehouseDataList);

            return response;
        } catch (Exception e) {
            WarehouseResponse response = new WarehouseResponse();
            response.setResponseResult(false);
            response.setErrorMessage("Error fetching warehouse data: " + e.getMessage());
            response.setRespResultCount(0);
            response.setData(null);
            return response;
        }
    }
}