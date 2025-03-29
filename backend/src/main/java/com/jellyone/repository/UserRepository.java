package com.jellyone.repository;

import com.jellyone.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByUsername(String username);

    @Query(
            value = """
            SELECT u.* 
            FROM users u
            WHERE (:search IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) 
                   OR LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
                   )
            GROUP BY u.id
        """,
            nativeQuery = true
    )
    Page<User> findAllUsersWithSomeParameters(@Param("search") String search, Pageable pageable);
}
