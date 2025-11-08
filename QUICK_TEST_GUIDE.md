# Quick Test Guide - 3 New Features

## ✅ Feature 1: Back Button Navigation

**Test Steps:**

1. Open app → Login
2. Tap **Campus** → Press back button ✓ (should go to main page)
3. Tap **Classroom** → Press back button ✓ (should go to main page)
4. Tap **Campus** → Tap **Notices** → Press back button ✓ (should close overlay)
5. Press back button again ✓ (should go to main page)
6. Tap **Classroom** → **Communication** → Select a group → Press back button ✓ (should go to groups
   list)

**Expected:** Back button never exits app (except on main page)

---

## ✅ Feature 2: Campus Button Text Visibility

**Test Steps:**

1. Tap **Campus** button
2. **Check all 4 buttons:**
    - Order Food ✓
    - Notices ✓
    - Feedback ✓
    - Live Crowd ✓

**Expected:** All text fully visible, not cut off

---

## ✅ Feature 3: Interactive Classroom Chat

**Test Steps:**

1. Tap **Classroom** → **Communication**
2. Select any class group (e.g., "Chemistry Class")
3. **Type** "Hello teacher!" in input field
4. **Press** send button (📤)
5. **Check:** Message appears with "You" as sender ✓
6. **Type** another message and send
7. **Check:** New message appears below ✓

**Expected:** Can type and send multiple messages

---

## 🎯 All Features Working?

- [ ] Back button navigates correctly (not exiting app)
- [ ] Campus button text all visible
- [ ] Can send chat messages

**All checkboxes checked = All features working! 🎉**
