package fr.microtec.geo2.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import fr.microtec.geo2.common.StringUtils;
import fr.microtec.geo2.controller.ProgramController.ProgramResponse;
import fr.microtec.geo2.controller.ProgramController.ProgramResponse.ProgramRow;
import fr.microtec.geo2.persistance.StringEnum;
import fr.microtec.geo2.persistance.entity.FunctionResult;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdre;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreLigne;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdreLogistique;
import fr.microtec.geo2.persistance.entity.tiers.GeoBaseTarif;
import fr.microtec.geo2.persistance.entity.tiers.GeoEntrepot;
import fr.microtec.geo2.persistance.entity.tiers.GeoModeLivraison;
import fr.microtec.geo2.persistance.repository.ordres.GeoFunctionOrdreRepository;
import fr.microtec.geo2.persistance.repository.ordres.GeoOrdreLigneRepository;
import fr.microtec.geo2.persistance.repository.ordres.GeoOrdreLogistiqueRepository;
import fr.microtec.geo2.persistance.repository.ordres.GeoOrdreRepository;
import fr.microtec.geo2.persistance.repository.tiers.GeoBaseTarifRepository;
import fr.microtec.geo2.persistance.repository.tiers.GeoEntrepotRepository;
import fr.microtec.geo2.persistance.repository.tiers.GeoFournisseurRepository;
import fr.microtec.geo2.persistance.repository.tiers.GeoGroupageRepository;
import fr.microtec.geo2.persistance.repository.tiers.GeoTransporteurRepository;
import fr.microtec.geo2.service.fs.Maddog2FileSystemService;
import fr.microtec.geo2.service.fs.Maddog2FileSystemService.PATH_KEY;
import lombok.Data;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProgramService {

    static final String XLSX_MIME = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static final String XLS_MIME = "application/vnd.ms-excel";

    public static final String GEO2_PROGRAM_OUTPUT = "GEO2_PROGRAM_OUTPUT";
    public static final String GEO2_PROGRAM_FILENAME = "GEO2_PROGRAM_NAME";
    public static final String GEO2_PROGRAM_FILETYPE = "GEO2_PROGRAM_TYPE";

    private final GeoEntrepotRepository entrepotRepo;
    private final GeoOrdreRepository ordreRepo;
    private final GeoOrdreLigneRepository olRepo;
    private final GeoOrdreLogistiqueRepository ordreLogistiqueRepo;
    private final GeoFournisseurRepository fournisseurRepo;
    private final GeoGroupageRepository groupageRepo;
    private final GeoTransporteurRepository transporteurRepo;
    private final GeoBaseTarifRepository baseTarifRepo;
    private final GeoFunctionOrdreRepository functionOrdreRepo;
    private final EntityManager entityManager;
    private final Maddog2FileSystemService md2Service;

    public ProgramService(
            EntityManager entityManager,
            GeoEntrepotRepository entrepotRepo,
            GeoOrdreRepository ordreRepo,
            GeoOrdreLigneRepository olRepo,
            GeoOrdreLogistiqueRepository ordreLogistiqueRepo,
            GeoFournisseurRepository fournisseurRepo,
            GeoGroupageRepository groupageRepo,
            GeoTransporteurRepository transporteurRepo,
            GeoBaseTarifRepository baseTarifRepo,
            GeoFunctionOrdreRepository functionOrdreRepo,
            Maddog2FileSystemService md2Service) {
        this.entityManager = entityManager;
        this.entrepotRepo = entrepotRepo;
        this.ordreRepo = ordreRepo;
        this.olRepo = olRepo;
        this.ordreLogistiqueRepo = ordreLogistiqueRepo;
        this.fournisseurRepo = fournisseurRepo;
        this.groupageRepo = groupageRepo;
        this.transporteurRepo = transporteurRepo;
        this.baseTarifRepo = baseTarifRepo;
        this.functionOrdreRepo = functionOrdreRepo;
        this.md2Service = md2Service;
    }

    /** Save program file to Maddog2 system */
    public void archive(String societe, String utilisateur, MultipartFile chunk) throws IOException {
        Path dest = this.md2Service.createDirectory("/", PATH_KEY.GEO_IMPORT_GB.toString().toLowerCase());
        this.md2Service.save(
                dest.resolve(societe + "_" +
                        utilisateur + "_" +
                        LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "_" +
                        chunk.getOriginalFilename()),
                chunk.getBytes(),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    @Data
    private static class DateMinTesco {
        HashMap<Integer, String> ls_array_programme;
        HashMap<Integer, LocalDateTime> ls_array_date_depart_ordre;
    }

    /** Récupération la plus petite date de départ pour l'ordre [TESCO] */
    private static DateMinTesco fetchDateMinTesco(Sheet sheet, int COL_LOAD_REFERENCE, int COL_DEPART_DATE) {

        HashMap<Integer, String> ls_array_programme = new HashMap<>();
        HashMap<Integer, LocalDateTime> ls_array_date_depart_ordre = new HashMap<>();

        // On saute la première ligne qui contient les en têtes
        Integer ll_row = 2;
        String ls_programme_ordre = "";
        Integer ll_ind = 0;

        String ls_value = sheet
                .getRow(ll_row - 1)
                .getCell(COL_LOAD_REFERENCE)
                .getStringCellValue()
                .trim();
        ls_value = ls_value.substring(0, ls_value.indexOf('/') - 1);
        LocalDateTime ls_depart_date_ordre = sheet
                .getRow(ll_row - 1)
                .getCell(COL_DEPART_DATE)
                .getLocalDateTimeCellValue();
        LocalDateTime ls_depart_date_ordre_prec = LocalDateTime.of(2500, 1, 1, 0, 0, 0);

        while (ls_value != null && !ls_value.isBlank()) {
            if (!ls_programme_ordre.equals(ls_value)) {
                ll_ind++;
                ls_programme_ordre = ls_value;
                ls_array_programme.put(ll_ind, ls_programme_ordre);
                ls_array_date_depart_ordre.put(ll_ind, ls_depart_date_ordre);
                ls_depart_date_ordre_prec = LocalDateTime.of(2500, 1, 1, 0, 0, 0);
            }
            if (ls_depart_date_ordre.isBefore(ls_depart_date_ordre_prec)) {
                ls_array_date_depart_ordre.put(ll_ind, ls_depart_date_ordre);
                ls_depart_date_ordre_prec = ls_depart_date_ordre;
            }
            ll_row++;
            try {
                ls_value = sheet
                        .getRow(ll_row - 1)
                        .getCell(COL_LOAD_REFERENCE)
                        .getStringCellValue()
                        .trim();
                ls_value = ls_value.substring(0, ls_value.indexOf('/') - 1);
                ls_depart_date_ordre = sheet
                        .getRow(ll_row - 1)
                        .getCell(COL_DEPART_DATE)
                        .getLocalDateTimeCellValue();
            } catch (Exception e) {
                ls_value = null;
            }
        }

        val res = new DateMinTesco();
        res.setLs_array_date_depart_ordre(ls_array_date_depart_ordre);
        res.setLs_array_programme(ls_array_programme);
        return res;

    }

    public ProgramResponse importTesco(MultipartFile chunks, String societe, String utilisateur, Boolean generic)
            throws IOException {
        val res = new ProgramResponse();

        // Position des colonnes qui nous interressent dans le tableau Excel
        // int COL_PROGRAMME = 0;
        int COL_LOAD_REFERENCE = 1;
        // int COL_TPND = 2;
        int COL_ARTS_REF = 3;
        int COL_DEPOT_NAME = 5;
        int COL_PRIX_VENTE = 6;
        int COL_PRIX_MINI_SA = 7;
        int COL_PACKHOUSE = 8;
        int COL_DEPART_DATE = 10;
        int COL_DELIVERY_DATE = 12;
        // int COL_DEPOT_DATE = 14;
        int COL_QTY_CASE = 16;
        int COL_QTY_PALLETS = 17;
        int COL_CASES_PER_PALLETS = 18;
        int COL_BB_DATE = 19;
        int COL_JC = 20;
        int COL_HAULIER = 21;
        int COL_ORD_CREATE = 22;
        int COL_ORD_PERE_SA = 23;

        List<String> addedOrdreRefs = new ArrayList<>();
        // load sheet
        Workbook workbook = ProgramService.loadFile(chunks);

        // try {

        Sheet sheet = workbook.getSheetAt(2);

        DateMinTesco dmt = ProgramService.fetchDateMinTesco(sheet, COL_LOAD_REFERENCE, COL_DEPART_DATE);
        HashMap<Integer, String> ls_array_programme = dmt.getLs_array_programme();
        HashMap<Integer, LocalDateTime> ls_array_date_depart_ordre = dmt.getLs_array_date_depart_ordre();
        LocalDateTime ls_depart_date_ordre;

        String ls_load_ref_prec = "";
        String ls_cen_ref_prec = "";
        String ls_nordre_prec = "";
        Character ls_create_ligne;
        String ls_ordref_inscrit_prec = "";
        List<String> ls_ordre_cree = new ArrayList<>();
        List<String> ls_loadref_no_dupplic = new ArrayList<>();
        Integer ll_parse;
        String ls_stock_dern_loadref = "";

        final AtomicReference<String> ls_nordre = new AtomicReference<>("");
        final AtomicReference<String> ls_ord_ref = new AtomicReference<>("");

        outer: for (Row row : sheet) {
            ProgramRow pRow = new ProgramRow();

            // ignore header row
            if (row.getRowNum() == 0)
                continue;

            // exit when no more data to consume
            String ls_load_reference;
            try {
                ls_load_reference = row.getCell(COL_LOAD_REFERENCE).getStringCellValue();
                if (ls_load_reference.isBlank())
                    break;
            } catch (Exception e) {
                break;
            }

            pRow.setLoadRef(ls_load_reference);
            String ls_programme = ls_load_reference.split("/")[0];
            // val ls_tpnd = row.getCell(COL_TPND).getStringCellValue();
            String ls_depot_name = row.getCell(COL_DEPOT_NAME).getStringCellValue().toUpperCase().trim();
            pRow.setDepot(ls_depot_name);
            String ls_packhouse = row.getCell(COL_PACKHOUSE).getStringCellValue().trim();
            LocalDateTime ls_depart_date = row.getCell(COL_DEPART_DATE).getLocalDateTimeCellValue();
            pRow.setDateDepart(ls_depart_date);
            ls_depart_date_ordre = ls_depart_date;
            LocalDateTime ls_delivery_date = row.getCell(COL_DELIVERY_DATE).getLocalDateTimeCellValue();
            pRow.setDateLivraison(ls_delivery_date);
            // LocalDateTime ls_depot_date =
            // row.getCell(COL_DEPOT_DATE).getLocalDateTimeCellValue();
            Double ls_qty_case = row.getCell(COL_QTY_CASE).getNumericCellValue();
            Double ls_qty_pallets = row.getCell(COL_QTY_PALLETS).getNumericCellValue();
            Double ld_qty_pallets = ls_qty_pallets;
            Integer ll_qty_pallets = ld_qty_pallets.intValue();
            Double ls_case_per_pallets = row.getCell(COL_CASES_PER_PALLETS).getNumericCellValue();
            String ls_jc = row.getCell(COL_JC).getStringCellValue();
            String ls_haulier = row.getCell(COL_HAULIER).getStringCellValue().toUpperCase().trim();
            List<String> ls_array_art = List.of(row.getCell(COL_ARTS_REF).getStringCellValue().trim().split("-"));
            Double ld_prix_vte = row.getCell(COL_PRIX_VENTE).getNumericCellValue();

            Double ld_prix_mini_sa;

            for (int i = 1; i < ls_array_programme.size() + 1; i++) {
                if (ls_array_programme.get(i) == ls_programme)
                    ls_depart_date_ordre = ls_array_date_depart_ordre.get(i);
            }

            final AtomicReference<String> ls_soc_code = new AtomicReference<>("");
            final AtomicReference<String> ls_cli_ref = new AtomicReference<>("");
            if (ls_load_reference.startsWith("TES")) {
                ls_soc_code.set("BUK");
                ls_cli_ref.set("007396"); // TESCOSTORESGBP 007396
            } else if (ls_load_reference.startsWith("NEW") || ls_load_reference.startsWith("ISS")) {
                ls_soc_code.set("SA");
                ls_cli_ref.set("007488"); // BWUK 007488
            } else {
                pRow.pushErreur("Erreur préfixe Load reference");
                res.pushRow(pRow);
                continue;
            }

            String[] ls_array_load = ls_load_reference.split("/");
            final AtomicReference<String> ls_ind_mod_liv = new AtomicReference<>("");
            String ls_concat;
            String ls_ref_cli = "";

            if (!ls_load_reference.startsWith("NEW") && !ls_load_reference.startsWith("ISS")) { // TES......./
                if (ls_array_load[1].startsWith("BWTRUE")) {
                    ls_ind_mod_liv.set("D");
                    ls_concat = " " + ls_ind_mod_liv.get() + "%";
                    ls_ref_cli = ls_array_load[0] + '/' + ls_depot_name;
                } else if (ls_array_load[1].startsWith("BWXD")) {
                    ls_ind_mod_liv.set("X");
                    ls_concat = ' ' + ls_ind_mod_liv.get() + "%";
                    ls_ref_cli = ls_array_load[0] + '/' + "TEYNHAM";
                } else {
                    pRow.pushErreur("Erreur préfixe Load reference DIRECT ou XDOC");
                    res.pushRow(pRow);
                    continue;
                }
            } else { // SP..../
                ls_ind_mod_liv.set("D");
                ls_concat = "";
                /*
                 * Le champ DEPOT NAME du fichier EXCEL devra être rempli
                 * if isnull(ls_depot_name) or ls_depot_name = '' then
                 * if left(ls_array_load[3], 3) = 'ISS' then
                 * ls_depot_name = 'TESCO ISS'
                 * elseif left(ls_array_load[3], 3) = 'NEW' then
                 * ls_depot_name = 'TESCO NEWLINGFRUIT'
                 * end if
                 * end if
                 */
            }

            val entrepot = this.entrepotRepo.findOne((root, cq, cb) -> cb.and(
                    cb.equal(root.get("client").get("id"), ls_cli_ref.get()),
                    cb.equal(root.get("societe").get("id"), ls_soc_code.get()),
                    cb.equal(root.get("modeLivraison"),
                            StringEnum.getValueOf(GeoModeLivraison.class, ls_ind_mod_liv.get())),
                    cb.like(root.get("code"), ls_depot_name + ls_concat),
                    cb.isTrue(root.get("valide"))));
            if (entrepot.isEmpty()) {
                pRow.pushErreur("Erreur entrepôt non trouvé: " + ls_depot_name);
                res.pushRow(pRow);
                continue;
            }

            if (ls_haulier.isBlank()) {
                pRow.pushErreur("Erreur transporteur non renseigné !!");
                res.pushRow(pRow);
                continue;
            }

            String ls_transp_approche = "";
            String ls_transp_final = ls_haulier;
            if (ls_haulier.startsWith("APPROCHE")) {
                ls_transp_approche = ls_haulier.substring(9, ls_haulier.indexOf('+') - 1);
                ls_transp_final = ls_haulier.substring(ls_haulier.indexOf('+') + 2, ls_haulier.length());
            }

            // EX DLUO: BB = 08 DEC ; JC = L:8
            // Si ls_bb_date est vide ne rien renseigner
            // Si ls_bb_date "NO DATE/NO BB" il faudra renseigner : "SANS BOX ; SANS
            // IMPRESSION"
            String ls_dluo = "";
            try {
                LocalDateTime ls_bb_date = row.getCell(COL_BB_DATE).getLocalDateTimeCellValue();
                String ls_month = ls_bb_date.format(DateTimeFormatter.ofPattern("MMM").withLocale(Locale.ENGLISH));
                String ls_day = StringUtils.padLeft(Integer.toString(ls_bb_date.getDayOfMonth()), "0", 2);
                ls_dluo = "BB = " + ls_day + " " + ls_month.toUpperCase() + " ; JC = " + ls_jc;
            } catch (Exception e) {
                try {
                    if (row.getCell(COL_BB_DATE).getStringCellValue().trim().equals("NO DATE/NO BB"))
                        ls_dluo = "SANS BOX ; SANS IMPRESSION";
                    if (row.getCell(COL_BB_DATE).getStringCellValue().trim().equals("SANS BOX / SANS IMPRESSION"))
                        ls_dluo = "SANS BOX / SANS IMPRESSION";
                } catch (Exception e2) {
                }
            }

            Optional<GeoOrdre> existing_ordre = this.ordreRepo.findOne((root, cq, cb) -> cb.and(
                    cb.equal(root.get("codeChargement"), ls_load_reference),
                    cb.equal(root.get("entrepot"), entrepot.get()),
                    cb.equal(root.get("client").get("id"), ls_cli_ref.get())));

            if (existing_ordre.isPresent()) {

                ls_ord_ref.set((String) existing_ordre.get().getId());

                if (!ls_load_reference.equals(ls_load_ref_prec)
                        && !entrepot.get().getId().equals(ls_cen_ref_prec)) {

                    val ls_nordre_curr = existing_ordre.get().getNumero();
                    if (!ls_nordre_curr.equals(ls_nordre_prec))
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
                        ls_depart_date_ordre,
                        "ORD",
                        ls_delivery_date,
                        ls_load_reference);
                if (functionRes.getRes() != FunctionResult.RESULT_OK) {
                    pRow.pushErreur(functionRes.getMsg());
                    res.pushRow(pRow);
                    continue;
                }

                ls_ord_ref.set((String) functionRes.getData().get("ls_ord_ref"));
                addedOrdreRefs.add(ls_ord_ref.get());
                ls_load_ref_prec = ls_load_reference;
                ls_cen_ref_prec = entrepot.get().getId();

                if (functionRes.getRes().equals(FunctionResult.RESULT_OK)
                        && !ls_ord_ref.get().isBlank()) {
                    ls_create_ligne = 'O';
                    if (!ls_ord_ref.get().equals(ls_ordref_inscrit_prec)) {
                        if (!ls_load_reference.startsWith("NEW") && !ls_load_reference.startsWith("ISS")) {
                            ls_ordre_cree.add(ls_ord_ref.get());
                        }
                        ls_ordref_inscrit_prec = ls_ord_ref.get();
                        GeoOrdre ordre = this.ordreRepo.getOne(ls_ord_ref.get());
                        ordre.setUserModification(utilisateur);
                        this.ordreRepo.save(ordre);
                        ls_nordre.set(ordre.getNumero());
                        pRow.setOrdreNum(ls_nordre.get());
                        pRow.pushMessage("Ordre créé");
                    }
                } else {
                    ls_nordre.set("");
                    ls_create_ligne = 'N';
                    pRow.pushMessage("Numéro d'ordre invalide: " + ls_ord_ref);
                    row.getCell(COL_ORD_CREATE, MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue("ERREUR");
                    row.getCell(COL_ORD_PERE_SA, MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue("ERREUR");
                }
            }

            if (ls_create_ligne.equals('O')) {
                if (ls_load_reference.startsWith("NEW") || ls_load_reference.startsWith("ISS"))
                    row.getCell(COL_ORD_PERE_SA, MissingCellPolicy.CREATE_NULL_AS_BLANK)
                            .setCellValue(ls_nordre.get());
                else {
                    GeoOrdre ordre = this.ordreRepo.getOne(ls_ord_ref.get());
                    row.getCell(COL_ORD_CREATE, MissingCellPolicy.CREATE_NULL_AS_BLANK)
                            .setCellValue(ordre.getNumero());
                }

                // Test si on est dans le cas de demi-palette
                if ((ld_qty_pallets - ll_qty_pallets == 0) || ll_qty_pallets == 0) {
                    ll_parse = 1;
                } else {
                    ll_parse = 2;
                    // Dupplication vers la SA: inscription des load reference ayant des demi
                    // palettes
                    if (ls_load_reference.startsWith("TES")) {
                        val fou_uk = this.fournisseurRepo
                                .findOne((root, cq, cb) -> cb.equal(root.get("code"), ls_packhouse));
                        if (fou_uk.isPresent() && fou_uk.get().getNumeroVersionUK() != null) {
                            if (fou_uk.get().getNumeroVersionUK() == 1) { // Cas pas de gestion de demi-palette
                                                                          // alors on
                                                                          // stocke le load reference à éviter
                                if (ls_stock_dern_loadref != ls_load_reference) {
                                    ls_loadref_no_dupplic.add(ls_load_reference); // On écrit le code de chargement
                                                                                  // à
                                                                                  // éviter
                                    ls_stock_dern_loadref = ls_load_reference; // stockage du dernier load ref
                                                                               // d'inscrit
                                                                               // dans l'array
                                }
                            }
                        }
                    }
                    // Fin Dupplication
                }

                for (int ll_demi_pallets = 1; ll_demi_pallets <= ll_parse; ll_demi_pallets++) {
                    if (ll_demi_pallets == 2) { // On modifie les qtés de colis commandé pour la demi-palette
                        ls_case_per_pallets = row.getCell(COL_CASES_PER_PALLETS).getNumericCellValue();
                        ls_qty_case = (ld_qty_pallets - ll_qty_pallets) * ls_case_per_pallets;
                        ll_qty_pallets = 1;
                    } else {
                        if (ll_qty_pallets > 0)
                            ls_qty_case = ls_case_per_pallets * ll_qty_pallets;
                        else {
                            ls_qty_case = ls_case_per_pallets * ld_qty_pallets;
                            ll_qty_pallets = 1;
                        }
                    }
                    ls_qty_pallets = ll_qty_pallets.doubleValue();

                    for (int ll_count = 0; ll_count < ls_array_art.size(); ll_count++) {
                        if (ll_count > 0) {
                            ls_qty_case = 0d;
                            ls_qty_pallets = 0d;
                            ls_case_per_pallets = 0d;
                        }

                        val ls_art = StringUtils.padLeft(ls_array_art.get(ll_count), "0", 6);

                        Boolean ls_art_existe = true;
                        try {
                            this.entityManager
                                    .createNativeQuery(
                                            "select 'O' from GEO_ARTICLE_COLIS where art_ref = :ls_art and valide = 'O'")
                                    .setParameter("ls_art", ls_art)
                                    .getSingleResult();
                        } catch (NoResultException e) {
                            ls_art_existe = false;
                        }

                        if (ls_art_existe) {
                            String ls_prog = "";
                            if (ls_programme.startsWith("TES"))
                                ls_prog = "TESCO";
                            else if (ls_programme.startsWith("NEW") || ls_programme.startsWith("ISS"))
                                ls_prog = "SP";
                            else if (ls_programme.startsWith("OF"))
                                ls_prog = "ORCHARD";

                            if (ls_prog.equals("TESCO"))
                                ld_prix_mini_sa = 0d;
                            else if (ls_prog.equals("SP")) {
                                ld_prix_mini_sa = row.getCell(COL_PRIX_MINI_SA).getNumericCellValue();
                            } else
                                ld_prix_mini_sa = 0d;

                            FunctionResult ls_rc = this.functionOrdreRepo.fCreateLigneOrdre(
                                    ls_ord_ref.get(),
                                    ls_art,
                                    ls_packhouse,
                                    entrepot.get().getId(),
                                    ls_case_per_pallets,
                                    ls_qty_pallets,
                                    ls_qty_case,
                                    ld_prix_vte,
                                    0d,
                                    ls_prog,
                                    ls_dluo);

                            // HANDLE ls_rc
                            if (ls_rc.getRes() != FunctionResult.RESULT_OK) {
                                pRow.pushErreur(
                                        "Erreur création ligne article pour ORD_REF: " + ls_ord_ref.get() + " -> "
                                                + ls_rc.getMsg());
                                res.pushRow(pRow);
                                continue outer;
                            }

                            String orl_ref = (String) ls_rc.getData().get("ls_orl_ref");
                            GeoOrdreLigne ordLig = this.olRepo.getOne(orl_ref);
                            ordLig.setUserModification(utilisateur);
                            this.olRepo.save(ordLig);

                            row.getCell(COL_ORD_CREATE, MissingCellPolicy.CREATE_NULL_AS_BLANK)
                                    .setCellValue(ls_nordre.get());

                            // CREER ORDLOG

                            LocalDateTime ls_DATLIV_GRP, ls_datdep_grp_p;
                            String ls_grp_code;
                            val relDepartDate = ls_depart_date;
                            if (ls_haulier.startsWith("APPROCHE")) {
                                ls_DATLIV_GRP = relDepartDate.plusDays(1);
                                ls_datdep_grp_p = relDepartDate.plusDays(1);
                                ls_grp_code = "TERRYLOIRE";
                            } else {
                                ls_DATLIV_GRP = null;
                                ls_datdep_grp_p = relDepartDate;
                                ls_grp_code = "";
                            }

                            val ls_DATDEP_FOU_P = relDepartDate;
                            val ls_DATDEP_FOU_P_YYYYMMDD = relDepartDate
                                    .format(DateTimeFormatter.ofPattern("yyyyMMdd"));

                            val ls_ordlog_existe = this.ordreLogistiqueRepo.findOne((root, cq, cb) -> cb.and(
                                    cb.equal(root.get("ordre").get("id"), ls_ord_ref.get()),
                                    cb.equal(root.get("fournisseur").get("code"), ls_packhouse)));

                            if (ls_ordlog_existe.isEmpty()) {
                                GeoOrdreLogistique ordlog = new GeoOrdreLogistique();
                                try {
                                    ordlog.setOrdre(this.ordreRepo.getOne(ls_ord_ref.get()));
                                    ordlog.setCodeFournisseur(ls_packhouse);
                                    ordlog.setGroupage(this.groupageRepo.getOne(ls_grp_code));
                                    ordlog.setTransporteurGroupage(
                                            this.transporteurRepo.getOne(ls_transp_approche));
                                    ordlog.setDateDepartPrevueFournisseur(ls_DATDEP_FOU_P);
                                    ordlog.setDateDepartPrevueFournisseurRaw(ls_DATDEP_FOU_P_YYYYMMDD);
                                    ordlog.setTotalPalettesCommandees(ls_qty_pallets.floatValue());
                                    ordlog.setTypeLieuGroupageArrivee('G');
                                    ordlog.setTypeLieuDepart('F');
                                    ordlog.setDateLivraisonLieuGroupage(ls_DATLIV_GRP);
                                    ordlog.setDateDepartPrevueGroupage(ls_datdep_grp_p);
                                    ordlog = this.ordreLogistiqueRepo.save(ordlog);
                                } catch (Exception e) {
                                    pRow.pushErreur(
                                            "Erreur création transport d'approche pour ORD_REF: " + ls_ord_ref
                                                    + ". " + e.getMessage());
                                    res.pushRow(pRow);
                                    continue;
                                }
                            }

                        } else
                            pRow.pushMessage("Article invalide: " + ls_art);

                    }

                } // boucle demi-palettes
            } else {
                row.getCell(COL_ORD_CREATE, MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue("ERREUR");
                row.getCell(COL_ORD_PERE_SA, MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue("ERREUR");
            }

            res.pushRow(pRow);

        }

        Integer ll_creation_ordre_sa = 0;
        String ls_nordre_regroup = "";
        String ls_nordre_regroup_prec = "";
        for (String ll_ord : ls_ordre_cree) {
            Boolean lb_no_dupplic = false;
            val ordre = this.ordreRepo.findOne((root, cq, cb) -> cb.equal(root.get("id"), ll_ord));
            if (ordre.isPresent()) {

                val ls_code_chargement = ordre.get().getCodeChargement();
                val ls_nordre_ = ordre.get().getNumero();
                for (String ll_cpt : ls_loadref_no_dupplic) {
                    if (ll_cpt.equals(ls_code_chargement))
                        lb_no_dupplic = true;
                }
                if (lb_no_dupplic == false) {
                    ls_nordre_regroup_prec = ls_nordre_regroup;
                    FunctionResult ll_return = this.functionOrdreRepo
                            .fnMajOrdreRegroupementV2(ll_ord, societe, generic, utilisateur);
                    if (ll_return.getRes() == FunctionResult.RESULT_OK) {
                        ls_nordre_regroup = this.entityManager
                                .createNativeQuery("select nordre_rgp from GEO_ORDRE where ord_ref = :ord_ref")
                                .setParameter("ord_ref", ll_ord)
                                .getSingleResult()
                                .toString();

                        // écriture dans le fichier du n° ordre de regroupement
                        Integer ll_row = 2;
                        Row row = sheet.getRow(ll_row - 1);
                        String ls_value = row.getCell(COL_ORD_CREATE).getStringCellValue();
                        while (!ls_value.isBlank()) {
                            // do while not IsNull(ls_value) and ls_value <> ""
                            if (ls_value.equals(ls_nordre_))
                                row.getCell(COL_ORD_PERE_SA, MissingCellPolicy.CREATE_NULL_AS_BLANK)
                                        .setCellValue(ls_nordre_regroup);
                            row = sheet.getRow(ll_row - 1);
                            ll_row++;
                            try {
                                ls_value = row.getCell(COL_ORD_CREATE).getStringCellValue();
                            } catch (Exception e) {
                                break;
                            }
                        }
                        if (!ls_nordre_regroup.equals(ls_nordre_regroup_prec)) {
                            ll_creation_ordre_sa++;
                        }
                    } else {
                        res.getRow(ls_nordre_).get()
                                .pushErreur("Erreur création dupplication SA ORDRE " + ll_ord + " -> "
                                        + ll_return.getMsg());
                    }
                }
            }
        }

        // debut mise à jour des prix MINI sur les ordres SA
        int ll_cpt_update_prix_mini = 0;
        Integer ll_row = 2;

        // st_progress.text = "Début mise à jour des prix MINI des ordres SA autre que
        // programme 'NEW' et 'ISS'"
        String ls_ordre_sa_prec = "";
        String ls_programme = sheet.getRow(ll_row - 1).getCell(COL_LOAD_REFERENCE).getStringCellValue();
        List<String> ls_array_art_sa = new ArrayList<>();
        final AtomicReference<String> ls_ordre_sa = new AtomicReference<>("");
        final AtomicReference<String> ls_ord_ref_sa = new AtomicReference<>("");

        while (ls_programme != null && !ls_programme.isBlank()) {

            try {
                ls_ordre_sa
                        .set(Double
                                .toString(sheet.getRow(ll_row - 1).getCell(COL_ORD_PERE_SA).getNumericCellValue()));
            } catch (Exception e) {
                ls_ordre_sa.set(sheet.getRow(ll_row - 1)
                        .getCell(COL_ORD_PERE_SA, MissingCellPolicy.CREATE_NULL_AS_BLANK)
                        .getStringCellValue());
            }
            if (!ls_ordre_sa.get().isBlank()) {
                int ll_pos = ls_programme.indexOf('/');
                ls_programme = ls_programme.substring(0, ll_pos - 1);
                // l'alimentation du prix MINI est faite au moment de la création de la ligne
                if (!ls_programme.startsWith("NEW")
                        && (ls_programme.startsWith("ISS") || ls_programme.startsWith("TES"))) {
                    List<String> ls_array_art = List
                            .of(sheet.getRow(ll_row - 1).getCell(COL_ARTS_REF).getStringCellValue().trim()
                                    .split("-"));
                    Double ld_prix_mini_sa = sheet.getRow(ll_row - 1).getCell(COL_PRIX_MINI_SA)
                            .getNumericCellValue();
                    if (!ls_ordre_sa.get().equals(ls_ordre_sa_prec)) {
                        ls_ordre_sa_prec = ls_ordre_sa.get();
                        // int ll_ind = 0;

                        List<GeoOrdreLigne> lignes = this.olRepo.findAll((root, cq, cb) -> cb.and(
                                cb.equal(root.get("ordre").get("numero"), ls_ordre_sa.get()),
                                cb.equal(root.get("ordre").get("societe").get("id"), "SA"),
                                cb.or(cb.isNull(root.get("achatPrixUnitaire")),
                                        cb.equal(root.get("achatPrixUnitaire"), 0)),
                                cb.isNull(root.get("achatUnite").get("id")),
                                cb.equal(root.get("ordre").get("campagne"),
                                        root.get("ordre").get("societe").get("campagne"))));

                        if (!lignes.isEmpty())
                            ls_ord_ref_sa.set(lignes.get(0).getOrdre().getId());

                        for (GeoOrdreLigne ligne : lignes) {
                            ls_array_art_sa.add(ligne.getArticle().getId());
                            // ll_ind++;
                        }
                    }

                    for (int ll_ind_sa = 0; ll_ind_sa < ls_array_art_sa.size(); ll_ind_sa++) {
                        for (int ll_ind = 0; ll_ind < ls_array_art.size(); ll_ind++) {
                            if (ls_array_art_sa.get(ll_ind_sa).equals(ls_array_art.get(ll_ind))) {
                                val cur_art = ls_array_art.get(ll_ind);
                                val cur_art_sa = ls_array_art_sa.get(ll_ind_sa);
                                List<GeoOrdreLigne> articlesSA = this.olRepo.findAll((root, cq, cb) -> cb.and(
                                        cb.equal(root.get("ordre").get("id"), ls_ord_ref_sa.get()),
                                        cb.equal(root.get("article").get("id"), cur_art)));
                                articlesSA.forEach(ligne -> {
                                    ligne.setAchatPrixUnitaire(ld_prix_mini_sa);
                                    ligne.setAchatUnite(this.baseTarifRepo.getOne("COLIS"));
                                    ligne.setAchatDevise("EUR");
                                    ligne.setAchatDeviseTaux(1d);
                                    ligne.setAchatDevisePrixUnitaire(ld_prix_mini_sa);
                                    try {
                                        this.olRepo.save(ligne);
                                        res.incrementPrixMiniCount();
                                    } catch (Exception e) {
                                        res.getRow(ligne.getNumero())
                                                .ifPresent(r -> r.pushErreur(
                                                        "Erreur update prix mini pour ORD_REF: "
                                                                + ls_ord_ref_sa.get()
                                                                + ", ARTICLE: "
                                                                + cur_art_sa));
                                    }
                                });
                            }
                        }
                    }
                }
            }
            ll_row++;
            try {
                ls_programme = sheet.getRow(ll_row - 1).getCell(COL_LOAD_REFERENCE).getStringCellValue();
            } catch (Exception e) {
                ls_programme = null;
            }

        }

        // st_progress.text = "Fin mise à jour des prix MINI des ordres SA"
        // fin mise à jour des prix MINI sur les ordres SA

        res.setOrdreCount(ll_creation_ordre_sa);

        OutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        this.writeOutput(out, chunks);
        workbook.close();

        // } catch (Exception exception) {
        // addedOrdreRefs.forEach(id -> this.ordreRepo.deleteById(id));
        // var throwable = new RuntimeException(exception.getMessage());
        // log.error("Program import has failed: ", throwable);
        // throw throwable;
        // }

        return res;
    }

    public ProgramResponse importOrchard(MultipartFile chunks, String utilisateur) throws IOException {

        val res = new ProgramResponse();

        // useful columns indexes
        // int COL_PROGRAMME = 0;
        int COL_LOAD_REFERENCE = 1;
        // int COL_TPND = 2;
        int COL_ARTS_REF = 3;
        int COL_DEPOT_NAME = 5;
        int COL_PRIX_VENTE = 6;
        int COL_PRIX_MINI = 7;
        int COL_PACKHOUSE = 8;
        int COL_DEPART_DATE = 9;
        int COL_DELIVERY_DATE = 10;
        int COL_QTY_CASE = 12;
        int COL_QTY_PALLETS = 13;
        int COL_CASES_PER_PALLETS = 14;
        int COL_BB_DATE = 15;
        int COL_HAULIER = 16;
        int COL_ORD_CREATE = 17;

        // load sheet
        List<String> addedOrdreRefs = new ArrayList<>();
        Workbook workbook = ProgramService.loadFile(chunks);
        try {
            Sheet sheet = workbook.getSheetAt(0);

            String ls_load_ref_prec = "";
            String ls_cen_ref_prec = "";
            String ls_nordre_prec = "";

            outer: for (Row row : sheet) {
                ProgramRow pRow = new ProgramRow();

                // ignore header row
                if (row.getRowNum() == 0)
                    continue;

                String ls_load_reference;
                try {
                    ls_load_reference = row.getCell(COL_LOAD_REFERENCE).getStringCellValue();
                    if (ls_load_reference.isBlank())
                        break;
                } catch (Exception e) {
                    break;
                }

                Character ls_create_ligne = 'N';
                pRow.setLoadRef(ls_load_reference);
                String ls_programme = ls_load_reference.split("/")[0];
                // val ls_tpnd = row.getCell(COL_TPND).getStringCellValue();
                String ls_depot_name = row.getCell(COL_DEPOT_NAME).getStringCellValue().toUpperCase().trim();
                pRow.setDepot(ls_depot_name);
                String ls_packhouse = row.getCell(COL_PACKHOUSE).getStringCellValue().trim();
                LocalDateTime ls_depart_date = row.getCell(COL_DEPART_DATE).getLocalDateTimeCellValue();
                pRow.setDateDepart(ls_depart_date);
                LocalDateTime ls_delivery_date = row.getCell(COL_DELIVERY_DATE).getLocalDateTimeCellValue();
                pRow.setDateLivraison(ls_delivery_date);
                Double ls_qty_case = row.getCell(COL_QTY_CASE).getNumericCellValue();
                Double ls_qty_pallets = row.getCell(COL_QTY_PALLETS).getNumericCellValue();
                Double ls_case_per_pallets = row.getCell(COL_CASES_PER_PALLETS).getNumericCellValue();
                String ls_haulier = row.getCell(COL_HAULIER).getStringCellValue().toUpperCase().trim();
                Double ld_prix_vte = row.getCell(COL_PRIX_VENTE).getNumericCellValue();
                Double ld_prix_mini = row.getCell(COL_PRIX_MINI).getNumericCellValue();

                final AtomicReference<String> ls_soc_code = new AtomicReference<>("");
                final AtomicReference<String> ls_cli_ref = new AtomicReference<>("");
                if (ls_load_reference.startsWith("OF")) {
                    ls_soc_code.set("BUK");
                    ls_cli_ref.set("007396"); // TESCOSTORESGBP 007396
                } else {
                    pRow.pushErreur("Erreur préfixe Load reference");
                    res.pushRow(pRow);
                    continue;
                }

                final AtomicReference<String> ls_ind_mod_liv = new AtomicReference<>("");
                if (ls_load_reference.startsWith("OFTRUE"))
                    ls_ind_mod_liv.set("D");
                else if (ls_load_reference.startsWith("OFXD"))
                    ls_ind_mod_liv.set("X");
                else {
                    pRow.pushErreur("Erreur préfixe Load reference DIRECT ou XDOC");
                    res.pushRow(pRow);
                    continue;
                }
                val ls_concat = " " + ls_ind_mod_liv + "%";

                // Pas de référence client pour ORCHARD
                val ls_ref_cli = "";
                val entrepot = this.entrepotRepo.findOne((root, cq, cb) -> cb.and(
                        cb.equal(root.get("client").get("id"), ls_cli_ref.get()),
                        cb.equal(root.get("societe").get("id"), ls_soc_code.get()),
                        cb.equal(root.get("modeLivraison"),
                                StringEnum.getValueOf(GeoModeLivraison.class, ls_ind_mod_liv.get())),
                        cb.like(root.get("code"), ls_depot_name + ls_concat),
                        cb.isTrue(root.get("valide"))));
                if (entrepot.isEmpty()) {
                    pRow.pushErreur("Erreur entrepôt non trouvé: " + ls_depot_name);
                    res.pushRow(pRow);
                    continue;
                }

                if (ls_haulier.isBlank()) {
                    pRow.pushErreur("Erreur transporteur non renseigné !!");
                    res.pushRow(pRow);
                    continue;
                }

                String ls_transp_approche = "";
                String ls_transp_final = ls_haulier;
                if (ls_haulier.startsWith("APPROCHE")) {
                    ls_transp_approche = ls_haulier.substring(10, ls_haulier.indexOf('+') - 1 - 10);
                    ls_transp_final = ls_haulier.substring(ls_haulier.indexOf('+') + 2, ls_haulier.length());
                }

                // EX DLUO: Toujours "/"
                // Doit être renseigné dnas le fichier Excel
                String ls_dluo;
                try {
                    ls_dluo = row.getCell(COL_BB_DATE).getLocalDateTimeCellValue().toString();
                } catch (Exception e) {
                    ls_dluo = "/";
                }

                Optional<GeoOrdre> existing_ordre = this.ordreRepo.findOne((root, cq, cb) -> cb.and(
                        cb.equal(root.get("codeChargement"), ls_load_reference),
                        cb.equal(root.get("entrepot"), entrepot.get()),
                        cb.equal(root.get("client").get("id"), ls_cli_ref.get())));

                final AtomicReference<String> ls_nordre = new AtomicReference<>("");
                final AtomicReference<String> ls_ord_ref = new AtomicReference<>("");
                if (existing_ordre.isPresent()) {

                    ls_ord_ref.set(existing_ordre.get().getId());
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
                            ls_depart_date,
                            "ORD",
                            ls_delivery_date,
                            ls_load_reference);
                    if (functionRes.getRes() != FunctionResult.RESULT_OK) {
                        pRow.pushErreur(functionRes.getMsg());
                        res.pushRow(pRow);
                        continue;
                    }

                    ls_ord_ref.set((String) functionRes.getData().get("ls_ord_ref"));
                    addedOrdreRefs.add(ls_ord_ref.get());
                    ls_load_ref_prec = ls_load_reference;
                    ls_cen_ref_prec = entrepot.get().getId();

                    if (functionRes.getRes().equals(FunctionResult.RESULT_OK)) {
                        ls_create_ligne = 'O';
                        GeoOrdre ordre = this.ordreRepo.getOne(ls_ord_ref.get());
                        ordre.setUserModification(utilisateur);
                        this.ordreRepo.save(ordre);
                        ls_nordre.set(ordre.getNumero());
                        pRow.setOrdreNum(ls_nordre.get());
                        pRow.pushMessage("Ordre créé");
                        res.incrementOrdreCount();
                    } else {
                        ls_nordre.set("");
                        pRow.pushMessage("Ordre NON créé, Numéro d'ordre invalide: " + ls_ord_ref);
                    }
                }

                List<String> ls_array_art;
                try {
                    ls_array_art = List.of(row.getCell(COL_ARTS_REF).getStringCellValue().trim().split("-"));
                } catch (Exception e) {
                    ls_array_art = List
                            .of(String.valueOf(((Double) row.getCell(COL_ARTS_REF).getNumericCellValue()).intValue()));
                }
                if (ls_create_ligne.equals('O')) {
                    for (int ll_count = 0; ll_count < ls_array_art.size(); ll_count++) {
                        if (ll_count > 1) {
                            ls_qty_case = 0d;
                            ls_qty_pallets = 0d;
                            ls_case_per_pallets = 0d;
                        }

                        val ls_art = StringUtils.padLeft(ls_array_art.get(ll_count), "0", 6);

                        Boolean ls_art_existe = true;
                        try {
                            this.entityManager
                                    .createNativeQuery(
                                            "select 'O' from GEO_ARTICLE_COLIS where art_ref = :ls_art and valide = 'O'")
                                    .setParameter("ls_art", ls_art)
                                    .getSingleResult();
                        } catch (NoResultException e) {
                            ls_art_existe = false;
                        }

                        if (ls_art_existe) {
                            String ls_prog = "";
                            if (ls_programme.startsWith("TES")
                                    || ls_programme.startsWith("SP"))
                                ls_prog = "TESCO";
                            else if (ls_programme.startsWith("OF"))
                                ls_prog = "ORCHARD";

                            FunctionResult ls_rc = this.functionOrdreRepo.fCreateLigneOrdre(
                                    ls_ord_ref.get(),
                                    ls_art,
                                    ls_packhouse,
                                    entrepot.get().getId(),
                                    ls_case_per_pallets,
                                    ls_qty_pallets,
                                    ls_qty_case,
                                    ld_prix_vte,
                                    ld_prix_mini,
                                    ls_prog,
                                    ls_dluo);

                            // HANDLE ls_rc
                            if (ls_rc.getRes() != FunctionResult.RESULT_OK) {
                                pRow.pushErreur(
                                        "Erreur création ligne article pour ORD_REF: " + ls_ord_ref.get() + " -> "
                                                + ls_rc.getMsg());
                                res.pushRow(pRow);
                                continue outer;
                            }

                            String orl_ref = (String) ls_rc.getData().get("ls_orl_ref");
                            GeoOrdreLigne ordLig = this.olRepo.getOne(orl_ref);
                            ordLig.setUserModification(utilisateur);
                            this.olRepo.save(ordLig);

                            row.getCell(COL_ORD_CREATE, MissingCellPolicy.CREATE_NULL_AS_BLANK)
                                    .setCellValue(ls_nordre.get());

                            // CREER ORDLOG

                            LocalDateTime ls_DATLIV_GRP, ls_datdep_grp_p;
                            String ls_grp_code;
                            val relDepartDate = ls_depart_date;
                            if (ls_haulier.startsWith("APPROCHE")) {
                                ls_DATLIV_GRP = relDepartDate.plusDays(1);
                                ls_datdep_grp_p = relDepartDate.plusDays(1);
                                ls_grp_code = "TERRYLOIRE";
                            } else {
                                ls_DATLIV_GRP = null;
                                ls_datdep_grp_p = relDepartDate;
                                ls_grp_code = "";
                            }

                            val ls_DATDEP_FOU_P = relDepartDate;
                            val ls_DATDEP_FOU_P_YYYYMMDD = relDepartDate
                                    .format(DateTimeFormatter.ofPattern("yyyyMMdd"));

                            val ls_ordlog_existe = this.ordreLogistiqueRepo.findOne((root, cq, cb) -> cb.and(
                                    cb.equal(root.get("ordre").get("id"), ls_ord_ref.get()),
                                    cb.equal(root.get("fournisseur").get("code"), ls_packhouse)));

                            if (ls_ordlog_existe.isEmpty()) {
                                GeoOrdreLogistique ordlog = new GeoOrdreLogistique();
                                try {
                                    ordlog.setOrdre(this.ordreRepo.getOne(ls_ord_ref.get()));
                                    ordlog.setCodeFournisseur(ls_packhouse);
                                    ordlog.setGroupage(this.groupageRepo.getOne(ls_grp_code));
                                    ordlog.setTransporteurGroupage(this.transporteurRepo.getOne(ls_transp_approche));
                                    ordlog.setDateDepartPrevueFournisseur(ls_DATDEP_FOU_P);
                                    ordlog.setDateDepartPrevueFournisseurRaw(ls_DATDEP_FOU_P_YYYYMMDD);
                                    ordlog.setTotalPalettesCommandees(ls_qty_pallets.floatValue());
                                    ordlog.setTypeLieuGroupageArrivee('G');
                                    ordlog.setTypeLieuDepart('F');
                                    ordlog.setDateLivraisonLieuGroupage(ls_DATLIV_GRP);
                                    ordlog.setDateDepartPrevueGroupage(ls_datdep_grp_p);
                                    ordlog = this.ordreLogistiqueRepo.save(ordlog);
                                } catch (Exception e) {
                                    pRow.pushErreur("Erreur création transport d'approche pour ORD_REF: " + ls_ord_ref
                                            + ". " + e.getMessage());
                                    res.pushRow(pRow);
                                    continue;
                                }
                            }

                        } else
                            pRow.pushMessage("Article invalide: " + ls_art);

                    }
                }

                res.pushRow(pRow);
            }

            OutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            this.writeOutput(out, chunks);
            workbook.close();
        } catch (Exception exception) {
            addedOrdreRefs.forEach(id -> this.ordreRepo.deleteById(id));
            throw new RuntimeException(exception.getMessage());
        }
        return res;
    }

    public ProgramResponse importPreordre(MultipartFile chunks, String utilisateur) throws IOException {

        val res = new ProgramResponse();

        // useful columns indexes
        int COL_ORD_CREATE = 0;
        int COL_ENTREPOT = 2;
        int COL_PROPRIETAIRE = 3;
        int COL_STATION = 4;
        int COL_REF_CLI = 5;
        int COL_ARTS_REF = 9;
        int COL_DATE_DEPART = 11;
        int COL_DATE_LIVRAISON = 12;
        int COL_INSTRUCTION_LOG = 13;
        int COL_COM_CONF_CDE = 14;
        int COL_QTE_COLIS = 15;
        int COL_PAL_INTER = 16;
        int COL_QTE_COLIS_PAL = 17;
        int COL_QTE_PAL = 18;
        int COL_TYP_PAL = 19;
        int COL_ASSISTANTE = 20;
        int COL_CIAL = 21;
        int COL_PRIX_ACHAT = 22;
        int COL_UNITE_ACHAT = 23;
        int COL_PRIX_VENTE = 24;
        int COL_UNITE_VENTE = 25;
        int COL_TRANSPORTEUR = 26;

        // load sheet
        List<String> addedOrdreRefs = new ArrayList<>();
        Workbook workbook = ProgramService.loadFile(chunks);
        try {
            Sheet sheet = workbook.getSheetAt(0);

            outer: for (Row row : sheet) {
                ProgramRow pRow = new ProgramRow();

                // ignore header row
                if (row.getRowNum() == 0)
                    continue;

                String ls_a_traite;
                try {
                    ls_a_traite = row.getCell(COL_ENTREPOT).getStringCellValue().trim();
                    if (ls_a_traite.isBlank())
                        break;
                } catch (Exception e) {
                    break;
                }

                String ls_create_ligne = "N";
                Integer ord_ref_create = ((Double) row.getCell(COL_ORD_CREATE).getNumericCellValue()).intValue();
                String ls_value = ord_ref_create == 0 ? "" : StringUtils.padLeft(ord_ref_create.toString(), "0", 6);
                String ls_depot_name = row.getCell(COL_ENTREPOT).getStringCellValue().toUpperCase().trim();
                pRow.setDepot(ls_depot_name);
                String ls_ref_cli = row.getCell(COL_REF_CLI).getStringCellValue().toUpperCase().trim();
                pRow.setRefCli(ls_ref_cli);

                if (ls_value.isBlank()) {

                    String ls_proprietaire = row.getCell(COL_PROPRIETAIRE).getStringCellValue().toUpperCase().trim();
                    String ls_station = row.getCell(COL_STATION).getStringCellValue().toUpperCase().trim();
                    pRow.setLoadRef(ls_station);
                    List<String> ls_array_art;
                    try {
                        ls_array_art = Arrays.asList(row.getCell(COL_ARTS_REF).getStringCellValue().trim().split("-"));
                    } catch (Exception e) {
                        ls_array_art = List
                                .of(String.valueOf(
                                        ((Double) row.getCell(COL_ARTS_REF).getNumericCellValue()).intValue()));
                    }

                    LocalDateTime ls_depart_date = row.getCell(COL_DATE_DEPART).getLocalDateTimeCellValue();
                    pRow.setDateDepart(ls_depart_date);

                    LocalDateTime ls_delivery_date = row.getCell(COL_DATE_LIVRAISON).getLocalDateTimeCellValue();
                    pRow.setDateLivraison(ls_delivery_date);

                    String ls_instruction_log = row.getCell(COL_INSTRUCTION_LOG).getStringCellValue().toUpperCase()
                            .trim();
                    String ls_comment_confirm_cde = row
                            .getCell(COL_COM_CONF_CDE, MissingCellPolicy.CREATE_NULL_AS_BLANK)
                            .getStringCellValue().toUpperCase().trim();
                    Double ls_qty_case = row.getCell(COL_QTE_COLIS).getNumericCellValue();
                    Double ls_qty_pallets = row.getCell(COL_QTE_PAL).getNumericCellValue();
                    Double ls_case_per_pallets = row.getCell(COL_QTE_COLIS_PAL).getNumericCellValue();
                    Double ls_qty_pallets_inter = row.getCell(COL_PAL_INTER).getNumericCellValue();
                    String ls_type_pallet = row.getCell(COL_TYP_PAL).getStringCellValue().toUpperCase().trim();
                    String ls_assistante = row.getCell(COL_ASSISTANTE).getStringCellValue().toUpperCase().trim();
                    String ls_commercial = row.getCell(COL_CIAL).getStringCellValue().toUpperCase().trim();
                    Double ld_prix_achat;
                    try {
                        ld_prix_achat = row.getCell(COL_PRIX_ACHAT).getNumericCellValue();
                    } catch (Exception e) {
                        ld_prix_achat = 0d;
                    }
                    final AtomicReference<String> ls_unite_achat = new AtomicReference<>("");
                    try {
                        ls_unite_achat.set(row.getCell(COL_UNITE_ACHAT).getStringCellValue().toUpperCase().trim());
                    } catch (Exception e) {
                        ls_unite_achat.set("");
                    }

                    String ls_bat_code;
                    if (!ls_unite_achat.get().isBlank()) {
                        Optional<GeoBaseTarif> baseTarif = this.baseTarifRepo.findOne((root, cq, cb) -> cb.and(
                                cb.equal(root.get("id"), ls_unite_achat.get())));
                        if (baseTarif.isPresent()) {
                            ls_bat_code = baseTarif.get().getId();
                        } else {
                            pRow.pushErreur("Erreur unité d'achat incorrect: " + ls_unite_achat.get());
                            res.pushRow(pRow);
                            continue;
                        }
                    }
                    Double ld_prix_vente;
                    try {
                        ld_prix_vente = row.getCell(COL_PRIX_VENTE).getNumericCellValue();
                    } catch (Exception e) {
                        ld_prix_vente = 0d;
                    }
                    final AtomicReference<String> ls_unite_vente = new AtomicReference<>("");
                    try {
                        ls_unite_vente.set(row.getCell(COL_UNITE_VENTE).getStringCellValue().toUpperCase().trim());
                    } catch (Exception e) {
                        ls_unite_vente.set("");
                    }
                    if (!ls_unite_vente.get().isBlank()) {
                        Optional<GeoBaseTarif> baseTarif = this.baseTarifRepo.findOne((root, cq, cb) -> cb.and(
                                cb.equal(root.get("id"), ls_unite_vente.get())));
                        if (baseTarif.isPresent()) {
                            ls_bat_code = baseTarif.get().getId();
                        } else {
                            pRow.pushErreur("Erreur unité de vente incorrect: " + ls_unite_vente.get());
                            res.pushRow(pRow);
                            continue;
                        }
                    }

                    String ls_transporteur = row.getCell(COL_TRANSPORTEUR, MissingCellPolicy.CREATE_NULL_AS_BLANK)
                            .getStringCellValue().toUpperCase().trim();

                    String ls_cen_ref;
                    String ls_soc_code;
                    String ls_cli_ref;
                    Optional<GeoEntrepot> entrepot = this.entrepotRepo.findOne((root, cq, cb) -> cb.and(
                            cb.equal(root.get("code"), ls_depot_name),
                            cb.isTrue(root.get("client").get("valide")),
                            cb.isTrue(root.get("valide"))));
                    if (entrepot.isEmpty()) {
                        pRow.pushErreur("Erreur entrepôt non trouvé: " + ls_depot_name);
                        res.pushRow(pRow);
                        continue;
                    } else {
                        ls_cen_ref = entrepot.get().getId();
                        ls_soc_code = entrepot.get().getClient().getSociete().getId();
                        ls_cli_ref = entrepot.get().getClient().getId();
                    }

                    long countStation = this.fournisseurRepo.count((root, cq, cb) -> cb.and(
                            cb.equal(root.get("code"), ls_station),
                            cb.isTrue(root.get("valide"))));
                    if (countStation == 0) {
                        pRow.pushErreur("Erreur Station emballeuse non trouvé: " + ls_station);
                        res.pushRow(pRow);
                        continue;
                    }

                    long countProprietaire = this.fournisseurRepo.count((root, cq, cb) -> cb.and(
                            cb.equal(root.get("code"), ls_proprietaire),
                            cb.isTrue(root.get("valide"))));
                    if (countProprietaire == 0) {
                        pRow.pushErreur("Erreur Propriétaire non trouvé: " + ls_station);
                        res.pushRow(pRow);
                        continue;
                    }

                    String ls_transp_final;
                    if (!ls_transporteur.isBlank())
                        ls_transp_final = ls_transporteur;
                    else {
                        if (ls_soc_code.equals("SA"))
                            ls_transp_final = "-";
                        // erreur si soc_code = BWS et transporteur non renseigné
                        else {
                            pRow.pushErreur("Erreur transporteur non renseigné !!");
                            res.pushRow(pRow);
                            continue;
                        }
                    }

                    FunctionResult functionRes = this.functionOrdreRepo.fCreatePreordre(
                            ls_soc_code, ls_cli_ref, ls_cen_ref, ls_transp_final, ls_ref_cli, false,
                            false, ls_depart_date, ls_delivery_date, ls_instruction_log, ls_assistante, ls_commercial);
                    if (functionRes.getRes() != FunctionResult.RESULT_OK) {
                        pRow.pushErreur(functionRes.getMsg());
                        res.pushRow(pRow);
                        continue;
                    }

                    String ls_ord_ref = (String) functionRes.getData().get("ls_ord_ref");
                    addedOrdreRefs.add(ls_ord_ref);

                    String ls_nordre;
                    if (functionRes.getRes().equals(FunctionResult.RESULT_OK)) {
                        ls_create_ligne = "O";
                        GeoOrdre ordre = this.ordreRepo.getOne(ls_ord_ref);
                        ordre.setUserModification(utilisateur);
                        this.ordreRepo.save(ordre);
                        ls_nordre = ordre.getNumero();
                        pRow.setOrdreNum(ls_nordre);
                        pRow.pushMessage("Ordre créé");
                        row.getCell(COL_ORD_CREATE, MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(ls_nordre);
                        res.incrementOrdreCount();
                    } else {
                        ls_nordre = "";
                        pRow.pushErreur("Ordre NON créé, Numéro d'ordre invalide: " + ls_ord_ref);
                        res.pushRow(pRow);
                        continue;
                    }

                    if (ls_create_ligne.equals("O")) {
                        for (int ll_count = 0; ll_count < ls_array_art.size(); ll_count++) {
                            if (ll_count >= 1) {
                                ls_qty_case = 0d;
                                ls_qty_pallets = 0d;
                                ls_case_per_pallets = 0d;
                            }

                            val ls_art = StringUtils.padLeft(ls_array_art.get(ll_count), "0", 6);

                            Boolean ls_art_existe = true;
                            try {
                                this.entityManager
                                        .createNativeQuery(
                                                "select 'O' from AVI_ART_GESTION where art_ref = :ls_art and valide = 'O'")
                                        .setParameter("ls_art", ls_art)
                                        .getSingleResult();
                            } catch (NoResultException e) {
                                ls_art_existe = false;
                            }

                            if (ls_art_existe) {

                                FunctionResult ls_rc = this.functionOrdreRepo.fCreateLignePreordre(
                                        ls_ord_ref, ls_cen_ref, ls_qty_case, ls_qty_pallets, ls_case_per_pallets,
                                        ls_type_pallet, ls_qty_pallets_inter, ls_art, ls_proprietaire, ls_station,
                                        ld_prix_vente, ld_prix_achat, ls_unite_achat.get(), ls_unite_vente.get());

                                // HANDLE ls_rc
                                if (ls_rc.getRes() != FunctionResult.RESULT_OK) {
                                    pRow.pushErreur(
                                            "Erreur création ligne article pour ORD_REF: " + ls_ord_ref + " -> "
                                                    + ls_rc.getMsg());
                                    res.pushRow(pRow);
                                    continue outer;
                                }

                                // CREER ORDLOG
                                val ls_DATDEP_FOU_P = ls_depart_date;
                                val ls_DATDEP_FOU_P_YYYYMMDD = ls_depart_date
                                        .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                                val ls_datdep_grp_p = ls_depart_date;

                                val ls_ordlog_existe = this.ordreLogistiqueRepo.findOne((root, cq, cb) -> cb.and(
                                        cb.equal(root.get("ordre").get("id"), ls_ord_ref),
                                        cb.equal(root.get("fournisseur").get("code"), ls_station)));

                                if (ls_ordlog_existe.isEmpty()) {
                                    GeoOrdreLogistique ordlog = new GeoOrdreLogistique();
                                    try {
                                        ordlog.setOrdre(this.ordreRepo.getOne(ls_ord_ref));
                                        ordlog.setCodeFournisseur(ls_station);
                                        ordlog.setDateDepartPrevueFournisseur(ls_DATDEP_FOU_P);
                                        ordlog.setDateDepartPrevueFournisseurRaw(ls_DATDEP_FOU_P_YYYYMMDD);
                                        ordlog.setTotalPalettesCommandees(ls_qty_pallets.floatValue());
                                        ordlog.setTypeLieuGroupageArrivee('G');
                                        ordlog.setTypeLieuDepart('F');
                                        ordlog.setDateDepartPrevueGroupage(ls_datdep_grp_p);
                                        ordlog = this.ordreLogistiqueRepo.save(ordlog);
                                    } catch (Exception e) {
                                        pRow.pushErreur("Erreur création transport d'approche pour ORD_REF: " +
                                                ls_ord_ref
                                                + ". " + e.getMessage());
                                        res.pushRow(pRow);
                                        continue;
                                    }
                                }

                            } else {
                                pRow.pushErreur("Article invalide: " + ls_art);
                            }

                        }
                    }
                } else {
                    pRow.pushErreur("Ordre déjà existant " + ls_value + " !!");
                    pRow.setDepot(ls_depot_name);
                    pRow.setOrdreNum(ls_value);
                    pRow.setRefCli(ls_ref_cli);
                }

                res.pushRow(pRow);
            }

            OutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            this.writeOutput(out, chunks);
            workbook.close();
        } catch (Exception exception) {
            addedOrdreRefs.forEach(id -> this.ordreRepo.deleteById(id));
            throw new RuntimeException(exception.getMessage());
        }
        return res;
    }

    /** Write a program output in session */
    private void writeOutput(OutputStream stream, MultipartFile chunks) {
        val session = ProgramService.getSession();
        session.setAttribute(GEO2_PROGRAM_OUTPUT, stream);
        session.setAttribute(GEO2_PROGRAM_FILETYPE, chunks.getContentType());
        session.setAttribute(GEO2_PROGRAM_FILENAME, chunks.getOriginalFilename());
    }

    /** Get the program output stored in session */
    public static ByteArrayOutputStream getOutput() {
        return (ByteArrayOutputStream) ProgramService.getSession().getAttribute(GEO2_PROGRAM_OUTPUT);
    }

    /** Get the program file name stored in session */
    public static String getFileName() {
        return (String) ProgramService.getSession().getAttribute(GEO2_PROGRAM_FILENAME);
    }

    /** Get the program file type stored in session */
    public static MediaType getFileType() {
        String type = (String) ProgramService.getSession().getAttribute(GEO2_PROGRAM_FILETYPE);
        return MediaType.parseMediaType(type);
    }

    /** Clear session program attributes */
    public static void clearSession() {
        ProgramService.getSession().removeAttribute(GEO2_PROGRAM_OUTPUT);
        ProgramService.getSession().removeAttribute(GEO2_PROGRAM_FILENAME);
        ProgramService.getSession().removeAttribute(GEO2_PROGRAM_FILETYPE);
    }

    /** Get current HTTP session */
    private static HttpSession getSession() {
        RequestContextHolder.currentRequestAttributes().getSessionId();
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attr.getRequest().getSession(true);
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
