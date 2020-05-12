package fr.microtec.geo2.persistance.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Auto convert java boolean to O/N character in database with hibernate.
 */
@Converter(autoApply = true)
public class BooleanCharacterConverter implements AttributeConverter<Boolean, Character> {

	@Override
	public Character convertToDatabaseColumn(Boolean value) {
		if (value == null) {
			return null;
		}

		return Boolean.TRUE.equals(value) ? 'O' : 'N';
	}

	@Override
	public Boolean convertToEntityAttribute(Character character) {
		if (character == null) {
			return null;
		}

		return character == 'O';
	}
}
