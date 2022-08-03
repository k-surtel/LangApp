package com.ks.langapp.data.repository

import com.ks.langapp.data.database.entities.Card
import com.ks.langapp.data.database.entities.Deck
import kotlinx.coroutines.flow.Flow

interface LangRepository {

    suspend fun saveDeck(deck: Deck): Long

    suspend fun deleteDeck(deck: Deck)

    suspend fun getDeck(deckId: Long): Deck?

    suspend fun saveCard(card: Card)

    suspend fun saveAllCards(cards: List<Card>)

    suspend fun deleteCard(card: Card)

    suspend fun getCard(cardId: Long): Card?

    fun getAllDecks(): Flow<List<Deck>>

    fun getCardsOfADeck(deckId: Long): Flow<List<Card>>

}