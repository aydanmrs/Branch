package com.example.branchapp

data class Card(
    val id: Int,
    val image: Int,
    var isFaceUp: Boolean = false,
    var isMatched: Boolean = false
)