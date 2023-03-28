package com.hmmelton.bytechef.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.hmmelton.bytechef.data.model.local.LocalUser

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: LocalUser)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateUser(user: LocalUser)

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): LocalUser?
}
