# Unswipe App Cards Overview

## 📱 **Complete Card System in the App**

The Unswipe app uses a card-based UI design with multiple types of cards serving different functions throughout the user experience.

---

## 🏠 **DASHBOARD CARDS**

### **1. Main Screen Time Card (Header)**
**Location**: Dashboard Header  
**Purpose**: Primary usage display  
**Functionality**:
- Shows total screen time for the day
- Large, prominent display with user greeting
- Uses secondary color theme for emphasis
- Acts as the main focal point of the dashboard

```kotlin
// Shows: "3h 42m" + "Total screen time today"
Card(containerColor = UnswipeSecondary) {
    Text(totalScreenTime, style = headlineLarge)
    Text("Total screen time today", style = bodyMedium)
}
```

### **2. Weekly Usage Chart Card**
**Location**: Center of dashboard  
**Purpose**: Visual progress tracking  
**Functionality**:
- Displays 7-day usage history as color-coded bars
- **Green**: Good usage (under 50% of limit)
- **Yellow**: Moderate usage (50-80% of limit)  
- **Orange**: Approaching limit (80-100% of limit)
- **Red**: Over limit (100%+ of limit)
- Interactive bars with day labels
- Highlights today's usage with special styling
- Shows "No data available yet" empty state

```kotlin
Card(elevation = 2.dp) {
    Text("Weekly Progress", style = titleMedium)
    UsageBarChart(data = weeklyProgress) // Color-coded bars
    // Day labels: Mon, Tue, Wed, Thu, Fri, Sat, Sun
}
```

### **3. Stat Cards (Row of 3)**
**Location**: Bottom of dashboard  
**Purpose**: Key metrics display  
**Functionality**:
- **Screen Unlocks**: Daily device unlock count
- **App Launches**: Number of times social media apps were opened
- **Goal**: Progress percentage toward daily limit (placeholder: "80%")
- Clean, minimal design with large numbers and descriptive labels

```kotlin
Row(horizontalArrangement = spacedBy(16.dp)) {
    StatCard(label = "Screen Unlocks", value = "47")
    StatCard(label = "App Launches", value = "23") 
    StatCard(label = "Goal", value = "80%")
}
```

### **4. Permission Prompt Cards**
**Location**: Top of dashboard (when needed)  
**Purpose**: Guide users to grant required permissions  
**Functionality**:
- **Usage Statistics Card**: Red warning card with "Grant" button
- **Accessibility Service Card**: Red warning card with "Grant" button
- Direct navigation to Android system settings
- Warning icon and clear explanation text
- Only shown when permissions are missing

```kotlin
Card(containerColor = errorContainer) {
    Icon(Icons.Default.Warning)
    Text("Usage Statistics Permission Required")
    Text("Grant usage access to track your social media time")
    OutlinedButton("Grant") // Opens Settings.ACTION_USAGE_ACCESS_SETTINGS
}
```

---

## 🔐 **PERMISSION SCREEN CARDS**

### **5. Permission Request Cards**
**Location**: Permission onboarding screen  
**Purpose**: Explain and request system permissions  
**Functionality**:

#### **Usage Statistics Permission Card**
- **Icon**: Analytics icon
- **Title**: "Usage Statistics"  
- **Description**: "Track time spent on social media apps like Instagram, TikTok, and YouTube"
- **State**: Changes color when granted (primary container vs surface variant)
- **Action**: "Grant Permission" button → opens usage access settings

#### **Accessibility Service Permission Card**
- **Icon**: Accessibility icon
- **Title**: "Accessibility Service"
- **Description**: "Show confirmation dialogs when you try to open blocked apps"
- **State**: Visual feedback when permission is granted
- **Action**: "Grant Permission" button → opens accessibility settings

```kotlin
PermissionCard(
    icon = Icons.Default.Analytics,
    title = "Usage Statistics",
    isGranted = hasPermission,
    containerColor = if (isGranted) primaryContainer else surfaceVariant
)
```

---

## ⚙️ **SETTINGS SCREEN CARDS**

### **6. Current Daily Limit Card**
**Location**: Daily Limit Settings screen  
**Purpose**: Display current usage limit  
**Functionality**:
- Shows current daily limit in large, prominent text
- Uses primary container color for emphasis
- Centered layout with "Current Limit" label
- Updates in real-time as user adjusts slider

```kotlin
Card(containerColor = primaryContainer) {
    Text("Current Limit", style = labelMedium)
    Text("2h 30m", style = headlineMedium) // Dynamic value
}
```

### **7. Information/Tip Card**
**Location**: Bottom of Daily Limit screen  
**Purpose**: Provide helpful guidance  
**Functionality**:
- Light bulb emoji + "Tip" title
- Educational content about healthy usage patterns
- Suggestion: "Start with a realistic limit and gradually reduce it"
- Context: "Most people spend 2-3 hours daily on social media"
- Uses surface variant color for subtle appearance

```kotlin
Card(containerColor = surfaceVariant) {
    Text("💡 Tip", style = titleSmall)
    Text("Start with a realistic limit and gradually reduce it...")
}
```

---

## 🚨 **CONFIRMATION DIALOG CARDS**

### **8. Main Confirmation Card**
**Location**: App launch confirmation dialog  
**Purpose**: Interrupt and provide context before app launch  
**Functionality**:
- **App Icon**: Shows the app being launched (with warning indicator if over limit)
- **App Name**: Large, bold title
- **Usage Message**: Context-aware message about current usage
- **Progress Bar**: Visual representation of daily limit progress
- **Motivational Card**: Nested card with timer icon and encouraging message
- **Action Buttons**: Context-sensitive (normal vs over-limit scenarios)

```kotlin
Card(elevation = 16.dp) {
    Image(appIcon, modifier = size(80.dp))
    Text(appName, style = headlineSmall)
    Text(usageMessage) // "You've used Instagram for 2h 15m today"
    
    // Nested motivational card
    Card(containerColor = if (isOverLimit) errorContainer else primaryContainer) {
        Icon(Icons.Default.Timer)
        Text("Consider your digital wellness goals 🎯")
    }
}
```

### **9. Usage Progress Card (Nested)**
**Location**: Inside confirmation dialog  
**Purpose**: Show visual progress toward daily limit  
**Functionality**:
- Linear progress indicator with color coding:
  - **Green/Primary**: Normal usage
  - **Orange/Tertiary**: Approaching limit (>80%)
  - **Red/Error**: Over limit
- Percentage text below progress bar
- Dynamic color based on usage level

---

## 🎨 **CARD DESIGN SYSTEM**

### **Color Coding**
- **Primary Container**: Important positive information (current limits, granted permissions)
- **Error Container**: Warnings and over-limit states  
- **Surface Variant**: Subtle information and tips
- **Secondary**: Main usage display (header card)

### **Elevation Levels**
- **2dp**: Standard cards (weekly chart, stat cards)
- **16dp**: Modal dialogs (confirmation dialog)
- **Default**: Most informational cards

### **Interactive Elements**
- **Permission Cards**: Grant buttons with direct system navigation
- **Chart Bars**: Clickable for detailed day view
- **Confirmation Buttons**: Context-aware actions (normal vs over-limit)

---

## 📊 **Card Functionality Summary**

| Card Type | Primary Function | User Action | Data Source |
|-----------|------------------|-------------|-------------|
| **Screen Time Header** | Display total daily usage | Visual reference | UsageStatsManager |
| **Weekly Chart** | Show 7-day progress | Click for details | Room database |
| **Stat Cards** | Key metrics overview | Visual reference | Usage tracking |
| **Permission Prompts** | Guide permission setup | Tap to grant | System permission APIs |
| **Daily Limit** | Show/set usage limits | Adjust slider | DataStore preferences |
| **Confirmation Dialog** | Interrupt app launches | Confirm/cancel | Real-time usage data |
| **Tip Cards** | Provide guidance | Read information | Static content |

---

## 🎯 **Card User Experience Flow**

### **First Time User**
1. **Permission Cards** → Guide through required setup
2. **Empty Chart Card** → "No data available yet" state
3. **Tip Cards** → Educational content

### **Active User**
1. **Screen Time Header** → Quick daily overview
2. **Weekly Chart** → Progress visualization  
3. **Stat Cards** → Detailed metrics
4. **Confirmation Cards** → Real-time intervention

### **Over-Limit User**
1. **Red Progress Bars** → Visual warning in chart
2. **Error-colored Confirmation** → Strong intervention message
3. **Alternative Action Buttons** → "Take a Break Instead"

The card system provides a cohesive, informative, and interactive experience that guides users through their digital wellness journey while providing real-time feedback and gentle interventions.