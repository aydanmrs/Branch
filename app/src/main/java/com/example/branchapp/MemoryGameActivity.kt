package com.example.branchapp

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.branchapp.databinding.ActivityMemoryGameBinding

class MemoryGameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMemoryGameBinding
    private lateinit var cards: MutableList<Card>
    private lateinit var adapter: CardAdapter
    private var firstCard: Card? = null
    private var secondCard: Card? = null
    private var isGameFinished = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMemoryGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.main_bg)
        }

        binding.backButton.setOnClickListener {
            val intent = Intent(this, StartActivity2::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }
        setupCards()

    }

    private fun setupCards() {
        val images = listOf(
            R.drawable.bear,
            R.drawable.bee,
            R.drawable.dog,
            R.drawable.cat,
            R.drawable.elephant,
            R.drawable.giraffe,
            R.drawable.lion,
            R.drawable.rabbit,
            R.drawable.penguin,
            R.drawable.zebra
        )

        cards = (images.shuffled().take(10) + images.shuffled().take(10)).shuffled().mapIndexed { index, image ->
            Card(id = index, image = image)
        }.toMutableList()
    }

    private fun setupRecyclerView() {
        val spanCount = 4
        binding.recyclerView.layoutManager = GridLayoutManager(this, spanCount)
        adapter = CardAdapter(cards) { card ->
            handleCardClick(card)
        }
        binding.recyclerView.adapter = adapter
    }

    private fun handleCardClick(card: Card) {
        if (isGameFinished || card.isFaceUp || card.isMatched) {
            return
        }

        card.isFaceUp = true

        if (firstCard == null) {
            firstCard = card
        } else {
            secondCard = card
            checkForMatch(firstCard!!, secondCard!!)
            firstCard = null
            secondCard = null
        }

        adapter.notifyDataSetChanged()

        if (cards.all { it.isMatched }) {
        }
    }
    private fun checkForMatch(card1: Card, card2: Card) {
        if (card1.image == card2.image) {
            card1.isMatched = true
            card2.isMatched = true
        } else {
            binding.recyclerView.postDelayed({
                card1.isFaceUp = false
                card2.isFaceUp = false
                adapter.notifyDataSetChanged()
            }, 1000)
        }
    }
}