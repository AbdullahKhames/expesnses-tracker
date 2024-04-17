package name.expenses.features.pocket_transfer.dao;

import jakarta.ejb.Local;
import name.expenses.features.pocket_transfer.models.PocketTransfer;
import name.expenses.globals.Page;
import name.expenses.globals.SortDirection;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Local
public interface PocketTransferDAO {
    PocketTransfer create(PocketTransfer expense);
    Optional<PocketTransfer> get(String refNo);
    Page<PocketTransfer> findAll(Long pageNumber, Long pageSize, String sortBy, SortDirection sortDirection);
    List<PocketTransfer> get();
    PocketTransfer update(PocketTransfer expense);
    String delete(String refNo);
    Set<PocketTransfer> getEntities(Set<String> refNos);
}