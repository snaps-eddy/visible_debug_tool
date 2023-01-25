package com.eddy.debuglibrary.domain.base

import kotlinx.coroutines.flow.Flow

interface UseCase<Type : Any, in Params> {

    operator fun invoke(params: Params): Flow<Type>

}