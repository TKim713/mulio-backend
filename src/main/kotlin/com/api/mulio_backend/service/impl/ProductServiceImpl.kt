package com.api.mulio_backend.service.impl

import com.api.mulio_backend.model.Product
import com.api.mulio_backend.service.ProductService
import com.api.mulio_backend.repository.ProductRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.bson.types.ObjectId
import java.util.*

@Service
class ProductServiceImpl @Autowired constructor(
    private val productRepository: ProductRepository
) : ProductService {
    private val vietnamTimeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh")

    override fun createProduct(product: Product): Product {
        val calendar = Calendar.getInstance(vietnamTimeZone)
        product.createdAt = calendar.time
        return productRepository.save(product)
    }

    override fun getProductById(productId: ObjectId): Product? {
        return productRepository.findById(productId.toString()).orElse(null)
    }

    override fun updateProduct(productId: String, product: Product): Product? {
        val existingProduct = productRepository.findById(ObjectId(productId).toString()).orElse(null) ?: return null
        existingProduct.apply {
            productName = product.productName
            price = product.price
            description = product.description
            size = product.size
            color = product.color
            amount = product.amount
            status = product.status
            productType = product.productType
            image = product.image
            updatedAt = Calendar.getInstance(vietnamTimeZone).time
        }
        return productRepository.save(existingProduct)
    }

    override fun deleteProduct(productId: ObjectId): Boolean {
        return if (productRepository.existsById(productId.toString())) {
            productRepository.deleteById(productId.toString())
            true
        } else {
            false
        }
    }

    override fun getAllProducts(): List<Product> {
        return productRepository.findAll()
    }

    override fun getProducts(pageable: Pageable): Page<Product> {
        return productRepository.findAll(pageable)
    }
}