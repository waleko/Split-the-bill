package me.alexkovrigin.splitthebill.ui.views.receiptsplitting

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.alexkovrigin.splitthebill.data.entity.User
import me.alexkovrigin.splitthebill.utilities.PaletteUtils
import me.alexkovrigin.splitthebill.utilities.asRubles

@Composable
fun UserSplittingCard(
    user: User,
    sum: Int?,
    onCheckedChange: (enabled: Boolean) -> Unit
) {
    val color =
        rememberSaveable(saver = PaletteUtils.colorSaver) { PaletteUtils.pickColorForUser(user) }

    Card(
        shape = MaterialTheme.shapes.small,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(4.dp)
        ) {
            Checkbox(
                checked = sum != null,
                colors = CheckboxDefaults.colors(checkedColor = color),
                onCheckedChange = onCheckedChange,
            )
            Spacer(
                modifier = Modifier.width(4.dp)
            )
            Text(
                text = user.displayName,
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = sum?.asRubles().orEmpty(),
            )
        }
    }
}

@Preview
@Composable
fun UserSplittingCardPreview() {
    UserSplittingCard(user = User(displayName = "Alex", userId = "Alex"), sum = 10900) {}
}