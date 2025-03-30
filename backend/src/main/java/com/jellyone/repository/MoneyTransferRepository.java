package com.jellyone.repository;

import com.jellyone.domain.MoneyTransfer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MoneyTransferRepository extends JpaRepository<MoneyTransfer, Long> {

    Page<MoneyTransfer> findAllByEventId(@Param("eventId") Long eventId, Pageable pageable);

}
