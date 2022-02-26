package com.ks.langapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ks.langapp.database.entities.Card
import com.ks.langapp.database.entities.Deck

@Dao
interface LangDatabaseDao {

    @Insert
    suspend fun insert(card: Card)

    @Insert
    suspend fun insertAll(cards: List<Card>)

    @Insert
    suspend fun insert(deck: Deck): Long

    @Update
    suspend fun update(card: Card)

    @Update
    suspend fun update(deck: Deck)

    @Query("DELETE FROM table_cards WHERE cardId = :key ")
    suspend fun deleteCard(key: Long)

    @Query("DELETE FROM table_decks WHERE deckId = :key ")
    suspend fun deleteDeck(key: Long)

    @Query("DELETE FROM table_cards WHERE deckId = :key ")
    suspend fun deleteCardsOfADeck(key: Long)

    @Query("SELECT * from table_cards WHERE cardId = :key")
    suspend fun getCard(key: Long): Card?

    @Query("SELECT * from table_decks WHERE deckId = :key")
    suspend fun getDeck(key: Long): Deck?

    @Query("DELETE FROM table_cards")
    suspend fun clearCards()

    @Query("DELETE FROM table_decks")
    suspend fun clearDecks()

    @Query("SELECT * FROM table_cards ORDER BY cardId DESC LIMIT 1")
    suspend fun getLastCard(): Card?

    @Query("SELECT * FROM table_cards WHERE deckId = :key ORDER BY cardId DESC")
    fun getCardsOfDeck(key: Long): LiveData<List<Card>>

    @Query("SELECT * FROM table_decks ORDER BY deckId DESC")
    fun getAllDecks(): LiveData<List<Deck>>
}