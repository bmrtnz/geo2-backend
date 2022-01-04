package fr.microtec.geo2.persistance.repository.function;

import javax.persistence.*;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Repository containing Geo1 PowerBuilder functions
 * To make it possible, functions have been translated to SQL procedures
 */
@NoRepositoryBean
public abstract class AbstractFunctionsRepositoryImpl {

  @PersistenceContext
  private EntityManager entityManager;

  /**
   * Build a procedure that return a standard result
   * @param name SQL procedure name
   * @return Created <code>StoredProcedureQuery</code>
   */
  protected FunctionQuery build(String name, Class<?> returnType, boolean standardOutput) {
    FunctionQuery query;

    if (returnType == null) {
      query = new FunctionQueryImpl<>(this.entityManager.unwrap(SharedSessionContractImplementor.class), name);
    } else {
      query = new FunctionQueryImpl<>(this.entityManager.unwrap(SharedSessionContractImplementor.class), name, returnType);
    }

    if (standardOutput) {
        query
              .attachOutput("res", Integer.class)
              .attachOutput("msg", String.class);
    }

    return query;
  }

  protected FunctionQuery build(String name, Class<?> returnType) {
    return this.build(name, returnType, true);
  }

  protected FunctionQuery build(String name) {
    return this.build(name, null, true);
  }

}
