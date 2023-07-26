package com.example.pokebook.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pokebook.R

@Composable
fun PokemonDetailScreen(
    onClickCard: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column {
        Image(
            imageVector = ImageVector.vectorResource(
                id = R.drawable.arrow_back_fill0_wght400_grad0_opsz48
            ),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Start)
                .clickable { onClickCard.invoke() }
        )
        TitleImage()
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(6.dp)
        ) {
            Text(
                text = "ピカチュウ",
                fontSize = 50.sp,
                modifier = modifier
                    .align(Alignment.CenterHorizontally),
            )
            Text(
                text = "ほっぺたの　両側に\n小さい　電気袋を　持つ。\nピンチのときに　放電する。",
                fontSize = 15.sp,
                modifier = modifier
                    .align(Alignment.CenterHorizontally),
            )

            BaseInfo(
                modifier = modifier
                    .align(Alignment.CenterHorizontally)
            )
            Ability(
                modifier = modifier
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = modifier.height(10.dp))
        }
    }
}

@Composable
private fun TitleImage() {
    Card(
        modifier = Modifier
            .padding(start = 8.dp, end = 8.dp, top = 20.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            contentAlignment = Alignment.TopEnd,
            modifier = Modifier
                .background(Color.Yellow)
        ) {
            Image(
                imageVector = ImageVector.vectorResource(
                    id = R.drawable.favorite_fill0_wght400_grad0_opsz48
                ),
                contentDescription = null,
                modifier = Modifier
                    .padding(5.dp)
                    .size(30.dp)
            )
            //  TODO　後々こちらに置き換える
            //            AsyncImage(
//                model = ImageRequest.Builder(context = LocalContext.current)
//                    .data(pokemon.imageUri)
//                    .crossfade(true)
//                    .build(),
//                modifier = Modifier.padding(bottom = 20.dp),
//                contentScale = ContentScale.Crop,
//                contentDescription = null
//            )
            Image(
                painter = painterResource(id = R.drawable.poke),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )
        }
    }
}


@Composable
private fun BaseInfo(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Text(
            text = "分類：ネズミポケモン",
            fontSize = 20.sp,
            modifier = modifier.padding(start = 10.dp, end = 10.dp, top = 5.dp)
        )
        Text(
            text = "タイプ：でんき",
            fontSize = 20.sp,
            modifier = modifier.padding(start = 10.dp, end = 10.dp, top = 5.dp)
        )
        Row(
            modifier = modifier
                .padding(bottom = 10.dp)
        ) {
            Text(
                text = "重さ：6.0kg",
                fontSize = 20.sp,
                modifier = modifier
                    .padding(start = 10.dp, end = 10.dp, top = 5.dp)
            )
            Text(
                text = "高さ：0.4m",
                fontSize = 20.sp,
                modifier = modifier
                    .padding(start = 10.dp, end = 10.dp, top = 5.dp)
            )
        }
    }
}

@Composable
private fun Ability(
    modifier: Modifier = Modifier
) {
    // TODO ゆくゆくはグラフ表示させたい
    Card(
        modifier = modifier
            .padding(start = 20.dp, end = 20.dp)
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        elevation = CardDefaults.cardElevation(4.dp),
    ) {
        Row(
            modifier = modifier
                .padding(horizontal = 10.dp)
        ) {
            Text(
                text = "ＨＰ：３５",
                fontSize = 20.sp,
                modifier = modifier
                    .padding(start = 10.dp, end = 10.dp, top = 5.dp),
                textAlign = TextAlign.Center
            )
            Text(
                text = "攻撃：５５",
                fontSize = 20.sp,
                modifier = modifier.padding(start = 10.dp, end = 10.dp, top = 5.dp)
            )
        }
        Row(
            modifier = modifier
                .padding(bottom = 10.dp)
        ) {
            Text(
                text = "防御：４０",
                fontSize = 20.sp,
                modifier = modifier.padding(start = 10.dp, end = 10.dp, top = 5.dp)
            )
            Text(
                text = "ＳＰＥＥＤ：９０",
                fontSize = 20.sp,
                modifier = modifier.padding(start = 10.dp, end = 10.dp, top = 5.dp)
            )
        }
    }
}