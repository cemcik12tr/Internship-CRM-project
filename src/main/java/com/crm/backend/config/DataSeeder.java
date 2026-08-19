package com.crm.backend.config;

import com.crm.backend.model.Catalog;
import com.crm.backend.model.enums.Status;
import com.crm.backend.repository.CatalogRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner{
    private final CatalogRepository catalogRepository;

    public DataSeeder(CatalogRepository catalogRepository){
        this.catalogRepository = catalogRepository;
    }
    
    @Override
    public void run(String... args) throws Exception{
        //eğer veritabanında katalog yoksa başlangıç verilerini ekle
        if(catalogRepository.count() == 0){
            Catalog cat1 = new Catalog();
            cat1.setId("CAT-001");
            cat1.setName("Mobil");
            cat1.setStatus(Status.ACTIVE);

            Catalog cat2 = new Catalog();
             cat2.setId("CAT-002");
            cat2.setName("Ev İnterneti");
            cat2.setStatus(Status.ACTIVE);


            catalogRepository.saveAll(List.of(cat1,cat2));
        
            System.out.println("Başlangıç katalog verileri (CAT-001, CAT-002) veritabanına eklendi.");
        }
    }
}