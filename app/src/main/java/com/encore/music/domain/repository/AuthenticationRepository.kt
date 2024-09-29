package com.encore.music.domain.repository

import com.encore.music.domain.model.authentication.User
import kotlinx.coroutines.flow.Flow

interface AuthenticationRepository {
    val currentUserId: String?

    val hasUser: Boolean

    val currentUser: Flow<User?>

    suspend fun getIdToken(): String?

    suspend fun createUserWithEmailAndPassword(
        name: String,
        email: String,
        password: String,
    ): User?

    suspend fun signInWithEmailAndPassword(
        email: String,
        password: String,
    ): User?

    suspend fun signInWithGoogle(idToken: String): User?

    suspend fun sendPasswordResetEmail(email: String)

    fun signOut()
}
