package org.waitforme.backend.repository.wait

object WaitingQuery {
    const val findOrderNoTop1 = """
        SELECT
            w.order_no
        FROM
            waiting AS w
        WHERE
            w.shop_id = :shopId
        ORDER BY
            w.order_no DESC
        LIMIT 1
    """

    const val findWaitingList = """
        SELECT
            u.name,
            u.phone_number AS user_phone,
            w.phone_number AS non_user_phone,
            w.call_count,
            w.head_count,
            w.order_no
        FROM
            waiting AS w
        LEFT JOIN
            user AS u ON u.id = w.user_id
        WHERE
            w.shop_id = :shopId
        AND
            w.status = 'WAIT'
        GROUP BY
            w.user_id, w.order_no, w.phone_number
        ORDER BY
            w.order_no ASC
        LIMIT
            :limit
        OFFSET
            :start
    """

    const val countWaitingList = """
        SELECT
            count(1)
        FROM
            waiting AS w
        WHERE
            w.shop_id = :shopId
        AND
            w.status = 'WAIT'
        GROUP BY
            w.user_id, w.order_no, w.phone_number
    """

    const val findByUserId = """
        SELECT
            w.shop_id,
            s.name
            w.status,
            w.created_at,
            w.order_no,
            w.head_count
        FROM
            waiting AS w
        INNER JOIN
            shop AS s ON s.id = w.shop_id
        WHERE
            w.user_id = :userId
        ORDER BY FIELD (w.status, 'WAIT'), w.created_at DESC
        LIMIT :limit,
        OFFSET :start
    """
}