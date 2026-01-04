package com.example.e_system

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.e_system.ui.theme.ESystemTheme

class SearchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ESystemTheme {
                SearchScreen(
                    onBackClicked = { finish() } // Finishes the activity on back press
                )
            }
        }
    }
}

// --- Screen and Core Components ---

@Composable
fun SearchScreen(onBackClicked: () -> Unit) {
    // State to hold the current search query
    var searchQuery by remember { mutableStateOf("") }
    // State to simulate search results (replace with real data fetching)
    val results = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            emptyList()
        } else {
            // Placeholder: Filter a list based on the query
            (1..10).map { "Result Item $it for \"$searchQuery\"" }.filter {
                it.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        topBar = {
            SearchAppBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onBackClicked = onBackClicked
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            if (searchQuery.isBlank()) {
                // Show a friendly message when the search is empty
                InitialSearchPrompt()
            } else {
                SearchResultsList(results = results)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAppBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onBackClicked: () -> Unit
) {
    // Material 3 Search Bar component for an excellent UX
    CenterAlignedTopAppBar(
        title = {
            // The actual search input field
            TextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                placeholder = { Text("Search ") },
                singleLine = true,
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(painter = painterResource(R.drawable.clear), contentDescription = "Clear Search")
                        }
                    }
                },
                // Customize colors to integrate better with the TopAppBar
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.surface,
                )
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClicked) {
                Icon(painter = painterResource(R.drawable.back),
                    modifier = Modifier
                        .size(24.dp)
                    , contentDescription = "Back")
            }
        }
    )
}

@Composable
fun InitialSearchPrompt() {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.search),
            contentDescription = "Search",
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Start typing to find what you need",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun SearchResultsList(results: List<String>) {
    if (results.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No results found.", color = MaterialTheme.colorScheme.error)
        }
        return
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(results) { item ->
            // Use ListItem for a standard, readable line item
            ListItem(
                headlineContent = { Text(item) },
                modifier = Modifier.clickable {
                    // TODO: Handle item click (e.g., navigate to product detail)
                    println("Clicked on: $item")
                }
            )
            Divider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewSearchScreen() {
    ESystemTheme {
        SearchScreen(onBackClicked = {})
    }
}