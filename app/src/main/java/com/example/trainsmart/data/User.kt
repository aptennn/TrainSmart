package com.example.trainsmart.data

data class User(
    val id: String = "id",
    val email: String = "email",
    val username: String = "username",
    val history: Map<String, List<String>> = emptyMap(),
)
