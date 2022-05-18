package com.hanna.sliidetest.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.Query
import androidx.room.Transaction
import com.hanna.sliidetest.models.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UsersDao {

    @Insert(onConflict = IGNORE)//chose to ignore, sine the created time is calculated locally..
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM user")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM user WHERE id = :id")
    fun getUserById(id: Int): User?

    @Query("UPDATE user SET name = :name, email =:email WHERE id = :id")//don't want to ovveride the creation time, hence a detailed update, rather than entire entity
    fun updateUser(id: Int, name: String, email: String)

    @Transaction
    suspend fun insertOrUpdate(users: List<User>) {
        users.map { user ->
            val itemsFromDB = getUserById(user.id)//check if user exists
            if (itemsFromDB == null) {
                insertUser(user)
            } else {
                updateUser(user.id, user.name, user.email)
            }
        }
    }
}