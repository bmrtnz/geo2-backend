package fr.microtec.geo2.persistance.converter;

import fr.microtec.geo2.persistance.StringEnum;

import javax.persistence.AttributeConverter;
import java.util.stream.Stream;

/**
 * Abstract class for string values enum converter.
 *
 * @param <T>
 */
public abstract class AbstractStringEnumConverter<T extends Enum<T> & StringEnum> implements AttributeConverter<T, String> {

	private final Class<T> enumClass;

	public AbstractStringEnumConverter(Class<T> enumClass) {
		this.enumClass = enumClass;
	}

	@Override
	public String convertToDatabaseColumn(T value) {
		if (value == null) {
			return null;
		}

		return value.getKey();
	}

	@Override
	public T convertToEntityAttribute(String value) {
		if (value == null) {
			return null;
		}

		return Stream.of(this.enumClass.getEnumConstants())
				.filter(c -> value.equals(c.getKey()))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException(String.format("No enum value (%s) found in %s", value, this.enumClass.getSimpleName())));
	}

}
