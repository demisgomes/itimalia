package com.abrigo.itimalia.domain.entities.paging

data class PagingOptions(val limit: Int = 10, val page: Int = 1, val orderBy: String = "modification_date", val direction: Direction = Direction.ASC)
