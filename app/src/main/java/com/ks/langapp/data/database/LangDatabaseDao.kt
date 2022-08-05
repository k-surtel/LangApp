package com.ks.langapp.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.ks.langapp.data.database.entities.Card
import com.ks.langapp.data.database.entities.CardStats
import com.ks.langapp.data.database.entities.Deck
import com.ks.langapp.data.database.entities.DeckStats
import kotlinx.coroutines.flow.Flow

@Dao
interface LangDatabaseDao {

    @Query("DELETE FROM table_decks WHERE deckId = :key ")
    suspend fun deleteDeck(key: Long)

    @Insert
    suspend fun insertAll(cards: List<Card>)

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

    @Query("SELECT * FROM table_card_stats WHERE deckId = :key ")
     suspend fun getCardStatsOfADeck(key: Long): List<CardStats>

    @Insert(onConflict = REPLACE)
    suspend fun insertCardStatsList(cardStats: List<CardStats>)

    @Insert
    suspend fun insert(deckStats: DeckStats)

    @Query("SELECT * FROM table_deck_stats")
    suspend fun getDeckStats(): List<DeckStats>
}