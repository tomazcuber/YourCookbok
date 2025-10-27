package com.tomazcuber.yourcookbok.data.local.mapper

import com.tomazcuber.yourcookbok.data.local.model.RecipeEntity
import com.tomazcuber.yourcookbok.domain.model.Ingredient
import com.tomazcuber.yourcookbok.domain.model.Recipe
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class RecipeEntityMapperTest {

    @Test
    fun `toEntity should map all fields correctly from domain to entity`() {
        // Given
        val domainModel = Recipe(
            id = "1",
            name = "Domain Recipe",
            instructions = "Domain Instructions",
            imageUrl = "http://example.com/domain.jpg",
            category = "Domain Category",
            area = "Domain Area",
            ingredients = listOf(Ingredient("Item", "1"))
        )

        // When
        val entity = domainModel.toEntity()

        // Then
        expectThat(entity) {
            get { id }.isEqualTo("1")
            get { name }.isEqualTo("Domain Recipe")
            get { instructions }.isEqualTo("Domain Instructions")
            get { imageUrl }.isEqualTo("http://example.com/domain.jpg")
            get { category }.isEqualTo("Domain Category")
            get { area }.isEqualTo("Domain Area")
            get { ingredients }.isEqualTo(listOf(Ingredient("Item", "1")))
        }
    }

    @Test
    fun `toDomain should map all fields correctly from entity to domain`() {
        // Given
        val entity = RecipeEntity(
            id = "2",
            name = "Entity Recipe",
            instructions = "Entity Instructions",
            imageUrl = "http://example.com/entity.jpg",
            category = "Entity Category",
            area = "Entity Area",
            ingredients = listOf(Ingredient("Item", "2"))
        )

        // When
        val domainModel = entity.toDomain()

        // Then
        expectThat(domainModel) {
            get { id }.isEqualTo("2")
            get { name }.isEqualTo("Entity Recipe")
            get { instructions }.isEqualTo("Entity Instructions")
            get { imageUrl }.isEqualTo("http://example.com/entity.jpg")
            get { category }.isEqualTo("Entity Category")
            get { area }.isEqualTo("Entity Area")
            get { ingredients }.isEqualTo(listOf(Ingredient("Item", "2")))
        }
    }
}