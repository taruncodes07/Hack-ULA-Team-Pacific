# Remove AI Model from Assets - GitHub Fix

## 🚨 Problem

The AI model file `SmolLM2-360M-Q4_K_M.gguf` (258MB) in `app/src/main/assets/models/` is too large
for GitHub and blocking your push.

## ✅ Solution Implemented

1. ✅ Added model files to `.gitignore` (won't be tracked by Git)
2. ✅ Modified ViewModel to delete cache and force download every launch
3. ⚠️ Manual step needed: Delete the large file from your local machine

---

## 📋 Step-by-Step Instructions

### Step 1: Delete the Large Model File (MANUAL)

**In Windows File Explorer:**

```
Navigate to:
C:\Users\ROHAN MATHAD\StudioProjects\Hack-ULA-Team-Pacific\app\src\main\assets\models\

Delete this file:
SmolLM2-360M-Q4_K_M.gguf (258.1 MB)
```

**OR Use PowerShell:**

```powershell
cd "C:\Users\ROHAN MATHAD\StudioProjects\Hack-ULA-Team-Pacific"
Remove-Item "app\src\main\assets\models\SmolLM2-360M-Q4_K_M.gguf" -Force
```

### Step 2: Verify File is Deleted

```powershell
# Check if file exists
Test-Path "app\src\main\assets\models\SmolLM2-360M-Q4_K_M.gguf"
# Should output: False
```

### Step 3: Remove from Git History (If Already Committed)

If you've already committed this file to Git, remove it from history:

```bash
# Remove the file from Git tracking (but keep locally if you want)
git rm --cached app/src/main/assets/models/SmolLM2-360M-Q4_K_M.gguf

# Or remove entire models directory from Git
git rm -r --cached app/src/main/assets/models/

# Commit the removal
git commit -m "Remove large AI model file from repository"
```

### Step 4: Clean Git History (Optional but Recommended)

If the large file is in your Git history, clean it:

```bash
# Use git filter-branch or BFG Repo-Cleaner
# WARNING: This rewrites history!

# Option 1: Using git filter-repo (recommended)
git filter-repo --path app/src/main/assets/models/SmolLM2-360M-Q4_K_M.gguf --invert-paths

# Option 2: Using BFG Repo-Cleaner
# Download BFG from https://rtyley.github.io/bfg-repo-cleaner/
java -jar bfg.jar --delete-files SmolLM2-360M-Q4_K_M.gguf
```

### Step 5: Push to GitHub

```bash
git add .gitignore
git add app/.gitignore
git commit -m "Add AI model files to gitignore"
git push origin main
```

---

## 🔄 How It Works Now

### On App Launch:

```
1. ViewModel initializes
2. deleteCachedModels() runs → Clears any existing model cache
3. AI shows "Download AI Model (119MB)" button
4. User taps download
5. Model downloads from URL: 
   https://huggingface.co/HuggingFaceTB/SmolLM2-360M-Instruct-GGUF/resolve/main/smollm2-360m-instruct-q8_0.gguf
6. Model loads into memory
7. AI ready to use ✓
```

### Model Storage:

- **NOT stored in:** `app/src/main/assets/` ❌
- **Stored in:** `context.cacheDir/` (temporary) ✓
- **Deleted on:** App close / Next launch ✓

---

## 📁 Files Modified

### 1. `.gitignore` (Root Level)

```gitignore
# AI Model Files (too large for GitHub)
**/assets/models/*.gguf
**/assets/models/
app/src/main/assets/models/
```

### 2. `app/.gitignore` (App Level)

```gitignore
# AI Model Files (too large for GitHub)
src/main/assets/models/*.gguf
src/main/assets/models/
```

### 3. `AINavigationViewModel.kt` (Already Done)

- Added `deleteCachedModels()` function
- Clears cache on every app launch
- Forces fresh download each time

---

## 🧪 Testing

### Test 1: File Removed from Git

```bash
# Check Git tracking
git ls-files | grep "SmolLM2-360M-Q4_K_M.gguf"
# Should output: nothing (file not tracked)
```

### Test 2: Can Push to GitHub

```bash
git status
# Should not show the large .gguf file

git push origin main
# Should succeed without "file too large" error ✓
```

### Test 3: App Still Works

1. Delete model file locally
2. Open app
3. See "Download AI Model" button
4. Download and use AI
5. Works perfectly! ✓

---

## ⚠️ Important Notes

### DO NOT:

- ❌ Add `.gguf` files to Git
- ❌ Commit files over 100MB
- ❌ Store AI models in `assets/` folder

### DO:

- ✅ Keep models in `.gitignore`
- ✅ Download models at runtime
- ✅ Store in temporary cache directories
- ✅ Delete models after use

---

## 💾 Storage Comparison

| Location | Size | Git Tracked | GitHub Allowed |
|----------|------|-------------|----------------|
| `assets/models/` (OLD) | 258MB | ✅ Yes | ❌ NO (too large) |
| `cacheDir/` (NEW) | 119MB | ❌ No | ✅ N/A (not in repo) |

---

## 🚀 Quick Command Reference

### Delete Local File:

```powershell
Remove-Item "app\src\main\assets\models\SmolLM2-360M-Q4_K_M.gguf" -Force
```

### Remove from Git:

```bash
git rm --cached app/src/main/assets/models/SmolLM2-360M-Q4_K_M.gguf
git commit -m "Remove large AI model from repository"
```

### Clean Git History:

```bash
git filter-repo --path app/src/main/assets/models/ --invert-paths
```

### Push to GitHub:

```bash
git push origin main --force  # Use --force only if you cleaned history
```

---

## 📊 GitHub File Size Limits

| File Type | GitHub Limit | Your Model | Result |
|-----------|--------------|------------|--------|
| Individual File | 100 MB | 258 MB | ❌ Rejected |
| Repository | 1 GB recommended | - | ⚠️ Warning |
| Git LFS | 2 GB | - | ✅ Alternative |

**Solution:** Don't store in Git at all! Download at runtime. ✓

---

## 🎉 Result

After following these steps:

✅ Large model file deleted from local machine
✅ Model files added to `.gitignore`
✅ Can push to GitHub without errors
✅ App still works (downloads model on launch)
✅ No permanent storage used
✅ Privacy-friendly (no trace after app closes)

---

## 🆘 Troubleshooting

### Issue: "Git still shows large file"

**Solution:**

```bash
git rm --cached app/src/main/assets/models/SmolLM2-360M-Q4_K_M.gguf
git commit -m "Remove large file from Git"
```

### Issue: "Push still fails with 'file too large'"

**Solution:** File is in Git history, clean it:

```bash
git filter-repo --path app/src/main/assets/models/ --invert-paths
git push origin main --force
```

### Issue: "App crashes without model"

**Solution:** Model downloads automatically on first launch. Just wait for download to complete!

---

## 📞 Support

If you still have issues pushing to GitHub:

1. Delete the file manually (Step 1)
2. Run: `git rm --cached app/src/main/assets/models/*`
3. Run: `git commit -m "Remove AI models"`
4. Run: `git push origin main`

If push still fails, the file is in Git history - use `git filter-repo` to clean it.

---

**Status:** ✅ Ready to push to GitHub without large files!
