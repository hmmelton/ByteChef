package com.hmmelton.bytechef.data.workers

import androidx.work.Data

/**
 * Class used to handle exception where Worker was not given required [Data] key
 */
class MissingKeyException(key: String) : Exception("Missing key: $key")