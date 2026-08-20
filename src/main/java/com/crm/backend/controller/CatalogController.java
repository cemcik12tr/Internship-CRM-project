package com.crm.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.crm.backend.dto.CatalogRequest;
import com.crm.backend.dto.CatalogUpdateRequest;
import com.crm.backend.model.Catalog;
import com.crm.backend.service.CatalogService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/catalogs")
public class CatalogController {
    private final CatalogService catalogService;
    
    public CatalogController(CatalogService catalogService){
        this.catalogService = catalogService;
    }

    @PostMapping
    public ResponseEntity<Catalog> createdCatalog(@Valid @RequestBody CatalogRequest request){
        Catalog createdCatalog = catalogService.createdCatalog(request);
        return ResponseEntity.ok(createdCatalog);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Catalog> updateCatalog(
            @PathVariable String id,
            @jakarta.validation.Valid @RequestBody CatalogUpdateRequest request) {
        Catalog updatedCatalog = catalogService.updateCatalog(id, request);
        return ResponseEntity.ok(updatedCatalog);
    }
    
}
