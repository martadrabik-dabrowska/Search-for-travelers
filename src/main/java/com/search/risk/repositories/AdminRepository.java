package com.search.risk.repositories;

import com.search.risk.model.Risk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AdminRepository extends JpaRepository<Risk, Integer> {


}
