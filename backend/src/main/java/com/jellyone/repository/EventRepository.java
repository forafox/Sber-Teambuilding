package com.jellyone.repository;

import com.jellyone.domain.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    @Query("""
                SELECT DISTINCT e 
                FROM Event e 
                LEFT JOIN e.participants p 
                WHERE e.author.id = :userId OR p.id = :userId
            """)
    Page<Event> findAllWithSomeParameters(@Param("userId") Long userId, Pageable pageable);
}