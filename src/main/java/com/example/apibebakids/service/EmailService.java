package com.example.apibebakids.service;

import com.example.apibebakids.dto.EmailRequestDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JdbcTemplate jdbcTemplateBebaKids;

    @Autowired
    private JdbcTemplate jdbcTemplateWatch;

    @Autowired
    private JdbcTemplate jdbcTemplateGeox;

    @Autowired
    private JavaMailSender mailSender;
    @Qualifier("jdbcTemplateBebaKidsBih")
    @Autowired
    private JdbcTemplate jdbcTemplateBebaKidsBih;

    // Method to select the correct JdbcTemplate based on system
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

    // Method to retrieve toEmail based on system, documentNumber, and documentType
    public String getToEmail(String system, String documentNumber, String documentType) {
        String sql = """
select email from (
    select "WarehouseToStore" document_type, o.ozn_otp_mal document,ob.napomena email from otprem_mp o
    left join magacin ob on ob.sif_mag = o.sif_mag
    where o.vrs_knj in ('2','3') and o.dat_otp_mal >=today-100 and o.storno = 'N' and o.status = 1
    and o.ozn_otp_mal not in (select ozn_otp_izl from otprem_mp where vrs_knj = 1 and dat_otp_mal >=today-100)
    union all
    select "StoreToStore" document_type,o.ozn_pre_mp document, ob.e_mail email from pren_mp o
    left join obj_mp ob on ob.sif_obj_mp = o.sif_obj_izl
    where o.vrs_knj in ('2','3') and o.dat_knj >=today-100 and o.storno = 'N' and o.status = 1
    and o.ozn_pre_mp not in (select ozn_pre_mp_izl from pren_mp where vrs_knj = 1 and dat_knj >=today-100)
    union all
    select "StoreToWarehouse" document_type,o.ozn_pov_mp document, ob.e_mail email from povrat_mp o
    left join obj_mp ob on ob.sif_obj_mp = o.sif_obj_mp
    where o.vrs_knj in ('2','3') and o.dat_pov_mp >=today-100 and o.storno = 'N' and o.status = 1
    and o.ozn_pov_mp not in (select ozn_pov_mp_izl from povrat_mp where vrs_knj = 1 and dat_pov_mp >=today-100)
    union all
    select "FranchiseToWarehouse" document_type,o.ozn_otp document, ob.napomena email from otprem o
    left join magacin ob on ob.sif_mag = o.sif_mag
    where o.vrs_knj in ('2','3') and o.dat_otp >=today-100 and o.storno = 'N' and o.status = 1
    and o.ozn_otp not in (select ext_ozn_dok from povrat_kup where dat_pov >=today-100 and storno = 'N' and status =1)
    ) as A where document = ? and document_type = ?
""";
        JdbcTemplate jdbcTemplate = getJdbcTemplate(system);
        return jdbcTemplate.queryForObject(sql, new Object[]{documentNumber, documentType}, String.class);
    }

    // Method to send the email, now using EmailRequestDTO
    public void sendEmail(EmailRequestDTO emailRequestDTO) throws MessagingException {
        //String fromEmail = emailRequestDTO.getFromEmail();
        String system = emailRequestDTO.getSystem();
        String documentNumber = emailRequestDTO.getDocumentNumber();
        String documentType = emailRequestDTO.getDocumentType();
        String ccEmail =emailRequestDTO.getFromEmail();

        // Retrieve the toEmail address using system, documentNumber, and documentType
        String toEmailObject = getToEmail(system, documentNumber, documentType);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setFrom("server@bebakids.com");
        helper.setTo(toEmailObject);
        helper.setCc(ccEmail);
        //helper.setTo("marko.vesic@bebakids.com");
        helper.setSubject("Razlika po prijemu robe | Dokument: " + documentNumber);

        // Compose HTML email body with items as a table and styled
        StringBuilder emailBody = new StringBuilder();
        emailBody.append("<div style='font-family: Arial, sans-serif; padding: 20px;'>");
        emailBody.append("<h2 style='color: #333;'>Broj dokumenta: ").append(documentNumber).append("</h1>");
        emailBody.append("<h3 style='color: #333;'>Tip Dokumenta: ").append(documentType).append("</h1>");
        emailBody.append("<p style='font-size: 14px; color: #555;'>U nastavku su stavke koje se ne slazu po prijemu robe. </p>");
        emailBody.append("<p style='font-size: 14px; color: #555;'>Kontaktirajte posaljioca za vise informacija. </p>");

// Tabela sa responsive stilom
        emailBody.append("<table style='width: 100%; max-width: 600px; border-collapse: collapse; margin-top: 20px; margin-left: auto; margin-right: auto;'>");
        emailBody.append("<thead>");
        emailBody.append("<tr style='background-color: #f8f8f8;'>")
                .append("<th style='padding: 8px; border: 1px solid #ddd; font-size: 14px;'>Sifra</th>")
                .append("<th style='padding: 8px; border: 1px solid #ddd; font-size: 14px;'>Naziv</th>")
                .append("<th style='padding: 8px; border: 1px solid #ddd; font-size: 14px;'>Velicina</th>")
                .append("<th style='padding: 8px; border: 1px solid #ddd; font-size: 14px;'>Poslata kolicina</th>")
                .append("<th style='padding: 8px; border: 1px solid #ddd; font-size: 14px;'>Skenirana kolicina</th>")
                .append("<th style='padding: 8px; border: 1px solid #ddd; font-size: 14px;'>Razlika</th>")
                .append("</tr>");
        emailBody.append("</thead>");
        emailBody.append("<tbody>");

// Prolazak kroz stavke i generisanje redova tabele
        for (EmailRequestDTO.Item item : emailRequestDTO.getItems()) {
            emailBody.append("<tr style='text-align: center;'>")
                    .append("<td style='padding: 8px; border: 1px solid #ddd; font-size: 13px;'>").append(item.getSku()).append("</td>")
                    .append("<td style='padding: 8px; border: 1px solid #ddd; font-size: 13px;'>").append(item.getName()).append("</td>")
                    .append("<td style='padding: 8px; border: 1px solid #ddd; font-size: 13px;'>").append(item.getSize()).append("</td>")
                    .append("<td style='padding: 8px; border: 1px solid #ddd; font-size: 13px;'>").append(item.getInvoicedQty()).append("</td>")
                    .append("<td style='padding: 8px; border: 1px solid #ddd; font-size: 13px;'>").append(item.getScannedQty()).append("</td>")
                    .append("<td style='padding: 8px; border: 1px solid #ddd; font-size: 13px;'>").append(item.getScannedQty()-item.getInvoicedQty()).append("</td>")
                    .append("</tr>");
        }
        emailBody.append("</tbody>");
        emailBody.append("</table>");

        // Footer
        emailBody.append("<p style='font-size: 12px; color: #777; margin-top: 20px;'>Ovaj email je generisan automatski. Nemojte odgovarati na njega.</p>");
        emailBody.append("<br><p style='font-family: Helvetica, Arial, sans-serif; font-size: 12px;'>")
                .append("<a href='http://www.bebakids.com' class='clink sig-hide logo-container'>")
                .append("<img src='http://www.bebakids.com/signature.png' alt='Kids Beba Doo' class='sig-logo' border='0'>")
                .append("</a></p>");

        emailBody.append("<p style='font-family: Helvetica, Arial, sans-serif; font-size: 12px; line-height: 14px; color: rgb(33, 33, 33);'>")
                .append("<span style='font-weight: bold; display: inline;' class='txt signature_name-input sig-hide'>Razvojni tim BEBAKIDS</span>")
                .append("<span style='display: inline;' class='title-sep sep'> / </span>")
                .append("<span style='display: inline;' class='txt signature_jobtitle-input sig-hide'>IT</span>")
                .append("<br><a class='link email signature_email-input sig-hide' href='mailto:veleprodaja@bebakids.com' style='display: inline;'>admin@bebakids.com</a>")
                .append("<span> / </span><span class='txt signature_mobilephone-input sig-hide'>+381648382015</span></p>");

        emailBody.append("<p style='font-family: Helvetica, Arial, sans-serif; font-size: 12px; line-height: 14px;'>")
                .append("<strong>Kids Beba Doo</strong><br>Office: +381 11 3972 911 / Fax: +381 11 3975 177<br>")
                .append("Ignjata Joba 37, 11050 Beograd, Srbija<br>")
                .append("<a href='http://www.bebakids.com'>http://www.bebakids.com</a></p>");

        emailBody.append("<p style='font-family: Helvetica, Arial, sans-serif; font-size: 12px;'>")
                .append("<a href='https://www.facebook.com/bebakids'>")
                .append("<img src='https://s3.amazonaws.com/htmlsig-assets/round/facebook.png' alt='Facebook' height='24' width='24'>")
                .append("</a> ")
                .append("<a href='http://integram.com/bebakids'>")
                .append("<img src='http://www.bebakids.com/instagram.png' alt='Instagram' height='24' width='24'>")
                .append("</a> ")
                .append("<a href='http://www.bebakids.com'>")
                .append("<img src='http://www.bebakids.com/bk.png' alt='BebaKids' height='24' width='24'>")
                .append("</a></p>");

        emailBody.append("<p style='font-family: Helvetica, Arial, sans-serif; font-size: 9px;'>")
                .append("Ova poruka može da sadrži poveriljive ili pravno privilegovane informacije...<br>")
                .append("Sacuvajmo prirodu! Ako nije neophodno, nemojte stampati ovu poruku!</p>");
        emailBody.append("</div>");

        // Set email content
        helper.setText(emailBody.toString(), true); // true for HTML content

        // Send the email
        mailSender.send(mimeMessage);
    }
}
