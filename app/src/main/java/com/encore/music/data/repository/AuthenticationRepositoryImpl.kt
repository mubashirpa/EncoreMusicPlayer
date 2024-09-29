package com.encore.music.data.repository

import android.net.Uri
import com.encore.music.domain.model.authentication.User
import com.encore.music.domain.repository.AuthenticationRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AuthenticationRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
) : AuthenticationRepository {
    override val currentUserId: String?
        get() = firebaseAuth.currentUser?.uid

    override val hasUser: Boolean
        get() = firebaseAuth.currentUser != null

    override val currentUser: Flow<User?>
        get() =
            callbackFlow {
                val listener =
                    FirebaseAuth.AuthStateListener { auth ->
                        this.trySend(
                            auth.currentUser?.let { user ->
                                User(
                                    email = user.email.orEmpty(),
                                    id = user.uid,
                                    name = user.displayName.orEmpty(),
                                    photoUrl = user.photoUrl.toString(),
                                    verified = user.isEmailVerified,
                                )
                            },
                        )
                    }
                firebaseAuth.addAuthStateListener(listener)
                awaitClose { firebaseAuth.removeAuthStateListener(listener) }
            }

    override suspend fun getIdToken(): String? =
        firebaseAuth.currentUser
            ?.getIdToken(true)
            ?.await()
            ?.token

    override suspend fun createUserWithEmailAndPassword(
        name: String,
        email: String,
        password: String,
    ): User? {
        val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        return authResult.user?.let { user ->
            user.updateUserProfile(name)
            User(
                email = user.email.orEmpty(),
                id = user.uid,
                name = user.displayName.orEmpty(),
                photoUrl = user.photoUrl.toString(),
                verified = user.isEmailVerified,
            )
        }
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String,
    ): User? {
        val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        return authResult.user?.let { user ->
            User(
                email = user.email.orEmpty(),
                id = user.uid,
                name = user.displayName.orEmpty(),
                photoUrl = user.photoUrl.toString(),
                verified = user.isEmailVerified,
            )
        }
    }

    override suspend fun signInWithGoogle(idToken: String): User? {
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        val authResult = firebaseAuth.signInWithCredential(firebaseCredential).await()
        return authResult.user?.let { user ->
            User(
                email = user.email.orEmpty(),
                id = user.uid,
                name = user.displayName.orEmpty(),
                photoUrl = user.photoUrl.toString(),
                verified = user.isEmailVerified,
            )
        }
    }

    override suspend fun sendPasswordResetEmail(email: String) {
        firebaseAuth.sendPasswordResetEmail(email).await()
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }

    private suspend fun FirebaseUser.updateUserProfile(
        displayName: String,
        photoUrl: String? = null,
    ) {
        val profileUpdates =
            userProfileChangeRequest {
                this.displayName = displayName
                photoUrl?.let {
                    this.photoUri = Uri.parse(photoUrl)
                }
            }

        updateProfile(profileUpdates).await()
    }
}
