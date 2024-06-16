package org.waitforme.backend.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.waitforme.backend.entity.shop.ShopImage
import org.waitforme.backend.enums.ImageType
import org.waitforme.backend.enums.ShopSorter
import org.waitforme.backend.enums.UserRole
import org.waitforme.backend.model.LoginUser
import org.waitforme.backend.model.request.CreateShopRequest
import org.waitforme.backend.model.request.UpdateShopRequest
import org.waitforme.backend.model.response.shop.*
import org.waitforme.backend.repository.shop.ShopImageRepository
import org.waitforme.backend.repository.shop.ShopRepository
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
    fun getShopList(loginUser: LoginUser?, title: String?, sorter: ShopSorter, pageRequest: PageRequest): Page<ShopListResponse> {
        val now = LocalDate.now()
        return shopRepository.findShopList(
            userId = loginUser?.id,
            title = title,
            startedAt = now,
            endedAt = now,
            sorter = sorter,
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

    fun getOwnerShopList(loginUser: LoginUser, title: String?, isEnd: Boolean, pageRequest: PageRequest): Page<OwnerShopListResponse> {
        val now = LocalDate.now()
        if (loginUser.authorities != UserRole.OWNER.authorities) {
            throw InvalidParameterException(
                "점주 (유저)만 사용가능한 기능입니다."
            )
        }
        return shopRepository.findOwnerShopList(
            userId = loginUser.id,
            title = title,
            startedAt = now,
            endedAt = now,
            isEnd = isEnd,
            pageable = pageRequest
        ).map { it.toResponse() }
    }

    @Transactional
    fun createShop(createShopRequest: CreateShopRequest): ShopDetailResponse {
        if (checkPeriod(createShopRequest.startedAt, createShopRequest.endedAt)) {
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

        var orderNo = 1
        createShopRequest.subImages?.map {
            imageList.add(
                ShopImage(
                    shopId = shop.id,
                    imageType = ImageType.DETAIL,
                    imagePath = imageUtil.uploadFile(it),
                    orderNo = orderNo++
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

    @Transactional
    fun updateShop(id: Int, updateShopRequest: UpdateShopRequest): ShopDetailResponse {
        var shop = shopRepository.findByIdAndIsShow(id) ?: throw NotFoundException(
            "팝업 정보를 찾을 수 없습니다."
        )

        shop.update(updateShopRequest)

        if (checkPeriod(shop.startedAt, shop.endedAt)) {
            throw InvalidParameterException(
                "팝업스토어의 기간 설정이 잘못되었습니다. startedAt: ${shop.startedAt}, endedAt: ${shop.endedAt}"
            )
        }

        // TODO: 등록된 사업자 유저인지 체크하는 로직 추가 필요

        if(!StringUtil.validateRegistrationNumber(shop.registrationNumber)) {
            throw InvalidParameterException("비정상적인 사업자 번호입니다.")
        }

        shopRepository.save(shop)
        val imageList = mutableListOf<ShopImage>()

        updateShopRequest.subImages?.let { images ->
            var orderNo = 1

            shopImageRepository.updateByShopIdAndTypeAndIsShow(shop.id, ImageType.DETAIL, false)
            images.map {
                imageList.add(
                    ShopImage(
                        shopId = shop.id,
                        imageType = ImageType.DETAIL,
                        imagePath = imageUtil.uploadFile(it),
                        orderNo = orderNo++
                    )
                )
            }
        }

        updateShopRequest.mainImage?.let {
            shopImageRepository.updateByShopIdAndTypeAndIsShow(shop.id, ImageType.MAIN, false)
            imageList.add(
                ShopImage(
                    shopId = shop.id,
                    imageType = ImageType.MAIN,
                    imagePath = imageUtil.uploadFile(it)
                )
            )
        }

        val shopImageInfo = imageList.takeIf { it.isNotEmpty() }?.let {
            shopImageRepository.saveAll(it)
        } ?: shopImageRepository.findByShopIdAndIsShowOrderByOrderNo(shop.id)

        return shop.toDetailResponse(imageInfo = shopImageInfo as List<ShopImage>)
    }

    fun changeExposure(id: Int, isShow: Boolean): Boolean {
        val shop = shopRepository.findByIdOrNull(id)
            ?: throw NotFoundException("팝업스토어를 찾을 수 없습니다.")

        shop.updateIsShow(isShow)
        return shopRepository.save(shop) != null
    }

    private fun checkPeriod(startedAt: LocalDate, endedAt: LocalDate): Boolean =
        (startedAt < LocalDate.now() || startedAt >= endedAt)
}