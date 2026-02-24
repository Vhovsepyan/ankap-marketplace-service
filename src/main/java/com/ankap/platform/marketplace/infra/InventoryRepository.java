package com.ankap.platform.marketplace.infra;

import com.ankap.platform.marketplace.domain.Inventory;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select i from Inventory i where i.productId = :pid")
  Optional<Inventory> findByProductIdForUpdate(@Param("pid") long productId);
}