package fr.microtec.geo2.persistance.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FunctionResult {
  private Integer res;
	private String msg;
}