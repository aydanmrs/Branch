package com.example.branchapp

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.branchapp.databinding.ActivityMemoryGameBinding
import com.example.branchapp.databinding.DialogGameOverBinding
import com.example.branchapp.databinding.DialogUndoBinding

class MemoryGameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMemoryGameBinding
    private lateinit var cards: MutableList<Card>
    private lateinit var adapter: CardAdapter
    private var firstCard: Card? = null
    private var secondCard: Card? = null
    private var isGameFinished = false
    private var handler = Handler(Looper.getMainLooper())
    private var timerRunnable: Runnable? = null
    private var remainingTime = 90
    private var isPaused = false


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

        binding.undoButton.setOnClickListener {
            if (isPaused) {
                resumeGame()
            } else {
                showUndoDialog()
            }
        }

        setupCards()
        setupRecyclerView()
        startTimer()

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
            endGame(true)
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

    private fun startTimer() {
        timerRunnable?.let { handler.removeCallbacks(it) }

        timerRunnable = object : Runnable {
            override fun run() {
                remainingTime--
                updateTimerUI()
                if (remainingTime > 0) {
                    handler.postDelayed(this, 1000)
                } else {
                    if (!isGameFinished) {
                        endGame(false)
                    }
                }
            }
        }
        handler.post(timerRunnable!!)
    }

    private fun updateTimerUI() {
        binding.timerTextView.text = String.format("%02d:%02d", remainingTime / 60, remainingTime % 60)
    }

    private fun endGame(isWin: Boolean) {
        isGameFinished = true
        timerRunnable?.let { handler.removeCallbacks(it) }

        val dialogBinding = DialogGameOverBinding.inflate(layoutInflater)
        val message = if (isWin) "You won the game!" else "You lost the game!"
        dialogBinding.messageTextView.text = message

        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogBinding.restartButton.setOnClickListener {
            dialog.dismiss()
            restartGame(resetTime = true)
        }

        dialogBinding.quitButton.setOnClickListener {
            dialog.dismiss()
            quitGame()
        }

        dialog.show()
    }
    private fun restartGame(resetTime: Boolean) {
        cards.forEach { card ->
            card.isFaceUp = false
            card.isMatched = false
        }
        cards.shuffle()
        adapter.notifyDataSetChanged()

        if (resetTime) {
            remainingTime = 90
            startTimer()
        } else {
            updateTimerUI()
        }

        isGameFinished = false
        binding.undoButton.visibility = View.VISIBLE
        binding.pauseButton.visibility = View.INVISIBLE
        isPaused = false
    }

    private fun quitGame() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

    private fun showUndoDialog() {
        timerRunnable?.let { handler.removeCallbacks(it) }
        isPaused = true

        val dialogBinding = DialogUndoBinding.inflate(layoutInflater)
        val undoDialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()

        undoDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)


        dialogBinding.btnYes.setOnClickListener {
            undoDialog.dismiss()
            restartGame(resetTime = true)
        }

        dialogBinding.btnNo.setOnClickListener {
            undoDialog.dismiss()
            resumeGame()
        }

        undoDialog.show()

        binding.undoButton.visibility = View.INVISIBLE
        binding.pauseButton.visibility = View.VISIBLE
    }

    private fun resumeGame() {
        startTimer()
        binding.undoButton.visibility = View.VISIBLE
        binding.pauseButton.visibility = View.INVISIBLE
        isPaused = false
    }
}