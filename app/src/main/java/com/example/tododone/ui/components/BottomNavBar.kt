package com.example.tododone.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.tododone.ui.navigation.Screen
import com.example.tododone.ui.navigation.bottomNavItems
import com.example.tododone.ui.theme.ActiveNavIndicator
import com.example.tododone.ui.theme.DarkSurface
import com.example.tododone.ui.theme.TextPrimary
import com.example.tododone.ui.theme.TextSecondary

@Composable
fun BottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Main Navigation Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(32.dp),
                    spotColor = Color.Black.copy(alpha = 0.3f)
                )
                .clip(RoundedCornerShape(32.dp))
                .background(DarkSurface.copy(alpha = 0.95f))
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left side items (Home, Calendar)
                bottomNavItems.take(2).forEach { screen ->
                    NavItem(
                        screen = screen,
                        isSelected = currentRoute == screen.route,
                        onClick = { onNavigate(screen.route) }
                    )
                }

                // Spacer for FAB
                Spacer(modifier = Modifier.width(64.dp))

                // Right side items (AI Chat, Profile)
                bottomNavItems.drop(2).forEach { screen ->
                    NavItem(
                        screen = screen,
                        isSelected = currentRoute == screen.route,
                        onClick = { onNavigate(screen.route) }
                    )
                }
            }
        }

        // Center Floating Action Button
        FloatingActionButton(
            onClick = onAddClick,
            modifier = Modifier
                .offset(y = (-28).dp)
                .size(56.dp),
            shape = CircleShape,
            containerColor = ActiveNavIndicator,
            contentColor = Color.Black,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 8.dp,
                pressedElevation = 12.dp
            )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Task",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun NavItem(
    screen: Screen,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) ActiveNavIndicator else Color.Transparent,
        animationSpec = tween(durationMillis = 300),
        label = "backgroundColor"
    )

    val iconColor by animateColorAsState(
        targetValue = if (isSelected) Color.Black else TextSecondary,
        animationSpec = tween(durationMillis = 300),
        label = "iconColor"
    )

    val indicatorWidth by animateDpAsState(
        targetValue = if (isSelected) 48.dp else 40.dp,
        animationSpec = tween(durationMillis = 300),
        label = "indicatorWidth"
    )

    val indicatorHeight by animateDpAsState(
        targetValue = if (isSelected) 40.dp else 40.dp,
        animationSpec = tween(durationMillis = 300),
        label = "indicatorHeight"
    )

    Box(
        modifier = modifier
            .width(indicatorWidth)
            .height(indicatorHeight)
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        screen.icon?.let { icon ->
            Icon(
                imageVector = icon,
                contentDescription = screen.label,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
