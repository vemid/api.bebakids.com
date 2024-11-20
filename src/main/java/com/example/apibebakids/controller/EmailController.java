package com.example.apibebakids.controller;

import com.example.apibebakids.dto.EmailRequestDTO;
import com.example.apibebakids.model.EmailResponse;
import com.example.apibebakids.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Email API", description = "API za slanje email poruka sa stavkama dokumenta")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/sendEmail")
    @Operation(summary = "Šalje email sa informacijama o stavkama dokumenta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email uspešno poslat"),
            @ApiResponse(responseCode = "400", description = "Neispravni podaci"),
            @ApiResponse(responseCode = "500", description = "Greška prilikom slanja email-a")
    })
    public ResponseEntity<EmailResponse<?>> sendEmail(
            @Valid @RequestBody EmailRequestDTO emailRequestDTO, BindingResult bindingResult) {

        // Kreiramo novi ResponseModel objekat
        EmailResponse<Map<String, String>> response = new EmailResponse<>();

        // Provera za validacione greške
        if (bindingResult.hasErrors()) {
            response.setResponseResult(false);
            response.setErrorMessage("Validation error: " + bindingResult.getFieldError().getDefaultMessage());
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // Pokušaj slanja email-a
            emailService.sendEmail(emailRequestDTO);

            // Ako je uspešno, postavljamo responseResult na true
            response.setResponseResult(true);
            response.setErrorMessage(null);
            response.setRespResultCount(1);

            // Postavljanje "message" unutar data polja
            Map<String, String> data = new HashMap<>();
            data.put("message", "Mail uspešno poslat");
            response.setData(data);

            return ResponseEntity.ok(response);
        } catch (MessagingException e) {
            // Ako je došlo do greške u slanju email-a
            response.setResponseResult(false);
            response.setErrorMessage("Error sending email: " + e.getMessage());
            response.setRespResultCount(0);
            response.setData(null);

            return ResponseEntity.status(500).body(response);
        } catch (Exception e) {
            // Ako je došlo do druge vrste greške
            response.setResponseResult(false);
            response.setErrorMessage("Error: " + e.getMessage());
            response.setRespResultCount(0);
            response.setData(null);

            return ResponseEntity.status(500).body(response);
        }
    }
}
