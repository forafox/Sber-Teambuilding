package com.jellyone.repository;

import com.jellyone.domain.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    @Query(
            value = """
                    SELECT * 
                    FROM tasks t
                    WHERE t.event_id = :eventId
                    """,
            nativeQuery = true
    )
    Page<Task> findAllByEventId(@Param("eventId") Long eventId, Pageable pageable);

    List<Task> findAllByEventId(Long eventId);
}
