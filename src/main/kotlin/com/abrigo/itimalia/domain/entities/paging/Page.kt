package com.abrigo.itimalia.domain.entities.paging

data class Page<T>(val content: List<T>, val pagination: Pagination)
