package me.alexkovrigin.splitthebill.ui.views.userselection

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.flowlayout.FlowRow
import me.alexkovrigin.splitthebill.data.entity.User
import me.alexkovrigin.splitthebill.utilities.PaletteUtils
import me.alexkovrigin.splitthebill.viewmodels.MainActivityViewModel
import me.alexkovrigin.splitthebill.viewmodels.PayerSelectionViewModel

@Composable
fun PayerSelectScreen(
    navigateToReceiptSplitting: (payers: List<User>) -> Unit,
    viewModel: MainActivityViewModel = viewModel(MainActivityViewModel::class.java),
) {
    val payerViewModel = viewModel(PayerSelectionViewModel::class.java)

    val users by viewModel.getAllUsers().observeAsState(listOf())
    val selectedUsers = payerViewModel.chipsModel
    var userInput by rememberSaveable { mutableStateOf("") }

    ConstraintLayout {
        Column {
            Button(onClick = { navigateToReceiptSplitting(selectedUsers) }) {
                Text("Done")
            }
            FlowRow {
                selectedUsers.forEachIndexed { index, user ->
                    println(user)
                    Chip(
                        label = user.displayName,
                        color = PaletteUtils.pickColorForUser(user),
                        onClose = {
                            selectedUsers.removeAt(index)
                        })
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextField(value = userInput, onValueChange = { it ->
                userInput = if (it.contains('\n')) {
                    val formattedName = it.filterNot { it == '\n' }
                    selectedUsers.add(User(formattedName, formattedName))
                    ""
                } else {
                    it
                }
            })
            LazyColumn {
                items(users) { user ->
                    // TODO: 23.08.2021 existing users ui
                }
            }
        }

    }
}