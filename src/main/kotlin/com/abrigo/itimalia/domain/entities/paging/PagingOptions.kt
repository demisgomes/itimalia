package com.abrigo.itimalia.domain.entities.paging

data class PagingOptions(val limit: Int = 10, val page: Int = 1, val orderBy: OrderBy = OrderBy.ID, val direction: Direction = Direction.ASC)
