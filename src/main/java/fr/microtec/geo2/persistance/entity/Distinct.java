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

	public Distinct(Float key, Long count) {
		this.key = key == null
				? "[null]"
				: key.toString();
		this.count = count;
	}

	@Id
	private String key;

	@Column
	private Long count;

}
