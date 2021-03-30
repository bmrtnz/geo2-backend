package fr.microtec.geo2.service.fs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

/**
 * This service is used for access to maddog2 NAS file system.
 */
@Service
@Secured("ROLE_USER")
public class Maddog2FileSystemService extends FileSystemService {

	/**
	 * PATH_KEY use by client, it's use to order files.
	 */
	public enum PATH_KEY {
		CLIENTS("/tiers/clients/"),
		FOURNISSEURS("/tiers/fournisseurs/"),
		TRANSPORTEURS("/tiers/transporteurs/"),
		LIEUX_PASSAGE_A_QUAI("/tiers/lieuxpassageaquai/"),
		ARTICLES("/articles/"),
		ORDRES("/ordres/");

		private String path;
		PATH_KEY(String basePath) { this.path = basePath; }

		public static PATH_KEY from(String s) {
			for (PATH_KEY pathKey : PATH_KEY.values()) {
				if (pathKey.name().equalsIgnoreCase(s)) return pathKey;
			}

			throw new IllegalArgumentException();
		}
	}

	public Maddog2FileSystemService(@Value("${geo2.maddog2.path}") String basePath) {
		super();
		this.basePath = basePath;
	}

	/**
	 * Build base relative path for given path_key and id.
	 */
	public String buildKeyIdPath(String key, String id) {
		return PATH_KEY.from(key).path + id + "/";
	}

}
