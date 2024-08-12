package com.example.branchapp

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.branchapp.databinding.ActivityTicTacToeBinding
import com.example.branchapp.databinding.DialogWinnerBinding

class TicTacToeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTicTacToeBinding
    private var currentPlayer = "X"
    private var lastStartingPlayer = "X"
    private val board = Array(3) { arrayOfNulls<String>(3) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityTicTacToeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            window.statusBarColor = Color.TRANSPARENT
        }

        initializeBoard()

        binding.undo.setOnClickListener {
            resetBoard()
        }

        binding.backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }
    }

    @SuppressLint("ResourceType")
    private fun initializeBoard() {
        val buttons = arrayOf(
            arrayOf(binding.button1, binding.button2, binding.button3),
            arrayOf(binding.button4, binding.button5, binding.button6),
            arrayOf(binding.button7, binding.button8, binding.button9)
        )

        for (i in 0..2) {
            for (j in 0..2) {
                buttons[i][j].setOnClickListener {
                    onButtonClicked(it, i, j)
                }
                buttons[i][j].apply {
                    textSize = 64f
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                    setBackgroundResource(R.drawable.button_background)
                    setTextColor(getColor(android.R.color.white))
                }
            }
        }
    }

    private fun onButtonClicked(view: View, row: Int, col: Int) {
        val button = view as androidx.appcompat.widget.AppCompatButton

        if (button.text.isNotEmpty()) {
            showCustomToast("This area is full!")
            return
        }

        button.text = currentPlayer
        board[row][col] = currentPlayer

        if (checkForWin()) {
            lastStartingPlayer = currentPlayer
            showWinnerDialog("$currentPlayer won!")
        } else if (isBoardFull()) {
            showWinnerDialog("Draw!")
        } else {
            currentPlayer = if (currentPlayer == "X") "O" else "X"
        }
    }

    private fun showCustomToast(message: String) {
        val inflater = layoutInflater
        val customToastLayout = inflater.inflate(R.layout.toast_custom_layout, binding.root, false)

        val toastText = customToastLayout.findViewById<TextView>(R.id.toastMessage)
        toastText.text = message

        val customToast = Toast(applicationContext)
        customToast.duration = Toast.LENGTH_SHORT
        customToast.view = customToastLayout
        customToast.show()
    }

    private fun showWinnerDialog(message: String) {
        val dialog = Dialog(this)
        val dialogBinding = DialogWinnerBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)

        dialogBinding.textViewWinner.text = message
        dialogBinding.buttonReturn.setOnClickListener {
            dialog.dismiss()
            resetBoard()
        }
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }
    private fun checkForWin(): Boolean {
        for (i in 0..2) {
            if (board[i][0] == currentPlayer && board[i][1] == currentPlayer && board[i][2] == currentPlayer) {
                return true
            }
        }

        for (i in 0..2) {
            if (board[0][i] == currentPlayer && board[1][i] == currentPlayer && board[2][i] == currentPlayer) {
                return true
            }
        }

        if (board[0][0] == currentPlayer && board[1][1] == currentPlayer && board[2][2] == currentPlayer) {
            return true
        }

        return board[0][2] == currentPlayer && board[1][1] == currentPlayer && board[2][0] == currentPlayer
    }

    private fun isBoardFull(): Boolean {
        for (i in 0..2) {
            for (j in 0..2) {
                if (board[i][j].isNullOrEmpty()) {
                    return false
                }
            }
        }
        return true
    }
    private fun resetBoard() {
        for (i in 0..2) {
            for (j in 0..2) {
                board[i][j] = null
            }
        }

        val buttons = arrayOf(
            arrayOf(binding.button1, binding.button2, binding.button3),
            arrayOf(binding.button4, binding.button5, binding.button6),
            arrayOf(binding.button7, binding.button8, binding.button9)
        )

        for (i in 0..2) {
            for (j in 0..2) {
                buttons[i][j].text = ""
            }
        }

        currentPlayer = lastStartingPlayer
    }
}