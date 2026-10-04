package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface OptimizationDao {

    @Query("SELECT * FROM optimization_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<OptimizationRecord>>

    @Query("SELECT * FROM optimization_records ORDER BY timestamp DESC LIMIT 20")
    fun getRecentRecords(): Flow<List<OptimizationRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: OptimizationRecord)

    @Query("DELETE FROM optimization_records")
    suspend fun clearAllRecords()

    @Query("SELECT * FROM game_apps ORDER BY appName ASC")
    fun getAllGames(): Flow<List<GameApp>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: GameApp)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGames(games: List<GameApp>)

    @Update
    suspend fun updateGame(game: GameApp)

    @Query("DELETE FROM game_apps WHERE packageName = :packageName")
    suspend fun deleteGame(packageName: String)
}
