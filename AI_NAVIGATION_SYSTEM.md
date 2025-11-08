# AI Navigation System - Hybrid Approach

## Overview

The Campus Network app uses a **3-tier hybrid AI navigation system** that ensures fast, accurate
responses:

### Tier 1: Keyword Matching (Instant - 0ms)

- Smart pattern matching with comprehensive coverage
- **Handles 90%+ of queries instantly**
- No AI model needed - works offline
- Specific responses for specific queries

### Tier 2: AI Model (If needed - 3-8s)

- SmolLM2-360M model (119MB)
- Only used for complex or unrecognized queries
- Simplified prompts for faster generation
- 8-second timeout

### Tier 3: Fallback Response (Instant)

- General help menu
- Always provides useful information
- Never leaves user without an answer

## Response Time Guarantees

| Query Type | Response Time | Example |
|------------|---------------|---------|
| **Greetings** | Instant | "hi", "hello" → Redirects to app features |
| **Specific Feature** | Instant | "how to check library crowd" → Step-by-step |
| **Class Notes** | Instant | "where are my class notes" → Classroom → Materials |
| **Complex/Unclear** | 3-8 seconds | Edge cases → AI model generates response |
| **Unrecognized** | Instant | Unknown query → General help menu |

## Supported Queries

### ✅ Instant Responses (Keyword Matching)

**Crowd Checking:**

- "how to check crowd in library"
- "is canteen busy"
- "gym crowd status"
- "check lab crowding"

**Food/Ordering:**

- "how to order food"
- "canteen menu"
- "i'm hungry"

**Materials/Notes:**

- "where are my class notes" ⭐
- "how to access materials"
- "find chemistry PDFs"
- "previous year papers"

**Attendance:**

- "check attendance"
- "how many days present"
- "absent days"

**Timetable:**

- "view timetable"
- "class schedule"

**Communication:**

- "chat with teacher"
- "class group messages"

**Books/Library:**

- "borrowed books"
- "library books"

**Feedback:**

- "send feedback"
- "file complaint"

**Announcements:**

- "campus announcements"
- "notices"

**Calendar:**

- "check calendar"
- "exam dates"
- "events"

**Payments:**

- "check fees"
- "payment dues"

**Personal Info:**

- "my profile"
- "personal details"

### 🚫 Redirected Queries

**Off-topic/Greetings:**

- "hi", "hello", "hey" → "I can only help with app navigation..."
- "how are you" → Same redirect
- "tell me a joke" → Same redirect

## Technical Implementation

### File: `AINavigationViewModel.kt`

**Key Methods:**

1. **`askQuestion(query: String)`**
    - Entry point for all queries
    - Flow: Keyword → AI → Fallback

2. **`tryKeywordMatch(query: String): String?`**
    - Returns response if pattern matches
    - Returns `null` if no match (triggers AI/fallback)
    - Uses lowercase normalization and regex patterns

3. **`buildSimplePrompt(query: String): String`**
    - Ultra-simplified prompt for AI
    - Reduces context window pressure
    - Forces short responses (3-4 steps)

4. **`getFallbackResponse(query: String): String`**
    - General help menu
    - Always succeeds

## Model Download

- **Model:** SmolLM2-360M-Instruct-GGUF
- **Size:** 119MB
- **Download:** Automatic on first use
- **Loading:** ~30-60 seconds (one-time per app session)

## User Experience

### First Launch:

1. App shows "AI Assistant Ready to Download"
2. User taps "Download AI Model"
3. Progress bar shows download (119MB)
4. Model loads into memory (~45s)
5. Ready! 🎉

### Normal Usage:

- **Most queries:** Instant response (keyword matching)
- **Complex queries:** 3-8 second response (AI)
- **Always:** Helpful response guaranteed

## Why This Approach Works

### ❌ Previous Issues:

- **1.7B model too slow** (~30+ seconds per query)
- **Large prompts** (features guide 266 lines)
- **No fallback** (stuck on "AI is thinking...")
- **Poor UX** (users waited too long)

### ✅ Current Solution:

- **Instant for 90% of queries** (keyword matching)
- **Simplified AI** (360M model, short prompts)
- **Always responsive** (fallback system)
- **Great UX** (instant or < 10 seconds)

## Testing the System

### Test Cases:

```kotlin
// Instant responses
askQuestion("how to check library crowd")
askQuestion("where are my class notes")  // ⭐ Specific requirement
askQuestion("order food")
askQuestion("hi")  // Should redirect

// May use AI (3-8s)
askQuestion("what can I do in this app")
askQuestion("how do I find information about upcoming events")

// Fallback
askQuestion("asdfghjkl")  // Nonsense → help menu
```

## Monitoring

Check logcat for:

```
AIAgent: Using keyword match (instant response)  // ← Good! 90% should be this
AIAgent: Trying AI model for complex query       // ← Occasional
AIAgent: Using fallback response                 // ← Rare
```

## Future Improvements

1. **Add more keyword patterns** as users ask new questions
2. **Track unrecognized queries** to improve coverage
3. **A/B test AI model** (try 135M for even faster responses)
4. **Add voice input** (speech-to-text)
5. **Multi-language support** (Hindi, regional languages)

---

**Result:** Fast, accurate, reliable AI navigation that answers "where are my class notes" and
similar queries **instantly**! 🚀
