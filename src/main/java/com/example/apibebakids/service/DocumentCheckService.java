package com.example.apibebakids.service;

import com.example.apibebakids.model.DocumentCheckResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.w3c.dom.DocumentType;

import java.util.List;

@Service
public class DocumentCheckService {

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

    public DocumentCheckResponse getDocumentsData(String documentType, String document,String system) {
        String sql = """
            select * from (
            select "WarehouseToStore" document_type,o1.ozn_otp_mal document, e.bar_kod,r.naz_rob,o.sif_rob,o.sif_ent_rob,sum(o.kolic) kolic from otprem_mp_st o
            left join otprem_mp o1 on o1.ozn_otp_mal = o.ozn_otp_mal
            left join roba r on r.sif_rob = o.sif_rob
            left join ean_kod e on e.sif_rob = o.sif_rob and e.sif_ent_rob = o.sif_ent_rob
            where o1.vrs_knj in ('2','3') and o1.dat_otp_mal >=today-100 and o1.storno = 'N' and o1.status = 1
            and o1.ozn_otp_mal not in (select ozn_otp_izl from otprem_mp where vrs_knj = 1 and dat_otp_mal >=today-100)
            group by 1,2,3,4,5,6
            union all
            select "StoreToStore" document_type,o1.ozn_pre_mp document, e.bar_kod,r.naz_rob,o.sif_rob,o.sif_ent_rob,sum(o.kolic) kolic from pren_mp_st o
            left join pren_mp o1 on o1.ozn_pre_mp = o.ozn_pre_mp
        left join roba r on r.sif_rob = o.sif_rob
            left join ean_kod e on e.sif_rob = o.sif_rob and e.sif_ent_rob = o.sif_ent_rob
            where o1.vrs_knj in ('2','3') and o1.dat_knj >=today-100 and o1.storno = 'N' and o1.status = 1
            and o1.ozn_pre_mp not in (select ozn_pre_mp_izl from pren_mp where vrs_knj = 1 and dat_knj >=today-100)
            group by 1,2,3,4,5,6
            union all
            select "StoreToWarehouse" document_type,o1.ozn_pov_mp document, e.bar_kod,r.naz_rob,o.sif_rob,o.sif_ent_rob,sum(o.kolic) kolic from povrat_mp_st o
            left join povrat_mp o1 on o1.ozn_pov_mp = o.ozn_pov_mp
        left join roba r on r.sif_rob = o.sif_rob
            left join ean_kod e on e.sif_rob = o.sif_rob and e.sif_ent_rob = o.sif_ent_rob
            where o1.vrs_knj in ('2','3') and o1.dat_pov_mp >=today-100 and o1.storno = 'N' and o1.status = 1
            and o1.ozn_pov_mp not in (select ozn_pov_mp_izl from povrat_mp where vrs_knj = 1 and dat_pov_mp >=today-100)
            group by 1,2,3,4,5,6
            union all
            select "FranchiseToWarehouse" document_type,o1.ozn_otp document, e.bar_kod,r.naz_rob,o.sif_rob,o.sif_ent_rob,sum(o.kolic) kolic from otprem_st o
            left join otprem o1 on o1.ozn_otp = o.ozn_otp
        left join roba r on r.sif_rob = o.sif_rob
            left join ean_kod e on e.sif_rob = o.sif_rob and e.sif_ent_rob = o.sif_ent_rob
            where o1.vrs_knj in ('2','3') and o1.dat_otp >=today-100 and o1.storno = 'N' and o1.status = 1
            and o1.ozn_otp not in (select ext_ozn_dok from povrat_kup where dat_pov >=today-100 and storno = 'N' and status =1)
            group by 1,2,3,4,5,6) as A where document like ? and document_type like ?
        """;

        try {
            // Assuming we're using the 'bebakids' system for this query
            JdbcTemplate jdbcTemplate = getJdbcTemplate(system);

            String likePatternDocumet = "%" + document + "%";
            String likePatternDocumentType = "%" + documentType + "%";
            Object[] params = new Object[]{likePatternDocumet, likePatternDocumentType};

            List<DocumentCheckResponse.DocumentItem> DocumentItems = jdbcTemplate.query(sql, params, (rs, rowNum) -> {
                DocumentCheckResponse.DocumentItem item = new DocumentCheckResponse.DocumentItem();
                item.setDocumentType(rs.getString("document_type"));
                item.setDocument(rs.getString("document"));
                item.setBarKod(rs.getString("bar_kod"));
                item.setNazRob(rs.getString("naz_rob"));
                item.setSifRob(rs.getString("sif_rob"));
                item.setSifEntRob(rs.getString("sif_ent_rob"));
                item.setKolic(rs.getDouble("kolic"));
                return item;
            });

            DocumentCheckResponse response = new DocumentCheckResponse();
            response.setResponseResult(!DocumentItems.isEmpty());
            response.setErrorMessage(DocumentItems.isEmpty() ? "No document found for Document Type: " + documentType + ", and Document: " +document : null);
            response.setRespResultCount(DocumentItems.size());
            response.setData(DocumentItems);

            return response;
        } catch (Exception e) {
            DocumentCheckResponse response = new DocumentCheckResponse();
            response.setResponseResult(false);
            response.setErrorMessage("Error fetching stock data: " + e.getMessage());
            response.setRespResultCount(0);
            response.setData(null);
            return response;
        }
    }
}