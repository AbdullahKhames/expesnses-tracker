package name.expenses.features.user.dao;

import jakarta.ejb.Local;
import name.expenses.features.user.models.UserGroup;

@Local
public interface GroupDao {
    UserGroup save(UserGroup group);
}
