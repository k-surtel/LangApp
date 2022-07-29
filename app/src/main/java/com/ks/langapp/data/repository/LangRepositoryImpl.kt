package com.ks.langapp.data.repository

import android.util.Log
import com.ks.langapp.data.database.LangDatabaseDao
import com.ks.langapp.data.database.entities.Card
import com.ks.langapp.data.database.entities.Deck
import kotlinx.coroutines.flow.Flow

class LangRepositoryImpl(
    private val dao: LangDatabaseDao
) : LangRepository {

    override suspend fun saveDeck(deck: Deck): Long {
        return dao.insert(deck)
    }

    override suspend fun deleteDeck(deck: Deck) {
        dao.deleteCardsOfADeck(deck.deckId)
        dao.deleteDeck(deck.deckId)
    }

    override suspend fun getDeck(deckId: Long): Deck? {
        return dao.getDeck(deckId)
    }

    override suspend fun saveCard(card: Card) {
        dao.insert(card)
    }

    override suspend fun deleteCard(card: Card) {
        dao.deleteCard(card.cardId)
    }

    override suspend fun getCard(cardId: Long): Card? {
        return dao.getCard(cardId)
    }

    override fun getAllDecks(): Flow<List<Deck>> {
        return dao.getAllDecks()
    }

    override fun getCardsOfADeck(deckId: Long): Flow<List<Card>> {
        return dao.getCardsOfADeck(deckId)
    }
}