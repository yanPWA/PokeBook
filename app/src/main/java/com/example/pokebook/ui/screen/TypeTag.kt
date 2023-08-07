package com.example.pokebook.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pokebook.R

@Preview(showBackground = true)
@Composable
fun TypeTagPreview() {
    TypeTag(typeList = listOf("fighting", "poison", "ground", "shadow"))
}


/**
 * typeタグの生成
 */
@Composable
fun TypeTag(
    typeList: List<String>,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(top = 5.dp)
    ) {
        Text(
            text = stringResource(R.string.pokemon_type),
            fontSize = 20.sp,
            modifier = modifier,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
        )
        typeList.forEach { type ->
            Text(
                text = type.convertToJaTypeNameByTypeName(),
                modifier = modifier
                    .padding(1.dp)
                    .background(
                        color = type.convertToColorCodeByTypeName(),
                        shape = RoundedCornerShape(5.dp)
                    )
                    .padding(2.dp),
                color = Color.White,
            )
        }
    }
}

/**
 * ノーマル
 * かくとう
 * どく
 * じめん
 * ひこう
 * エスパー
 * むし
 * いわ
 * ゴースト
 * ドラゴン
 * あく
 * はがね
 * フェアリー
 * ほのお
 * みず
 * でんき
 * くさ
 * ダーク
 * こおり
 * 未知
 */
enum class TypeName(
    val typeName: String,
    val jaTypeName: String,
    val number: String,
    val color: Color
) {
    NORMAL("normal", "ノーマル", "1", Color(color = 0xFFAEAEAE)),
    FIGHTING("fighting", "かくとう", "2", Color(color = 0xFFEE6969)),
    POISON("poison", "どく", "4", Color(color = 0xFFAB7ACA)),
    GROUND("ground", "じめん", "5", Color(color = 0xFFC8A841)),
    FLYING("flying", "ひこう", "3", Color(color = 0xFF64A7F1)),
    PSYCHIC("psychic", "エスパー", "14", Color(color = 0xFF9AC30E)),
    BUG("bug", "むし", "7", Color(color = 0xFF51CB5A)),
    ROCK("rock", "いわ", "6", Color(color = 0xFFFAC727)),
    GHOST("ghost", "ゴースト", "8", Color(color = 0xFF756EB4)),
    DRAGON("dragon", "ドラゴン", "16", Color(color = 0xFF9AC30E)),
    DARK("dark", "あく", "17", Color(color = 0xFFFF8859)),
    STEEL("steel", "はがね", "9", Color(color = 0xFF818AA4)),
    FAIRY("fairy", "フェアリー", "18", Color(color = 0xFFFC7799)),
    FIRE("fire", "ほのお", "10", Color(color = 0xFFFFA766)),
    WATER("water", "みず", "11", Color(color = 0xFF64C5F7)),
    ELECTRIC("electric", "でんき", "13", Color(color = 0xFFE7D400)),
    GRASS("grass", "くさ", "12", Color(color = 0xFF9AC30E)),
    SHADOW("shadow", "ダーク", "10002", Color(color = 0xFF333333)),
    ICE("ice", "こおり", "15", Color(color = 0xFF60E9F5)),
    UNKNOWN("unknown", "未知", "10001", Color(color = 0xFFAEAEAE)),
}

/**
 * 日本語名 -> TypeNumber
 */
fun String.convertToTypeNumber(): String {
    return TypeName.values().find { it.jaTypeName == this }?.number ?: ""
}

/**
 * TypeNumber -> 日本語名
 */
fun String.convertToJaTypeName(): String {
    return TypeName.values().find { it.number == this }?.jaTypeName ?: ""
}

/**
 * typeName -> 日本語名
 */
fun String.convertToJaTypeNameByTypeName(): String {
    return TypeName.values().find { it.typeName == this }?.jaTypeName ?: ""
}

/**
 * 日本語名 -> カラーコードに変換
 */
fun String.convertToColorCode(): Color {
    return TypeName.values().find { it.jaTypeName == this }?.color ?: Color(color = 0xFFAEAEAE)
}

/**
 * typeName -> カラーコード
 */
fun String.convertToColorCodeByTypeName(): Color {
    return TypeName.values().find { it.typeName == this }?.color ?: Color(color = 0xFFAEAEAE)
}
