package com.api.mulio_backend.controller

import com.api.mulio_backend.helper.exception.CustomException
import com.api.mulio_backend.helper.request.CreateProductRequest
import com.api.mulio_backend.helper.request.ReviewRequest
import com.api.mulio_backend.helper.response.*
import com.api.mulio_backend.model.Product
import com.api.mulio_backend.model.Review
import com.api.mulio_backend.repository.ReviewRepository
import com.api.mulio_backend.service.ProductService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.bson.types.ObjectId
import org.springframework.data.domain.PageImpl
import org.springframework.http.HttpStatus

@RestController
@RequestMapping("/api/products")
class ProductController @Autowired constructor(
    private val productService: ProductService,
    private val reviewRepository: ReviewRepository
) {

    // Tạo product
    @PostMapping
    fun createProduct(@RequestBody createProductRequest: CreateProductRequest): ResponseEntity<ResponseObject<Product>> {
        return try {
            val createdProduct = productService.createProduct(createProductRequest)
            ResponseEntity.status(HttpStatus.OK)
                .body(ResponseObject(HttpStatus.OK.value(), "Product created successfully", createdProduct))
        } catch (e: CustomException) {
            ResponseEntity.status(e.status)
                .body(ResponseObject(e.status.value(), e.message ?: "An error occurred", null))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseObject(HttpStatus.BAD_REQUEST.value(), "Error creating product: ${e.message}", null))
        }
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
        val updatedProduct =
            productService.updateProduct(id, updateProductRequest) ?: return ResponseEntity.notFound().build()
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
    fun getProducts(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) search: String? // New search parameter
    ): ResponseEntity<ResponseMessage<Page<Product>>> {
        val pageable: Pageable = PageRequest.of(page, size)
        val products = productService.getProducts(pageable, search) // Pass search to service layer
        return ResponseEntity.ok(ResponseMessage("Products retrieved successfully", products))
    }


    // Lấy theo sku base code
    @GetMapping("/by-sku")
    fun getProductsBySkuBase(@RequestParam skuBase: String): ResponseEntity<ResponseObject<List<ProductResponse>>> {
        val groupedProducts = productService.getProductsBySkuBase(skuBase)
        return ResponseEntity.ok(ResponseObject(HttpStatus.OK.value(), "Products retrieved successfully", groupedProducts))
    }

    // Lấy theo product type
    @GetMapping("/by-product-type/{productType}")
    fun getProductByProductType(@PathVariable productType: String): ResponseEntity<ResponseObject<List<Product>>> {
        return try {
            val products = productService.getProductByProductType(productType)
            if (products.isNotEmpty()) {
                ResponseEntity.ok(ResponseObject(HttpStatus.OK.value(), "Products found", products))
            } else {
                ResponseEntity.ok(
                    ResponseObject(
                        HttpStatus.OK.value(),
                        "No products found for type: $productType",
                        emptyList()
                    )
                )
            }
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseObject(HttpStatus.BAD_REQUEST.value(), "Error retrieving products: ${e.message}", null))
        }
    }

    // Lấy theo sku base, màu, size
    @GetMapping("/by-sku/{skuBase}")
    fun getProductBySkuBaseAndAttributes(
        @PathVariable skuBase: String,
        @RequestParam color: String,
        @RequestParam(required = false) size: String?
    ): ResponseEntity<ResponseObject<Product>> {
        return try {
            val product = productService.getProductBySkuBaseAndAttributes(skuBase, color, size)

            if (product != null) {
                ResponseEntity.ok(
                    ResponseObject(HttpStatus.OK.value(), "Product found successfully", product)
                )
            } else {
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseObject(HttpStatus.NOT_FOUND.value(), "Product not found", null))
            }
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                    ResponseObject(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Error fetching product: ${e.message}",
                        null
                    )
                )
        }
    }

    @GetMapping("/by-sku/{skuBase}/reviews")
    fun getReviewsBySkuBase(@PathVariable skuBase: String): ResponseEntity<ResponseMessage<List<ReviewResponse>>> {
        val reviews = productService.getReviewsBySkuBase(skuBase)
        return ResponseEntity.ok(ResponseMessage("Reviews retrieved successfully", reviews))
    }

    @GetMapping("/sizes/{skuBase}")
    fun getListSizeBySkuBase(
        @PathVariable skuBase: String
    ): ResponseEntity<ResponseObject<List<String?>>> {
        return try {
            val sizes = productService.getListSizeBySkuBase(skuBase)

            ResponseEntity.ok(ResponseObject(HttpStatus.OK.value(), "Sizes retrieved successfully", sizes))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                    ResponseObject(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Error fetching sizes: ${e.message}",
                        emptyList()
                    )
                )
        }
    }

    @GetMapping("/colors/{skuBase}")
    fun getListColorBySkuBase(
        @PathVariable skuBase: String
    ): ResponseEntity<ResponseObject<List<String>>> {
        return try {
            val colors = productService.getListColorBySkuBase(skuBase)

            ResponseEntity.ok(
                ResponseObject(HttpStatus.OK.value(), "Colors retrieved successfully", colors)
            )
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                    ResponseObject(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Error fetching colors: ${e.message}",
                        emptyList()
                    )
                )
        }
    }


    @PostMapping("/reviews/{productId}")
    fun addReview(
        @RequestHeader("Authorization") token: String,
        @PathVariable productId: String,
        @RequestBody reviewRequest: ReviewRequest
    ): ResponseEntity<ResponseObject<Review>> {
        val jwtToken = token.replace("Bearer ", "")
        return try {
            val review = productService.addReview(
                jwtToken,
                ObjectId(productId),
                reviewRequest
            )
            ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseObject(HttpStatus.CREATED.value(), "Review added successfully", review))
        } catch (e: CustomException) {
            ResponseEntity.status(e.status)
                .body(ResponseObject(e.status.value(), "Error adding review: ${e.message}", null))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred: ${e.message}", null))
        }
    }

    @PutMapping("/reviews/{reviewId}")
    fun updateReview(
        @RequestHeader("Authorization") token: String,
        @PathVariable reviewId: String,
        @RequestBody reviewRequest: ReviewRequest
    ): ResponseEntity<ResponseObject<Review>> {
        val jwtToken = token.replace("Bearer ", "")
        return try {
            val updatedReview = productService.updateReview(jwtToken, ObjectId(reviewId), reviewRequest)
            ResponseEntity.ok(ResponseObject(HttpStatus.OK.value(), "Review updated successfully", updatedReview))
        } catch (e: CustomException) {
            ResponseEntity.status(e.status)
                .body(ResponseObject(e.status.value(), "Error updating review: ${e.message}", null))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred: ${e.message}", null))
        }
    }

    @GetMapping("/reviews/{productId}")
    fun getReviewsByProductId(@PathVariable productId: String): ResponseEntity<ResponseMessage<List<Review>>> {
        val reviews = productService.getReviewsByProductId(ObjectId(productId))
        return ResponseEntity.ok(ResponseMessage("Reviews retrieved successfully", reviews))
    }
}