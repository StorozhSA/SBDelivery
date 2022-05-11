package ru.storozh.custom.cards.dish

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ru.storozh.custom.R


@Suppress("DEPRECATION")
class ViewCardDish
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ViewGroup(context, attrs, defStyleAttr), LifecycleObserver, RoundedRectShadowOwner {

    //Dimensions and sizes
    private var w: Int = 0
    private var h: Int = 0

    private val dp8 = dpToPx(8)
    private val dp16i = dpToIntPx(16)
    private val dp28i = dpToIntPx(28)
    private val dp32i = dpToIntPx(32)
    private val dp48i = dpToIntPx(48)
    private val dp160i = dpToIntPx(160)
    private val dp158i = dpToIntPx(158)
    private val sp5 = spToPx(5.5f)
    private val pad3i = dpToIntPx(3)
    private val pad4i = dpToIntPx(4)

    //Colors
    private val dishNameTextColor = Color.WHITE
    private val dishPriceTextColor = Color.rgb(235, 97, 35)
    private val cardBackgroundColor = Color.rgb(36, 34, 44)
    private val cardAddButtonColor = Color.argb(255, 235, 97, 35)

    //Paints
    private val paintBg by lazy {
        Paint().apply {
            color = cardBackgroundColor
        }
    }

    //Paths (card)
    private val path by lazy {
        Path().apply {
            addRoundRect(
                RectF(0f, 0f, w.toFloat(), h.toFloat()),
                floatArrayOf(dp8, dp8, dp8, dp8, dp8, dp8, dp8, dp8),
                Path.Direction.CW
            )
        }
    }

    //Rects (card)
    private val rectF by lazy {
        RectF(0f, 0f, w.toFloat(), h.toFloat())
    }
    private val rect by lazy {
        Rect(0, 0, w, h)
    }

    //Fonts
    private val defTypeFace = Typeface.create("sans-serif-medium", Typeface.NORMAL)

    //Label currency
    private val currency = "₽"

    //Listeners sets
    private val listenerAddButton: MutableSet<(v: View) -> Unit> = mutableSetOf()
    private val listenerView: MutableSet<(v: View) -> Unit> = mutableSetOf()
    private val listenerLikeButton: MutableSet<(v: View) -> Unit> = mutableSetOf()

    //Embedded state
    private val _state: MutableLiveData<State> = MutableLiveData(State())
    val state: LiveData<State> = _state

    //Dish photo
    private val ivDishPhoto by lazy {
        ImageView(context, attrs, defStyleAttr).apply {
            scaleType = ImageView.ScaleType.CENTER_CROP
            maxHeight = dp160i
            minimumHeight = dp160i
            setImageResource(R.drawable.wallpaper)
            addView(this)
        }
    }

    //Like
    private val ivDishLike by lazy {
        ImageView(context, attrs, defStyleAttr).apply {
            scaleType = ImageView.ScaleType.CENTER_CROP
            maxWidth = dp16i
            maxHeight = dp16i
            minimumWidth = dp16i
            minimumHeight = dp16i
            elevation = 3f
            setImageResource(R.drawable.ic_favorite_on)
            addView(this)
        }
    }

    //Price
    private val tvDishPrice by lazy {
        TextView(context, attrs, defStyleAttr).apply {
            typeface = defTypeFace
            setTextColor(dishPriceTextColor)
            textSize = sp5
            text = "1280 $currency"
            addView(this)
        }
    }

    //Dish name
    private val tvDishName by lazy {
        TextView(context, attrs, defStyleAttr).apply {
            typeface = defTypeFace
            setTextColor(dishNameTextColor)
            textSize = sp5
            text = "Сет Королевский в собственном соку"
            width = dp158i - pad4i - pad4i
            addView(this)
        }
    }

    //Button add dish to basket
    private val fbDishAdd by lazy {
        FloatingActionButton(context, attrs, defStyleAttr).apply {
            setImageResource(R.drawable.ic_add)
            useCompatPadding = true
            compatElevation = 3f
            scaleType = ImageView.ScaleType.CENTER
            backgroundTintList = ColorStateList.valueOf(cardAddButtonColor)
            addView(this)
        }
    }

    //region Listeners
    fun addOnClickListenerForLikeButton(listener: (v: View) -> Unit) {
        listenerLikeButton.add(listener)
    }

    fun removeOnClickListenerForLikeButton(listener: (v: View) -> Unit) {
        listenerLikeButton.remove(listener)
    }

    fun addOnClickListenerForAddButton(listener: (v: View) -> Unit) {
        listenerAddButton.add(listener)
    }

    fun removeOnClickListenerForAddButton(listener: (v: View) -> Unit) {
        listenerAddButton.remove(listener)
    }

    fun addOnClickListener(listener: (v: View) -> Unit) {
        listenerView.add(listener)
    }

    fun removeOnClickListener(listener: (v: View) -> Unit) {
        listenerView.remove(listener)
    }
    //endregion

    fun setId(id: String) {
        _state.update {
            it.copy(id = id)
        }
    }

    fun getCardId(): String = _state.value?.id!!

    fun setLike(like: Boolean) {
        _state.update {
            it.copy(isLike = like)
        }
    }

    fun getCardLike(): Boolean = _state.value?.isLike!!

    fun setName(str: String) {
        tvDishName.text = str
    }

    fun setPrice(str: String) {
        tvDishPrice.text = "$str  $currency"
    }

    fun getPhoto(): ImageView {
        return ivDishPhoto
    }

    //Init
    init {
        Log.d("ViewCardDish", "INIT")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            outlineProvider = RoundedRectViewOutlineProvider()
        }

        fbDishAdd.setOnClickListener {
            Log.d(
                "ViewCardDish",
                "setOnClickListener OnClickListenerForAddButton id = ${getCardId()}"
            )
            listenerAddButton.forEach { listener -> listener.invoke(it) }
        }
        ivDishLike.setOnClickListener {
            Log.d(
                "ViewCardDish",
                "setOnClickListener OnClickListenerForLikeButton id = ${getCardId()}"
            )
            listenerLikeButton.forEach { listener -> listener.invoke(it) }
        }
        this.setOnClickListener {
            Log.d("ViewCardDish", "setOnClickListener OnClickListener id = ${getCardId()}")
            listenerView.forEach { listener -> listener.invoke(it) }
        }

        _state.observeForever {
            //Log.d("ViewCardDish", "state changed")
            if (it.isLike) {
                ivDishLike.setImageResource(R.drawable.ic_favorite_on)
            } else {
                ivDishLike.setImageResource(R.drawable.ic_favorite_off)
            }
        }

        addOnClickListenerForLikeButton {
            _state.update {
                it.copy(isLike = !it.isLike)
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun create() {
        Log.d("ViewCardDish", "ON_CREATE  id = ${getCardId()}")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun start() {
        Log.d("ViewCardDish", "ON_START  id = ${getCardId()}")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stop() {
        Log.d("ViewCardDish", "ON_STOP  id = ${getCardId()}")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroy() {
        Log.d("ViewCardDish", "ON_DESTROY  id = ${getCardId()}")
        listenerAddButton.clear()
        listenerView.clear()
        listenerLikeButton.clear()
    }

    fun registerLifecycle(lifecycle: Lifecycle) {
        lifecycle.addObserver(this)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        h = getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
        w = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        measureChild(ivDishPhoto, widthMeasureSpec, heightMeasureSpec)
        measureChild(ivDishLike, widthMeasureSpec, heightMeasureSpec)
        measureChild(tvDishPrice, widthMeasureSpec, heightMeasureSpec)
        measureChild(tvDishName, widthMeasureSpec, heightMeasureSpec)
        measureChild(fbDishAdd, widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(w, h)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        ivDishPhoto.apply { layout(0, 0, w, maxHeight) }
        ivDishLike.layout(w - dp32i, dp16i, w - dp16i, dp32i)
        tvDishPrice.apply {
            layout(
                pad3i,
                ivDishPhoto.maxHeight + pad3i,
                measuredWidth + pad3i + pad3i,
                ivDishPhoto.maxHeight + measuredHeight + pad3i
            )
        }
        tvDishName.apply {
            layout(
                pad3i,
                ivDishPhoto.maxHeight + tvDishPrice.measuredHeight,
                measuredWidth,
                measuredHeight + ivDishPhoto.maxHeight + tvDishPrice.measuredHeight + pad3i + pad3i
            )
        }
        fbDishAdd.layout(w - dp48i, ivDishPhoto.height - dp28i, w, ivDishPhoto.height + dp28i)
    }

    override fun dispatchDraw(canvas: Canvas?) {
        canvas?.let {
            canvas.drawRoundRect(rectF, dp8, dp8, paintBg)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                canvas.clipPath(path)
            } else {
                @Suppress("DEPRECATION")
                canvas.clipPath(path, Region.Op.INTERSECT)
            }
        }
        super.dispatchDraw(canvas)
    }

    override fun cardBackgroundRect() = rect

    override fun cardCornerRadius() = dp8

    private fun dpToIntPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }

    private fun dpToPx(dp: Int): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            context.resources.displayMetrics
        )
    }

    private fun spToPx(sp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            sp,
            context.resources.displayMetrics
        )
    }

    private fun <T> MutableLiveData<T>.update(upd: (currentState: T) -> T) {
        val updatedState: T = upd(this.value!!)
        this.value = updatedState
    }

    data class State(val id: String = "", val isLike: Boolean = false)
}