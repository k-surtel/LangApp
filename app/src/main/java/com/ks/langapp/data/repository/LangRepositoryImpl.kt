package com.ks.langapp.data.repository

import com.ks.langapp.data.database.LangDatabaseDao
import com.ks.langapp.data.database.entities.Card
import com.ks.langapp.data.database.entities.CardStats
import com.ks.langapp.data.database.entities.Deck
import com.ks.langapp.data.database.entities.DeckStats
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

    override suspend fun saveAllCards(cards: List<Card>) {
        dao.insertAll(cards)
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

    override suspend fun getCardStatsOfADeck(deckId: Long): List<CardStats> {
        return dao.getCardStatsOfADeck(deckId)
    }

    override suspend fun saveCardStatsList(cardStats: List<CardStats>) {
        dao.insertCardStatsList(cardStats)
    }

    override suspend fun saveDeckStats(deckStats: DeckStats) {
        dao.insert(deckStats)
    }

    override suspend fun getDeckStats(): List<DeckStats> {
        return dao.getDeckStats()
    }
}