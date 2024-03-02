package com.github.kr328.clash.service.data

import androidx.room.*
import java.util.*

@Dao
@TypeConverters(Converters::class)
interface ImportedDao {
    @Query("SELECT * FROM imported WHERE uuid = :uuid")
    suspend fun queryByUUID(uuid: UUID): Imported?

    @Query("SELECT * FROM imported WHERE name = :name LIMIT 1" )
    suspend fun queryByName(name: String): Pending?

    @Query("SELECT uuid FROM imported WHERE name = :name LIMIT 1" )
    suspend fun queryUUIDByName(name: String): UUID?

    @Query("SELECT uuid FROM imported ORDER BY createdAt")
    suspend fun queryAllUUIDs(): List<UUID>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(imported: Imported): Long

    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun update(imported: Imported)

    @Query("DELETE FROM imported WHERE uuid = :uuid")
    suspend fun remove(uuid: UUID)

    @Query("SELECT EXISTS(SELECT 1 FROM imported WHERE uuid = :uuid)")
    suspend fun exists(uuid: UUID): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM imported WHERE name = :name)")
    suspend fun exists(name: String): Boolean
}