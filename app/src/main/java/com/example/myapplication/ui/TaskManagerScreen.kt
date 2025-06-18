package com.example.myapplication.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp // PERBAIKAN: Impor ikon yang benar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.model.Task
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskManagerScreen(
    onLogoutClick: () -> Unit,
    viewModel: TaskViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Task Manager", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = onLogoutClick) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp, // PERBAIKAN: Gunakan ikon yang benar
                            contentDescription = "Logout"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Tugas")
            }
        }
    ) { padding ->
        if (showDialog) {
            TaskInputDialog(
                onAdd = { title, deadline ->
                    viewModel.addTask(title, deadline)
                    showDialog = false
                },
                onDismiss = { showDialog = false }
            )
        }

        val now = LocalDateTime.now()
        val allTasks = uiState.tasks
        val sortMode = uiState.sortMode

        val sortedTasks = if (sortMode == "name") {
            allTasks.sortedBy { it.title.lowercase() }
        } else {
            allTasks.sortedBy { it.deadline }
        }

        val activeTasks = sortedTasks.filter { !it.isDone }
        val completedTasks = sortedTasks.filter { it.isDone }
        val overdueTasks = activeTasks.filter { it.deadline.isBefore(now) }
        val upcomingTasks = activeTasks.filterNot { it.deadline.isBefore(now) }

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                SortButtons(
                    currentSortMode = sortMode,
                    onSortChange = { viewModel.changeSortMode(it) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (overdueTasks.isNotEmpty()) {
                item {
                    Text("Tugas Terlewat", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 8.dp))
                }
                items(overdueTasks, key = { it.id }) { task ->
                    TaskItem(task = task, viewModel = viewModel, isOverdue = true)
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }

            if (upcomingTasks.isNotEmpty()) {
                item {
                    Text("Tugas Aktif", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 8.dp))
                }
                items(upcomingTasks, key = { it.id }) { task ->
                    TaskItem(task = task, viewModel = viewModel)
                }
            }

            if (completedTasks.isNotEmpty()) {
                item {
                    Divider(modifier = Modifier.padding(vertical = 16.dp))
                    Text("Selesai", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 8.dp))
                }
                items(completedTasks, key = { it.id }) { task ->
                    TaskItem(task = task, viewModel = viewModel)
                }
            }
        }
    }
}


@Composable
fun SortButtons(currentSortMode: String, onSortChange: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = { onSortChange("name") },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (currentSortMode == "name") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                contentColor = if (currentSortMode == "name") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
            )
        ) {
            Text("Nama")
        }
        Button(
            onClick = { onSortChange("deadline") },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (currentSortMode == "deadline") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                contentColor = if (currentSortMode == "deadline") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
            )
        ) {
            Text("Deadline")
        }
    }
}

@Composable
fun TaskItem(task: Task, viewModel: TaskViewModel, isOverdue: Boolean = false) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isOverdue) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isDone,
                onCheckedChange = { viewModel.toggleTaskDone(task) }
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = if (task.isDone) TextStyle(textDecoration = TextDecoration.LineThrough) else TextStyle.Default
                )
                Text(
                    text = task.deadline.format(DateTimeFormatter.ofPattern("dd MMM, HH:mm")),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            IconButton(onClick = { viewModel.deleteTask(task) }) {
                Icon(Icons.Default.Delete, contentDescription = "Hapus")
            }
        }
    }
}

@Composable
fun TaskInputDialog(onAdd: (String, LocalDateTime) -> Unit, onDismiss: () -> Unit) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var selectedDateTime by remember { mutableStateOf<LocalDateTime?>(null) }
    val formatter = DateTimeFormatter.ofPattern("dd MMM yy, HH:mm")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Tugas Baru") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Judul Tugas") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(onClick = {
                        val calendar = Calendar.getInstance()
                        DatePickerDialog(context, { _, y, m, d ->
                            TimePickerDialog(context, { _, h, min ->
                                selectedDateTime = LocalDateTime.of(y, m + 1, d, h, min)
                            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
                    }) {
                        Text("Pilih Waktu")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = selectedDateTime?.format(formatter) ?: "Belum diatur",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && selectedDateTime != null) {
                        onAdd(title, selectedDateTime!!)
                    }
                },
                enabled = title.isNotBlank() && selectedDateTime != null
            ) {
                Text("Tambah")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}