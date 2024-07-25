package com.example.readerapp.model

data class MUser(
    val id: String?,
    val userId: String,
    val displayName: String,
    val avatarUrl: String,
    val quote: String,
    val profession: String
) {
    fun tomap(): MutableMap<String, Any> {
        return mutableMapOf(
            "userId" to this.userId,
            "displayName" to this.displayName,
            "avatar_url" to this.avatarUrl,
            "quote" to this.quote,
            "profession" to this.profession
        )
    }
}
