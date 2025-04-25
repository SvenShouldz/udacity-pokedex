package com.shouldz.pokedex.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
@SmallTest
class CaughtPokemonDaoTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase
    private lateinit var dao: CaughtPokemonDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        dao = database.caughtPokemonDao()
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        database.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetPokemonByName() = runBlocking {
        // GIVEN
        val pokemon = CaughtPokemon(name = "pikachu", spriteUrl = "url_pika")

        // WHEN - Insert
        dao.insertCaughtPokemon(pokemon)

        // THEN - Observe LiveData
        var loadedPokemon: CaughtPokemon? = null
        dao.getCaughtPokemonByName("pikachu").observeForever {
            loadedPokemon = it
        }

        // Verify
        assertNotNull(loadedPokemon)
        assertEquals(pokemon.name, loadedPokemon?.name)
        assertEquals(pokemon.spriteUrl, loadedPokemon?.spriteUrl)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetAllPokemon() = runBlocking {
        // GIVEN
        val pika = CaughtPokemon(name = "pikachu", spriteUrl = "url_pika")
        val bulba = CaughtPokemon(name = "bulbasaur", spriteUrl = "url_bulba")
        dao.insertCaughtPokemon(pika)
        dao.insertCaughtPokemon(bulba)

        // WHEN - Observe LiveData
        var allPokemon: List<CaughtPokemon>? = null
        dao.getAllCaughtPokemon().observeForever {
            allPokemon = it
        }

        // THEN
        assertNotNull(allPokemon)
        assertEquals(2, allPokemon?.size)
        // Check if both are present
        assertTrue(allPokemon?.any { it.name == "bulbasaur" } ?: false)
        assertTrue(allPokemon?.any { it.name == "pikachu" } ?: false)
        assertEquals("bulbasaur", allPokemon?.get(0)?.name) // Verify sort order
    }

    @Test
    @Throws(Exception::class)
    fun insertAndDeletePokemon() = runBlocking {
        // GIVEN
        val pokemon = CaughtPokemon(name = "squirtle", spriteUrl = "url_squirtle")
        dao.insertCaughtPokemon(pokemon)

        // WHEN - Delete
        dao.deleteCaughtPokemon("squirtle")

        // THEN - Observe LiveData
        var loadedPokemon: CaughtPokemon? = null
        dao.getCaughtPokemonByName("squirtle").observeForever {
            loadedPokemon = it
        }

        // Verify
        assertNull("Pokemon should be null after deletion", loadedPokemon)
    }

    @Test
    @Throws(Exception::class)
    fun getNonExistentPokemonReturnsNull() = runBlocking {
        // GIVEN - DB is empty

        // WHEN - Observe LiveData for a non-existent Pokemon
        var loadedPokemon: CaughtPokemon? = CaughtPokemon("dummy", "dummy")
        dao.getCaughtPokemonByName("mewtwo").observeForever {
            loadedPokemon = it
        }

        // THEN
        assertNull("Non-existent Pokemon should return null", loadedPokemon)
    }
}
