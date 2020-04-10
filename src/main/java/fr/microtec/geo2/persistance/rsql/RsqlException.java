package fr.microtec.geo2.persistance.rsql;

/**
 * Exception for Rsql parsing and specification building.
 */
public class RsqlException extends RuntimeException {

	public RsqlException() {
	}

	public RsqlException(String message) {
		super(message);
	}

	public RsqlException(String message, Throwable cause) {
		super(message, cause);
	}

}
