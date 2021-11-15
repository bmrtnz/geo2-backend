package fr.microtec.geo2.persistance.repository;

import org.springframework.stereotype.Repository;

import fr.microtec.geo2.persistance.entity.FunctionResult;

@Repository
public interface FunctionsRepository {

  /**
   * Vérifie si la création de l'ordre pour l'entrepot est autorisé
   */
  FunctionResult ofValideEntrepotForOrdre(String code_entrepot);
}
