package name.expenses.features.user.dao.dao_impl;

import jakarta.inject.Singleton;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import name.expenses.config.advice.RepoAdvice;
import name.expenses.features.user.dao.GroupDao;
import name.expenses.features.user.models.UserGroup;
import name.expenses.features.user.models.Role;

@Singleton
@Interceptors(RepoAdvice.class)
@Transactional
public class GroupDaoImpl implements GroupDao {
    @PersistenceContext(unitName = "expenses-unit")
    private EntityManager entityManager;

    @Override
    public UserGroup save(UserGroup group) {
        if (group.getId() != null && entityManager.find(Role.class, group.getId()) != null) {
            return entityManager.merge(group);
        } else {
            entityManager.persist(group);
            return group;
        }
    }
}
