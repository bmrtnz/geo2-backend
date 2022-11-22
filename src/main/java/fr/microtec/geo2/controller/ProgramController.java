package fr.microtec.geo2.controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import fr.microtec.geo2.service.ProgramService;

enum Program {
    Tesco,
    Orchard,
}

/** Handle programs actions, such as import */
@RestController
@RequestMapping("/program")
public class ProgramController {

    private final ProgramService service;

    public ProgramController(ProgramService service) {
        this.service = service;
    }

    @PostMapping("/{program}")
    public Object upload(
            @PathVariable Program program,
            @RequestParam("chunk") MultipartFile chunk)
            throws IOException {

        switch (program) {
            case Orchard:
                return this.service.importOrchard(chunk.getInputStream());

            case Tesco:
                return this.service.importTesco(chunk.getInputStream());

            default:
                throw new RuntimeException(String.format("Program %1 does not exist", program));
        }
    }
}
