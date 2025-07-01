# Unswipe App Cards Analysis

## Overview
The Unswipe app uses various card components to display information to users throughout the application. These cards serve different purposes, from showing usage statistics to requesting permissions and displaying confirmation dialogs.

## Card Types Identified

### 1. **StatCard** - Usage Statistics Display
**Location**: `app/src/main/java/com/unswipe/android/ui/components/StatCard.kt`

**Purpose**: Displays key usage metrics in a compact, visually appealing format

**Information Displayed**:
- **Screen Unlocks**: Number of times the user has unlocked their device today
- **App Launches**: Number of times social media apps were launched today  
- **Goal Progress**: Shows progress toward daily usage goals (currently shows "80%" as placeholder)

**Usage**: Found on the Dashboard Screen in a row of three cards showing different statistics

### 2. **DashboardHeader Card** - Total Screen Time Display
**Location**: `app/src/main/java/com/unswipe/android/ui/components/DashboardHeader.kt`

**Purpose**: Prominently displays the user's total screen time for the day

**Information Displayed**:
- **Total Screen Time Today**: Main metric showing how much time has been spent on social media today
- **User Greeting**: Personalized greeting with user's name
- **Visual Emphasis**: Uses a colored card background to make the information stand out

### 3. **WeeklyUsageChart Card** - Weekly Progress Visualization
**Location**: `app/src/main/java/com/unswipe/android/ui/dashboard/WeeklyUsageChart.kt`

**Purpose**: Shows weekly usage patterns in a visual chart format

**Information Displayed**:
- **Weekly Progress**: Bar chart showing daily usage for the past 7 days
- **Day Labels**: Shows abbreviated day names (M, T, W, T, F, S, S)
- **Usage Percentage**: Visual bars with color coding:
  - 🔴 Red: Over daily limit (≥100%)
  - 🟠 Orange: Approaching limit (≥80%)
  - 🟡 Yellow: Moderate usage (≥50%)
  - 🟢 Green: Good usage (<50%)
- **Today Highlight**: Current day is highlighted with different styling

### 4. **PermissionCard** - Permission Request Information
**Location**: `app/src/main/java/com/unswipe/android/ui/permissions/PermissionRequestScreen.kt`

**Purpose**: Explains required permissions and their current status

**Information Displayed**:
- **Usage Statistics Permission**:
  - Purpose: Track time spent on social media apps
  - Status: Granted/Not Granted
  - Action: Button to open system settings
- **Accessibility Service Permission**:
  - Purpose: Show confirmation dialogs when opening blocked apps
  - Status: Enabled/Disabled
  - Action: Button to open accessibility settings

**Visual States**: Cards change color based on permission status (granted = primary container, not granted = surface variant)

### 5. **PermissionPromptCard** - Dashboard Permission Reminders
**Location**: `app/src/main/java/com/unswipe/android/ui/dashboard/DashboardScreen.kt`

**Purpose**: Shows permission prompts on the dashboard when permissions are missing

**Information Displayed**:
- **Usage Statistics Prompt**: Reminder to grant usage access
- **Accessibility Service Prompt**: Reminder to enable accessibility service
- **Warning Icon**: Visual indicator that action is required
- **Grant Button**: Quick access to permission settings

**Visual Design**: Uses error container colors to indicate urgency

### 6. **Daily Limit Cards** - Limit Configuration Display
**Location**: `app/src/main/java/com/unswipe/android/ui/settings/DailyLimitScreen.kt`

**Information Displayed**:
- **Current Limit Card**: 
  - Shows the currently set daily time limit
  - Prominently displays the time (e.g., "2h 30m")
  - Uses primary container styling for emphasis
- **Information Card** (Tip Card):
  - Provides helpful guidance about setting realistic limits
  - Shows usage statistics context ("Most people spend 2-3 hours daily")
  - Uses surface variant styling for informational content

### 7. **Enhanced Confirmation Dialog Cards** - App Launch Confirmation
**Location**: `app/src/main/java/com/unswipe/android/ui/components/ConfirmationDialog.kt`

**Purpose**: Shows detailed information when user attempts to open a social media app

**Information Displayed**:
- **App Information**:
  - App name and icon
  - Warning indicator if over daily limit
- **Usage Progress**:
  - Progress bar showing percentage of daily limit used
  - Color-coded based on usage level
- **Motivational Message Card**:
  - Contextual messages about digital wellness
  - Timer icon for visual emphasis
  - Different styling for over-limit vs normal usage
- **Action Context**: Different button layouts based on usage status

## Card Design Patterns

### Visual Hierarchy
- **Primary Cards**: Important metrics use primary color schemes (screen time, current limit)
- **Warning Cards**: Permission prompts and over-limit warnings use error colors
- **Informational Cards**: Tips and secondary info use surface variants
- **Success Cards**: Granted permissions use primary container colors

### Information Architecture
- **Metric Cards**: Show value prominently with descriptive label below
- **Status Cards**: Use icons and color coding to show current state
- **Action Cards**: Include buttons or links for user interaction
- **Progress Cards**: Use visual indicators (bars, charts) for data representation

### Responsive Design
- Cards adapt to different screen sizes
- Use weight-based layouts for consistent spacing
- Maintain readable typography across all card types

## Usage Context

### Dashboard Screen
- Primary location for most cards
- Shows real-time usage data
- Combines multiple card types for comprehensive overview

### Settings Screens
- Configuration-focused cards
- Interactive elements for user input
- Informational cards for guidance

### Permission Screens
- Status-focused cards
- Clear action items
- Visual feedback for completion

### Confirmation Dialogs
- Context-aware information display
- Progressive disclosure based on usage patterns
- Motivational messaging integration

## Summary

The Unswipe app uses **7 main types of cards** to display information to users:

1. **StatCard** - Usage statistics (unlocks, launches, goals)
2. **DashboardHeader Card** - Total daily screen time
3. **WeeklyUsageChart Card** - 7-day usage visualization
4. **PermissionCard** - Permission explanations and status
5. **PermissionPromptCard** - Dashboard permission reminders
6. **Daily Limit Cards** - Current limit and tip information
7. **Confirmation Dialog Cards** - App launch confirmation details

These cards collectively provide users with comprehensive information about their social media usage patterns, app permissions, daily limits, and real-time feedback to support their digital wellness goals.