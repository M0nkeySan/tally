package io.github.m0nkeysan.gamekeeper.ui.screens.yahtzee.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.m0nkeysan.gamekeeper.core.model.CategoryStat
import io.github.m0nkeysan.gamekeeper.core.model.GlobalCategoryStat
import io.github.m0nkeysan.gamekeeper.core.model.YahtzeeCategory
import io.github.m0nkeysan.gamekeeper.ui.screens.yahtzee.formatAverage
import io.github.m0nkeysan.gamekeeper.ui.screens.yahtzee.getCategoryColor

/**
 * Color-coded heatmap displaying average scores for each Yahtzee category
 */
@Composable
fun CategoryHeatmap(
    categoryStats: Map<YahtzeeCategory, CategoryStat>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Upper section
        Text(
            text = "UPPER SECTION",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 4.dp)
        )
        
        val upperCategories = YahtzeeCategory.entries.filter { it.isUpperSection() }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            upperCategories.forEach { category ->
                CategoryBox(
                    category = category,
                    stat = categoryStats[category],
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Lower section
        Text(
            text = "LOWER SECTION",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 4.dp)
        )
        
        val lowerCategories = YahtzeeCategory.entries.filter { it.isLowerSection() }
        
        // First row of lower section (3 of a Kind, 4 of a Kind, Full House)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            lowerCategories.take(3).forEach { category ->
                CategoryBox(
                    category = category,
                    stat = categoryStats[category],
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        // Second row of lower section (Small Straight, Large Straight, Chance)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            lowerCategories.drop(3).take(3).forEach { category ->
                CategoryBox(
                    category = category,
                    stat = categoryStats[category],
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        // Third row of lower section (Yahtzee)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CategoryBox(
                category = lowerCategories.last(),
                stat = categoryStats[lowerCategories.last()],
                modifier = Modifier.weight(1f)
            )
            // Padding boxes to maintain alignment
            repeat(2) {
                Box(modifier = Modifier.weight(1f))
            }
        }
    }
}

/**
 * Global category heatmap for overall statistics
 */
@Composable
fun GlobalCategoryHeatmap(
    categoryStats: Map<YahtzeeCategory, GlobalCategoryStat>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Upper section
        Text(
            text = "UPPER SECTION",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 4.dp)
        )
        
        val upperCategories = YahtzeeCategory.entries.filter { it.isUpperSection() }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            upperCategories.forEach { category ->
                GlobalCategoryBox(
                    category = category,
                    stat = categoryStats[category],
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Lower section
        Text(
            text = "LOWER SECTION",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 4.dp)
        )
        
        val lowerCategories = YahtzeeCategory.entries.filter { it.isLowerSection() }
        
        // First row of lower section (3 of a Kind, 4 of a Kind, Full House)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            lowerCategories.take(3).forEach { category ->
                GlobalCategoryBox(
                    category = category,
                    stat = categoryStats[category],
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        // Second row of lower section (Small Straight, Large Straight, Chance)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            lowerCategories.drop(3).take(3).forEach { category ->
                GlobalCategoryBox(
                    category = category,
                    stat = categoryStats[category],
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        // Third row of lower section (Yahtzee)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            GlobalCategoryBox(
                category = lowerCategories.last(),
                stat = categoryStats[lowerCategories.last()],
                modifier = Modifier.weight(1f)
            )
            // Padding boxes to maintain alignment
            repeat(2) {
                Box(modifier = Modifier.weight(1f))
            }
        }
    }
}

/**
 * Individual category box in the heatmap
 */
@Composable
private fun CategoryBox(
    category: YahtzeeCategory,
    stat: CategoryStat?,
    modifier: Modifier = Modifier
) {
    val color = if (stat != null) getCategoryColor(stat) else MaterialTheme.colorScheme.surfaceVariant
    val average = stat?.average ?: 0.0
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.2f))
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = category.displayName.split(" ").first().uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            Text(
                text = formatAverage(average),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = color,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Individual global category box in the heatmap
 */
@Composable
private fun GlobalCategoryBox(
    category: YahtzeeCategory,
    stat: GlobalCategoryStat?,
    modifier: Modifier = Modifier
) {
    val color = if (stat != null) getCategoryColor(stat.average) else MaterialTheme.colorScheme.surfaceVariant
    val average = stat?.average ?: 0.0
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.2f))
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = category.displayName.split(" ").first().uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            Text(
                text = formatAverage(average),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = color,
                textAlign = TextAlign.Center
            )
        }
    }
}
