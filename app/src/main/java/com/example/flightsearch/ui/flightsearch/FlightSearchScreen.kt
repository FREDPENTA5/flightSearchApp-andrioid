package com.example.flightsearch.ui.flightsearch

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.flightsearch.data.preferences.ThemePreference
import com.example.flightsearch.data.repository.AirportSummary
import com.example.flightsearch.data.repository.FlightRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightSearchScreen(
    uiState: FlightSearchUiState,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    onSelectAirport: (AirportSummary) -> Unit,
    onToggleFavorite: (FlightRoute) -> Unit,
    onThemePreferenceChange: (ThemePreference) -> Unit
) {
    val themeMenuExpanded = remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(AppTab.Home) }

    if (!uiState.isAppReady) {
        SplashLoadingScreen()
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("SkyRoute", style = MaterialTheme.typography.titleLarge)
                        Text(
                            "Flight route planner",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { themeMenuExpanded.value = true }) {
                        val icon = when (uiState.themePreference) {
                            ThemePreference.LIGHT -> Icons.Default.LightMode
                            ThemePreference.DARK -> Icons.Default.DarkMode
                            ThemePreference.SYSTEM -> Icons.Default.SettingsBrightness
                        }
                        Icon(icon, contentDescription = "Theme")
                    }
                    androidx.compose.material3.DropdownMenu(
                        expanded = themeMenuExpanded.value,
                        onDismissRequest = { themeMenuExpanded.value = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("System") },
                            onClick = {
                                onThemePreferenceChange(ThemePreference.SYSTEM)
                                themeMenuExpanded.value = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Light") },
                            onClick = {
                                onThemePreferenceChange(ThemePreference.LIGHT)
                                themeMenuExpanded.value = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Dark") },
                            onClick = {
                                onThemePreferenceChange(ThemePreference.DARK)
                                themeMenuExpanded.value = false
                            }
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                NavigationBarItem(
                    selected = selectedTab == AppTab.Home,
                    onClick = { selectedTab = AppTab.Home },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = selectedTab == AppTab.Favorites,
                    onClick = { selectedTab = AppTab.Favorites },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Favorites") },
                    label = { Text("Favorites") }
                )
                NavigationBarItem(
                    selected = selectedTab == AppTab.Settings,
                    onClick = { selectedTab = AppTab.Settings },
                    icon = { Icon(Icons.Default.Tune, contentDescription = "Settings") },
                    label = { Text("Settings") }
                )
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            AppTab.Home -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    DecorativeCornerAccent(modifier = Modifier.align(Alignment.TopEnd))
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Text(
                                text = "Where are you flying from?",
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Text(
                                text = "Enter a city, airport, or code to begin.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = uiState.query,
                                onValueChange = onQueryChange,
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Search departure airport") },
                                singleLine = true,
                                trailingIcon = {
                                    if (uiState.query.isNotBlank()) {
                                        IconButton(onClick = onClearQuery) {
                                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                                        }
                                    }
                                }
                            )

                            AnimatedVisibility(
                                visible = uiState.suggestions.isNotEmpty(),
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                    )
                                ) {
                                    Column(modifier = Modifier.padding(vertical = 6.dp)) {
                                        uiState.suggestions.take(6).forEach { airport ->
                                            SuggestionRow(
                                                airport = airport,
                                                onClick = { onSelectAirport(airport) }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        if (uiState.isLoading) {
                            item { Text("Searching airports...", style = MaterialTheme.typography.bodyMedium) }
                        }

                        if (uiState.query.isNotBlank() && uiState.suggestions.isEmpty() && !uiState.isLoading) {
                            item {
                                Text(
                                    "No airports found. Check spelling or try an airport code.",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        if (uiState.selectedAirport != null) {
                            item {
                                AssistChip(
                                    onClick = {},
                                    label = { Text("Selected: ${uiState.selectedAirport.iataCode}") },
                                    leadingIcon = {
                                        Icon(Icons.Default.FlightTakeoff, contentDescription = null)
                                    },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                    )
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                SectionTitle("Popular routes from ${uiState.selectedAirport.iataCode}")
                            }
                            items(uiState.routeResults, key = { "${it.departureCode}-${it.destinationCode}" }) { route ->
                                RouteCard(
                                    route = route,
                                    isFavorite = uiState.favoriteRoutes.any {
                                        it.departureCode == route.departureCode &&
                                            it.destinationCode == route.destinationCode
                                    },
                                    onToggleFavorite = { onToggleFavorite(route) }
                                )
                            }
                        } else if (uiState.query.isBlank()) {
                            item { SectionTitle("Quick favorites") }
                            if (uiState.favoriteRoutes.isEmpty()) {
                                item {
                                    Text(
                                        "You do not have favorite routes yet. Search an airport and star routes to save them.",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            } else {
                                items(uiState.favoriteRoutes.take(3), key = { "${it.departureCode}-${it.destinationCode}" }) { route ->
                                    RouteCard(route = route, isFavorite = true, onToggleFavorite = { onToggleFavorite(route) })
                                }
                            }
                        }
                    }
                }
            }

            AppTab.Favorites -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item { SectionTitle("Saved routes") }
                    if (uiState.favoriteRoutes.isEmpty()) {
                        item {
                            Text(
                                "No favorites yet. Save routes from Home to see them here.",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    } else {
                        items(uiState.favoriteRoutes, key = { "${it.departureCode}-${it.destinationCode}" }) { route ->
                            RouteCard(route = route, isFavorite = true, onToggleFavorite = { onToggleFavorite(route) })
                        }
                    }
                }
            }

            AppTab.Settings -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SectionTitle("Appearance")
                    Text(
                        "Use the top-right icon to switch between System, Light, and Dark mode.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    SectionTitle("How this app works")
                    Text(
                        "1) Search and select a departure airport.\n" +
                            "2) Browse suggested destination routes.\n" +
                            "3) Save important routes in Favorites.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun AirportRow(airport: AirportSummary, onSelect: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(airport.iataCode, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(airport.name, style = MaterialTheme.typography.bodyLarge)
            }
            TextButton(onClick = onSelect) {
                Icon(Icons.Default.Route, contentDescription = null)
                Spacer(modifier = Modifier.height(0.dp))
                Text("Routes")
            }
        }
    }
}

@Composable
private fun SuggestionRow(airport: AirportSummary, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = airport.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = airport.iataCode,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Text(
                text = "Select",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun RouteCard(route: FlightRoute, isFavorite: Boolean, onToggleFavorite: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "${route.departureCode} -> ${route.destinationCode}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssistChip(
                        onClick = {},
                        label = { Text("Popular") },
                        leadingIcon = { Icon(Icons.Default.Route, contentDescription = null) }
                    )
                    if (isFavorite) {
                        AssistChip(
                            onClick = {},
                            label = { Text("Saved") },
                            leadingIcon = { Icon(Icons.Default.Bookmark, contentDescription = null) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.20f)
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(route.departureName, style = MaterialTheme.typography.bodyMedium)
                Text(route.destinationName, style = MaterialTheme.typography.bodyMedium)
            }
            IconToggleButton(checked = isFavorite, onCheckedChange = { onToggleFavorite() }) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                    contentDescription = if (isFavorite) "Remove favorite" else "Save favorite",
                    tint = if (isFavorite) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SplashLoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            CircularProgressIndicator()
            Text("Preparing your flight dashboard...", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun DecorativeCornerAccent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .offset(x = 90.dp, y = (-90).dp)
            .width(220.dp)
            .height(220.dp)
            .clip(MaterialTheme.shapes.extraLarge)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.22f),
                        Color.Transparent
                    )
                )
            )
    )
}

private enum class AppTab {
    Home, Favorites, Settings
}
