package com.mlmesa.savingdays.ui.statistics

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.developerstring.jetco.ui.charts.barchart.ColumnBarChart
import com.developerstring.jetco.ui.charts.barchart.config.BarChartDefaults
import com.developerstring.jetco.ui.charts.piechart.PieChart
import com.developerstring.jetco.ui.charts.piechart.config.PieChartDefaults
import com.mlmesa.savingdays.R
import com.mlmesa.savingdays.data.model.CurrencyScale
import com.mlmesa.savingdays.ui.components.ScreenTitle
import com.mlmesa.savingdays.ui.theme.Saving365Theme

@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currencyScale by viewModel.currencyScale.collectAsStateWithLifecycle()

    StatisticsScreen(
        modifier = Modifier,
        uiState = uiState,
        currencyScale = currencyScale,
        selectYear = viewModel::selectYear
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    modifier: Modifier = Modifier,
    uiState: StatisticsUiState,
    currencyScale: CurrencyScale,
    selectYear: (Int) -> Unit = {}
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { ScreenTitle(title = stringResource(R.string.statistics_screen_title)) })
        }
    ){ paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            // Summary cards
            SummaryCardsRow(
                totalSaved = uiState.totalSavedAllTime,
                bestMonth = uiState.monthlySavings.maxByOrNull { it.totalSaved },
                currencyScale = currencyScale
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Monthly chart section
            MonthlyChartSection(
                monthlySavings = uiState.monthlySavings,
                selectedYear = uiState.selectedYear,
                availableYears = uiState.availableYears,
                currencyScale = currencyScale,
                onYearSelected = selectYear
            )

            Spacer(modifier = Modifier.height(24.dp))

            YearlyProgressSection(
                totalSaved = uiState.totalSavedAllTime,
                totalTarget = uiState.totalToSave,
                difference = uiState.difference,
                currencyScale = currencyScale
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Yearly chart section
            if (uiState.yearlySavings.size > 1) {
                YearlyChartSection(
                    yearlySavings = uiState.yearlySavings,
                    currencyScale = currencyScale
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun SummaryCardsRow(
    totalSaved: Int,
    bestMonth: MonthlySaving?,
    currencyScale: CurrencyScale
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Savings,
            label = stringResource(R.string.summary_card_total_saved),
            value = currencyScale.formatAmount(totalSaved),
            gradientColors = listOf(
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.tertiary
            )
        )
        SummaryCard(
            modifier = Modifier.weight(1f),
            icon = Icons.AutoMirrored.Filled.TrendingUp,
            label = stringResource(R.string.summary_card_best_month),
            value = if (bestMonth != null && bestMonth.totalSaved > 0)
                "${bestMonth.monthName} ${currencyScale.formatAmount(bestMonth.totalSaved)}"
            else "—",
            gradientColors = listOf(
                MaterialTheme.colorScheme.secondary,
                MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
private fun SummaryCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String,
    gradientColors: List<Color>
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(gradientColors.map { it.copy(alpha = 0.15f) })
                )
                .padding(16.dp)
        ) {
            Column {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = gradientColors.first(),
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun MonthlyChartSection(
    monthlySavings: List<MonthlySaving>,
    selectedYear: Int,
    availableYears: List<Int>,
    currencyScale: CurrencyScale,
    onYearSelected: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.monthly_chart_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Year chips
            if (availableYears.size > 1) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    availableYears.forEach { year ->
                        FilterChip(
                            selected = year == selectedYear,
                            onClick = { onYearSelected(year) },
                            label = { Text(year.toString()) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (monthlySavings.isEmpty() || monthlySavings.all { it.totalSaved == 0 }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_data_year),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                val chartData = monthlySavings.associate { monthly ->
                    monthly.monthName to (currencyScale.calculateAmount(monthly.totalSaved)).toFloat()
                }

                ColumnBarChart(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(270.dp),
                    chartData = chartData,
                    barChartConfig = BarChartDefaults.columnBarChartConfig(
                        color = MaterialTheme.colorScheme.primary,
                        width = 16.dp,
                        shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
                    ),
                    yAxisConfig = BarChartDefaults.yAxisConfig(
                        textStyle = MaterialTheme.typography.labelSmall,
                    ),
                    xAxisConfig = BarChartDefaults.xAxisConfig(
                        textStyle = MaterialTheme.typography.labelSmall,
                    ),
                    enableAnimation = true,
                    scrollEnable = true,
                    maxTextLengthXAxis = 3,
                    enableGridLines = true
                )
            }
        }
    }
}


@Composable
private fun YearlyProgressSection(
    totalSaved: Int,
    totalTarget: Int,
    difference: Int,
    currencyScale: CurrencyScale
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            val data = mapOf(
                stringResource(R.string.total_saved) to currencyScale.calculateAmount(totalSaved).toFloat(),
                stringResource(R.string.total_to_save) to currencyScale.calculateAmount(difference).toFloat(),
            )

            PieChart(
                chartData = data,
                pieChartConfig = PieChartDefaults.pieChartConfig(
                    radius = 80.dp,
                    thickness = 30.dp,
                    textStyle = MaterialTheme.typography.labelSmall,
                    colorsList = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.error
                    )
                )
            )
            SuggestionChip(
                onClick = { },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Savings,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                },
                label = {
                    Text(text = stringResource(
                        R.string.final_goal,
                        currencyScale.formatAmount(totalTarget)
                    ), style = MaterialTheme.typography.labelMedium)
                },
                colors = SuggestionChipDefaults.suggestionChipColors(),
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary

                )

            )
        }

    }
}

@Composable
private fun YearlyChartSection(
    yearlySavings: List<YearlySaving>,
    currencyScale: CurrencyScale
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.summary_card_yearly_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            val chartData = yearlySavings.associate { yearly ->
                yearly.year.toString() to (currencyScale.calculateAmount(yearly.totalSaved)).toFloat()
            }

            ColumnBarChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(270.dp),
                chartData = chartData,
                barChartConfig = BarChartDefaults.columnBarChartConfig(
                    color = MaterialTheme.colorScheme.secondary,
                    width = 40.dp,
                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                ),
                yAxisConfig = BarChartDefaults.yAxisConfig(
                    textStyle = MaterialTheme.typography.labelSmall,
                ),
                xAxisConfig = BarChartDefaults.xAxisConfig(
                    textStyle = MaterialTheme.typography.labelSmall,
                ),
                enableAnimation = true,
                scrollEnable = false,
                maxTextLengthXAxis = 4,
                enableGridLines = true
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun StatisticsScreenPreview() {

    val mockMonthlySavings = listOf(
        MonthlySaving(1, "Ene", 500, 20),
        MonthlySaving(2, "Feb", 800, 25),
        MonthlySaving(3, "Mar", 1200, 30),
        MonthlySaving(4, "Abr", 400, 15),
        MonthlySaving(5, "May", 950, 28),
        MonthlySaving(6, "Jun", 1100, 30)
    )

    val mockYearlySavings = listOf(
        YearlySaving(2023, 12000),
        YearlySaving(2024, 15000)
    )

    val mockUiState = StatisticsUiState(
        isLoading = false,
        totalSavedAllTime = 27000,
        monthlySavings = mockMonthlySavings,
        yearlySavings = mockYearlySavings,
        selectedYear = 2024,
        availableYears = listOf(2023, 2024),
        totalToSave = 30000,
        difference = 1000
    )

    val mockCurrencyScale = CurrencyScale.MEXICO

    Saving365Theme() { // Usa el tema de tu aplicación
        StatisticsScreen(
            uiState = mockUiState,
            currencyScale = mockCurrencyScale,
            selectYear = { }
        )
    }
}
