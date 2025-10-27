package com.tomazcuber.yourcookbok.data.remote.mapper

import com.tomazcuber.yourcookbok.data.remote.dto.MealDbRecipeDto
import com.tomazcuber.yourcookbok.domain.model.Ingredient
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.containsExactly
import strikt.assertions.isEmpty
import strikt.assertions.isEqualTo

class RecipeDtoMapperTest {

    @Nested
    @DisplayName("toDomain")
    inner class ToDomain {

        @Test
        fun `given well-formed DTO, then map all fields correctly`() {
            // Given
            val dto = MealDbRecipeDto(
                id = "123",
                name = "Test Recipe",
                instructions = "Test Instructions",
                imageUrl = "http://example.com/image.jpg",
                category = "Test Category",
                area = "Test Area",
                ingredient1 = "Chicken", measure1 = "1 lb",
                ingredient2 = "Salt", measure2 = "1 tsp"
            )

            // When
            val recipe = dto.toDomain()

            // Then
            expectThat(recipe) {
                get { id }.isEqualTo("123")
                get { name }.isEqualTo("Test Recipe")
                get { instructions }.isEqualTo("Test Instructions")
                get { imageUrl }.isEqualTo("http://example.com/image.jpg")
                get { category }.isEqualTo("Test Category")
                get { area }.isEqualTo("Test Area")
                get { ingredients }.containsExactly(
                    Ingredient("Chicken", "1 lb"),
                    Ingredient("Salt", "1 tsp")
                )
            }
        }

        @Test
        fun `given DTO with null optional fields, then map to empty strings`() {
            // Given
            val dto = MealDbRecipeDto(
                id = "123",
                name = "Test Recipe",
                instructions = null,
                imageUrl = null,
                category = null,
                area = null
            )

            // When
            val recipe = dto.toDomain()

            // Then
            expectThat(recipe) {
                get { instructions }.isEqualTo("")
                get { imageUrl }.isEqualTo("")
                get { category }.isEqualTo("")
                get { area }.isEqualTo("")
            }
        }

        @Test
        fun `given DTO with messy ingredient data, then parse ingredients correctly`() {
            // Given
            val dto = MealDbRecipeDto(
                id = "123",
                name = "Test Recipe",
                ingredient1 = "Chicken", measure1 = "1 lb",       // Valid
                ingredient2 = "Salt", measure2 = null,             // Valid, empty measure
                ingredient3 = null, measure3 = "1 tsp",           // Invalid, null ingredient
                ingredient4 = "", measure4 = "1 pinch",             // Invalid, blank ingredient
                ingredient5 = "Water", measure5 = "2 cups"       // Valid
            )

            // When
            val recipe = dto.toDomain()

            // Then
            expectThat(recipe.ingredients).containsExactly(
                Ingredient("Chicken", "1 lb"),
                Ingredient("Salt", ""),
                Ingredient("Water", "2 cups")
            )
        }

        @Test
        fun `given DTO with no ingredients, then ingredients list is empty`() {
            // Given
            val dto = MealDbRecipeDto(id = "123", name = "Test Recipe")

            // When
            val recipe = dto.toDomain()

            // Then
            expectThat(recipe.ingredients).isEmpty()
        }
    }
}