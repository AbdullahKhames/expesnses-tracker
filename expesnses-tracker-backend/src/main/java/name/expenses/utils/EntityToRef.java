package name.expenses.utils;


import name.expenses.globals.GetRefNo;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class EntityToRef {
    public static <T extends GetRefNo> Set<String> getRefs(Collection<T> productAssociation) {
        return productAssociation.stream()
                .map(GetRefNo::getRefNo)
                .collect(Collectors.toSet());
    }
}
