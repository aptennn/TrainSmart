package com.example.trainsmart.data

// МЕНЯТЬ ОПАСНО, ИСП В WORKOUT CREATE

data class Exercise(
    var id: String = "id",
    val name : String = "Name",
    val photoUrl : String = "Url",
    val description : String = "Description",
    val technique : String = "technique"
)
