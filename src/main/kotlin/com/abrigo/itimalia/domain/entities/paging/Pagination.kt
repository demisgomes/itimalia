package com.abrigo.itimalia.domain.entities.paging

data class Pagination(
    val page: Int,
    val limit: Int,
    val nextPage: Int?,
    val total: Int,
    val numberOfPages: Int,
    val orderBy: OrderBy,
    val direction: Direction
)
