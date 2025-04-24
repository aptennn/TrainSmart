package com.example.trainsmart.firestore

import android.util.Log
import com.example.trainsmart.data.Exercise
import com.example.trainsmart.data.User
import com.example.trainsmart.data.Workout
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import com.example.trainsmart.ui.workouts.Workout as UiWorkout

class FireStoreClient {

    private val tag = "FirestoreClient: "

    private val db = FirebaseFirestore.getInstance()
    private val collectionUsers = "users"
    private val collectionBasicEx = "basic-exercises"
    private val collectionBasicWorkouts = "basic-workouts"
    private val collectionPublishedWorkouts = "published-workouts"

    /**
     *   /users/ collection interface
     */

    fun insertUser(
        user: User
    ): Flow<String?> {
        return callbackFlow {
            db.collection(collectionUsers).document(user.id).set(user.ToHashMap())
                .addOnSuccessListener { document ->
                    println(tag + "insert user with id: ${user.id}")

                    CoroutineScope(Dispatchers.IO).launch {
                        updateUser(user).collect {}
                    }
                    trySend(user.id)
                }.addOnFailureListener { e ->
                    e.printStackTrace()
                    println(tag + "error inserting user: ${e.message}")
                    trySend(null)
                }
            awaitClose {}
        }
    }

    fun updateUser(
        user: User
    ): Flow<Boolean> {
        return callbackFlow {
            db.collection(collectionUsers).document(user.id).set(user.ToHashMap())
                .addOnSuccessListener { document ->
                    println(tag + "update user with id: ${user.id}")
                    trySend(true)
                }.addOnFailureListener { e ->
                    e.printStackTrace()
                    println(tag + "error updating user: ${e.message}")
                    trySend(false)
                }
            awaitClose {}
        }
    }

    fun getUser(
        id: String
    ): Flow<User?> {
        return callbackFlow {
            db.collection(collectionUsers).get().addOnSuccessListener { result ->

                var user: User? = null
                for (document in result) {
                    if (document.data["id"] == id) {

                        user = document.data.toUser()
                        println(tag + "found user with id: ${user.id}")
                        trySend(user)
                    }

                    if (user == null) {
                        println(tag + "user not found: $id")
                        trySend(null)
                    }
                }
            }.addOnFailureListener { e ->
                e.printStackTrace()
                println(tag + "error getting user: ${e.message}")
                trySend(null)
            }
            awaitClose {}
        }
    }

    private fun User.ToHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "id" to id, "email" to email, "username" to username
        )
    }

    private fun Map<String, Any>.toUser(): User {
        return User(
            id = this["id"] as String,
            email = this["email"] as String,
            username = this["username"] as String
        )
    }

    /** /basic-exercises/ interface
     *  only get() methods. It's is a built-in collection.
     */

    private fun Exercise.ToHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "name" to name,
            "photoUrl" to photoUrl,
            "description" to description,
            "technique" to technique
        )
    }

    private fun Map<String, Any>.toExercise(): Exercise {
        return Exercise(
            // no id = this["id"] as String,
            name = this["name"] as String,
            photoUrl = this["photoUrl"] as String,
            description = this["description"] as String,
            technique = this["technique"] as String
        )
    }

    fun getExercisesByIds(
        idS: List<String>
    ): Flow<List<Exercise?>?> {
        return callbackFlow {
            db.collection(collectionBasicEx).get().addOnSuccessListener { result ->

                val exercises = mutableListOf<Exercise>()
                for (document in result) {
                    if (idS.contains(document.id)) {

                        val exercise = document.toExercise()
                        exercises.add(exercise)
                        println(tag + "found exercise with id: ${document.id}")
                    }
                }
                if (exercises.isEmpty()) trySend(null)
                else trySend(exercises)
            }.addOnFailureListener { e ->
                e.printStackTrace()
                println(tag + "error getting exercises: ${e.message}")
                trySend(null)
            }
            awaitClose {}
        }
    }

    fun getAllExercises(
    ): Flow<List<Exercise?>> {
        return callbackFlow {
            db.collection(collectionBasicEx).get().addOnSuccessListener { result ->

                var exercises = mutableListOf<Exercise>()

                for (document in result) {

                    val exercise = document.toExercise()
                    exercise.id = document.id
                    exercises.add(exercise)

                }

                if (exercises.isEmpty()) {
                    println(tag + "exercises list is empty")
                    trySend(emptyList())
                } else {
                    println(tag + "exercises list not null")
                    trySend(exercises)
                }
            }.addOnFailureListener { e ->
                e.printStackTrace()
                println(tag + "error getting exercises: ${e.message}")
                trySend(emptyList())
            }
            awaitClose {}
        }
    }

    private fun DocumentSnapshot.toExercise(): Exercise {
        return Exercise(
            id = this.id,
            name = getString("name") ?: "",
            photoUrl = getString("photoUrl") ?: "",
            description = getString("description") ?: "",
            technique = getString("technique") ?: ""
        )
    }

    /** /basic-workouts/ interface
     *  only get() methods. It's is a built-in collection.
     */

    fun getAllWorkouts(): Flow<Map<String, Workout?>> {
        return combine(
            getBasicWorkouts(),
            getPublishedWorkouts()
        ) { basic, published ->
            mutableMapOf<String, Workout?>().apply {
                putAll(basic)
                putAll(published)
            }
        }
    }

    fun getBasicWorkouts(
    ): Flow<Map<String, Workout?>> {
        return callbackFlow {
            db.collection(collectionBasicWorkouts).get().addOnSuccessListener { result ->

                val mapW = mutableMapOf<String, Workout?>()

                for (document in result) {
                    val idW = document.id
                    val workout = document.data.toWorkout()
                    mapW[idW] = workout
                }

                if (mapW.isEmpty()) {
                    println(tag + "workouts map is empty")
                    trySend(emptyMap())
                } else {
                    println(tag + "workouts map not null")
                    trySend(mapW)
                }
            }.addOnFailureListener { e ->
                e.printStackTrace()
                println(tag + "error getting workouts: ${e.message}")
                trySend(emptyMap())
            }
            awaitClose {}
        }
    }

    fun getPublishedWorkouts(
    ): Flow<Map<String, Workout?>> {
        return callbackFlow {
            db.collection(collectionPublishedWorkouts).get().addOnSuccessListener { result ->

                val mapW = mutableMapOf<String, Workout?>()

                for (document in result) {
                    val idW = document.id
                    val workout = document.data.toWorkout()
                    mapW[idW] = workout
                }

                if (mapW.isEmpty()) {
                    println(tag + "workouts map is empty")
                    trySend(emptyMap())
                } else {
                    println(tag + "workouts map not null")
                    trySend(mapW)
                }
            }.addOnFailureListener { e ->
                e.printStackTrace()
                println(tag + "error getting workouts: ${e.message}")
                trySend(emptyMap())
            }
            awaitClose {}
        }
    }

    fun saveWorkout(workout: Workout): Flow<Boolean> {
        return callbackFlow {
            db.collection(collectionPublishedWorkouts).add(workout).addOnSuccessListener {
                println("🔥 Тренировка '${workout.name}' успешно сохранена!")
                trySend(true)
            }.addOnFailureListener { e ->
                println("❌ Ошибка при сохранении тренировки: ${e.message}")
                trySend(false)
            }
            awaitClose {}
        }
    }

    fun isLikedByMe(workout: UiWorkout?, uid: String): Boolean {
        if (workout != null) return workout.likes.contains(uid)
        return false
    }

    fun updateLikes(workoutId: String, uid: String, isLiked: Boolean): Flow<Boolean> =
        callbackFlow {
            val docRef = FirebaseFirestore.getInstance()
                .collection(collectionPublishedWorkouts) // change to your actual collection name
                .document(workoutId)

            Log.d(
                "Firestore",
                "Updating likes for workout: $workoutId | user: $uid | isLiked: $isLiked"
            )

            val updateTask = if (isLiked) {
                docRef.update("likes", FieldValue.arrayUnion(uid))
            } else {
                docRef.update("likes", FieldValue.arrayRemove(uid))
            }

            updateTask.addOnSuccessListener {
                Log.d("Firestore", "✅ Firestore update successful")
                trySend(true).isSuccess
                close()
            }.addOnFailureListener { e ->
                Log.e("Firestore", "❌ Firestore update failed", e)
                trySend(false).isSuccess
                close()
            }

            awaitClose {
                Log.d("Firestore", "Flow closed for updateLikes()")
            }
        }


    private fun Workout.ToHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "name" to name,
            "photoUrl" to photoUrl,
            "duration" to duration,
            "exercises" to exercises,
            "type" to type,
            "likes" to likes
        )
    }

    private fun Map<String, Any>.toWorkout(): Workout {
        return Workout(
            name = this["name"] as String,
            photoUrl = this["photoUrl"] as String,
            duration = this["duration"] as String,
            exercises = this["exercises"] as Map<String, String>,
            type = this["type"] as String,
            likes = this["likes"] as List<String>
        )
    }


}