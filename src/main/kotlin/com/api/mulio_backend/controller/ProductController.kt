package com.api.mulio_backend.controller

import com.api.mulio_backend.helper.request.CreateProductRequest
import com.api.mulio_backend.helper.response.ResponseMessage
import com.api.mulio_backend.model.Product
import com.api.mulio_backend.service.ProductService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.bson.types.ObjectId
import java.util.*

@RestController
@RequestMapping("/api/products")
class ProductController @Autowired constructor(
    private val productService: ProductService
) {

    @PostMapping
    fun createProduct(@RequestBody createProductRequest: CreateProductRequest): ResponseEntity<ResponseMessage<Product>> {
        val product = Product(
            productName = createProductRequest.productName,
            price = createProductRequest.price,
            description = createProductRequest.description,
            size = createProductRequest.size,
            color = createProductRequest.color,
            amount = createProductRequest.amount,
            status = createProductRequest.status,
            productType = createProductRequest.productType,
            image = createProductRequest.image,
            createdAt = Date()
        )
        val createdProduct = productService.createProduct(product)
        return ResponseEntity.ok(ResponseMessage("Product created successfully", createdProduct))
    }

    @GetMapping("/{id}")
    fun getProductById(@PathVariable id: String): ResponseEntity<ResponseMessage<Product>> {
        val product = productService.getProductById(ObjectId(id)) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(ResponseMessage("Product retrieved successfully", product))
    }

    @PutMapping("/{id}")
    fun updateProduct(
        @PathVariable id: String, @RequestBody updateProductRequest: CreateProductRequest
    ): ResponseEntity<ResponseMessage<Product>> {
        val product = Product(
            productId = ObjectId(id),
            productName = updateProductRequest.productName,
            price = updateProductRequest.price,
            description = updateProductRequest.description,
            size = updateProductRequest.size,
            color = updateProductRequest.color,
            amount = updateProductRequest.amount,
            status = updateProductRequest.status,
            productType = updateProductRequest.productType,
            image = updateProductRequest.image,
            createdAt = productService.getProductById(ObjectId(id))?.createdAt ?: Date(),
            updatedAt = Date()
        )
        val updatedProduct = productService.updateProduct(id, product) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(ResponseMessage("Product updated successfully", updatedProduct))
    }

    @DeleteMapping("/{id}")
    fun deleteProduct(@PathVariable id: String): ResponseEntity<ResponseMessage<Void>> {
        return if (productService.deleteProduct(ObjectId(id))) {
            ResponseEntity.ok(ResponseMessage("Product deleted successfully", null))
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping
    fun getAllProducts(): ResponseEntity<ResponseMessage<List<Product>>> {
        val products = productService.getAllProducts()
        return ResponseEntity.ok(ResponseMessage("Products retrieved successfully", products))
    }

    @GetMapping("/page")
    fun getProducts(@RequestParam(defaultValue = "0") page: Int, @RequestParam(defaultValue = "10") size: Int): ResponseEntity<ResponseMessage<Page<Product>>> {
        val pageable: Pageable = PageRequest.of(page, size)
        val products = productService.getProducts(pageable)
        return ResponseEntity.ok(ResponseMessage("Products retrieved successfully", products))
    }
}