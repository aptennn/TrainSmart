package com.example.trainsmart.data

data class Workout(
    val name : String = "Name",
    val photoUrl : String = "Url",
    val duration : String = "Duration",
    val exercisesIds: Map<String, String> = mapOf("uid" to "times-repetitions"), // подходы-повторения
    val description : String = "Description",
    val type : String = "Type"
)
