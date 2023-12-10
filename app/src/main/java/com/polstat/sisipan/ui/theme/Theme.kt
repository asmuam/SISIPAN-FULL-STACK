/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.polstat.sisipan.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.polstat.sisipan.ui.theme.md_theme_dark_background
import com.polstat.sisipan.ui.theme.md_theme_dark_error
import com.polstat.sisipan.ui.theme.md_theme_dark_errorContainer
import com.polstat.sisipan.ui.theme.md_theme_dark_inverseOnSurface
import com.polstat.sisipan.ui.theme.md_theme_dark_inversePrimary
import com.polstat.sisipan.ui.theme.md_theme_dark_inverseSurface
import com.polstat.sisipan.ui.theme.md_theme_dark_onBackground
import com.polstat.sisipan.ui.theme.md_theme_dark_onError
import com.polstat.sisipan.ui.theme.md_theme_dark_onErrorContainer
import com.polstat.sisipan.ui.theme.md_theme_dark_onPrimary
import com.polstat.sisipan.ui.theme.md_theme_dark_onPrimaryContainer
import com.polstat.sisipan.ui.theme.md_theme_dark_onSecondary
import com.polstat.sisipan.ui.theme.md_theme_dark_onSecondaryContainer
import com.polstat.sisipan.ui.theme.md_theme_dark_onSurface
import com.polstat.sisipan.ui.theme.md_theme_dark_onSurfaceVariant
import com.polstat.sisipan.ui.theme.md_theme_dark_onTertiary
import com.polstat.sisipan.ui.theme.md_theme_dark_onTertiaryContainer
import com.polstat.sisipan.ui.theme.md_theme_dark_outline
import com.polstat.sisipan.ui.theme.md_theme_dark_primary
import com.polstat.sisipan.ui.theme.md_theme_dark_primaryContainer
import com.polstat.sisipan.ui.theme.md_theme_dark_secondary
import com.polstat.sisipan.ui.theme.md_theme_dark_secondaryContainer
import com.polstat.sisipan.ui.theme.md_theme_dark_surface
import com.polstat.sisipan.ui.theme.md_theme_dark_surfaceTint
import com.polstat.sisipan.ui.theme.md_theme_dark_surfaceVariant
import com.polstat.sisipan.ui.theme.md_theme_dark_tertiary
import com.polstat.sisipan.ui.theme.md_theme_dark_tertiaryContainer
import com.polstat.sisipan.ui.theme.md_theme_light_background
import com.polstat.sisipan.ui.theme.md_theme_light_error
import com.polstat.sisipan.ui.theme.md_theme_light_errorContainer
import com.polstat.sisipan.ui.theme.md_theme_light_inverseOnSurface
import com.polstat.sisipan.ui.theme.md_theme_light_inversePrimary
import com.polstat.sisipan.ui.theme.md_theme_light_inverseSurface
import com.polstat.sisipan.ui.theme.md_theme_light_onBackground
import com.polstat.sisipan.ui.theme.md_theme_light_onError
import com.polstat.sisipan.ui.theme.md_theme_light_onErrorContainer
import com.polstat.sisipan.ui.theme.md_theme_light_onPrimary
import com.polstat.sisipan.ui.theme.md_theme_light_onPrimaryContainer
import com.polstat.sisipan.ui.theme.md_theme_light_onSecondary
import com.polstat.sisipan.ui.theme.md_theme_light_onSecondaryContainer
import com.polstat.sisipan.ui.theme.md_theme_light_onSurface
import com.polstat.sisipan.ui.theme.md_theme_light_onSurfaceVariant
import com.polstat.sisipan.ui.theme.md_theme_light_onTertiary
import com.polstat.sisipan.ui.theme.md_theme_light_onTertiaryContainer
import com.polstat.sisipan.ui.theme.md_theme_light_outline
import com.polstat.sisipan.ui.theme.md_theme_light_primary
import com.polstat.sisipan.ui.theme.md_theme_light_primaryContainer
import com.polstat.sisipan.ui.theme.md_theme_light_secondary
import com.polstat.sisipan.ui.theme.md_theme_light_secondaryContainer
import com.polstat.sisipan.ui.theme.md_theme_light_surface
import com.polstat.sisipan.ui.theme.md_theme_light_surfaceTint
import com.polstat.sisipan.ui.theme.md_theme_light_surfaceVariant
import com.polstat.sisipan.ui.theme.md_theme_light_tertiary
import com.polstat.sisipan.ui.theme.md_theme_light_tertiaryContainer

const val stronglyDeemphasizedAlpha = 0.6f
const val slightlyDeemphasizedAlpha = 0.87f

val LightColors = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
    inversePrimary = md_theme_light_inversePrimary,
    surfaceTint = md_theme_light_surfaceTint,
)

val DarkColors = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
    inversePrimary = md_theme_dark_inversePrimary,
    surfaceTint = md_theme_dark_surfaceTint,
)

@Composable
fun SisipanTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        } else {
            if (darkTheme) DarkColors else LightColors
        }

    MaterialTheme(
        colorScheme = colorScheme,
        shapes = SisipanShapes,
        typography = SisipanTypography,
        content = content
    )
}
