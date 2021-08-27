package me.alexkovrigin.splitthebill.ui.views.receiptsplitting

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import me.alexkovrigin.splitthebill.data.entity.User
import me.alexkovrigin.splitthebill.data.entity.newSplitReceiptInfo
import me.alexkovrigin.splitthebill.viewmodels.MainActivityViewModel
import me.alexkovrigin.splitthebill.viewmodels.ReceiptSplittingViewModel

@ExperimentalPagerApi
@Composable
fun ReceiptSplittingScreen(
    qr: String,
    users: List<User>,
    navigateToSummary: (summaryUID: String) -> Unit,
    viewModel: MainActivityViewModel = viewModel(MainActivityViewModel::class.java)
) {
    val state = viewModel.getSaveableReceiptFromDB(qr).observeAsState()
    val nonFormattedReceipt = state.value ?: return
    // format once and store that value
    val receipt = rememberSaveable { nonFormattedReceipt.format() }
    val pageCount = receipt.items.size

    val receiptSplittingViewModel = viewModel(ReceiptSplittingViewModel::class.java)
    receiptSplittingViewModel.initialize(users, receipt.items)

    val pagerState = rememberPagerState(pageCount = pageCount)

    val animationScope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxHeight(0.9f)
                .fillMaxWidth()
        ) { page ->
            ItemSplitCard(
                item = receipt.items[page],
                page = page,
                receiptSplittingViewModel = receiptSplittingViewModel
            )
        }
        Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Bottom) {
            PagerNavButtons(pagerState, animationScope) {
                val failedList =
                    receiptSplittingViewModel.rc.validateSplittingAndGetIncompletePages()
                if (failedList.isNotEmpty()) {
                    Toast.makeText(context, "Not all items have been split!", Toast.LENGTH_SHORT)
                        .show()
                    animationScope.launch {
                        pagerState.animateScrollToPage(failedList.first())
                    }
                    return@PagerNavButtons
                }

                val splitReceiptInfo = receipt.newSplitReceiptInfo()

                // navigate to summary
                val splitting = receiptSplittingViewModel.rc.produceSplitting()

                viewModel.addNewSplitting(splitReceiptInfo, splitting)
                navigateToSummary(splitReceiptInfo.uid)
            }
        }
    }
}

