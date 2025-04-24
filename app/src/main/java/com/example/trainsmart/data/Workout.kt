package com.example.trainsmart.data

data class Workout(
    val name: String = "Name",
    val photoUrl: String = "Url",
    val author: String = "Author",
    val exercises: Map<String, String> = mapOf("uid" to "times-repetitions"), // подходы-повторения
    val type: String = "Type",
    val likes: List<String> = mutableListOf()
)
