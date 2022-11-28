package fr.microtec.geo2.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicReference;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import fr.microtec.geo2.controller.ProgramController;
import fr.microtec.geo2.controller.ProgramController.ProgramResponse;
import fr.microtec.geo2.controller.ProgramController.ProgramResponse.ProgramRow;
import fr.microtec.geo2.persistance.entity.FunctionResult;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreLogistique;
import fr.microtec.geo2.persistance.repository.ordres.GeoFunctionOrdreRepository;
import fr.microtec.geo2.persistance.repository.ordres.GeoOrdreLogistiqueRepository;
import fr.microtec.geo2.persistance.repository.ordres.GeoOrdreRepository;
import fr.microtec.geo2.persistance.repository.tiers.GeoEntrepotRepository;
import fr.microtec.geo2.persistance.repository.tiers.GeoFournisseurRepository;
import fr.microtec.geo2.persistance.repository.tiers.GeoGroupageRepository;
import fr.microtec.geo2.persistance.repository.tiers.GeoTransporteurRepository;
import lombok.val;

@Service
public class ProgramService {

    static final String XLSX_MIME = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static final String XLS_MIME = "application/vnd.ms-excel";

    private final GeoEntrepotRepository entrepotRepo;
    private final GeoOrdreRepository ordreRepo;
    private final GeoOrdreLogistiqueRepository ordreLogistiqueRepo;
    private final GeoFournisseurRepository fournisseurRepo;
    private final GeoGroupageRepository groupageRepo;
    private final GeoTransporteurRepository transporteurRepo;
    private final GeoFunctionOrdreRepository functionOrdreRepo;
    private final EntityManager entityManager;

    public ProgramService(
            EntityManager entityManager,
            GeoEntrepotRepository entrepotRepo,
            GeoOrdreRepository ordreRepo,
            GeoOrdreLogistiqueRepository ordreLogistiqueRepo,
            GeoFournisseurRepository fournisseurRepo,
            GeoGroupageRepository groupageRepo,
            GeoTransporteurRepository transporteurRepo,
            GeoFunctionOrdreRepository functionOrdreRepo) {
        this.entityManager = entityManager;
        this.entrepotRepo = entrepotRepo;
        this.ordreRepo = ordreRepo;
        this.ordreLogistiqueRepo = ordreLogistiqueRepo;
        this.fournisseurRepo = fournisseurRepo;
        this.groupageRepo = groupageRepo;
        this.transporteurRepo = transporteurRepo;
        this.functionOrdreRepo = functionOrdreRepo;
    }

    public ProgramResponse importTesco(MultipartFile chunks) {
        return null;
    }

    public ProgramResponse importOrchard(MultipartFile chunks) throws IOException {

        val res = new ProgramResponse();

        // useful columns indexes
        // int COL_PROGRAMME = 1;
        int COL_LOAD_REFERENCE = 2;
        // int COL_TPND = 3;
        int COL_ARTS_REF = 4;
        int COL_DEPOT_NAME = 6;
        int COL_PRIX_VENTE = 7;
        int COL_PRIX_MINI = 8;
        int COL_PACKHOUSE = 9;
        int COL_DEPART_DATE = 10;
        int COL_DELIVERY_DATE = 11;
        int COL_QTY_CASE = 13;
        int COL_QTY_PALLETS = 14;
        int COL_CASES_PER_PALLETS = 15;
        int COL_BB_DATE = 16;
        int COL_HAULIER = 17;
        int COL_ORD_CREATE = 18;

        // load sheet
        Workbook workbook = ProgramService.loadFile(chunks);
        Sheet sheet = workbook.getSheetAt(0);

        String ls_load_ref_prec = "";
        String ls_cen_ref_prec = "";
        String ls_nordre_prec = "";

        for (Row row : sheet) {
            ProgramRow pRow = new ProgramRow();

            // ignore header row
            if (row.getRowNum() == 0)
                continue;

            // exit when no more data to consume
            if (row.getCell(COL_LOAD_REFERENCE).getStringCellValue().isBlank())
                break;

            Character ls_create_ligne = 'N';
            val ls_load_reference = row.getCell(COL_LOAD_REFERENCE).getStringCellValue();
            val ls_programme = ls_load_reference.split("/")[0];
            // val ls_tpnd = row.getCell(COL_TPND).getStringCellValue();
            val ls_depot_name = row.getCell(COL_DEPOT_NAME).getStringCellValue().toUpperCase().trim();
            val ls_packhouse = row.getCell(COL_PACKHOUSE).getStringCellValue().trim();
            val ls_depart_date = row.getCell(COL_DEPART_DATE).getStringCellValue();
            val ls_delivery_date = row.getCell(COL_DELIVERY_DATE).getStringCellValue();
            String ls_qty_case = row.getCell(COL_QTY_CASE).getStringCellValue().trim();
            String ls_qty_pallets = row.getCell(COL_QTY_PALLETS).getStringCellValue().trim();
            String ls_case_per_pallets = row.getCell(COL_CASES_PER_PALLETS).getStringCellValue().trim();
            val ls_bb_date = row.getCell(COL_BB_DATE).getStringCellValue();
            val ls_haulier = row.getCell(COL_HAULIER).getStringCellValue().toUpperCase().trim();
            val ls_array_art = row.getCell(COL_ARTS_REF).getStringCellValue().split("-");
            val ld_prix_vte = row.getCell(COL_PRIX_VENTE).getNumericCellValue();
            val ld_prix_mini = row.getCell(COL_PRIX_MINI).getNumericCellValue();

            final AtomicReference<String> ls_soc_code = new AtomicReference<>("");
            final AtomicReference<String> ls_cli_ref = new AtomicReference<>("");
            if (ls_load_reference.startsWith("OF")) {
                ls_soc_code.set("BUK");
                ls_cli_ref.set("007396"); // TESCOSTORESGBP 007396
            } else
                pRow.pushErreur("Erreur préfixe Load reference");

            final AtomicReference<String> ls_ind_mod_liv = new AtomicReference<>("");
            if (ls_load_reference.startsWith("OFTRUE"))
                ls_ind_mod_liv.set("D");
            else if (ls_load_reference.startsWith("OFXD"))
                ls_ind_mod_liv.set("X");
            else
                pRow.pushErreur("Erreur préfixe Load reference DIRECT ou XDOC");
            val ls_concat = " " + ls_ind_mod_liv + "%";

            // Pas de référence client pour ORCHARD
            val ls_ref_cli = "";
            val entrepot = this.entrepotRepo.findOne((root, cq, cb) -> cb.and(
                    cb.equal(root.get("client").get("id"), ls_ref_cli),
                    cb.equal(root.get("societe").get("id"), ls_soc_code.get()),
                    cb.equal(root.get("modeLivraison"), ls_ind_mod_liv.get()),
                    cb.like(root.get("code"), ls_depot_name + ls_concat),
                    cb.isTrue(root.get("valide"))));
            if (entrepot.isEmpty())
                pRow.pushErreur("Erreur entrepôt non trouvé: " + ls_depot_name);

            if (ls_haulier.isBlank())
                pRow.pushErreur("Erreur transporteur non renseigné !!");

            String ls_transp_approche = "";
            String ls_transp_final = ls_haulier;
            if (ls_haulier.startsWith("APPROCHE")) {
                ls_transp_approche = ls_haulier.substring(10, ls_haulier.indexOf('+') - 1 - 10);
                ls_transp_final = ls_haulier.substring(ls_haulier.indexOf('+') + 2, ls_haulier.length());
            }

            // EX DLUO: Toujours "/"
            // Doit être renseigné dnas le fichier Excel
            val ls_dluo = ls_bb_date.isBlank() ? "/" : ls_bb_date.trim();

            val existing_ordre = this.ordreRepo.findOne((root, cq, cb) -> cb.and(
                    cb.equal(root.get("codeChargement"), ls_load_reference),
                    cb.equal(root.get("entrepot"), entrepot.get()),
                    cb.equal(root.get("client").get("id"), ls_cli_ref.get())));

            final AtomicReference<String> ls_nordre = new AtomicReference<>("");
            final AtomicReference<String> ls_ord_ref = new AtomicReference<>("");
            if (existing_ordre.isPresent()) {

                if (ls_load_reference != ls_load_ref_prec && entrepot.get().getId() != ls_cen_ref_prec) {

                    val ls_nordre_curr = existing_ordre.get().getNumero();
                    if (ls_nordre_curr != ls_nordre_prec)
                        pRow.pushMessage("Ordre déjà existant " + ls_nordre_curr + " !!");

                    ls_create_ligne = 'N';
                    ls_nordre_prec = ls_nordre_curr;

                } else
                    ls_create_ligne = 'O';

            } else {
                FunctionResult functionRes = this.functionOrdreRepo.fCreateOrdreV4(
                        ls_soc_code.get(),
                        ls_cli_ref.get(),
                        entrepot.get().getId(),
                        ls_transp_final,
                        ls_ref_cli,
                        false,
                        false,
                        LocalDateTime.of(
                                LocalDate.parse(ls_depart_date,
                                        DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                LocalTime.MIN),
                        "ORD",
                        LocalDateTime.of(
                                LocalDate.parse(ls_delivery_date,
                                        DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                LocalTime.MIN),
                        ls_load_reference);
                if (functionRes.getRes() != FunctionResult.RESULT_OK)
                    pRow.pushErreur(functionRes.getMsg());

                ls_ord_ref.set((String) functionRes.getData().get("ls_ord_ref"));
                ls_load_ref_prec = ls_load_reference;
                ls_cen_ref_prec = entrepot.get().getId();

                if (functionRes.getRes().equals(FunctionResult.RESULT_OK)) {
                    ls_create_ligne = 'O';
                    ls_nordre.set(this.ordreRepo.getOne(ls_ord_ref.get()).getNumero());
                    pRow.pushMessage("Ordre créé");
                    res.incrementOrdreCount();
                } else {
                    ls_nordre.set("");
                    pRow.pushMessage("Ordre NON créé, Numéro d'ordre invalide: " + ls_ord_ref);
                }
            }

            if (ls_create_ligne.equals('O')) {
                for (int ll_count = 1; ll_count < ls_array_art.length + 1; ll_count++) {
                    if (ll_count > 1) {
                        ls_qty_case = "0";
                        ls_qty_pallets = "0";
                        ls_case_per_pallets = "0";
                    }

                    val ls_art = String.format("%" + 6 + "0", ls_array_art[ll_count]);

                    val ls_art_existe = this.entityManager
                            .createQuery("select 'O' from GEO_ARTICLE_COLIS where art_ref = :ls_art and valide = 'O'")
                            .setParameter("ls_art", ls_art)
                            .getSingleResult().equals('O');

                    if (ls_art_existe) {
                        String ls_prog = "";
                        if (ls_programme.substring(0, 2).equals("TES")
                                || ls_programme.substring(0, 1).equals("SP"))
                            ls_prog = "TESCO";
                        else if (ls_programme.substring(0, 1).equals("OF"))
                            ls_prog = "ORCHARD";

                        FunctionResult ls_rc = this.functionOrdreRepo.fCreateLigneOrdre(
                                ls_ord_ref.get(),
                                ls_art,
                                ls_packhouse,
                                entrepot.get().getId(),
                                Double.parseDouble(ls_case_per_pallets),
                                Double.parseDouble(ls_qty_pallets),
                                Double.parseDouble(ls_qty_case),
                                ld_prix_vte,
                                ld_prix_mini,
                                ls_prog,
                                ls_dluo);

                        // HANDLE ls_rc
                        if (ls_rc.getRes() != FunctionResult.RESULT_OK)
                            pRow.pushErreur("Erreur création ligne article pour ORD_REF: " + ls_ord_ref + " -> "
                                    + ls_rc.getMsg());

                        row.getCell(COL_ORD_CREATE).setCellValue(ls_nordre.get());

                        // CREER ORDLOG

                        LocalDateTime ls_DATLIV_GRP, ls_datdep_grp_p;
                        String ls_grp_code;
                        val relDepartDate = LocalDateTime
                                .of(LocalDate.parse(ls_depart_date, DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                        LocalTime.MIN);
                        if (ls_haulier.substring(0, 8).equals("APPROCHE")) {
                            ls_DATLIV_GRP = relDepartDate.plusDays(1);
                            ls_datdep_grp_p = relDepartDate.plusDays(1);
                            ls_grp_code = "TERRYLOIRE";
                        } else {
                            ls_DATLIV_GRP = null;
                            ls_datdep_grp_p = relDepartDate;
                            ls_grp_code = "";
                        }

                        val ls_DATDEP_FOU_P = relDepartDate;
                        val ls_DATDEP_FOU_P_YYYYMMDD = relDepartDate.format(DateTimeFormatter.ofPattern("yyyymmdd"));

                        val ls_ordlog_existe = this.ordreLogistiqueRepo.findOne((root, cq, cb) -> cb.and(
                                cb.equal(root.get("ordre").get("id"), ls_ord_ref.get()),
                                cb.equal(root.get("fournisseur").get("code"), ls_packhouse)));

                        if (ls_ordlog_existe.isEmpty()) {
                            GeoOrdreLogistique ordlog = new GeoOrdreLogistique();
                            try {
                                ordlog.setOrdre(this.ordreRepo.getOne(ls_ord_ref.get()));
                                ordlog.setFournisseur(this.fournisseurRepo.getOneByCode(ls_packhouse).get());
                                ordlog.setGroupage(this.groupageRepo.getOne(ls_grp_code));
                                ordlog.setTransporteurGroupage(this.transporteurRepo.getOne(ls_transp_approche));
                                ordlog.setDateDepartPrevueFournisseur(ls_DATDEP_FOU_P);
                                ordlog.setDateDepartPrevueFournisseurRaw(ls_DATDEP_FOU_P_YYYYMMDD);
                                ordlog.setTotalPalettesCommandees(Float.parseFloat(ls_qty_pallets));
                                ordlog.setTypeLieuGroupageArrivee('G');
                                ordlog.setTypeLieuDepart('F');
                                ordlog.setDateLivraisonLieuGroupage(ls_DATLIV_GRP);
                                ordlog.setDateDepartPrevueGroupage(ls_datdep_grp_p);
                                ordlog = this.ordreLogistiqueRepo.save(ordlog);

                            } catch (Exception e) {
                                pRow.pushErreur("Erreur création transport d'approche pour ORD_REF: " + ls_ord_ref);
                            }
                        }

                    } else
                        pRow.pushMessage("Article invalide: " + ls_art);

                }
            }

            res.incrementRowCount();
            res.pushRow(pRow);
        }

        FileOutputStream out = new FileOutputStream(chunks.getOriginalFilename());
        workbook.write(out);
        workbook.close();
        this.writeOutput(out);

        return res;
    }

    /** Write a program output in session */
    private void writeOutput(FileOutputStream stream) {
        RequestContextHolder.currentRequestAttributes().getSessionId();
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(true);
        session.setAttribute(ProgramController.GEO2_PROGRAM_OUTPUT, stream);
    }

    /** Get XLX or XLSX workbook */
    private static <F extends Workbook> F loadFile(MultipartFile chunks) throws IOException {
        switch (chunks.getContentType()) {
            case XLSX_MIME:
                return (F) new XSSFWorkbook(chunks.getInputStream());

            case XLS_MIME:
                return (F) new HSSFWorkbook(chunks.getInputStream());

            default:
                throw new RuntimeException("Unhandled document type");
        }
    }

}
