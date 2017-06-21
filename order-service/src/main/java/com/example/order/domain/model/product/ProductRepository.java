package com.example.order.domain.model.product;

public interface ProductRepository {

  Product productOfId(ProductId productId);

  void save(Product product);

}
