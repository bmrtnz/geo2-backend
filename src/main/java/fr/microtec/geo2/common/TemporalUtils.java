package fr.microtec.geo2.common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Date/Time operations utilities
 */
public class TemporalUtils {

  /**
   * ISO 8601 pattern with optional time
   */
  public static final String ISO8601_PATTERN = "yyyy-MM-dd['T'HH[:mm[:ss[.SSS'Z']]]]";
  
  /**
	 * Parse Object to ISO LocalDate
	 * 
	 * @param o Object argument
	 * @return ISO LocalDate
	 */
	public static LocalDate parseToLocalDate(Object o) {
		return LocalDate.parse(o.toString(), DateTimeFormatter.ISO_LOCAL_DATE);
	}

	/**
	 * Parse Object to ISO LocalDateTime
	 * 
	 * @param o Object argument
	 * @return ISO LocalDateTime
	 */
	public static LocalDateTime parseToLocalDateTime(Object o) {
		return LocalDateTime
		.parse(o.toString(), DateTimeFormatter.ofPattern(ISO8601_PATTERN));
	}

	/**
	 * Serialize LocalDateDate to ISO String
	 * 
	 * @param t LocalDateTime input
	 * @return ISO serialized string
	 */
	public static String serializeFromLocalDateTime(LocalDateTime t) {
		return t.format(DateTimeFormatter.ofPattern(ISO8601_PATTERN));
	}

}
