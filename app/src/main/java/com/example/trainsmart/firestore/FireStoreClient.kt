package com.example.trainsmart.firestore

import com.example.trainsmart.data.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class FireStoreClient {

    private val tag = "FirestoreClient: "

    private val db = FirebaseFirestore.getInstance()
    private val collection = "users"


    fun insertUser(
        user: User
    ): Flow<String?> {
        return callbackFlow {
            db.collection(collection)
                .document(user.id)
                .set(user.ToHashMap())
                .addOnSuccessListener { document ->
                    println(tag + "insert user with id: ${user.id}")

                    CoroutineScope(Dispatchers.IO).launch {
                        updateUser(user).collect{}
                    }
                    trySend(user.id)
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    println(tag + "error inserting user: ${e.message}")
                    trySend(null)
                }
            awaitClose{}
        }
    }

    fun updateUser(
        user: User
    ): Flow<Boolean> {
        return callbackFlow {
            db.collection(collection)
                .document(user.id)
                .set(user.ToHashMap())
                .addOnSuccessListener { document ->
                    println(tag + "update user with id: ${user.id}")
                    trySend(true)
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    println(tag + "error updating user: ${e.message}")
                    trySend(false)
                }
            awaitClose{}
        }
    }

    fun getUser(
        id: String
    ): Flow<User?> {
        return callbackFlow {
            db.collection(collection)
                .get()
                .addOnSuccessListener { result ->

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
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    println(tag + "error getting user: ${e.message}")
                    trySend(null)
                }
            awaitClose{}
        }
    }

    private fun User.ToHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "id" to id,
            "email" to email,
            "username" to username
        )
    }

    private fun Map<String, Any>.toUser(): User {
        return User(
            id = this["id"] as String,
            email = this["email"] as String,
            username = this["username"] as String
        )
    }

}