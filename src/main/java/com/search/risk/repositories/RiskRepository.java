package com.search.risk.repositories;

import com.search.risk.model.Risk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;


@Repository
public interface RiskRepository extends JpaRepository<Risk, Integer> {

    @Query(value = "select distinct r from Risk r JOIN r.countries cc  where r.name like %:param% or r.abbreviation like %:param% or r.description like %:param%" +
            " or cc.country like %:param%")
    List<Risk> findRisk(@Param("param") String param);

    @Transactional
    @Modifying
    @Query(value = "update Risk r set r.name = :newName, r.abbreviation =:newAbbreviation," +
            " r.description = :newDescription where r.id= :id")
    void updateRisk(@Param("newName") String name, @Param("newAbbreviation") String abbreviation,
                    @Param("newDescription") String description, @Param("id") int id);
}
