package com.mlmesa.savingdays.ui.onboarding

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mlmesa.savingdays.data.model.CurrencyScale
import com.mlmesa.savingdays.ui.theme.Saving365Theme

/**
 * Main Onboarding Screen with 4 pages (Stateful)
 */
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = hiltViewModel(),
    onOnboardingComplete: () -> Unit
) {
    val currentPage by viewModel.currentPage.collectAsStateWithLifecycle()
    val selectedScale by viewModel.selectedScale.collectAsStateWithLifecycle()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsStateWithLifecycle()
    val onboardingCompleted by viewModel.onboardingCompleted.collectAsStateWithLifecycle()

    // Navigate when onboarding is completed
    LaunchedEffect(onboardingCompleted) {
        if (onboardingCompleted) {
            onOnboardingComplete()
        }
    }

    OnboardingContent(
        currentPage = currentPage,
        selectedScale = selectedScale,
        notificationsEnabled = notificationsEnabled,
        onScaleSelected = viewModel::selectCurrencyScale,
        onNotificationsToggle = viewModel::setNotificationsEnabled,
        onPrevious = viewModel::previousPage,
        onNext = viewModel::nextPage,
        onFinish = viewModel::completeOnboarding
    )
}

/**
 * Stateless Onboarding Content for easier testing and previews
 */
@Composable
fun OnboardingContent(
    currentPage: Int,
    selectedScale: CurrencyScale,
    notificationsEnabled: Boolean,
    onScaleSelected: (CurrencyScale) -> Unit,
    onNotificationsToggle: (Boolean) -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(modifier = modifier) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Page indicators
            PageIndicator(
                currentPage = currentPage,
                totalPages = 4,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Page content with animation
            AnimatedContent(
                targetState = currentPage,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                    } else {
                        slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
                    }
                },
                modifier = Modifier.weight(1f),
                label = "OnboardingPageAnimation"
            ) { page ->
                when (page) {
                    0 -> WelcomePage()
                    1 -> HowItWorksPage()
                    2 -> CountrySelectionPage(
                        selectedScale = selectedScale,
                        onScaleSelected = onScaleSelected
                    )
                    3 -> NotificationsPage(
                        notificationsEnabled = notificationsEnabled,
                        onNotificationsToggle = onNotificationsToggle
                    )
                }
            }

            // Navigation buttons
            NavigationButtons(
                currentPage = currentPage,
                onPrevious = onPrevious,
                onNext = onNext,
                onFinish = onFinish
            )
        }
    }
}

@Composable
private fun PageIndicator(
    currentPage: Int,
    totalPages: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(totalPages) { index ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(if (index == currentPage) 12.dp else 8.dp)
                    .clip(CircleShape)
                    .background(
                        if (index == currentPage)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
            )
        }
    }
}

@Composable
private fun NavigationButtons(
    currentPage: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onFinish: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (currentPage > 0) {
            OutlinedButton(
                onClick = onPrevious,
                modifier = Modifier.weight(1f)
            ) {
                Text("Anterior")
            }
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }

        if (currentPage < 3) {
            Button(
                onClick = onNext,
                modifier = Modifier.weight(1f)
            ) {
                Text("Siguiente")
            }
        } else {
            Button(
                onClick = onFinish,
                modifier = Modifier.weight(1f)
            ) {
                Text("Comenzar")
            }
        }
    }
}

// ============ PAGE 1: WELCOME ============
@Composable
private fun WelcomePage() {
    OnboardingPageLayout(
        icon = Icons.Default.Savings,
        title = "¡Bienvenido a Ahorra365!",
        content = {
            Text(
                text = "Tu compañero para el reto anual de ahorro más popular del mundo.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Ahorra cada día una cantidad diferente y al final del año habrás acumulado una suma significativa.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    )
}

// ============ PAGE 2: HOW IT WORKS ============
@Composable
private fun HowItWorksPage() {
    OnboardingPageLayout(
        icon = Icons.Default.Info,
        title = "¿Cómo funciona?",
        content = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FeatureItem(
                    icon = Icons.Default.CalendarMonth,
                    title = "365 días, 365 montos",
                    description = "Cada día del año tiene asignado un monto del 1 al 365"
                )
                FeatureItem(
                    icon = Icons.Default.Savings,
                    title = "Orden aleatorio",
                    description = "Los montos aparecen en orden aleatorio para hacer el reto más divertido"
                )
                FeatureItem(
                    icon = Icons.Default.CheckCircle,
                    title = "Marca tu progreso",
                    description = "Cada día que ahorres, márcalo como completado"
                )
            }
        }
    )
}

@Composable
private fun FeatureItem(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ============ PAGE 3: COUNTRY SELECTION ============
@Composable
private fun CountrySelectionPage(
    selectedScale: CurrencyScale,
    onScaleSelected: (CurrencyScale) -> Unit
) {
    OnboardingPageLayout(
        icon = Icons.Default.Public,
        title = "Selecciona tu país",
        content = {
            Text(
                text = "Los montos se ajustarán según tu moneda local",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(CurrencyScale.getAllScales()) { scale ->
                    CountryItem(
                        scale = scale,
                        isSelected = scale == selectedScale,
                        onClick = { onScaleSelected(scale) }
                    )
                }
            }
        }
    )
}

@Composable
private fun CountryItem(
    scale: CurrencyScale,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = stringResource(scale.displayName),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
                Text(
                    text = "Rango: ${scale.symbol}1 - ${scale.formatAmount(365)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ============ PAGE 4: NOTIFICATIONS ============
@Composable
private fun NotificationsPage(
    notificationsEnabled: Boolean,
    onNotificationsToggle: (Boolean) -> Unit
) {
    // Permission launcher for Android 13+
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        onNotificationsToggle(granted)
    }

    OnboardingPageLayout(
        icon = Icons.Default.Notifications,
        title = "Recordatorios diarios",
        content = {
            Text(
                text = "Recibe una notificación diaria para recordarte guardar tu ahorro del día.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Activar notificaciones",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Hora predeterminada: 9:00 AM",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { enabled ->
                            if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                onNotificationsToggle(enabled)
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Puedes cambiar esta configuración en cualquier momento desde Ajustes.",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    )
}

// ============ COMMON LAYOUT ============
@Composable
private fun OnboardingPageLayout(
    icon: ImageVector,
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        content()
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingWelcomePreview() {
    Saving365Theme {
        OnboardingContent(
            currentPage = 0,
            selectedScale = CurrencyScale.MEXICO,
            notificationsEnabled = false,
            onScaleSelected = {},
            onNotificationsToggle = {},
            onPrevious = {},
            onNext = {},
            onFinish = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingHowItWorksPreview() {
    Saving365Theme {
        OnboardingContent(
            currentPage = 1,
            selectedScale = CurrencyScale.MEXICO,
            notificationsEnabled = false,
            onScaleSelected = {},
            onNotificationsToggle = {},
            onPrevious = {},
            onNext = {},
            onFinish = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingCountryPreview() {
    Saving365Theme {
        OnboardingContent(
            currentPage = 2,
            selectedScale = CurrencyScale.MEXICO,
            notificationsEnabled = false,
            onScaleSelected = {},
            onNotificationsToggle = {},
            onPrevious = {},
            onNext = {},
            onFinish = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingNotificationsPreview() {
    Saving365Theme {
        OnboardingContent(
            currentPage = 3,
            selectedScale = CurrencyScale.MEXICO,
            notificationsEnabled = true,
            onScaleSelected = {},
            onNotificationsToggle = {},
            onPrevious = {},
            onNext = {},
            onFinish = {}
        )
    }
}
