package fr.microtec.geo2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

	//private GeoFournisseurRepository fournisseurRepository;
	/*@Autowired
	private GeoEntrepotRepository entrepotRepository;
	@Autowired
	private GeoPaysRepository paysRepository;*/

	@GetMapping(path = "/test")
	public void test() {
		/*List<GeoPays> pays = this.paysRepository.findAll();
		System.out.println(pays.size());*/
		//Optional<GeoEntrepot> fournisseur = this.entrepotRepository.findById("001494");


		/*GeoEntrepot f = fournisseur.get();
		System.out.println(f.getAdresse1());*/
		//utilisateur.ifPresent(geoUtilisateur -> System.out.println(geoUtilisateur.getNomInterne()));
	}

}
