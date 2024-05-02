package name.expenses.features.pocket_transfer.dao;

import name.expenses.features.pocket_transfer.models.PocketAmount;
import name.expenses.globals.Page;
import name.expenses.globals.SortDirection;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PocketAmountDAO {
    PocketAmount create(PocketAmount expense);
    Optional<PocketAmount> get(String refNo);
    Page<PocketAmount> findAll(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection);
    List<PocketAmount> get();
    PocketAmount update(PocketAmount expense);
    String delete(String refNo);
    Set<PocketAmount> getEntities(Set<String> refNos);
}
