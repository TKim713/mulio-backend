package com.api.mulio_backend.service.impl

import com.api.mulio_backend.helper.exception.CustomException
import com.api.mulio_backend.helper.request.CreateProductRequest
import com.api.mulio_backend.helper.request.ReviewRequest
import com.api.mulio_backend.helper.response.ProductResponse
import com.api.mulio_backend.helper.response.ReviewResponse
import com.api.mulio_backend.model.Product
import com.api.mulio_backend.model.Review
import com.api.mulio_backend.model.Wishlist
import com.api.mulio_backend.repository.ProductRepository
import com.api.mulio_backend.repository.ReviewRepository
import com.api.mulio_backend.repository.UserRepository
import com.api.mulio_backend.repository.WishlistRepository
import com.api.mulio_backend.service.ProductService
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.util.*

@Service
class ProductServiceImpl @Autowired constructor(
    private val productRepository: ProductRepository,
    private val wishlistRepository: WishlistRepository,
    private val reviewRepository: ReviewRepository,
    private val userRepository: UserRepository
) : ProductService {
    private val vietnamTimeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh")

    override fun createProduct(createProductRequest: CreateProductRequest): Product {
        val calendar = Calendar.getInstance(vietnamTimeZone)

        val skuCode = generateSkuCode(
            skuBase = createProductRequest.skuBase,
            size = createProductRequest.size,
            color = createProductRequest.color
        )

        val existingProduct = productRepository.findBySkuCode(skuCode)

        return if (existingProduct != null) {
            if (existingProduct.productName != createProductRequest.productName) {
                throw IllegalArgumentException("Product with SKU code $skuCode already exists but has a different name: ${existingProduct.productName}")
            }

            existingProduct.amount = existingProduct.amount?.plus(createProductRequest.amount ?: 0)
            productRepository.save(existingProduct)
        } else {
            val product = Product(
                skuBase = createProductRequest.skuBase,
                skuCode = skuCode,
                productName = createProductRequest.productName,
                price = createProductRequest.price,
                description = createProductRequest.description,
                size = createProductRequest.size,
                color = createProductRequest.color,
                amount = createProductRequest.amount ?: 0,
                status = createProductRequest.status,
                productType = createProductRequest.productType,
                images = createProductRequest.images,
                createdAt = calendar.time
            )
            productRepository.save(product)
        }
    }

    override fun getProductById(productId: ObjectId): Product? {
        return productRepository.findById(productId.toString()).orElse(null)
    }

    override fun updateProduct(productId: String, updateProductRequest: CreateProductRequest): Product? {
        val existingProduct = productRepository.findById(ObjectId(productId).toString()).orElseThrow {
            CustomException("Product not found", HttpStatus.NOT_FOUND)
        }

        val newSkuCode = generateSkuCode(
            skuBase = updateProductRequest.skuBase,
            size = updateProductRequest.size,
            color = updateProductRequest.color
        )

        existingProduct.apply {
            skuBase = updateProductRequest.skuBase
            skuCode = newSkuCode
            productName = updateProductRequest.productName
            price = updateProductRequest.price
            description = updateProductRequest.description
            size = updateProductRequest.size
            color = updateProductRequest.color
            amount = updateProductRequest.amount ?: 0
            status = updateProductRequest.status
            productType = updateProductRequest.productType
            images = updateProductRequest.images
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

    override fun getProductsBySkuBase(skuBase: String): List<ProductResponse> {
        val products = productRepository.findBySkuBase(skuBase)

        val productIds = products.map { it.productId }
        val reviews = reviewRepository.findByProductIdIn(productIds)

        return products.groupBy { it.skuBase }.map { (skuBase, productList) ->
            val product = productList.first()

            val relevantReviews = reviews.filter { review ->
                productList.any { it.productId == review.productId }
            }
            val totalRating = relevantReviews.size
            val averageRating = if (totalRating > 0) {
                relevantReviews.sumOf { it.rating.toDouble() } / totalRating
            } else {
                0.0
            }

            val formattedAverageRating = String.format("%.1f", averageRating).toFloat()

            ProductResponse(
                skuBase = product.skuBase,
                productName = product.productName,
                price = product.price,
                description = product.description,
                status = product.status,
                productType = product.productType,
                averageRating = formattedAverageRating,
                totalRating = totalRating,
                sizes = productList.mapNotNull { it.size }.distinct(),
                colors = productList.map { it.color }.distinct(),
                images = productList.flatMap { it.images }.distinct(),
            )
        }
    }

    override fun getProductByProductType(productType: String): List<Product> {
        return productRepository.findByProductType(productType)
    }

    fun getColorCode(color: String): String {
        return colorMap[color.lowercase()] ?: "UN" // Nếu không tìm thấy màu thì trả về mã màu mặc định "UN"
    }

    // Phương thức để tạo SKU code
    fun generateSkuCode(skuBase: String, size: String?, color: String): String {
        val colorCode = getColorCode(color)  // Lấy mã màu từ bảng màu
        val sizePart = size?.let { "-$it" } ?: ""
        return "$skuBase$sizePart-$colorCode"
    }

    // Danh sách các màu và mã màu tương ứng
    val colorMap = mapOf(
        "đen" to "BK",  // Màu đen
        "xanh dương" to "BL",   // Màu xanh dương
        "đỏ" to "RD",    // Màu đỏ
        "xanh lá" to "GR",  // Màu xanh lá cây
        "vàng" to "YE", // Màu vàng
        "trắng" to "WH",  // Màu trắng
        "xám" to "GY",   // Màu xám
        "hồng" to "PK",    // Màu hồng
        "bạc" to "GRY",
        "nâu" to "BRW",
        "cam" to "ORNG",
        "be" to "BEIGE"
    )

    override fun getProductBySkuBaseAndAttributes(skuBase: String, color: String, size: String?): Product? {
        val products = productRepository.findBySkuBase(skuBase)

        val isAccessory = products.any { it.productType == "Phụ kiện" }

        if (isAccessory && size != null) {
            throw CustomException("Size parameter is not allowed for accessory products.", HttpStatus.BAD_REQUEST)
        }

        return if (isAccessory) {
            productRepository.findBySkuBaseAndColor(skuBase, color)
        } else {
            productRepository.findBySkuBaseAndColorAndSize(skuBase, color, size)
        }
    }

    override fun getListSizeBySkuBase(skuBase: String): List<String?> {
        val products = productRepository.findBySkuBase(skuBase)

        return products.map { it.size }.distinct().takeIf { it.isNotEmpty() } ?: emptyList()
    }

    override fun getListColorBySkuBase(skuBaseName: String): List<String> {
        val products = productRepository.findBySkuBase(skuBaseName)

        return products.map { it.color }.distinct().takeIf { it.isNotEmpty() } ?: emptyList()
    }

    override fun addToWishlist(userId: String, productId: String) {
        val existingUser = userRepository.findById(userId).orElseThrow {
            CustomException("User not found", HttpStatus.NOT_FOUND)
        }
        val existingProduct = productRepository.findById(productId.toString()).orElseThrow {
            CustomException("Product not found", HttpStatus.NOT_FOUND)
        }
        val wishlist = wishlistRepository.findByUserId(userId) ?: Wishlist(userId = existingUser.userId)
        if (!wishlist.productIds.contains(productId)) {
            wishlist.productIds.add(productId)
            wishlistRepository.save(wishlist)
        }
    }

    override fun getWishlist(userId: String): List<Product> {
        val wishlist = wishlistRepository.findByUserId(userId) ?: return emptyList()
        return productRepository.findAllById(wishlist.productIds)
    }

    override fun addReview(productId: ObjectId, reviewRequest: ReviewRequest): Review {
        val existingUser = userRepository.findById(reviewRequest.userId).orElseThrow {
            CustomException("User not found", HttpStatus.NOT_FOUND)
        }
        val existingProduct = productRepository.findById(productId.toString()).orElseThrow {
            CustomException("Product not found", HttpStatus.NOT_FOUND)
        }
        val review = Review(
            productId = existingProduct.productId,
            userId = existingUser.userId,
            rating = reviewRequest.rating,
            comment = reviewRequest.comment,
            images = reviewRequest.images)
        return reviewRepository.save(review)
    }

    override fun getReviewsByProductId(productId: ObjectId): List<Review> {
        return reviewRepository.findAll().filter { it.productId == productId }
    }

    override fun getReviewsBySkuBase(skuBase: String): List<ReviewResponse> {
        // Fetch products with the given skuBase
        val products = productRepository.findBySkuBase(skuBase)

        // Extract productIds
        val productIds = products.map { it.productId }

        // Fetch reviews for those productIds
        val reviews = reviewRepository.findAll().filter { it.productId in productIds }

        // Map Review to ReviewResponse
        return reviews.map { review ->
            val product = products.find { it.productId == review.productId }
            ReviewResponse(
                id = review.id.toString(),
                productId = review.productId.toString(),
                userId = review.userId,
                userName = userRepository.findById(review.userId).orElse(null).username, // Example function to fetch user name
                rating = review.rating,
                comment = review.comment,
                images = product?.images ?: emptyList()
            )
        }
    }

}