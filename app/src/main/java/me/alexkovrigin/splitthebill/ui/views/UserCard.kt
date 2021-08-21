package me.alexkovrigin.splitthebill.ui.views

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxColors
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.alexkovrigin.splitthebill.data.entity.User
import me.alexkovrigin.splitthebill.ui.theme.SplitTheBillTheme
import me.alexkovrigin.splitthebill.util.PaletteUtils

@Composable
fun UserCard(
    user: User,
    checkBoxColorsState: State<CheckboxColors>,
    checkBoxClicked: (enabled: Boolean) -> Unit
) {
    Card(
        shape = RoundedCornerShape(6.dp),
        backgroundColor = MaterialTheme.colors.background,
        elevation = 9.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(5.dp)
        ) {
            Checkbox(
                checked = false,
                colors = checkBoxColorsState.value,
                onCheckedChange = {
                    checkBoxClicked(it)
                }
            )
            Spacer(modifier = Modifier.padding(3.dp))
            Text(
                text = user.displayName,
                maxLines = 1,
                modifier = Modifier.padding(2.dp)
            )
        }

    }
}

@Preview
@Composable
fun UserCardPreview() {
    val colors = CheckboxDefaults.colors(
        checkedColor = PaletteUtils.pickColorForUser(0)
    )
    val colorState = remember { mutableStateOf(colors) }
    SplitTheBillTheme {
        Surface(color = MaterialTheme.colors.background) {
            UserCard(
                user = User("qwerty", "Monty Python"),
                checkBoxColorsState = colorState
            ) { }
        }
    }
}