package study.ywork.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.ywork.security.domain.Token;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {
    Optional<Token> findTokenByIdentifier(String identifier);
}
