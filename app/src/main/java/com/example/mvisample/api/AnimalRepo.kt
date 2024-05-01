package com.example.mvisample.api

class AnimalRepo(private val api: AnimalApi) {
    suspend fun getAnimals() = api.getAnimals()
}