package com.crm.backend.service;

import com.crm.backend.dto.ProductRequest;
import com.crm.backend.model.Catalog;
import com.crm.backend.model.Product;
import com.crm.backend.model.enums.Status;
import com.crm.backend.repository.CatalogRepository;
import com.crm.backend.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {
    private final CatalogRepository catalogRepository;
    private final ProductRepository productRepository;
    

    public ProductService(ProductRepository productRepository, CatalogRepository catalogRepository) {
        this.productRepository = productRepository;
        this.catalogRepository = catalogRepository;
    }
    
    //CRATE
    public Product createProduct(ProductRequest request){
        Catalog catalog = catalogRepository.findById(request.getCatalogId()).orElseThrow(()-> new RuntimeException("catalog not found"));
        
        if(catalog.getStatus() != Status.ACTIVE){
            throw new RuntimeException("seçilen katalog aktif değil");
        }

        Product product = new Product();
       
        String generatedProductId = "PRD-" +  UUID.randomUUID().toString().substring(0,8).toUpperCase();
        product.setId(generatedProductId);
        
        product.setCatalog(catalog);
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setStatus(Status.ACTIVE);
        product.setCreatedBy("system_user");

        return productRepository.save(product);
    }

    //READ
    public List<Product> getAllProduct(){
        return productRepository.findAll().stream()
                .filter(p -> p.getStatus() != Status.DELETED)
                .toList();
    }

    //UPDATE
    public Product updateProduct(String id, ProductRequest request){

        Product existingProduct = productRepository.findById(id)
            .filter(p -> p.getStatus() != Status.DELETED)
            .orElseThrow(() -> new RuntimeException("undefined product"));

        Catalog catalog =catalogRepository.findById(request.getCatalogId())
            .orElseThrow(()-> new RuntimeException("catalog not found"));

        existingProduct.setCatalog(catalog);
        existingProduct.setName(request.getName());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setStock(request.getStock());
        existingProduct.setUpdatedBy("system_user");
        existingProduct.setUpdatedDate(LocalDateTime.now());

        return productRepository.save(existingProduct);
    } 

    //DELETE
    public void deleteProduct(String id){
        Product existingProduct = productRepository.findById(id)
            .filter(p->p.getStatus() != Status.DELETED)
            .orElseThrow(()-> new RuntimeException("undefined product"));

        existingProduct.setStatus(Status.DELETED);
        existingProduct.setDeletedDate(LocalDateTime.now());
        existingProduct.setDeletedBy("system_user");

        productRepository.save(existingProduct);

    }

}