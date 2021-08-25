package me.alexkovrigin.splitthebill.ui.views.receiptsplitting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Nav buttons for receiptsplitting screen
 */
@ExperimentalPagerApi
@Composable
fun PagerNavButtons(
    pagerState: PagerState,
    animationScope: CoroutineScope,
    onFinished: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        val isFirst = pagerState.currentPage == 0
        if (!isFirst) {
            Button(
                onClick = {
                    animationScope.launch {
                        pagerState.animateScrollToPage(page = pagerState.targetPage - 1)
                    }
                },
                enabled = pagerState.currentPage != 0
            ) {
                Text("Back")
            }
            Spacer(modifier = Modifier.width(80.dp))
        }

        val isLast = pagerState.currentPage + 1 == pagerState.pageCount
        if (isLast) {
            Button(
                onClick = onFinished
            ) {
                Text("Finish")
            }
        } else {
            Button(
                onClick = {
                    animationScope.launch {
                        pagerState.animateScrollToPage(page = pagerState.targetPage + 1)
                    }
                }
            ) {
                Text("Next")
            }
        }
    }
    Spacer(modifier = Modifier.height(10.dp))
}

@ExperimentalPagerApi
@Preview
@Composable
fun PagerNavButtonsPreview() {
    val pagerState = rememberPagerState(pageCount = 2)
    val animationScope = rememberCoroutineScope()
    PagerNavButtons(pagerState = pagerState, animationScope = animationScope) {}
}