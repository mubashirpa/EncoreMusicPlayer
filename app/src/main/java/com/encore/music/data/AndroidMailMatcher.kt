package com.encore.music.data

import android.util.Patterns
import com.encore.music.domain.MailMatcher

class AndroidMailMatcher : MailMatcher {
    override fun matches(mail: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(mail).matches()
}
