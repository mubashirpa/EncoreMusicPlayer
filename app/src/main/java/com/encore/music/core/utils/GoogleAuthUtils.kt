package com.encore.music.core.utils

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.encore.music.core.Constants
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import java.security.MessageDigest
import java.util.UUID
import com.encore.music.R.string as Strings

class GoogleAuthUtils(
    private val context: Context,
) {
    suspend fun initGoogleSignIn(
        filterByAuthorizedAccounts: Boolean = false,
        autoSelectEnabled: Boolean = false,
        onSignInSuccess: (idToken: String) -> Unit,
        onSignInFailure: (message: String) -> Unit,
    ) {
        val credentialManager = CredentialManager.create(context)

        val nonce = generateNonce()

        val googleIdOption: GetGoogleIdOption =
            GetGoogleIdOption
                .Builder()
                .setFilterByAuthorizedAccounts(filterByAuthorizedAccounts)
                .setServerClientId(Constants.GOOGLE_SERVER_CLIENT_ID)
                .setAutoSelectEnabled(autoSelectEnabled)
                .setNonce(nonce)
                .build()

        val request: GetCredentialRequest =
            GetCredentialRequest
                .Builder()
                .addCredentialOption(googleIdOption)
                .build()

        try {
            val result =
                credentialManager.getCredential(
                    request = request,
                    context = context,
                )
            handleSignIn(
                result = result,
                onSignInSuccess = onSignInSuccess,
                onSignInFailure = onSignInFailure,
            )
        } catch (e: GetCredentialException) {
            onSignInFailure(context.getString(Strings.error_auth_google_credential_exception))
        } catch (e: GetCredentialCancellationException) {
            onSignInFailure(context.getString(Strings.error_auth_google_cancelled))
        } catch (e: Exception) {
            onSignInFailure("${e.message}")
        }
    }

    private fun handleSignIn(
        result: GetCredentialResponse,
        onSignInSuccess: (idToken: String) -> Unit,
        onSignInFailure: (message: String) -> Unit,
    ) {
        // Handle the successfully returned credential
        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        // Use googleIdTokenCredential and extract id to validate and authenticate on your server.
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential
                                .createFrom(credential.data)
                        val googleIdToken = googleIdTokenCredential.idToken
                        onSignInSuccess(googleIdToken)
                    } catch (e: GoogleIdTokenParsingException) {
                        onSignInFailure(context.getString(Strings.error_auth_google_invalid_id_token))
                    }
                } else {
                    // Catch any unrecognized custom credential type here.
                    onSignInFailure(context.getString(Strings.error_auth_google_unexpected_credential))
                }
            }

            else -> {
                // Catch any unrecognized credential type here.
                onSignInFailure(context.getString(Strings.error_auth_google_unexpected_credential))
            }
        }
    }

    private fun generateNonce(): String {
        val rawNonce = UUID.randomUUID().toString()
        val nonceBytes = rawNonce.toByteArray()
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val nonceDigest = messageDigest.digest(nonceBytes)
        val hashedNonce =
            nonceDigest.fold("") { str, it ->
                str + "%02x".format(it)
            }
        return hashedNonce
    }
}
