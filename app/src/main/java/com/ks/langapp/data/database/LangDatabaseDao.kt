package com.ks.langapp.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Update
import com.ks.langapp.data.database.entities.Card
import com.ks.langapp.data.database.entities.Deck
import kotlinx.coroutines.flow.Flow

@Dao
interface LangDatabaseDao {

    @Insert
    suspend fun insertAll(cards: List<Card>) //

    @Update
    suspend fun update(deck: Deck) //

    @Query("DELETE FROM table_decks WHERE deckId = :key ")
    suspend fun deleteDeck(key: Long) //

    @Query("DELETE FROM table_cards")
    suspend fun clearCards() //

    @Query("DELETE FROM table_decks")
    suspend fun clearDecks() //

    @Query("SELECT * FROM table_cards ORDER BY cardId DESC LIMIT 1")
    suspend fun getLastCard(): Card? //





    @Query("DELETE FROM table_cards WHERE deckId = :key ")
    suspend fun deleteCardsOfADeck(key: Long)

    @Insert(onConflict = REPLACE)
    suspend fun insert(deck: Deck): Long

    @Query("SELECT * from table_decks WHERE deckId = :key")
    suspend fun getDeck(key: Long): Deck?

    @Query("DELETE FROM table_cards WHERE cardId = :key ")
    suspend fun deleteCard(key: Long)

    @Insert(onConflict = REPLACE)
    suspend fun insert(card: Card)

    @Query("SELECT * from table_cards WHERE cardId = :key")
    suspend fun getCard(key: Long): Card?

    @Query("SELECT * FROM table_cards WHERE deckId = :key ORDER BY cardId DESC")
    fun getCardsOfADeck(key: Long): Flow<List<Card>>

    @Query("SELECT * FROM table_decks ORDER BY deckId DESC")
    fun getAllDecks(): Flow<List<Deck>>
}