package name.expenses.features.user.dao;

import jakarta.ejb.Local;
import name.expenses.features.user.models.Group;

@Local
public interface GroupDao {
    Group save(Group group);
}
