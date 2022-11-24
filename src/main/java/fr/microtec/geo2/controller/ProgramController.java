package fr.microtec.geo2.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import fr.microtec.geo2.service.ProgramService;
import lombok.Data;

/** Handle programs actions, such as import */
@RestController
@RequestMapping("/program")
public class ProgramController {

    public static final String GEO2_PROGRAM_OUTPUT = "GEO2_PROGRAM_OUTPUT";

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

    /** Program import response structure */
    @Data
    public static class ProgramResponse {
        Integer rowCount = 0;
        Integer ordreCount = 0;
        List<ProgramRow> rows = new ArrayList<>();

        public void incrementRowCount() {
            this.rowCount++;
        }

        public void incrementOrdreCount() {
            this.ordreCount++;
        }

        public void pushRow(ProgramRow p) {
            this.rows.add(p);
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
