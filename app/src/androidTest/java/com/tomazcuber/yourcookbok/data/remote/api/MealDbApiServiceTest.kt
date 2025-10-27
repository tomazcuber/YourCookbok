package com.tomazcuber.yourcookbok.data.remote.api

import androidx.test.filters.SmallTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull
import strikt.assertions.isNull
import javax.inject.Inject

@HiltAndroidTest
@SmallTest
class MealDbApiServiceTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var mockWebServer: MockWebServer

    @Inject
    lateinit var apiService: MealDbApiService

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @After
    fun tearDown() {
        mockWebServer.close()
    }

    @Test
    fun searchRecipes_withValidQuery_returnsSuccessResponse() = runTest {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(successResponse)
        mockWebServer.enqueue(mockResponse)

        // When
        val response = apiService.searchRecipes("Arrabiata")

        // Then
        val request = mockWebServer.takeRequest()
        expectThat(request.path).isEqualTo("/search.php?s=Arrabiata")

        val recipe = response.meals?.first()
        expectThat(recipe).isNotNull()
        expectThat(recipe) {
            get { this?.id ?: "" }.isEqualTo("52771")
            get { this?.name ?: "" }.isEqualTo("Spicy Arrabiata Penne")
            get { this?.category }.isEqualTo("Vegetarian")
        }
    }

    @Test
    fun searchRecipes_withNoResults_returnsEmptyList() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(emptyResponse)
        mockWebServer.enqueue(mockResponse)

        // When
        val response = apiService.searchRecipes("NonExistentQuery")

        // Then
        val request = mockWebServer.takeRequest()
        expectThat(request.path).isEqualTo("/search.php?s=NonExistentQuery")
        expectThat(response.meals).isNull()
    }

    private val successResponse = """
    {
      "meals": [
        {
          "idMeal": "52771",
          "strMeal": "Spicy Arrabiata Penne",
          "strCategory": "Vegetarian",
          "strArea": "Italian",
          "strMealThumb": "https://www.themealdb.com/images/media/meals/ustsqw1468250014.jpg"
        }
      ]
    }
    """.trimIndent()

    private val emptyResponse = """
    {
      "meals": null
    }
    """.trimIndent()
}