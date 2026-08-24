package com.crm.backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import com.crm.backend.repository.CatalogRepository;
import com.crm.backend.dto.CatalogRequest;
import com.crm.backend.dto.CatalogUpdateRequest;
import com.crm.backend.model.Catalog;

import com.crm.backend.model.enums.Status;



@Service
public class CatalogService {

    private final CatalogRepository catalogRepository;

    public CatalogService(CatalogRepository catalogRepository){
        this.catalogRepository = catalogRepository;
    }

    //CREATE
    public Catalog createdCatalog(CatalogRequest request){
        String trimmedName = request.getName().trim(); // boşlukları temizleme

        //büyük küçük harf duyarsızlık kontrolü
        if(catalogRepository.existsByNameIgnoreCase(trimmedName)){
            throw new RuntimeException("A catalog with the entered name already exists.");

        }

        Catalog catalog =new Catalog();
        catalog.setName(trimmedName);
        catalog.setStatus(Status.ACTIVE);
        catalog.setCreatedBy("system_user");

        String generetedId = "CAT-" + java.util.UUID.randomUUID().toString().substring(0,5).toUpperCase();
        catalog.setId(generetedId);

        return catalogRepository.save(catalog);
    }

    //UPDATE
    @org.springframework.transaction.annotation.Transactional
    public Catalog updateCatalog(String id, CatalogUpdateRequest request){
        Catalog existingCatalog = catalogRepository.findById(id)
            .orElseThrow(()-> new RuntimeException("Catalog not found with id:" + id));
    
        String trimmedName = request.getName().trim();
        
        if (catalogRepository.existsByNameIgnoreCaseAndIdNot(trimmedName, id)){
            throw new RuntimeException("A catalog with the entered name already exists.");
        }

        existingCatalog.setName(trimmedName);
        existingCatalog.setStatus(request.getStatus());
        existingCatalog.setUpdatedBy("system_user");
        existingCatalog.setUpdatedDate(LocalDateTime.now());

        return catalogRepository.save(existingCatalog);
        }

        //GET
        public List<Catalog> getAllCatalogs() {
            return catalogRepository.findByStatusNot(Status.DELETED);
        }

        //READ SINGLE
        public Catalog getCatalogById(String id){
            return catalogRepository.findById(id)
            .orElseThrow(()-> new RuntimeException("katalog bulunamadı!"));
        }

        //DELETE
        public void deleteCatalog(String id){
            Catalog existingCatalog = catalogRepository.findById(id)
                .filter(p->p.getStatus() != Status.DELETED)
                .orElseThrow(()-> new RuntimeException("undefined catalog"));

            existingCatalog.setUpdatedBy("system_user");

            existingCatalog.setStatus(Status.DELETED);
            existingCatalog.setDeletedDate(LocalDateTime.now());
            existingCatalog.setDeletedBy("system_user");
            existingCatalog.setUpdatedBy("sytem_user");
            
        catalogRepository.save(existingCatalog);
        }
    
}
