package fr.microtec.geo2.persistance.repository;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;

import org.springframework.stereotype.Repository;

import fr.microtec.geo2.persistance.entity.FunctionResult;

/**
 * Repository containing Geo1 PowerBuilder functions
 * To make it possible, functions have been translated to SQL procedures
 */
@Repository
// @Transactionnal
public class FunctionsRepositoryImpl implements FunctionsRepository {

  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Build a procedure that return a standard result
   * @param name SQL procedure name
   * @return Created <code>StoredProcedureQuery</code>
   */
  private StoredProcedureQuery build(String name) {
    return this.entityManager
    .createStoredProcedureQuery(name)
    .registerStoredProcedureParameter("res", Integer.class, ParameterMode.OUT)
    .registerStoredProcedureParameter("msg", String.class, ParameterMode.OUT);
  }

  /**
   * Helper to define and attach <code>StoredProcedureQuery</code> input parameters
   * @param spq The <code>StoredProcedureQuery</code>
   * @param name Input parameter name
   * @param type Input parameter type
   * @param value Input parameter value
   * @return Created <code>StoredProcedureQuery</code>
   */
  private StoredProcedureQuery attachInput(
    StoredProcedureQuery spq,
    String name,
    Class<?> type,
    Object value
  ) {
    return spq
    .registerStoredProcedureParameter(name, type, ParameterMode.IN)
    .setParameter(name, value);
  }

  /**
   * Fetch the <code>StoredProcedureQuery</code>
   * @param spq The <code>StoredProcedureQuery</code>
   * @return <code>FunctionResult</code> instance
   */
  private FunctionResult fetch(StoredProcedureQuery spq) {
    return FunctionResult.builder()
    .res((Integer) spq.getOutputParameterValue("res"))
    .msg((String) spq.getOutputParameterValue("msg"))
    .build();
  }

  public FunctionResult ofValideEntrepotForOrdre(String code_entrepot){
    StoredProcedureQuery spq = this.build("OF_VALIDE_ENTREPOT_FOR_ORDRE");
    spq = this.attachInput(spq, "code_entrepot", String.class, code_entrepot);
    return this.fetch(spq);
  }
}
