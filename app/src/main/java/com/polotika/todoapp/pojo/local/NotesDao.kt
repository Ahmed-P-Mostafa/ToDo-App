package com.polotika.todoapp.pojo.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.polotika.todoapp.pojo.data.models.NoteModel

@Dao
interface NotesDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addNote(noteModel: NoteModel)

    @Update
    suspend fun updateNote(noteModel: NoteModel)

    @Delete
    suspend fun deleteNote(noteModel: NoteModel)

    @Query("delete from notes_table")
    suspend fun deleteAllNotes()

    @Query("select * from notes_table order by id ASC")
    fun sortByDate():List<NoteModel>

    @Query("SELECT * FROM NOTES_TABLE WHERE TITLE LIKE :query order by case when priority like 'H%' then 1 when priority like 'M%' then 2 when priority like 'L%' then 3 end ")
    fun searchInDatabaseWherePriorityHigh(query:String):LiveData<List<NoteModel>>

    @Query("SELECT * FROM NOTES_TABLE WHERE TITLE LIKE :query order by case when priority like 'L%' then 1 when priority like 'M%' then 2 when priority like 'H%' then 3 end ")
    fun searchInDatabaseWherePriorityLow(query:String):LiveData<List<NoteModel>>

    @Query("SELECT * FROM NOTES_TABLE WHERE TITLE LIKE :query order by id ASC")
    fun searchInDatabaseWhereSortByDate(query:String):LiveData<List<NoteModel>>

    @Query("select * from notes_table order by case when priority like 'H%' then 1 when priority like 'M%' then 2 when priority like 'L%' then 3 end ")
    fun sortByHighPriority():List<NoteModel>

    @Query("select * from notes_table order by case when priority like 'L%' then 1 when priority like 'M%' then 2 when priority like 'H%' then 3 end ")
    fun sortByLowPriority():List<NoteModel>
}