package fr.microtec.geo2.persistance;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
public class Distinct {

	@Id
	private String key;

	@Column
	private BigDecimal count;

}
