package fr.microtec.geo2.persistance.converter;

import fr.microtec.geo2.persistance.StringEnum;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.convert.converter.Converter;

import javax.persistence.AttributeConverter;
import java.util.stream.Stream;

/**
 * Abstract class for string values enum converter.
 *
 * @param <T>
 */
public abstract class AbstractStringEnumConverter<T extends Enum<T> & StringEnum> implements AttributeConverter<T, String>, Converter<String, T> {

	private final Class<T> enumClass;

	public AbstractStringEnumConverter() {
		this.enumClass = (Class<T>) GenericTypeResolver.resolveTypeArgument(this.getClass(), AbstractStringEnumConverter.class);
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

		return StringEnum.getValueOf(this.enumClass, value);
	}

	@Override
	public T convert(String s) {
		return this.convertToEntityAttribute(s);
	}
}
