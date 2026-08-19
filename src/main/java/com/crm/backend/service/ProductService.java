package com.crm.backend.service;

import com.crm.backend.dto.ProductRequest;
import com.crm.backend.model.Product;
import com.crm.backend.repository.ProductRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    //CRATE
    public Product createProduct(ProductRequest request){
        Product product = new Product();
        product.setIsActive(true);
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCreatedDate(LocalDateTime.now());

        return productRepository.save(product);
    }

    //READ
    public List<Product> getAllProduct(){
        return productRepository.findAll();
    }

    //UPDATE
    public Product updateProduct(long id, ProductRequest request){
        Product existingProduct = productRepository.findById(id).orElseThrow(() -> new RuntimeException("undefined product"));

        existingProduct.setName(request.getName());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setStock(request.getStock());
        existingProduct.setUpdatedDate(LocalDateTime.now());

        return productRepository.save(existingProduct);
    } 

    //DELETE
    public void deleteProduct(long id){
        Product existingProduct = productRepository.findById(id).orElseThrow(()-> new RuntimeException("undefined product"));
        productRepository.delete(existingProduct);

    }

}