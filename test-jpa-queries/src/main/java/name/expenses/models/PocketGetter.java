package name.expenses.models;


import java.util.Set;

public interface PocketGetter extends GetRefNo {
    Set<Pocket> getPockets();
    void setPockets(Set<Pocket> pockets);
}