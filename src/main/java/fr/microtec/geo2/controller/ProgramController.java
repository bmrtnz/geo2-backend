package fr.microtec.geo2.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import fr.microtec.geo2.service.ProgramService;
import lombok.Data;

/** Handle programs actions, such as import */
@RestController
@Secured("ROLE_USER")
@RequestMapping("/program")
public class ProgramController {

    private final ProgramService service;

    public ProgramController(ProgramService service) {
        this.service = service;
    }

    @PostMapping("/{program}")
    public ProgramResponse upload(
            @PathVariable String program,
            @RequestParam("chunk") MultipartFile chunk)
            throws IOException {

        switch (Program.valueOf(program.toUpperCase())) {
            case ORCHARD:
                return this.service.importOrchard(chunk);

            case TESCO:
                return this.service.importTesco(chunk);

            default:
                throw new RuntimeException(String.format("Program %1 does not exist", program));
        }
    }

    /** If it exist, send the program in session, and then, free it */
    @GetMapping("/download")
    @ResponseBody
    private ResponseEntity<byte[]> download() {
        HttpHeaders headers = new HttpHeaders();
        ByteArrayOutputStream out;
        try {
            headers.setContentType(ProgramService.getFileType());
            headers.setContentDisposition(ContentDisposition
                    .builder("attachment")
                    .filename("retour_" + ProgramService.getFileName())
                    .build());
            out = ProgramService.getOutput();
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve program informations !");
        } finally {
            ProgramService.clearSession();
        }
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(out.toByteArray());
    }

    /** Program import response structure */
    @Data
    public static class ProgramResponse {
        Integer rowCount = 0;
        Integer ordreCount = 0;
        List<ProgramRow> rows = new ArrayList<>();

        public void incrementOrdreCount() {
            this.ordreCount++;
        }

        public void pushRow(ProgramRow p) {
            this.rows.add(p);
            this.rowCount = this.rows.size();
        }

        @Data
        public static class ProgramRow {
            String loadRef;
            String depot;
            LocalDateTime dateDepart;
            LocalDateTime dateLivraison;
            String ordreNum;
            List<String> erreurs = new ArrayList<>();
            List<String> messages = new ArrayList<>();

            public void pushMessage(String v) {
                this.messages.add(v);
            }

            public void pushErreur(String v) {
                this.erreurs.add(v);
            }
        }

    }
}
