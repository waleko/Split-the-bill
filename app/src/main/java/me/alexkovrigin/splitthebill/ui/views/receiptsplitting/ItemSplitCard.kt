package me.alexkovrigin.splitthebill.ui.views.receiptsplitting

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerScope
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState
import me.alexkovrigin.splitthebill.data.entity.Item
import me.alexkovrigin.splitthebill.data.entity.User
import me.alexkovrigin.splitthebill.ui.views.common.Examples
import me.alexkovrigin.splitthebill.ui.views.common.ItemDisplay
import me.alexkovrigin.splitthebill.viewmodels.ReceiptSplittingViewModel
import kotlin.math.absoluteValue

@ExperimentalPagerApi
@Composable
fun PagerScope.ItemSplitCard(
    item: Item,
    page: Int,
    receiptSplittingViewModel: ReceiptSplittingViewModel
) {
    ItemSplitCard(
        page,
        item,
        users = receiptSplittingViewModel.rc.users,
        splitting = receiptSplittingViewModel.rc.splitting[page]
            ?: error("No $page page in the VM for $item"),
        onItemSplitChanged = { page, userIndex, enabled ->
            receiptSplittingViewModel.rc.simpleSwitchUser(page, userIndex, enabled)
        }
    )
}

@ExperimentalPagerApi
@Composable
private fun PagerScope.ItemSplitCard(
    page: Int,
    item: Item,
    users: List<User>,
    splitting: MutableList<Int?>,
    onItemSplitChanged: (page: Int, userIndex: Int, enabled: Boolean) -> Unit
) {
    Card(
        elevation = 3.dp,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .graphicsLayer {
                val pageOffset = calculateCurrentOffsetForPage(page).absoluteValue

                lerp(
                    start = 0.85f,
                    stop = 1f,
                    fraction = 1f - pageOffset.coerceIn(0f, 1f)
                ).also { scale ->
                    scaleX = scale
                    scaleY = scale
                }

                alpha = lerp(
                    start = 0.5f,
                    stop = 1f,
                    fraction = 1f - pageOffset.coerceIn(0f, 1f)
                )
            }
            .fillMaxWidth(0.9f)
            .fillMaxHeight(0.9f)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 10.dp)
        ) {
            Column {
                ItemDisplay(item = item)
                Spacer(
                    modifier = Modifier.height(20.dp)
                )
                LazyColumn {
                    itemsIndexed(users) { index, user ->
                        UserSplittingCard(user = user, sum = splitting[index], onCheckedChange = {
                            onItemSplitChanged(page, index, it)
                        })
                    }
                }
            }

        }
    }
}

@ExperimentalPagerApi
@Preview
@Composable
fun ItemSplitCardPreview() {
    val pagerState = rememberPagerState(pageCount = 1)

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        val item = Examples.SimpleItem
        ItemSplitCard(
            item = item,
            page = page,
            users = listOf(
                User("Alex", "Alex"),
                User("Bill", "Bill"),
                User("Charlie", "Charlie")
            ),
            splitting = mutableListOf(null, 10, 32000),
            onItemSplitChanged = { _, _, _ -> }
        )
    }
}