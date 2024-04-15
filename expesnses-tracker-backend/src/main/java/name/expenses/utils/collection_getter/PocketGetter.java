package name.expenses.utils.collection_getter;

import name.expenses.features.pocket.models.Pocket;
import name.expenses.globals.GetRefNo;

import java.util.Set;

public interface PocketGetter extends GetRefNo {
    Set<Pocket> getPockets();
    void setPockets(Set<Pocket> pockets);
}