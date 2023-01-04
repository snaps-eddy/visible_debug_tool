package com.webling.debuglibrary.domain.log.usecase

import kotlinx.coroutines.flow.Flow

interface UseCase<Type : Any, in Params> {

    operator fun invoke(params: Params): Flow<Type>

}