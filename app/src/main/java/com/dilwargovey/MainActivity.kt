package com.dilwargovey

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Expense(val name: String, val amount: Double)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("expenses", MODE_PRIVATE)

        setContent {
            var name by remember { mutableStateOf("") }
            var amount by remember { mutableStateOf("") }
            var expenses by remember {
                mutableStateOf(
                    prefs.getStringSet("items", emptySet())!!
                        .mapNotNull {
                            val p = it.split("|")
                            if (p.size == 2)
                                Expense(p[0], p[1].toDoubleOrNull() ?: 0.0)
                            else null
                        }
                )
            }

            MaterialTheme {
                Column(
                    modifier = Modifier.fillMaxSize().padding(20.dp)
                ) {
                    Text("Dilwar Govey", style = MaterialTheme.typography.h4)
                    Text("Daily Expense Tracker")

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Expense name") }
                    )

                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("Amount") }
                    )

                    Button(onClick = {
                        val value = amount.toDoubleOrNull()
                        if (name.isNotBlank() && value != null && value > 0) {
                            expenses = expenses + Expense(name, value)
                            prefs.edit().putStringSet(
                                "items",
                                expenses.map { "${it.name}|${it.amount}" }.toSet()
                            ).apply()
                            name = ""
                            amount = ""
                        }
                    }) {
                        Text("Add Expense")
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text("Total: ${expenses.sumOf { it.amount }}")

                    LazyColumn {
                        items(expenses) { expense ->
                            Text("${expense.name}: ${expense.amount}")
                            Divider()
                        }
                    }
                }
            }
        }
    }
}
