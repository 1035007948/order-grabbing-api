package com.example.ordergrabbing.service;

import com.example.ordergrabbing.entity.Grab;
import com.example.ordergrabbing.repository.GrabRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GrabService {

    @Autowired
    private GrabRepository grabRepository;

    public Grab createGrab(Grab grab) {
        return grabRepository.save(grab);
    }

    public List<Grab> getAllGrabs() {
        return grabRepository.findAll();
    }

    public Optional<Grab> getGrabById(Long id) {
        return grabRepository.findById(id);
    }

    public Grab updateGrab(Long id, Grab grabDetails) {
        Grab grab = grabRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grab not found with id: " + id));
        grab.setStartTime(grabDetails.getStartTime());
        grab.setEndTime(grabDetails.getEndTime());
        grab.setProductName(grabDetails.getProductName());
        grab.setStock(grabDetails.getStock());
        return grabRepository.save(grab);
    }

    public void deleteGrab(Long id) {
        Grab grab = grabRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grab not found with id: " + id));
        grabRepository.delete(grab);
    }
}
