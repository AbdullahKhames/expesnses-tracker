package name.expenses.features.user.dao.dao_impl;

import jakarta.inject.Singleton;
import jakarta.interceptor.Interceptors;
import jakarta.transaction.Transactional;
import name.expenses.config.filters.RepoAdvice;
import name.expenses.features.user.dao.RoleRepo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import name.expenses.features.user.models.Role;
import name.expenses.features.user.models._2auth;

import java.util.List;
import java.util.Optional;
@Singleton
@Interceptors(RepoAdvice.class)
@Transactional
public class RoleRepoImpl implements RoleRepo {

    @PersistenceContext(unitName = "expenses-unit")
    private EntityManager entityManager;

    @Override
    public Optional<Role> findByName(String roleName) {
        return entityManager.createQuery(
                "SELECT r FROM Role r WHERE r.name = :roleName", Role.class)
                .setParameter("roleName", roleName)
                .getResultList()
                .stream()
                .findFirst();
    }

    @Override
    public boolean existsRoleByName(String name) {
        long count = entityManager.createQuery(
                "SELECT COUNT(r) FROM Role r WHERE r.name = :name", Long.class)
                .setParameter("name", name)
                .getSingleResult();
        return count > 0;
    }
    @Override
    public Role save(Role role) {
        if (role.getId() != null && entityManager.find(Role.class, role.getId()) != null) {
            return entityManager.merge(role);
        } else {
            entityManager.persist(role);
            return role;
        }
    }

    @Override
    public Optional<Role> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Role.class, id));
    }

    @Override
    public List<Role> findAll() {
        return entityManager.createQuery("SELECT r FROM Role r", Role.class)
                .getResultList();
    }
}
