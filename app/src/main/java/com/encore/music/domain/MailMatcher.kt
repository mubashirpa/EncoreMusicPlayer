package com.encore.music.domain

interface MailMatcher {
    fun matches(mail: String): Boolean
}
