package com.ks.langapp.data.repository

import com.ks.langapp.data.database.entities.Card
import com.ks.langapp.data.database.entities.Deck
import kotlinx.coroutines.flow.Flow

interface LangRepository {

    fun getAllDecks(): Flow<List<Deck>>

    fun getCardsOfADeck(deckId: Long): Flow<List<Card>>

    suspend fun getCard(cardId: Long): Card?

    suspend fun saveCard(card: Card)

    suspend fun deleteCard(card: Card)
}