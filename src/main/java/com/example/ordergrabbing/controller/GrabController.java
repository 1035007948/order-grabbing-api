package com.example.ordergrabbing.controller;

import com.example.ordergrabbing.entity.Grab;
import com.example.ordergrabbing.service.GrabService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grabs")
public class GrabController {

    @Autowired
    private GrabService grabService;

    @PostMapping
    public ResponseEntity<Grab> createGrab(@RequestBody Grab grab) {
        Grab createdGrab = grabService.createGrab(grab);
        return new ResponseEntity<>(createdGrab, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Grab>> getAllGrabs() {
        List<Grab> grabs = grabService.getAllGrabs();
        return new ResponseEntity<>(grabs, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Grab> getGrabById(@PathVariable Long id) {
        return grabService.getGrabById(id)
                .map(grab -> new ResponseEntity<>(grab, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Grab> updateGrab(@PathVariable Long id, @RequestBody Grab grabDetails) {
        try {
            Grab updatedGrab = grabService.updateGrab(id, grabDetails);
            return new ResponseEntity<>(updatedGrab, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGrab(@PathVariable Long id) {
        try {
            grabService.deleteGrab(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
