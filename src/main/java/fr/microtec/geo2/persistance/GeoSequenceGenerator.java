package fr.microtec.geo2.persistance;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.query.NativeQuery;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.Properties;

/**
 * Geo sequence generator strategy.
 */
public class GeoSequenceGenerator implements Configurable, IdentifierGenerator {

	public static final String SEQUENCE_PARAM = "sequenceName";
	public static final String MASK_PARAM = "mask";

	private static final String BASE_QUERY = "SELECT %s FROM DUAL";
	private static final String BASE_SEQUENCE_NAME = "%s.NEXTVAL";
	private static final String BASE_FORMAT_MASK = "TO_CHAR(%s, '%s')";

	private String sequenceQuery;

	/**
	 * Configure generator.
	 */
	@Override
	public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
		String sequenceName = ConfigurationHelper.getString(SEQUENCE_PARAM, params);
		if (sequenceName == null || sequenceName.isBlank()) {
			throw new HibernateException("Invalid sequence name provided");
		}

		sequenceName = String.format(BASE_SEQUENCE_NAME, sequenceName);
		boolean applyMask = params.containsKey(MASK_PARAM);
		if (applyMask) {
			String mask = ConfigurationHelper.getString(MASK_PARAM, params);

			sequenceName = String.format(BASE_FORMAT_MASK, sequenceName, mask);
		}

		this.sequenceQuery = String.format(BASE_QUERY, sequenceName);
	}

	/**
	 * Generate id value.
	 */
	@Override
	public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
		NativeQuery query = session.createNativeQuery(this.sequenceQuery);

		Object value = query.getSingleResult();

		return (String) value;
	}
}
