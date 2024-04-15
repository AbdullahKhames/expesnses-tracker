package name.expenses.utils.collection_getter;

import name.expenses.features.user.models.User;
import name.expenses.globals.GetRefNo;


public interface UserGetter extends GetRefNo {
    User getUser();
    void setUser(User user);
}
