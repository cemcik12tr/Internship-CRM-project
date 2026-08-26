package com.crm.backend.service;

import com.crm.backend.dto.ProductRequest;
import com.crm.backend.model.Catalog;
import com.crm.backend.model.Product;
import com.crm.backend.model.enums.Status;
import com.crm.backend.repository.CatalogRepository;
import com.crm.backend.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        catalog.setUpdatedBy("system_user");
        catalog.setUpdatedDate(LocalDateTime.now());
        String trimmedName = request.getName().trim();
        
        if (productRepository.existsByNameIgnoreCaseAndCatalogId(trimmedName, request.getCatalogId())) {
            // Jira US-10 ACC-04 Hata Mesajı
            throw new RuntimeException("A product with this name already exists in the selected catalog."); 
        }


        catalogRepository.save(catalog);
        return productRepository.save(product);
    }

    //READ
    public List<Product> getAllProduct(){
        return productRepository.findAll().stream()
                .filter(p -> p.getStatus() != Status.DELETED)
                .toList();
    }

    //READ single
    public Product getProductById(String id){
        return productRepository.findById(id)
            .orElseThrow(()-> new RuntimeException("Ürün bulunamadı!"));
    }

    //UPDATE
    @Transactional
    public Product updateProduct(String id, ProductRequest request){

        Product existingProduct = productRepository.findById(id)
            .filter(p -> p.getStatus() != Status.DELETED)
            .orElseThrow(() -> new RuntimeException("undefined product"));

        Catalog catalog =catalogRepository.findById(request.getCatalogId())
            .orElseThrow(()-> new RuntimeException("catalog not found"));

        Catalog oldCatalog = existingProduct.getCatalog();

        if(!oldCatalog.getId().equals(catalog.getId())){
            oldCatalog.setUpdatedBy("system_user");
            catalog.setUpdatedBy("system_user");
        }

        existingProduct.setCatalog(catalog);

        existingProduct.setStatus(request.getStatus());
        existingProduct.setName(request.getName());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setStock(request.getStock());
        existingProduct.setUpdatedBy("system_user");

        String trimmedName = request.getName().trim();

        if (productRepository.existsByNameIgnoreCaseAndCatalogIdAndIdNot(trimmedName, request.getCatalogId(), id)) {
             throw new RuntimeException("A product with this name already exists in the selected catalog.");
        }
     

        return productRepository.save(existingProduct);
    } 

    //DELETE
    public void deleteProduct(String id){
        Product existingProduct = productRepository.findById(id)
            .filter(p->p.getStatus() != Status.DELETED)
            .orElseThrow(()-> new RuntimeException("undefined product"));

        Catalog catalog = existingProduct.getCatalog();
        catalog.setUpdatedBy("system_user");

        existingProduct.setStatus(Status.DELETED);
        existingProduct.setDeletedDate(LocalDateTime.now());
        existingProduct.setDeletedBy("system_user");
        existingProduct.setUpdatedBy("sytem_user");
        
        productRepository.save(existingProduct);
    }

    //SEARCH
    public List<Product> searchProducts(String id , String name, String catalogName,
                                        String stockStatus, Status status,
                                        java.math.BigDecimal minPrice , java.math.BigDecimal maxPrice){
        // ACC-01: Arayüzden status seçilmezse varsayılan olarak ACTIVE ata
        if(status == null){
            status = Status.ACTIVE;
        }

        return productRepository.searchProducts(id, name, catalogName, stockStatus, status, minPrice, maxPrice);
    
     }






}