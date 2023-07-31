package com.example.pokebook.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
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
import com.example.pokebook.ui.viewModel.PokemonDetailScreenUiData
import java.lang.reflect.Type

@Preview(showBackground = true)
@Composable
fun TypeTagPreview() {
    TypeTag(typeList = listOf("fighting", "poison", "ground", "shadow"))
}


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
                text = when (type) {
                    TypeName.FIGHTING.typeName -> stringResource(R.string.type_name_fighting)
                    TypeName.POISON.typeName -> stringResource(R.string.type_name_poison)
                    TypeName.GROUND.typeName -> stringResource(R.string.type_name_ground)
                    TypeName.FLYING.typeName -> stringResource(R.string.type_name_flying)
                    TypeName.PSYCHIC.typeName -> stringResource(R.string.type_name_psychic)
                    TypeName.BUG.typeName -> stringResource(R.string.type_name_bug)
                    TypeName.ROCK.typeName -> stringResource(R.string.type_name_rock)
                    TypeName.GHOST.typeName -> stringResource(R.string.type_name_ghost)
                    TypeName.DRAGON.typeName -> stringResource(R.string.type_name_dragon)
                    TypeName.DARK.typeName -> stringResource(R.string.type_name_dark)
                    TypeName.STEEL.typeName -> stringResource(R.string.type_name_steel)
                    TypeName.FAIRY.typeName -> stringResource(R.string.type_name_fairy)
                    TypeName.FIRE.typeName -> stringResource(R.string.type_name_fire)
                    TypeName.WATER.typeName -> stringResource(R.string.type_name_water)
                    TypeName.ELECTRIC.typeName -> stringResource(R.string.type_name_electric)
                    TypeName.GRASS.typeName -> stringResource(R.string.type_name_grass)
                    TypeName.SHADOW.typeName -> stringResource(R.string.type_name_shadow)
                    TypeName.ICE.typeName -> stringResource(R.string.type_name_ice)
                    TypeName.NORMAL.typeName -> stringResource(R.string.type_name_normal)
                    TypeName.UNKNOWN.typeName -> stringResource(R.string.type_name_unknown)
                    else -> type
                },
                modifier = modifier
                    .padding(1.dp)
                    .background(
                        color = when (type) {
                            TypeName.FIGHTING.typeName -> Color(color = 0xFFEE6969)
                            TypeName.POISON.typeName -> Color(color = 0xFFAB7ACA)
                            TypeName.GROUND.typeName -> Color(color = 0xFFC8A841)
                            TypeName.FLYING.typeName -> Color(color = 0xFF64A7F1)
                            TypeName.PSYCHIC.typeName -> Color(color = 0xFF9AC30E)
                            TypeName.BUG.typeName -> Color(color = 0xFF51CB5A)
                            TypeName.ROCK.typeName -> Color(color = 0xFFFAC727)
                            TypeName.GHOST.typeName -> Color(color = 0xFF756EB4)
                            TypeName.DRAGON.typeName -> Color(color = 0xFF9AC30E)
                            TypeName.DARK.typeName -> Color(color = 0xFFFF8859)
                            TypeName.STEEL.typeName -> Color(color = 0xFF818AA4)
                            TypeName.FAIRY.typeName -> Color(color = 0xFFFC7799)
                            TypeName.FIRE.typeName -> Color(color = 0xFFFFA766)
                            TypeName.WATER.typeName -> Color(color = 0xFF64C5F7)
                            TypeName.ELECTRIC.typeName -> Color(color = 0xFFE7D400)
                            TypeName.GRASS.typeName -> Color(color = 0xFF9AC30E)
                            TypeName.SHADOW.typeName -> Color(color = 0xFF333333)
                            TypeName.ICE.typeName -> Color(color = 0xFF60E9F5)
                            else -> Color(color = 0xFFAEAEAE) //NORMAL,UNKNOWN
                        },
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
enum class TypeName(val typeName: String) {
    NORMAL("normal"),
    FIGHTING("fighting"),
    POISON("poison"),
    GROUND("ground"),
    FLYING("flying"),
    PSYCHIC("psychic"),
    BUG("bug"),
    ROCK("rock"),
    GHOST("ghost"),
    DRAGON("dragon"),
    DARK("dark"),
    STEEL("steel"),
    FAIRY("fairy"),
    FIRE("fire"),
    WATER("water"),
    ELECTRIC("electric"),
    GRASS("grass"),
    SHADOW("shadow"),
    ICE("ice"),
    UNKNOWN("unknown")
}
