package shopifychallenge2019.yogrtman.com.wordsearch

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.GridLayout
import android.widget.GridView
import android.widget.TextView

class MainActivity : AppCompatActivity(), OnWordFoundListener {
    lateinit var mWordSearchGrid: GridView
    lateinit var mWordSearchAdapter: WordGridAdapter

    lateinit var mWordListGrid: GridView
    lateinit var mWordListAdapter: WordListAdapter

    lateinit var wordList: ArrayList<String>

    var wordsFound = 0
    lateinit var txtFoundCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        wordList = resources.getStringArray(R.array.word_list).toCollection(ArrayList())

        mWordSearchGrid = findViewById(R.id.grid_wordsearch)
        mWordSearchAdapter = WordGridAdapter(this, this as OnWordFoundListener, wordList, 15, 15)
        mWordSearchGrid.adapter = mWordSearchAdapter
        mWordSearchGrid.onItemClickListener = object: AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mWordSearchAdapter.onCellClicked(mWordSearchGrid, position, id)
            }
        }

        mWordListGrid = findViewById(R.id.grid_wordlist)
        mWordListAdapter = WordListAdapter(this, wordList)
        mWordListGrid.adapter = mWordListAdapter

        txtFoundCount = findViewById(R.id.txt_words_found)
        txtFoundCount.text = "" + wordsFound
    }

    override fun onWordFound(word: String) {
        mWordListAdapter.markWordFound(word)
        wordsFound++
        txtFoundCount.text = "" + wordsFound
        if (wordsFound == wordList.size) {
            var dialogBuilder = AlertDialog.Builder(this as Context)
            dialogBuilder.setTitle("Congratulations!")
                    .setMessage("You found all $wordsFound words!\nPlay again?")
                    .setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            //reload activity to reset
                            finish()
                            startActivity(intent)
                        }
                    })
                    .setNegativeButton("No", null)
            dialogBuilder.show()
        }
    }
}