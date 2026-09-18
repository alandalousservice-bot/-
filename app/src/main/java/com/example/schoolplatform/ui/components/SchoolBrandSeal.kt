package com.example.schoolplatform.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.schoolplatform.ui.theme.SchoolGold
import com.example.schoolplatform.ui.theme.SchoolGreen
import com.example.schoolplatform.ui.theme.SchoolGreenDark

@Composable
fun SchoolBrandSeal(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    listOf(SchoolGreen, SchoolGreenDark)
                )
            )
            .border(2.dp, SchoolGold, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.School,
            contentDescription = "ختم المؤسسة",
            tint = SchoolGold,
            modifier = Modifier.size(size * 0.58f)
        )
    }
}

@Composable
fun SchoolBrandHeader(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SchoolBrandSeal(size = 44.dp)
        Column {
            Text(
                text = "مدرسة مزيان عمار الابتدائية",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.White
            )
            Text(
                text = "ولاية سطيف · وزارة التربية الوطنية",
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.82f)
            )
        }
    }
}
