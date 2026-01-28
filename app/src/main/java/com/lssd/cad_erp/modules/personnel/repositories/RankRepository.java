package com.lssd.cad_erp.modules.personnel.repositories;

import com.lssd.cad_erp.modules.personnel.domain.Rank;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RankRepository extends JpaRepository<Rank, Long> {
    Optional<Rank> findByName(String name);
}