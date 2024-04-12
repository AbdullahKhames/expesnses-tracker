package name.expenses.features.user.dao.dao_impl;

import jakarta.inject.Singleton;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import name.expenses.config.filters.RepoAdvice;
import name.expenses.features.user.dao.TokenRepo;
import name.expenses.features.user.models.Token;
import name.expenses.features.user.models.TokenType;

import java.util.List;
import java.util.Optional;
@Singleton
@Interceptors(RepoAdvice.class)
@Transactional
public class TokenRepoImpl implements TokenRepo {

    @PersistenceContext(unitName = "expenses-unit")
    private EntityManager entityManager;

    @Override
    public List<Token> findAllValidTokenByUser(Long userId) {
        return entityManager.createQuery(
                "SELECT t FROM Token t INNER JOIN User u ON t.user.id = u.id " +
                        "WHERE u.id = :userId AND (t.expired = false OR t.revoked = false)",
                Token.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    public Optional<Token> findByToken(String token) {
        return entityManager.createQuery(
                "SELECT t FROM Token t WHERE t.token = :token", Token.class)
                .setParameter("token", token)
                .getResultList()
                .stream()
                .findFirst();
    }

    @Override
    public List<Token> findAllTokenByUser(Long userId) {
        return entityManager.createQuery(
                "SELECT t FROM Token t INNER JOIN User u ON t.user.id = u.id WHERE u.id = :userId", Token.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    public Optional<Token> findTokenByUser_IdAndTokenType(Long userId, TokenType tokenType) {
        return entityManager.createQuery(
                "SELECT t FROM Token t INNER JOIN User u ON t.user.id = u.id " +
                        "WHERE u.id = :userId AND t.tokenType = :tokenType", Token.class)
                .setParameter("userId", userId)
                .setParameter("tokenType", tokenType)
                .getResultList()
                .stream()
                .findFirst();
    }

    @Override
    public Optional<Token> findByTokenAndTokenType(String jwt, TokenType tokenType) {
        return entityManager.createQuery(
                "SELECT t FROM Token t WHERE t.token = :token AND t.tokenType = :tokenType", Token.class)
                .setParameter("token", jwt)
                .setParameter("tokenType", tokenType)
                .getResultList()
                .stream()
                .findFirst();
    }

    @Override
    public Token save(Token token) {
        if (token.getId() != null && entityManager.find(Token.class, token.getId()) != null) {
            return entityManager.merge(token);
        } else {
            entityManager.persist(token);
            return token;
        }
    }

    @Override
    public void saveAll(List<Token> validUserTokens) {
        for (Token token : validUserTokens) {
            if (token.getId() != null && entityManager.find(Token.class, token.getId()) != null) {
                entityManager.merge(token);
            } else {
                entityManager.persist(token);
            }
        }
    }

    @Override
    public void deleteAll(List<Token> validUserTokens) {
        for (Token token : validUserTokens) {
            entityManager.remove(token);
        }
    }
}