package com.example.apibebakids.service;

import com.example.apibebakids.model.PopisRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Qualifier;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Service
public class PopisService {

    @Autowired
    @Qualifier("jdbcTemplateBebaKids")
    private JdbcTemplate jdbcTemplateBebakids;

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
                return jdbcTemplateBebakids;
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

    @Transactional
    public String createDocumentAndSaveItems(PopisRequest popisRequest) {
        JdbcTemplate jdbcTemplate = getJdbcTemplate(popisRequest.getSystem());

        // Generate a unique document number (or retrieve from DB logic)
        String documentNumber = generateDocumentNumber(jdbcTemplate, popisRequest.getRetailStore(), popisRequest.getNote());

        // Insert items associated with the document
        String insertItemsQuery = "INSERT INTO pop_sta_mp_st (ozn_pop_sta, sif_rob, sif_ent_rob, kol_pop, rbr) VALUES (?, ?, ?, ?, ?)";
        int rbr = 1;

        for (var item : popisRequest.getItems()) {
            jdbcTemplate.update(insertItemsQuery, documentNumber, item.getSku(), item.getSize(), item.getQty(), rbr);
            rbr++;
        }

        return documentNumber;
    }

    private String generateDocumentNumber(JdbcTemplate jdbcTemplate, String retailStore,String note) {
        // Formiraj trenutni datum u formatu "dd.MM.yyyy"
        String currentDate = new SimpleDateFormat("dd.MM.yyyy").format(new Date());

        // SQL upit za pozivanje procedure
        String sql = "EXECUTE PROCEDURE get_popis_dokument(?, ?, ?,?)";

        // Poziv procedure sa parametrima i preuzimanje rezultata
        return jdbcTemplate.queryForObject(
                sql,
                new Object[]{"ST", retailStore, currentDate, note},
                String.class
        );
    }
}
