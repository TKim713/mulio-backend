package com.api.mulio_backend.service.impl

import com.api.mulio_backend.helper.exception.CustomException
import com.api.mulio_backend.helper.request.CreateProductRequest
import com.api.mulio_backend.model.Product
import com.api.mulio_backend.repository.ProductRepository
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
    private val productRepository: ProductRepository
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

    override fun getProductsBySkuBase(skuBase: String): List<Product> {
        return productRepository.findBySkuBase(skuBase)
    }

    override fun getProductByProductType(productType: String): List<Product> {
        return productRepository.findByProductType(productType)
    }

    fun getColorCode(color: String): String {
        return colorMap[color.lowercase()] ?: "UN" // Nếu không tìm thấy màu thì trả về mã màu mặc định "UN"
    }

    // Phương thức để tạo SKU code
    fun generateSkuCode(skuBase: String, size: String, color: String): String {
        val colorCode = getColorCode(color)  // Lấy mã màu từ bảng màu
        return "$skuBase-$size-$colorCode"
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
        "bạc" to "GRY"
    )

    override fun getProductBySkuBaseAndAttributes(skuBase: String, color: String, size: String): Product? {
        return productRepository.findBySkuBaseAndColorAndSize(skuBase, color, size)
    }

    override fun getListSizeBySkuBase(skuBase: String): List<String> {
        val products = productRepository.findBySkuBase(skuBase)

        return products.map { it.size }.distinct().takeIf { it.isNotEmpty() } ?: emptyList()
    }

    override fun getListColorBySkuBase(skuBaseName: String): List<String> {
        val products = productRepository.findBySkuBase(skuBaseName)

        return products.map { it.color }.distinct().takeIf { it.isNotEmpty() } ?: emptyList()
    }
}