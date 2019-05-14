package shopifychallenge2019.yogrtman.com.wordsearch

import android.content.Context
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import java.util.*

class WordListAdapter(val mContext: Context, val mWordList: ArrayList<String>) : BaseAdapter(){
    lateinit var mTvWordList: ArrayList<TextView>

    init {
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mTvWordList = ArrayList<TextView>()
        for (word in mWordList) {
            val baseView = inflater.inflate(R.layout.layout_word, null)
            val textView = baseView.findViewById<TextView>(R.id.txt_word)
            textView.text = word
            mTvWordList.add(textView)
        }
    }

    fun markWordFound(word: String) {
        val index = Math.max(mWordList.indexOf(word), mWordList.indexOf(word.reversed()))
        Log.d("WordListAdapter", "markWordFound: $word, index of " + index)
        if (index >= 0) {
            mTvWordList[index].paintFlags = mTvWordList[index].paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return mTvWordList[position]
    }

    override fun getItem(position: Int): Any {
        return 0
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return mWordList.size
    }
}