package com.github.kr328.clash.service.data

import androidx.room.*
import java.util.*

@Dao
@TypeConverters(Converters::class)
interface PendingDao {
    @Query("SELECT * FROM pending WHERE uuid = :uuid")
    suspend fun queryByUUID(uuid: UUID): Pending?

    @Query("SELECT * FROM pending WHERE name = :name LIMIT 1" )
    suspend fun queryByName(name: String): Pending?

    @Query("SELECT uuid FROM pending WHERE name = :name LIMIT 1" )
    suspend fun queryUUIDByName(name: String): UUID?

    @Query("DELETE FROM pending WHERE uuid = :uuid")
    suspend fun remove(uuid: UUID)

    @Query("SELECT EXISTS(SELECT 1 FROM pending WHERE uuid = :uuid)")
    suspend fun exists(uuid: UUID): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM pending WHERE name = :name)")
    suspend fun exists(name: String): Boolean

    @Query("SELECT uuid FROM pending ORDER BY createdAt")
    suspend fun queryAllUUIDs(): List<UUID>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pending: Pending)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(pending: Pending)
}
