package com.example.apibebakids.service;

import com.example.apibebakids.model.DocumentCheckResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.w3c.dom.DocumentType;

import java.util.List;
import java.util.Objects;

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

        """;

        if(Objects.equals(documentType, "WarehouseToStore"))
        {
            sql = """
select "WarehouseToStore" document_type,o1.ozn_otp_mal document, e.bar_kod,r.naz_rob,o.sif_rob,o.sif_ent_rob,sum(o.kolic) kolic from otprem_mp_st o
            left join otprem_mp o1 on o1.ozn_otp_mal = o.ozn_otp_mal
            left join roba r on r.sif_rob = o.sif_rob
            left join ean_kod e on e.sif_rob = o.sif_rob and e.sif_ent_rob = o.sif_ent_rob
            where o1.vrs_knj in ('2','3') and o1.dat_otp_mal >=today-100 and o1.storno = 'N' and o1.status = 1
            and o1.ozn_otp_mal not in (select ozn_otp_izl from otprem_mp where vrs_knj = 1 and dat_otp_mal >=today-100)
            and o1.ozn_otp_mal like ?
            group by 1,2,3,4,5,6
""";
        } else if (Objects.equals(documentType, "StoreToStore")) {
            sql = """
select "StoreToStore" document_type,o1.ozn_pre_mp document, e.bar_kod,r.naz_rob,o.sif_rob,o.sif_ent_rob,sum(o.kolic) kolic from pren_mp_st o
            left join pren_mp o1 on o1.ozn_pre_mp = o.ozn_pre_mp
        left join roba r on r.sif_rob = o.sif_rob
            left join ean_kod e on e.sif_rob = o.sif_rob and e.sif_ent_rob = o.sif_ent_rob
            where o1.vrs_knj in ('2','3') and o1.dat_knj >=today-100 and o1.storno = 'N' and o1.status = 1
            and o1.ozn_pre_mp not in (select ozn_pre_mp_izl from pren_mp where vrs_knj = 1 and dat_knj >=today-100)
            and o1.ozn_pre_mp like ?
            group by 1,2,3,4,5,6
""";
        }
        else if (Objects.equals(documentType, "StoreToWarehouse")) {
            sql = """
select "StoreToWarehouse" document_type,o1.ozn_pov_mp document, e.bar_kod,r.naz_rob,o.sif_rob,o.sif_ent_rob,sum(o.kolic) kolic from povrat_mp_st o
            left join povrat_mp o1 on o1.ozn_pov_mp = o.ozn_pov_mp
        left join roba r on r.sif_rob = o.sif_rob
            left join ean_kod e on e.sif_rob = o.sif_rob and e.sif_ent_rob = o.sif_ent_rob
            where o1.vrs_knj in ('2','3') and o1.dat_pov_mp >=today-100 and o1.storno = 'N' and o1.status = 1
            and o1.ozn_pov_mp not in (select ozn_pov_mp_izl from povrat_mp where vrs_knj = 1 and dat_pov_mp >=today-100)
            and o1.ozn_pov_mp like ?
            group by 1,2,3,4,5,6
""";
        }

        else if (Objects.equals(documentType, "FranchiseToWarehouse")) {
            sql = """
select "FranchiseToWarehouse" document_type,o1.ozn_otp document, e.bar_kod,r.naz_rob,o.sif_rob,o.sif_ent_rob,sum(o.kolic) kolic from otprem_st o
            left join otprem o1 on o1.ozn_otp = o.ozn_otp
        left join roba r on r.sif_rob = o.sif_rob
            left join ean_kod e on e.sif_rob = o.sif_rob and e.sif_ent_rob = o.sif_ent_rob
            where o1.vrs_knj in ('1') and o1.dat_otp >=today-100 and o1.storno = 'N' and o1.status = 1
            and o1.ozn_otp like ?
            group by 1,2,3,4,5,6
""";
        }

        else if (Objects.equals(documentType, "ProductionToStore")) {
            sql = """
select "ProductionToStore" document_type,o1.ozn_nal_izp document, e.bar_kod,r.naz_rob,o.sif_rob,o.sif_ent_rob,sum(o.kolic) kolic from nal_izdp_st o
            left join nal_izdp o1 on o1.ozn_nal_izp = o.ozn_nal_izp
            left join roba r on r.sif_rob = o.sif_rob
            left join ean_kod e on e.sif_rob = o.sif_rob and e.sif_ent_rob = o.sif_ent_rob
            where o1.vrs_knj in ('2','3') and o1.dat_nal_izp >=today-100 and o1.storno = 'N'
            and o1.ozn_nal_izp like ?
            group by 1,2,3,4,5,6
""";
        }

        else if (Objects.equals(documentType, "ProductionToWarehouse")) {
            sql = """
select "ProductionToWarehouse" document_type,o1.ozn_nal_pre document, e.bar_kod,r.naz_rob,o.sif_rob,o.sif_ent_rob,sum(o.kolic) kolic from nal_pre_st o
            left join nal_pre o1 on o1.ozn_nal_pre = o.ozn_nal_pre
            left join roba r on r.sif_rob = o.sif_rob
            left join ean_kod e on e.sif_rob = o.sif_rob and e.sif_ent_rob = o.sif_ent_rob
            where o1.vrs_knj in ('2','3') and o1.dat_nal_pre >=today-100 and o1.storno = 'N'
            and o1.ozn_nal_pre like ?
            group by 1,2,3,4,5,6
""";
        }



        try {
            // Assuming we're using the 'bebakids' system for this query
            JdbcTemplate jdbcTemplate = getJdbcTemplate(system);

            String likePatternDocumet = "%" + document + "%";
            //String likePatternDocumentType = "%" + documentType + "%";
            Object[] params = new Object[]{ likePatternDocumet};

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