package org.waitforme.backend.service

import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.waitforme.backend.entity.shop.ShopImage
import org.waitforme.backend.enums.ImageType
import org.waitforme.backend.model.request.CreateShopRequest
import org.waitforme.backend.model.response.shop.*
import org.waitforme.backend.repository.shop.ShopImageRepository
import org.waitforme.backend.repository.shop.ShopRepository
import org.waitforme.backend.util.AwsUtil
import org.waitforme.backend.util.ImageUtil
import org.waitforme.backend.util.StringUtil
import org.webjars.NotFoundException
import java.security.InvalidParameterException
import java.time.LocalDate


@Service
class ShopService(
    private val shopRepository: ShopRepository,
    private val shopImageRepository: ShopImageRepository,
    private val imageUtil: ImageUtil,
) {
    fun getShopList(pageRequest: PageRequest): List<ShopListResponse> {
        val now = LocalDate.now()
        return shopRepository.findShopList(
            startedAt = now,
            endedAt = now,
            pageable = pageRequest
        ).map { it.toResponse() }
    }

    fun getShopDetail(id: Int): FrontShopDetailResponse {
        return shopRepository.findByIdAndIsShow(id)?.let { shop ->
            val images = shopImageRepository.findByShopIdAndIsShowOrderByOrderNo(shop.id)
                .map { it.toResponse() }
            shop.toFrontDetailResponse(images)
        } ?: throw NotFoundException("팝업을 찾을 수 없습니다.")
    }

    @Transactional
    fun createShop(createShopRequest: CreateShopRequest): ShopDetailResponse {
        if (createShopRequest.startedAt < LocalDate.now() ||
                createShopRequest.startedAt >= createShopRequest.endedAt) {
            throw InvalidParameterException(
                "팝업스토어의 기간 설정이 잘못되었습니다. startedAt: ${createShopRequest.startedAt}, endedAt: ${createShopRequest.endedAt}"
            )
        }

        // TODO: 등록된 사업자 유저인지 체크하는 로직 추가 필요

        if(!StringUtil.validateRegistrationNumber(createShopRequest.registrationNumber)) {
            throw InvalidParameterException("비정상적인 사업자 번호입니다.")
        }

        val shop = shopRepository.save(createShopRequest.toEntity())
        val imageList = mutableListOf<ShopImage>()

        createShopRequest.subImages?.map {
            imageList.add(
                ShopImage(
                    shopId = shop.id,
                    imageType = ImageType.DETAIL,
                    imagePath = imageUtil.uploadFile(it)
                )
            )
        } ?: throw InvalidParameterException("한장 이상의 상세 이미지를 등록해주세요.")

        imageList.add(
            ShopImage(
                shopId = shop.id,
                imageType = ImageType.MAIN,
                imagePath = imageUtil.uploadFile(createShopRequest.mainImage)
            )
        )
        val shopImageInfo = shopImageRepository.saveAll(imageList)

        return shop.toDetailResponse(imageInfo = shopImageInfo as List<ShopImage>)
    }
}