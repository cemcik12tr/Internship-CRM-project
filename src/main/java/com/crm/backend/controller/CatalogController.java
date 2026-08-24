package com.crm.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.crm.backend.dto.CatalogRequest;
import com.crm.backend.dto.CatalogUpdateRequest;
import com.crm.backend.model.Catalog;

import com.crm.backend.service.CatalogService;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/catalogs")
public class CatalogController {
    private final CatalogService catalogService;
    
    public CatalogController(CatalogService catalogService){
        this.catalogService = catalogService;
    }

    //CREATE
    @PostMapping
    public ResponseEntity<Catalog> createdCatalog(@Valid @RequestBody CatalogRequest request){
        Catalog createdCatalog = catalogService.createdCatalog(request);
        return ResponseEntity.ok(createdCatalog);
    }

    //UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Catalog> updateCatalog(
            @PathVariable String id,
            @jakarta.validation.Valid @RequestBody CatalogUpdateRequest request) {
        Catalog updatedCatalog = catalogService.updateCatalog(id, request);
        return ResponseEntity.ok(updatedCatalog);
    }
    
    //READ
    @GetMapping
    public ResponseEntity<List<Catalog>> getAllCatalogs() {
        List<Catalog> catalogs = catalogService.getAllCatalogs();
        return ResponseEntity.ok(catalogs);
    }

    //READ SİNGLE
    @GetMapping("/{id}")
    public ResponseEntity<Catalog> getCatalogById(@PathVariable String id){
        Catalog catalog = catalogService.getCatalogById(id);
        return ResponseEntity.ok(catalog);
    }

    //DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCatalog(@PathVariable String id){
        catalogService.deleteCatalog(id);
        return ResponseEntity.ok().build();
    }
}
