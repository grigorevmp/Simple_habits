package com.grigorevmp.habits.ui.habits

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.grigorevmp.habits.ui.home.HabitViewModel


@Composable
fun HabitListModule(habitViewModel: HabitViewModel? = null) {
    var dialogShown by remember { mutableStateOf(false) }

    if (dialogShown) {
        Dialog(
            onDismissRequest = { dialogShown = false }
        ) {
            Surface {
                AddHabit(habitViewModel)
            }
        }
    }

    Surface(Modifier.fillMaxSize()) {
        HabitList(habitViewModel)
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
            FloatingActionButton(
                onClick = { dialogShown = true },
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Localized description")
            }
        }
    }
}

@Composable
fun AddHabit(habitViewModel: HabitViewModel?) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Column(
        Modifier.fillMaxWidth()
    ) {
        TextField(modifier = Modifier.fillMaxWidth(),
            value = title,
            onValueChange = { newValue -> title = newValue },
            label = { Text("Title") })
        TextField(modifier = Modifier.fillMaxWidth(),
            value = description,
            onValueChange = { newValue -> description = newValue },
            label = { Text("Description") })

        Button(modifier = Modifier.align(Alignment.End),
            onClick = { habitViewModel?.addHabit(title, description) }) {
            Text("Add Habit")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeModulePreview() {
    HabitListModule()
}


