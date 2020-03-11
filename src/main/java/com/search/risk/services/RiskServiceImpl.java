package com.search.risk.services;


import com.search.risk.model.Risk;
import com.search.risk.repositories.RiskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RiskServiceImpl implements RiskService {

    private RiskRepository riskRepository;

    private List<Risk> riskList;

    public RiskServiceImpl(RiskRepository riskRepository) {
        this.riskRepository = riskRepository;
    }

    @Override
    public List<Risk> findRisk(String param) {
        this.riskList = this.riskRepository.findRisk(param);
        return this.riskList;
    }

    @Override
    public List<Risk> findAll() {
        this.riskList = this.riskRepository.findAll();
        return this.riskList;
    }

    @Override
    public void updateRisk(String name, String abbreviation, String description, int id) {
        this.riskRepository.updateRisk(name, abbreviation, description, id);
    }

    @Override
    public void saveRisk(Risk risk) {
        this.riskRepository.save(risk);
    }

    @Override
    public void deleteRisk(Risk risk) {
        this.riskRepository.delete(risk);
    }
}
