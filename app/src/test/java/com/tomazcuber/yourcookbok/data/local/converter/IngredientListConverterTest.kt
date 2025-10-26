package com.tomazcuber.yourcookbok.data.local.converter

import com.tomazcuber.yourcookbok.domain.model.Ingredient
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.containsExactly
import strikt.assertions.isEmpty
import strikt.assertions.isEqualTo

class IngredientListConverterTest {

    private lateinit var converter: IngredientListConverter

    @BeforeEach
    fun setUp() {
        converter = IngredientListConverter()
    }

    @Test
    fun `fromIngredientList should convert list to valid json string`() {
        // Arrange
        val ingredients = listOf(
            Ingredient(name = "Chicken", measure = "1 breast"),
            Ingredient(name = "Salt", measure = "1 tsp")
        )
        val expectedJson = "[{\"name\":\"Chicken\",\"measure\":\"1 breast\"},{\"name\":\"Salt\",\"measure\":\"1 tsp\"}]"

        // Act
        val json = converter.fromIngredientList(ingredients)

        // Assert
        expectThat(json).isEqualTo(expectedJson)
    }

    @Test
    fun `toIngredientList should convert json string to valid list`() {
        // Arrange
        val json = "[{\"name\":\"Chicken\",\"measure\":\"1 breast\"},{\"name\":\"Salt\",\"measure\":\"1 tsp\"}]"
        val expectedIngredients = listOf(
            Ingredient(name = "Chicken", measure = "1 breast"),
            Ingredient(name = "Salt", measure = "1 tsp")
        )

        // Act
        val ingredients = converter.toIngredientList(json)

        // Assert
        expectThat(ingredients).containsExactly(*expectedIngredients.toTypedArray())
    }

    @Test
    fun `fromIngredientList should handle empty list`() {
        // Arrange
        val ingredients = emptyList<Ingredient>()

        // Act
        val json = converter.fromIngredientList(ingredients)

        // Assert
        expectThat(json).isEqualTo("[]")
    }

    @Test
    fun `toIngredientList should handle empty json array`() {
        // Arrange
        val json = "[]"

        // Act
        val ingredients = converter.toIngredientList(json)

        // Assert
        expectThat(ingredients).isEmpty()
    }
}
