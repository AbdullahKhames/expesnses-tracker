package name.expenses.features.pocket.dao;

import jakarta.ejb.Local;
import name.expenses.features.pocket.models.Pocket;
import name.expenses.globals.Page;
import name.expenses.globals.SortDirection;

import java.util.List;
import java.util.Optional;

@Local
public interface PocketDAO {
    Pocket create(Pocket expense);
    Optional<Pocket> get(String refNo);
    Page<Pocket> findAll(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection);
    List<Pocket> get();
    Pocket update(Pocket expense);
    String delete(String refNo);

    Long checkAccountAssociation(Pocket pocket);
}