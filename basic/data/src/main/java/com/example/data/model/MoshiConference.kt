package com.example.data.model

import com.example.domain.model.Conference

data class MoshiConference(
    val name: String? = null,
    val link: String? = null,
    val start: String? = null,
    val end: String? = null,
    val location: String? = null
) {
    fun convert() = Conference(name, link, start, end, location)
}