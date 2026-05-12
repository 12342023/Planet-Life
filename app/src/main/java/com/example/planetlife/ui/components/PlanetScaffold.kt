package com.example.planetlife.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.example.planetlife.navigation.AppRoute
import com.example.planetlife.ui.theme.CityGold
import com.example.planetlife.ui.theme.Cream
import com.example.planetlife.ui.theme.CreamBorder
import com.example.planetlife.ui.theme.CreamLight
import com.example.planetlife.ui.theme.CrystalBlue
import com.example.planetlife.ui.theme.DesertOrange
import com.example.planetlife.ui.theme.DreamPurple
import com.example.planetlife.ui.theme.ForestGreen
import com.example.planetlife.ui.theme.ShadowPurple
import com.example.planetlife.ui.theme.SkyBottom
import com.example.planetlife.ui.theme.SkyTop
import com.example.planetlife.ui.theme.TextBrown
import com.example.planetlife.ui.theme.TitleBlue

@Composable
fun SkyBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(SkyTop, SkyBottom))),
    ) {
        content()
    }
}

@Composable
fun PageContent(
    title: String,
    subtitle: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    SkyBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TitlePanel(title = title, subtitle = subtitle)
            content()
        }
    }
}

@Composable
fun TitlePanel(title: String, subtitle: String? = null) {
    CreamPanel {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MiniPlanetIcon()
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.headlineLarge)
                if (subtitle != null) {
                    Text(text = subtitle, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@Composable
fun CreamPanel(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, CreamBorder, RoundedCornerShape(22.dp)),
        color = Cream,
        shape = RoundedCornerShape(22.dp),
        tonalElevation = 1.dp,
        shadowElevation = 3.dp,
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = content,
        )
    }
}

@Composable
fun PlaceholderPlanet(modifier: Modifier = Modifier) {
    DynamicPlanet(
        forest = 52,
        crystal = 38,
        dream = 46,
        city = 24,
        desert = 18,
        shadow = 12,
        modifier = modifier,
    )
}

@Composable
fun DynamicPlanet(
    forest: Int,
    crystal: Int,
    dream: Int,
    city: Int,
    desert: Int,
    shadow: Int,
    modifier: Modifier = Modifier,
) {
    Canvas(
        modifier = modifier
            .size(220.dp)
            .clip(CircleShape),
    ) {
        drawPlanet(
            forest = forest.coerceIn(0, 100),
            crystal = crystal.coerceIn(0, 100),
            dream = dream.coerceIn(0, 100),
            city = city.coerceIn(0, 100),
            desert = desert.coerceIn(0, 100),
            shadow = shadow.coerceIn(0, 100),
        )
    }
}

private fun DrawScope.drawPlanet(
    forest: Int,
    crystal: Int,
    dream: Int,
    city: Int,
    desert: Int,
    shadow: Int,
) {
    val baseColor = when (maxOf(forest, crystal, dream, city, desert, shadow)) {
        forest -> ForestGreen
        crystal -> CrystalBlue
        dream -> DreamPurple
        city -> CityGold
        desert -> DesertOrange
        else -> ShadowPurple
    }

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(CrystalBlue.copy(alpha = 0.9f), baseColor),
            center = Offset(size.width * 0.36f, size.height * 0.28f),
            radius = size.minDimension * 0.72f,
        ),
    )

    val forestDepth = 0.34f + forest / 250f
    val forestPath = Path().apply {
        moveTo(size.width * 0.08f, size.height * forestDepth)
        cubicTo(size.width * 0.34f, size.height * (forestDepth - 0.16f), size.width * 0.62f, size.height * (forestDepth - 0.06f), size.width * 0.90f, size.height * (forestDepth + 0.04f))
        lineTo(size.width * 0.82f, size.height * 0.82f)
        cubicTo(size.width * 0.54f, size.height * 0.95f, size.width * 0.18f, size.height * 0.84f, size.width * 0.08f, size.height * forestDepth)
        close()
    }
    drawPath(forestPath, ForestGreen.copy(alpha = 0.45f + forest / 220f))

    val citySize = size.minDimension * (0.08f + city / 460f)
    drawRect(
        color = CityGold.copy(alpha = 0.28f + city / 210f),
        topLeft = Offset(size.width * 0.58f, size.height * 0.18f),
        size = Size(citySize, citySize * 0.72f),
    )
    drawRect(
        color = CityGold.copy(alpha = 0.22f + city / 260f),
        topLeft = Offset(size.width * 0.68f, size.height * 0.22f),
        size = Size(citySize * 0.72f, citySize),
    )

    drawArc(
        color = DesertOrange.copy(alpha = 0.18f + desert / 150f),
        startAngle = 18f,
        sweepAngle = 24f + desert * 1.1f,
        useCenter = true,
        topLeft = Offset(size.width * 0.46f, size.height * 0.38f),
        size = Size(size.width * 0.54f, size.height * 0.54f),
    )
    drawCircle(
        color = DreamPurple.copy(alpha = 0.2f + dream / 180f),
        radius = size.minDimension * (0.08f + dream / 520f),
        center = Offset(size.width * 0.32f, size.height * 0.34f),
    )
    drawCircle(
        color = CrystalBlue.copy(alpha = 0.28f + crystal / 180f),
        radius = size.minDimension * (0.05f + crystal / 620f),
        center = Offset(size.width * 0.45f, size.height * 0.22f),
    )
    drawCircle(
        color = ShadowPurple.copy(alpha = 0.12f + shadow / 160f),
        radius = size.minDimension * (0.06f + shadow / 520f),
        center = Offset(size.width * 0.72f, size.height * 0.72f),
    )
    drawCircle(
        color = Color.White.copy(alpha = 0.52f),
        radius = size.minDimension * 0.06f,
        center = Offset(size.width * 0.30f, size.height * 0.24f),
    )
}

@Composable
fun EcologyMeter(label: String, value: Int, color: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = label, color = TextBrown, fontWeight = FontWeight.Bold)
            Text(text = value.toString(), color = color, fontWeight = FontWeight.Bold)
        }
        LinearProgressIndicator(
            progress = { value / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(8.dp)),
            color = color,
            trackColor = CreamBorder,
        )
    }
}

@Composable
fun MiniPlanetIcon() {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(ForestGreen, CrystalBlue))),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "PL", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun PlanetBottomNavigation(
    tabs: List<AppRoute>,
    currentDestination: NavDestination?,
    onTabSelected: (AppRoute) -> Unit,
) {
    Surface(color = CreamLight, shadowElevation = 10.dp) {
        NavigationBar(
            containerColor = CreamLight,
            tonalElevation = 0.dp,
        ) {
            tabs.forEach { tab ->
                val selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true
                NavigationBarItem(
                    selected = selected,
                    onClick = { onTabSelected(tab) },
                    icon = {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(if (selected) ForestGreen.copy(alpha = 0.18f) else Color.Transparent),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = tab.iconText,
                                color = if (selected) ForestGreen else TitleBlue,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    },
                    label = {
                        Text(
                            text = stringResource(tab.labelRes),
                            color = if (selected) ForestGreen else TextBrown,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        )
                    },
                )
            }
        }
    }
}

val EcologyPreviewItems = listOf(
    Triple("森林", 52, ForestGreen),
    Triple("水晶", 38, CrystalBlue),
    Triple("梦境", 46, DreamPurple),
    Triple("城市", 24, CityGold),
    Triple("荒漠", 18, DesertOrange),
    Triple("暗影", 12, ShadowPurple),
)
