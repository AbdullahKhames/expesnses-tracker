package name.expenses.features.pocket.dao;

import jakarta.ejb.Local;
import name.expenses.features.pocket.models.Pocket;
import name.expenses.globals.Page;
import name.expenses.globals.SortDirection;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Local
public interface PocketDAO {
    Pocket create(Pocket expense);
    Optional<Pocket> get(String refNo);
    Page<Pocket> findAll(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection);

    Page<Pocket> findAllWithoutAccount(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection);

    List<Pocket> get();
    String delete(String refNo);

    Long checkAccountAssociation(Pocket pocket);
    Set<Pocket> getEntities(Set<String> refNos);

    Set<Pocket> saveAll(Set<Pocket> pockets);
    Set<Pocket> updateAll(Set<Pocket> pockets);
    Pocket update(Pocket expense);

    List<Pocket> getByName(String name);
}