package org.waitforme.backend.repository.user

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.waitforme.backend.entity.user.UserWithdraw

@Repository
interface UserWithdrawRepository : CrudRepository<UserWithdraw, Int>
