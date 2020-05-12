package fr.microtec.geo2.persistance.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Auto convert java boolean to 1/0 integer in database with hibernate.
 */
@Converter
public class BooleanIntegerConverter implements AttributeConverter<Boolean, Integer> {

	@Override
	public Integer convertToDatabaseColumn(Boolean value) {
		if (value == null) {
			return null;
		}

		return Boolean.TRUE.equals(value) ? 1 : 0;
	}

	@Override
	public Boolean convertToEntityAttribute(Integer integer) {
		if (integer == null) {
			return null;
		}

		return integer == 1;
	}
}
