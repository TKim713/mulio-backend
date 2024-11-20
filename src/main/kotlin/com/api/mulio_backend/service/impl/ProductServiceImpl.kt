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

data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

@Service
class ProductServiceImpl @Autowired constructor(
    private val productRepository: ProductRepository
) : ProductService {
    private val vietnamTimeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh")

    override fun createProduct(product: Product): Product {
        val calendar = Calendar.getInstance(vietnamTimeZone)
        product.createdAt = calendar.time
        // Tính productCount cho sản phẩm mới
        val productCount = productRepository.findAll()
            .count { it.productType == product.productType && it.size == product.size && it.color == product.color && it.price == product.price }

        // Gán amount bằng productCount
        product.amount = productCount + 1
        return productRepository.save(product)
    }

    override fun getProductById(productId: ObjectId): Product? {
        return productRepository.findById(productId.toString()).orElse(null)
    }

    override fun updateProduct(productId: String, product: Product): Product? {
        val existingProduct = productRepository.findById(ObjectId(productId).toString()).orElse(null) ?: return null
        existingProduct.apply {
            code = product.code
            productName = product.productName
            price = product.price
            description = product.description
            size = product.size
            color = product.color
            status = product.status
            productType = product.productType
            images = product.images
            updatedAt = Date()
        }
        return productRepository.save(existingProduct)
    }

    override fun deleteProduct(productId: ObjectId): Boolean {
        val productToDelete = productRepository.findById(productId.toString()).orElse(null) ?: return false

        productRepository.deleteById(productId.toString())

        // Recalculate productCount for remaining products with the same attributes
        val remainingProducts = productRepository.findAll()
            .filter { it.productType == productToDelete.productType && it.size == productToDelete.size && it.color == productToDelete.color && it.price == productToDelete.price }

        val productCount = remainingProducts.size

        // Update amount for remaining products
        remainingProducts.forEach { it.amount = productCount }
        productRepository.saveAll(remainingProducts)

        return true
    }

    override fun getAllProducts(): List<Product> {
        return productRepository.findAll()
    }

    override fun getProducts(pageable: Pageable): Page<Product> {
        return productRepository.findAll(pageable)
    }

    override fun getAllGroupByProductType(): List<Map<String, Any>> {
        val products = productRepository.findAll()
        return products.groupBy { Quadruple(it.productType, it.size, it.color, it.price) }
            .map { (key, groupedProducts) ->
                mapOf(
                    "productType" to key.first,
                    "size" to key.second,
                    "color" to key.third,
                    "price" to key.fourth,
                    "productCount" to groupedProducts.size,
                    "products" to groupedProducts.map { product ->
                        mapOf(
                            "code" to product.code,
                            "productName" to product.productName,
                            "price" to product.price,
                            "description" to product.description,
                            "amount" to product.amount,
                            "status" to product.status,
                            "images" to product.images
                        )
                    }
                )
            }
    }
}