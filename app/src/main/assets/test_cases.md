# Test Cases for Campus Navigation App

## Test Case 1: Email Login Flow (Student Authentication)

### Objective

Verify that a student can successfully log in using college email and unique OTP verification.

### Prerequisites

- App is installed on the device
- Test user data exists in `email_users.json`

### Test Steps

1. Launch the app
2. Wait for splash screen (2 seconds)
3. On Login Selection screen, tap **"Login"** button
4. Enter email: `rohan.mathad@rvce.edu.in`
5. Tap **"Send OTP"** button
6. Verify OTP field appears
7. Enter OTP: `123456`
8. Tap **"Verify"** button
9. Wait for green checkmark animation with "Verified!" text
10. Verify navigation to Student Main Page

### Expected Results

- Glassmorphic UI with frosted glass fields
- Purple gradient borders with glow effects
- OTP field only appears after email verification
- Green checkmark animation with bounce effect
- Success text fades in smoothly
- User is taken to Student Main Page
- Session is saved (reopening goes directly to main page)

### Test Data - Three Users

**User 1: Rohan Mathad**

```json
{
  "email": "rohan.mathad@rvce.edu.in",
  "otp": "123456",
  "rollNumber": "1RV23AI001",
  "department": "AI & ML"
}
```

**User 2: Aditi Rao**

```json
{
  "email": "aditi.rao@rvce.edu.in",
  "otp": "234567",
  "rollNumber": "1RV23AI015",
  "department": "AI & ML"
}
```

**User 3: Arjun Kumar**

```json
{
  "email": "arjun.kumar@rvce.edu.in",
  "otp": "345678",
  "rollNumber": "1RV23AI032",
  "department": "AI & ML"
}
```

### Edge Cases to Test

- Unregistered email → "Email not registered" error, no OTP field
- Invalid email format → "Please enter a valid college email"
- Wrong OTP (e.g., 999999) → "Invalid OTP" error
- Each user's unique OTP must work (cannot use another user's OTP)
- Case-insensitive email matching (ROHAN.MATHAD@rvce.edu.in should work)

---

## Test Case 2: Guest User Registration and Login Flow

### Objective

Verify that a guest user can successfully register and log in using phone number and OTP
verification.

### Prerequisites

- App is installed on the device
- Test user data exists in `users.json` with phone number "1234567890"

### Test Steps

1. Launch the app
2. Wait for splash screen (2 seconds)
3. On Login Selection screen, tap **"Guest"** button
4. Enter phone number: `1234567890`
5. Tap **"Verify Phone"** button
6. Verify OTP field appears
7. Enter OTP: `123456`
8. Tap **"Verify OTP"** button
9. Wait for green checkmark animation
10. Verify navigation to Guest Main Page

### Expected Results

- ✅ No white flash during transitions
- ✅ OTP field only appears after successful phone verification
- ✅ Green checkmark animation displays smoothly
- ✅ User is taken to Guest Main Page
- ✅ User profile shows: John Doe, 1234567890, Computer Science
- ✅ Session is saved (closing and reopening app goes directly to main page)

### Test Data

```json
{
  "phoneNumber": "1234567890",
  "name": "John Doe",
  "email": "john.doe@example.com",
  "address": "123 Main St, Los Angeles, CA",
  "branch": "Computer Science",
  "college": "University of Los Angeles",
  "classNumber": "CSE-2024-A",
  "classTeacherName": "Dr. Sarah Johnson",
  "teacherContact": "+1-555-0101",
  "fatherName": "Robert Doe",
  "fatherContact": "+1-555-0199"
}
```

---

## Test Case 3: Unregistered Phone Number Rejection

### Objective

Verify that the app properly rejects unregistered phone numbers and does not show OTP field.

### Prerequisites

- App is installed on the device
- Phone number "9999999999" does NOT exist in `users.json`

### Test Steps

1. Launch the app (or sign out if already logged in)
2. Wait for splash screen
3. On Login Selection screen, tap **"Guest"** button
4. Enter phone number: `9999999999`
5. Tap **"Verify Phone"** button
6. Observe the error message
7. Verify OTP field does NOT appear

### Expected Results

- ✅ Error message displays: "Phone number not registered. Please use a registered number."
- ✅ OTP input field does NOT appear
- ✅ User remains on phone input screen
- ✅ User can try again with a different number
- ✅ No navigation occurs
- ✅ No session is created

### Edge Cases to Test

- Empty phone number → Should show "Please enter a valid 10-digit phone number"
- 9-digit number → Should show validation error
- 11-digit number → Should be truncated/prevented
- Letters or special characters → Should be filtered out (number input type)

---

## Test Case 4: Navigation and Back Button Behavior

### Objective

Verify proper navigation flow, back button behavior, and session persistence.

### Prerequisites

- App is installed
- User is logged in as guest

### Test Steps - Part A: Back Button on Main Page

1. Launch app (should go directly to Guest Main Page if logged in)
2. Press device back button once
3. Observe app behavior

**Expected Results:**

- ✅ App exits immediately (no double press needed)
- ✅ Session remains saved

### Test Steps - Part B: Session Persistence

1. Close the app completely (swipe away from recent apps)
2. Reopen the app
3. Observe which screen appears

**Expected Results:**

- ✅ App skips splash and login screens
- ✅ Goes directly to Guest Main Page
- ✅ User profile is still loaded
- ✅ No white flash during startup

### Test Steps - Part C: Sign Out Flow

1. On Guest Main Page, tap user name/avatar in header
2. Profile dialog opens
3. Scroll to bottom
4. Tap red **"Sign Out"** button
5. App exits
6. Reopen the app
7. Observe which screen appears

**Expected Results:**

- ✅ Profile dialog displays all user information
- ✅ Sign out button is visible and red
- ✅ App exits after sign out
- ✅ Session is cleared
- ✅ On reopen, app starts from Splash → Login Selection
- ✅ User must log in again

### Test Steps - Part D: Navigation Transitions

1. Navigate through: Login Selection → Guest Auth → Main Page
2. Observe all transitions

**Expected Results:**

- ✅ No white flashes between screens
- ✅ Smooth fade transitions (300ms)
- ✅ Black background maintained throughout
- ✅ No UI glitches or flickering

### Test Steps - Part E: Feature Interactions

1. Tap **Announcements** button
2. Verify announcements modal opens with colored categories
3. Close announcements
4. Tap **Calendar** button
5. Navigate between months using arrows
6. Tap a calendar event to see details
7. Close calendar
8. Tap **Timetable** button
9. Pinch to zoom in on timetable
10. Drag to pan around
11. Tap reset button
12. Close timetable

**Expected Results:**

- ✅ All modals open and close smoothly
- ✅ Announcements show time ago (e.g., "2 hours ago")
- ✅ Calendar shows day of week with dates
- ✅ Month navigation works in both directions
- ✅ Timetable supports pinch zoom (0.5x - 5x)
- ✅ Panning is accelerated (2.5x) when zoomed
- ✅ Reset button returns to original view
- ✅ All interactions feel smooth and responsive

---

## Additional Test Scenarios

### Regression Tests

- Verify OTP resend functionality
- Test with both test users (1234567890 and 9876543210)
- Test profile with all 11 fields populated
- Verify scrollable profile dialog
- Test full-screen timetable display

### Performance Tests

- App startup time < 3 seconds
- Smooth 60fps animations
- No memory leaks after repeated navigation
- Session load time < 500ms

### UI/UX Tests

- All text is readable (contrast check)
- Touch targets ≥ 48dp
- Purple theme consistent throughout
- Glassmorphism effects render correctly
- Animations feel natural and purposeful
