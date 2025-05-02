package com.example.trainsmart.data

data class Workout(
    val id: String = "id",
    val name: String = "Name",
    val photoUrl: String = "Url",
    val author: String = "Author",
    val exercises: Map<String, String> = mapOf("uid" to "times-repetitions"),
    val type: String = "Type",
    val likes: List<String> = mutableListOf(),
    val dislikes: List<String> = mutableListOf()
)
