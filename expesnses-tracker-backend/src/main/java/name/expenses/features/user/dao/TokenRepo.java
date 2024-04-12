package name.expenses.features.user.dao;

import jakarta.ejb.Local;
import name.expenses.features.user.models.Token;
import name.expenses.features.user.models.TokenType;

import java.util.List;
import java.util.Optional;
@Local
public interface TokenRepo {
//    @Query("select  t from  Token  t inner join User  u on t.user.id = u.id " +
//            "where u.id = :userId and (t.expired = false  or t.revoked = false )")
    List<Token> findAllValidTokenByUser(Long userId);

    Optional<Token> findByToken(String token);
//    @Query("select  t from  Token  t inner join User  u on t.user.id = u.id where u.id = :userId")
    List<Token> findAllTokenByUser(Long userId);

    Optional<Token> findTokenByUser_IdAndTokenType(Long userId , TokenType tokenType);

    Optional<Token> findByTokenAndTokenType(String jwt , TokenType tokenType);

    Token save(Token token);

    void saveAll(List<Token> validUserTokens);

    void deleteAll(List<Token> validUserTokens);
}