package fr.microtec.geo2.persistance.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Distinct {

	public Distinct(Number key, Long count) {
		this(parseKey(key), null, count);
	}

    public Distinct(String key, Long count) {
        this(key, null, count);
    }

    public Distinct(Number key, String description, Long count) {
        this.key = parseKey(key);
        this.description = description;
        this.count = count;
    }

	@Id
	private String key;

    @Column
    private String description;

	@Column
	private Long count;

    private static String parseKey(Number key) {
        return key == null ? "[null]" : key.toString();
    }

}
