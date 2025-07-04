package com.unswipe.android.util

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log

/**
 * Utility class for converting Android package names to user-friendly app names
 * for confirmation dialogs and UI display
 */
object AppNameMapper {
    
    private const val TAG = "AppNameMapper"
    
    /**
     * Comprehensive mapping of package names to user-friendly app names
     * This covers popular social media, entertainment, and productivity apps
     */
    private val PACKAGE_TO_NAME_MAP = mapOf(
        // Social Media
        "com.zhiliaoapp.musically" to "TikTok",
        "com.instagram.android" to "Instagram", 
        "com.facebook.katana" to "Facebook",
        "com.snapchat.android" to "Snapchat",
        "com.twitter.android" to "Twitter",
        "com.reddit.frontpage" to "Reddit",
        "com.linkedin.android" to "LinkedIn",
        "com.pinterest" to "Pinterest",
        "com.tumblr" to "Tumblr",
        "com.discord" to "Discord",
        
        // Entertainment
        "com.google.android.youtube" to "YouTube",
        "com.netflix.mediaclient" to "Netflix",
        "com.spotify.music" to "Spotify",
        "com.amazon.avod.thirdpartyclient" to "Prime Video",
        "com.hulu.plus" to "Hulu",
        "com.disney.disneyplus" to "Disney+",
        "com.twitch.android.app" to "Twitch",
        "com.soundcloud.android" to "SoundCloud",
        "com.amazon.mp3" to "Amazon Music",
        "com.pandora.android" to "Pandora",
        
        // Communication
        "com.whatsapp" to "WhatsApp",
        "com.facebook.orca" to "Messenger",
        "org.telegram.messenger" to "Telegram",
        "com.viber.voip" to "Viber",
        "com.skype.raider" to "Skype",
        "us.zoom.videomeetings" to "Zoom",
        "com.microsoft.teams" to "Microsoft Teams",
        "com.slack" to "Slack",
        
        // News & Information
        "flipboard.app" to "Flipboard",
        "com.google.android.apps.magazines" to "Google News",
        "com.cnn.mobile.android.phone" to "CNN",
        "com.nytimes.android" to "New York Times",
        "com.washingtonpost.rainbow" to "Washington Post",
        "com.guardian" to "The Guardian",
        "com.medium.reader" to "Medium",
        
        // Shopping
        "com.amazon.mShop.android.shopping" to "Amazon",
        "com.ebay.mobile" to "eBay",
        "com.contextlogic.wish" to "Wish",
        "com.etsy.android" to "Etsy",
        "com.alibaba.aliexpresshd" to "AliExpress",
        "com.shopify.arrive" to "Shopify",
        
        // Games (Popular Social/Addictive ones)
        "com.king.candycrushsaga" to "Candy Crush Saga",
        "com.supercell.clashofclans" to "Clash of Clans",
        "com.supercell.brawlstars" to "Brawl Stars",
        "com.pokemon.pokemongo" to "Pokémon GO",
        "com.roblox.client" to "Roblox",
        "com.ea.gp.apexlegendsmobilefps" to "Apex Legends Mobile",
        
        // Dating
        "com.tinder" to "Tinder",
        "com.bumble.app" to "Bumble",
        "com.match.android.matchmobile" to "Match",
        "co.hinge.app" to "Hinge",
        
        // Productivity (that can be distracting)
        "com.google.android.gm" to "Gmail",
        "com.chrome.beta" to "Chrome Beta",
        "com.chrome.canary" to "Chrome Canary",
        "com.chrome.dev" to "Chrome Dev",
        "com.android.chrome" to "Chrome",
        "com.google.android.apps.docs.editors.sheets" to "Google Sheets",
        "com.google.android.apps.docs" to "Google Docs",
        "com.microsoft.office.outlook" to "Outlook",
        "com.microsoft.office.word" to "Microsoft Word",
        "com.microsoft.office.excel" to "Microsoft Excel",
        
        // Additional Popular Apps
        "com.ubercab" to "Uber",
        "com.lyft.android" to "Lyft",
        "com.airbnb.android" to "Airbnb",
        "com.booking" to "Booking.com",
        "com.duolingo" to "Duolingo",
        "com.headspace.android" to "Headspace",
        "com.calm.android" to "Calm"
    )
    
    /**
     * Get user-friendly app name from package name
     * Uses multiple fallback strategies to ensure we always get a readable name
     */
    fun getAppName(context: Context, packageName: String): String {
        // Strategy 1: Check our predefined mapping first (fastest and most reliable)
        PACKAGE_TO_NAME_MAP[packageName]?.let { return it }
        
        // Strategy 2: Try to get the name from PackageManager
        try {
            val packageManager = context.packageManager
            val appInfo: ApplicationInfo = packageManager.getApplicationInfo(packageName, 0)
            val appName = packageManager.getApplicationLabel(appInfo).toString()
            
            // Only use PackageManager result if it's not just the package name
            if (appName != packageName && !appName.contains(".")) {
                return appName
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.w(TAG, "Package not found: $packageName", e)
        } catch (e: Exception) {
            Log.w(TAG, "Error getting app name for $packageName", e)
        }
        
        // Strategy 3: Extract readable name from package name
        return extractReadableNameFromPackage(packageName)
    }
    
    /**
     * Extract a readable name from package name as last resort
     * Examples:
     * - com.google.android.youtube -> YouTube
     * - com.instagram.android -> Instagram
     * - com.zhiliaoapp.musically -> Musically
     */
    private fun extractReadableNameFromPackage(packageName: String): String {
        return try {
            val parts = packageName.split(".")
            
            // Look for meaningful parts in reverse order (most specific first)
            for (i in parts.size - 1 downTo 0) {
                val part = parts[i]
                
                // Skip common suffixes
                if (part in listOf("android", "app", "mobile", "client", "plus", "lite")) {
                    continue
                }
                
                // Skip version numbers
                if (part.matches(Regex("v?\\d+.*"))) {
                    continue
                }
                
                // If we find a meaningful part, capitalize and return
                if (part.length > 2) {
                    return part.replaceFirstChar { it.uppercase() }
                }
            }
            
            // If no meaningful part found, use the last non-generic part
            val lastPart = parts.lastOrNull { it.length > 2 } ?: parts.last()
            lastPart.replaceFirstChar { it.uppercase() }
            
        } catch (e: Exception) {
            Log.w(TAG, "Error extracting name from package: $packageName", e)
            // Absolute fallback - just use the package name
            packageName
        }
    }
    
    /**
     * Get confirmation message for an app
     * Returns formatted message: "Do you really want to open [AppName]?"
     */
    fun getConfirmationMessage(context: Context, packageName: String): String {
        val appName = getAppName(context, packageName)
        return "Do you really want to open $appName?"
    }
    
    /**
     * Check if an app is a known social media app
     */
    fun isSocialMediaApp(packageName: String): Boolean {
        val socialMediaPackages = setOf(
            "com.zhiliaoapp.musically", // TikTok
            "com.instagram.android", // Instagram
            "com.facebook.katana", // Facebook
            "com.snapchat.android", // Snapchat
            "com.twitter.android", // Twitter
            "com.reddit.frontpage", // Reddit
            "com.linkedin.android", // LinkedIn
            "com.pinterest", // Pinterest
            "com.tumblr", // Tumblr
            "com.discord" // Discord
        )
        return packageName in socialMediaPackages
    }
    
    /**
     * Check if an app is a known entertainment app
     */
    fun isEntertainmentApp(packageName: String): Boolean {
        val entertainmentPackages = setOf(
            "com.google.android.youtube", // YouTube
            "com.netflix.mediaclient", // Netflix
            "com.spotify.music", // Spotify
            "com.amazon.avod.thirdpartyclient", // Prime Video
            "com.hulu.plus", // Hulu
            "com.disney.disneyplus", // Disney+
            "com.twitch.android.app", // Twitch
            "com.soundcloud.android" // SoundCloud
        )
        return packageName in entertainmentPackages
    }
    
    /**
     * Get app category for better messaging
     */
    fun getAppCategory(packageName: String): String {
        return when {
            isSocialMediaApp(packageName) -> "social media"
            isEntertainmentApp(packageName) -> "entertainment"
            else -> "app"
        }
    }
}