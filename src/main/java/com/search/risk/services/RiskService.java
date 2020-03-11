package com.search.risk.services;

import com.search.risk.model.Risk;

import java.util.List;

public interface RiskService {

    List<Risk> findRisk(String param);

    List<Risk> findAll();

    void updateRisk(String name, String abbreviation, String description, int id);

    void saveRisk(Risk risk);

    void deleteRisk(Risk risk);


}
