package com.javatechie.service;

import com.javatechie.entity.Product;
import com.javatechie.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
@CacheConfig(cacheNames={"products"})
public class ProductService {

    @Autowired
    private ProductRepository repository;

//    @PostConstruct
//    public void initDB() {
//        List<Product> products = IntStream.rangeClosed(1, 10)
//                .mapToObj(i -> new Product("product" + i, new Random().nextInt(5000), "desc" + i, "type" + i))
//                .collect(Collectors.toList());
//        repository.saveAll(products);
//    }

   @CachePut(key = "#product.id")
    public Product addProduct(Product product) {
        return repository.save(product);

    }
    @Cacheable(key = "#id")
    public Product getProduct(int id) {
        log.info("ProductService : getProduct interact with DB");
        return repository.findById(id).get();
    }

    @Cacheable(value = "price")
    public List<Product> getProducts() {
        log.info("ProductService : getProducts interact with DB");
        return repository.findAll();
    }

    @CachePut(key = "#id")
    public Product updateProduct(int id, Product productRequest) {
        // get the product from DB by id
        // update with new value getting from request
        Product existingProduct = repository.findById(id).get(); // DB
        existingProduct.setName(productRequest.getName());
        existingProduct.setDescription(productRequest.getDescription());
        existingProduct.setPrice(productRequest.getPrice());
        existingProduct.setProductType(existingProduct.getProductType());
        return repository.save(existingProduct);
    }
    @CacheEvict(key = "#id")
    public String deleteProduct(int id) {
        repository.deleteById(id);
        return "product deleted";
    }
    @Cacheable(key="#product.productType", condition="#product.price > 2000")
    public List<Product> getProductsByType(Product product){
        log.info("ProductService : getProductsByType interact with DB");
       return repository.findByProductType(product.getProductType());
    }


}
