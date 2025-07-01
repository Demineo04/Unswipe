# Pattern Recognition Threshold Updates

## 📊 **Updated Thresholds for Better Real-World Detection**

### **Changes Made**

#### **1. Binge Usage Detection**
- **Previous**: Sessions > 2 hours
- **Updated**: Sessions > 30 minutes
- **Rationale**: 30 minutes is more realistic for detecting problematic social media sessions

#### **2. Impulse Usage Detection**
- **Previous**: > 50 app launches per day
- **Updated**: > 20 app launches per day  
- **Rationale**: 20+ launches per day better captures frequent checking behavior

### **Files Modified**

#### **1. UsagePatternAnalyzer.kt**
```kotlin
// Updated binge detection threshold
private fun detectBingeUsagePattern(events: List<UsageEvent>): UsagePattern? {
    val longSessions = sessions.filter { it.duration > TimeUnit.MINUTES.toMillis(30) } // Changed from 2 hours
}

// Updated impulse detection threshold
private fun detectImpulseUsagePattern(events: List<UsageEvent>): UsagePattern? {
    val highLaunchDays = dailyLaunchCounts.values.count { it > 20 } // Changed from 50
}

// Updated confidence calculation baseline
private fun calculateBingeConfidence(sessionCount: Int, avgDuration: Double): Double {
    val durationScore = (avgDuration / TimeUnit.HOURS.toMillis(2)).coerceAtMost(1.0) // Changed from 4 hours
}
```

#### **2. ContextualUsageEvent.kt**
```kotlin
// Updated comment to reflect new threshold
BINGE_USAGE,        // Long sessions (>30 minutes) - Changed from >2 hours
```

### **Impact on User Experience**

#### **More Sensitive Detection**
- **Binge Usage**: Will now detect 30+ minute sessions as potential binges
- **Impulse Usage**: Will identify users who check apps 20+ times per day

#### **Earlier Intervention**
- Users will receive helpful notifications sooner
- Better prevention of extended usage sessions
- More accurate detection of compulsive checking behavior

#### **Realistic Thresholds**
- 30 minutes aligns with research on attention spans and healthy usage
- 20 launches per day captures realistic problematic checking patterns
- Improved pattern confidence scoring with 2-hour baseline

### **Pattern Detection Examples**

#### **Binge Usage (New 30-minute threshold)**
- **Trigger**: 3+ sessions of 30+ minutes in 30 days
- **Notification**: "Detected 5 long usage sessions (avg: 45m). Try setting session timers or taking regular breaks."

#### **Impulse Usage (New 20-launch threshold)**  
- **Trigger**: 7+ days with 20+ app launches
- **Notification**: "Frequent app checking behavior (23 launches/day). Try batching your social media time."

### **Benefits of Updated Thresholds**

1. **Earlier Detection**: Catch problematic patterns before they become severe
2. **More Relevant**: Align with real-world usage patterns and research
3. **Better Prevention**: Help users before reaching extreme usage levels
4. **Increased Sensitivity**: Detect more nuanced patterns of problematic use

### **Maintained Features**

- ✅ Confidence scoring system still works
- ✅ All other pattern types unchanged (stress, procrastination, sleep disruption)
- ✅ Notification rate limiting (3 per day) still applies
- ✅ Context-aware timing still functional
- ✅ Pattern analysis still runs on 30-day windows

The updated thresholds make the pattern recognition system more practical and helpful for real users while maintaining the sophisticated analysis and smart notification features.