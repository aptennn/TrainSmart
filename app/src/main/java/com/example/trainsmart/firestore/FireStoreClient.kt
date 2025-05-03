package com.example.trainsmart.firestore

import android.util.Log
import com.example.trainsmart.data.Exercise
import com.example.trainsmart.data.User
import com.example.trainsmart.data.Workout
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import com.example.trainsmart.ui.workouts.Workout as UiWorkout

class FireStoreClient {

    private val tag = "FirestoreClient: "

    private val db = FirebaseFirestore.getInstance()
    private val collectionUsers = "users"
    private val collectionBasicEx = "basic-exercises"
    private val collectionBasicWorkouts = "basic-workouts"
    private val collectionPublishedWorkouts = "published-workouts"
    val likeScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    enum class LikeType {
        LIKED,         // Поставить лайк
        DISLIKED,      // Поставить дизлайк
        UNLIKED,       // Убрать лайк
        UNDISLIKED     // Убрать дизлайк
    }

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

    fun getUserNew(id: String, onResult: (User?) -> Unit) {
        db.collection(collectionUsers).get()
            .addOnSuccessListener { result ->
                var foundUser: User? = null
                for (document in result) {
                    if (document.data["id"] == id) {
                        foundUser = document.data.toUser()
                        println(tag + "found user with id: ${foundUser.id}")
                        break
                    }
                }
                if (foundUser == null) {
                    println(tag + "user not found: $id")
                }
                onResult(foundUser)
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                println(tag + "error getting user: ${e.message}")
                onResult(null)
            }
    }

    fun getHistoryByUserId(userId: String): Flow<Map<String, List<String>>?> {
        return callbackFlow {
            db.collection(collectionUsers).document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val history = document.get("history") as? Map<String, List<String>>
                        Log.d(tag, "History retrieved for user $userId: ${history?.size} entries")
                        trySend(history)
                    } else {
                        Log.d(tag, "User document not found: $userId")
                        trySend(null)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(tag, "Error getting user history", e)
                    trySend(null)
                }
            awaitClose {}
        }
    }

    private fun User.ToHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "id" to id,
            "email" to email,
            "username" to username,
            "history" to history,
        )
    }

    private fun Map<String, Any>.toUser(): User {
        return User(
            id = this["id"] as String,
            email = this["email"] as String,
            username = this["username"] as String,
            history = (this["history"] as? Map<String, List<String>>) ?: emptyMap()
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

    fun getWorkoutsByIds(workoutsIds: List<String>): Flow<List<Workout>> {
        return combine(
            getBasicWorkouts(),
            getPublishedWorkouts()
        ) { basic, published ->
            val allWorkouts = basic + published
            workoutsIds.mapNotNull { id ->
                allWorkouts[id]?.copy(id = id)
            }
        }
    }

    fun getBasicWorkouts(
    ): Flow<Map<String, Workout?>> {
        return callbackFlow {
            db.collection(collectionBasicWorkouts).get().addOnSuccessListener { result ->

                val mapW = mutableMapOf<String, Workout?>()

                for (document in result) {
                    mapW[document.id] = document.toWorkout()
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
                    mapW[document.id] = document.toWorkout()
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

    fun isLikedByMe(workout: UiWorkout?, uid: String): String {
        if (workout != null) {
            if (workout.dislikes.contains(uid)) {
                println(uid + ": id:" + workout.dislikes.contains(uid) + " : " + workout.title + " " + workout.likes)
                return "DISLIKED"
            }
            else if (workout.likes.contains(uid)) {
                return "LIKED"
            }
        }
        return "NONE"
    }




    fun updateLikesFireAndForget(
        workoutId: String,
        uid: String,
        likeType: LikeType
    ) {
        likeScope.launch {
            val success = updateLikes(workoutId, uid, likeType)
            Log.d("Firestore", "updateLikes complete: $success")
            // Здесь нельзя обновлять UI напрямую, так что просто логируем
        }
    }

    suspend fun updateLikes(workoutId: String, uid: String, likeType: LikeType): Boolean =
        suspendCancellableCoroutine { cont ->

            cont.invokeOnCancellation {
                // ничего не делаем — просто игнорируем
            }
            val db = FirebaseFirestore.getInstance()
            val docRefPublished = db.collection(collectionPublishedWorkouts).document(workoutId)
            val docRefBasic = db.collection(collectionBasicWorkouts).document(workoutId)

            val publishedUpdates = mutableListOf<Task<Void>>()
            val basicUpdates = mutableListOf<Task<Void>>()

            when (likeType) {
                LikeType.LIKED -> {
                    publishedUpdates += docRefPublished.update(
                        mapOf(
                            "likes" to FieldValue.arrayUnion(uid),
                            "dislikes" to FieldValue.arrayRemove(uid)
                        )
                    )
                    basicUpdates += docRefBasic.update(
                        mapOf(
                            "likes" to FieldValue.arrayUnion(uid),
                            "dislikes" to FieldValue.arrayRemove(uid)
                        )
                    )
                }

                LikeType.DISLIKED -> {
                    publishedUpdates += docRefPublished.update(
                        mapOf(
                            "likes" to FieldValue.arrayRemove(uid),
                            "dislikes" to FieldValue.arrayUnion(uid)
                        )
                    )
                    basicUpdates += docRefBasic.update(
                        mapOf(
                            "likes" to FieldValue.arrayRemove(uid),
                            "dislikes" to FieldValue.arrayUnion(uid)
                        )
                    )
                }

                LikeType.UNLIKED -> {
                    publishedUpdates += docRefPublished.update("likes", FieldValue.arrayRemove(uid))
                    basicUpdates += docRefBasic.update("likes", FieldValue.arrayRemove(uid))
                }

                LikeType.UNDISLIKED -> {
                    publishedUpdates += docRefPublished.update("dislikes", FieldValue.arrayRemove(uid))
                    basicUpdates += docRefBasic.update("dislikes", FieldValue.arrayRemove(uid))
                }
            }

            val allUpdates = publishedUpdates + basicUpdates

            Tasks.whenAllComplete(allUpdates)
                .addOnCompleteListener {
                    val success = allUpdates.any { it.isSuccessful }
                    cont.resume(success)
                }
                .addOnFailureListener { e ->
                    cont.resume(false)
                }
        }

    /*suspend fun updateLikes(workoutId: String, uid: String, likeType: LikeType): Boolean =
        suspendCancellableCoroutine { cont ->
            val db = FirebaseFirestore.getInstance()
            val docRefPublished = db.collection(collectionPublishedWorkouts).document(workoutId)
            val docRefBasic = db.collection(collectionBasicWorkouts).document(workoutId)

            val batch = db.batch()

            when (likeType) {
                LikeType.LIKED -> {
                    batch.update(docRefPublished, mapOf(
                        "likes" to FieldValue.arrayUnion(uid),
                        "dislikes" to FieldValue.arrayRemove(uid)
                    ))
                    batch.update(docRefBasic, mapOf(
                        "likes" to FieldValue.arrayUnion(uid),
                        "dislikes" to FieldValue.arrayRemove(uid)
                    ))
                }

                LikeType.DISLIKED -> {
                    batch.update(docRefPublished, mapOf(
                        "likes" to FieldValue.arrayRemove(uid),
                        "dislikes" to FieldValue.arrayUnion(uid)
                    ))
                    batch.update(docRefBasic, mapOf(
                        "likes" to FieldValue.arrayRemove(uid),
                        "dislikes" to FieldValue.arrayUnion(uid)
                    ))
                }

                LikeType.UNLIKED -> {
                    batch.update(docRefPublished, "likes", FieldValue.arrayRemove(uid))
                    batch.update(docRefBasic, "likes", FieldValue.arrayRemove(uid))
                }

                LikeType.UNDISLIKED -> {
                    batch.update(docRefPublished, "dislikes", FieldValue.arrayRemove(uid))
                    batch.update(docRefBasic, "dislikes", FieldValue.arrayRemove(uid))
                }
            }

            batch.commit()
                .addOnSuccessListener { cont.resume(true) }
                .addOnFailureListener { cont.resume(false) }
        }
    */


    /*fun updateLikes(workoutId: String, uid: String, likeType: LikeType): Flow<Boolean> =
    callbackFlow {
        val db = FirebaseFirestore.getInstance()
        val docRefPublished = db.collection(collectionPublishedWorkouts).document(workoutId)
        val docRefBasic = db.collection(collectionBasicWorkouts).document(workoutId)

        Log.d(
            "Firestore",
            "Updating likeType for workout: $workoutId | user: $uid | likeType: $likeType"
        )

        val publishedUpdates = mutableListOf<Task<Void>>()
        val basicUpdates = mutableListOf<Task<Void>>()

        when (likeType) {
            LikeType.LIKED -> {
                publishedUpdates += docRefPublished.update(
                    mapOf(
                        "likes" to FieldValue.arrayUnion(uid),
                        "dislikes" to FieldValue.arrayRemove(uid)
                    )
                )
                basicUpdates += docRefBasic.update(
                    mapOf(
                        "likes" to FieldValue.arrayUnion(uid),
                        "dislikes" to FieldValue.arrayRemove(uid)
                    )
                )
            }

            LikeType.DISLIKED -> {
                publishedUpdates += docRefPublished.update(
                    mapOf(
                        "likes" to FieldValue.arrayRemove(uid),
                        "dislikes" to FieldValue.arrayUnion(uid)
                    )
                )
                basicUpdates += docRefBasic.update(
                    mapOf(
                        "likes" to FieldValue.arrayRemove(uid),
                        "dislikes" to FieldValue.arrayUnion(uid)
                    )
                )
            }

            LikeType.UNLIKED -> {
                publishedUpdates += docRefPublished.update(
                    "likes",
                    FieldValue.arrayRemove(uid)
                )
                basicUpdates += docRefBasic.update("likes", FieldValue.arrayRemove(uid))
            }

            LikeType.UNDISLIKED -> {
                publishedUpdates += docRefPublished.update(
                    "dislikes",
                    FieldValue.arrayRemove(uid)
                )
                basicUpdates += docRefBasic.update("dislikes", FieldValue.arrayRemove(uid))
            }
        }

        val allUpdates = publishedUpdates + basicUpdates

        Tasks.whenAllComplete(allUpdates)
            .addOnCompleteListener {
                val success = allUpdates.any { it.isSuccessful }
                Log.d(
                    "Firestore",
                    "Update result - Published: ${publishedUpdates.all { it.isSuccessful }}, Basic: ${basicUpdates.all { it.isSuccessful }}"
                )
                trySend(success)
                close()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Combined task failed", e)
                trySend(false)
                close()
            }

        awaitClose {
            Log.d("Firestore", "Flow closed for updateLikes()")
        }
    }*/

        fun addWorkoutToHistory(userId: String, date: String, workoutId: String): Flow<Boolean> {
            return callbackFlow {
                val userRef = db.collection(collectionUsers).document(userId)

                db.runTransaction { transaction ->
                    val snapshot = transaction.get(userRef)
                    val history =
                        snapshot.get("history") as? Map<String, List<String>> ?: emptyMap()

                    val workoutsForDate = history[date] ?: emptyList()
                    if (workoutsForDate.contains(workoutId)) {
                        throw FirebaseFirestoreException(
                            "Workout already exists in history",
                            FirebaseFirestoreException.Code.ABORTED
                        )
                    }

                    val updates = hashMapOf<String, Any>(
                        "history.$date" to FieldValue.arrayUnion(workoutId)
                    )
                    transaction.update(userRef, updates)

                    true
                }.addOnSuccessListener { result ->
                    if (result) {
                        Log.d(tag, "Workout $workoutId added to history for date $date")
                        trySend(true)
                    }
                }.addOnFailureListener { e ->
                    when (e) {
                        is FirebaseFirestoreException
                            -> if (e.code == FirebaseFirestoreException.Code.ABORTED) {
                            Log.d(tag, "Duplicate workout: $workoutId")
                            trySend(false)
                        } else {
                            Log.e(tag, "Error adding workout to history", e)
                            trySend(false)
                        }

                        else -> {
                            Log.e(tag, "Unexpected error", e)
                            trySend(false)
                        }
                    }
                }

                awaitClose {}
            }
        }

        private fun Workout.ToHashMap(): HashMap<String, Any> {
            return hashMapOf(
                "name" to name,
                "photoUrl" to photoUrl,
                "author" to author,
                "exercises" to exercises,
                "type" to type,
                "likes" to likes,
                "dislikes" to dislikes
            )
        }

        private fun Map<String, Any>.toWorkout(): Workout {
            return Workout(
                name = this["name"] as String,
                photoUrl = this["photoUrl"] as String,
                author = this["author"] as String,
                exercises = this["exercises"] as Map<String, String>,
                type = this["type"] as String,
                likes = this["likes"] as List<String>,
                dislikes = this["dislikes"] as List<String>
            )
        }

        private fun DocumentSnapshot.toWorkout(): Workout {
            return Workout(
                id = this.id,
                name = getString("name") ?: "",
                photoUrl = getString("photoUrl") ?: "",
                author = getString("author") ?: "",
                exercises = get("exercises") as? Map<String, String> ?: emptyMap(),
                type = getString("type") ?: "",
                likes = get("likes") as? List<String> ?: emptyList(),
                dislikes = get("dislikes") as? List<String> ?: emptyList()
            )
        }
    }
