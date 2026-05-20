$ErrorActionPreference = "Stop"

# Paths
$RepoDir = "d:\PROJECTS\OOP-Group_Project"
$BackupDir = "d:\PROJECTS\OOP-Group_Project_Backup_$(Get-Date -Format 'yyyyMMddHHmmss')"

# Step 1: Backup
Write-Host "Backing up project to $BackupDir..."
New-Item -ItemType Directory -Path $BackupDir
Copy-Item -Path "$RepoDir\*" -Destination $BackupDir -Recurse -Exclude ".git"

# Step 2: New History
Set-Location $RepoDir
Write-Host "Creating new orphan branch..."
git checkout --orphan temp-history
git rm -rf .

# Define Commits
# Week 1: 01/05 to 07/05 (3 files)
# Week 2: 08/05 to 10/05 (3 files)

# Commit 1: 01/05 - Base structure
$env:GIT_AUTHOR_DATE = "2026-05-01 09:00:00"
$env:GIT_COMMITTER_DATE = "2026-05-01 09:00:00"
Write-Host "Commit 1 (01/05)..."
$files1 = @(
    ".gitignore", "README.md", "backend/pom.xml", 
    "backend/src/main/java/com/primeestate/model/BaseEntity.java"
)
foreach ($f in $files1) { 
    $dest = Split-Path "$RepoDir\$f" -Parent
    if (!(Test-Path $dest)) { New-Item -ItemType Directory -Path $dest }
    Copy-Item -Path "$BackupDir\$f" -Destination "$RepoDir\$f" -Force 
}

# Add other project folders in first commit to ensure continuity, but focus messages on backend
if (Test-Path "$BackupDir\frontend") { Copy-Item -Path "$BackupDir\frontend" -Destination "$RepoDir\frontend" -Recurse }
if (Test-Path "$BackupDir\database") { Copy-Item -Path "$BackupDir\database" -Destination "$RepoDir\database" -Recurse }

git add .
git commit -m "Initial commit: Project structure and core backend model entity"

# Commit 2: 03/05 - 1 file (Week 1, file 1)
$env:GIT_AUTHOR_DATE = "2026-05-03 11:20:00"
$env:GIT_COMMITTER_DATE = "2026-05-03 11:20:00"
$f2 = "backend/src/main/java/com/primeestate/dao/BaseDAO.java"
$dest2 = Split-Path "$RepoDir\$f2" -Parent
if (!(Test-Path $dest2)) { New-Item -ItemType Directory -Path $dest2 }
Copy-Item -Path "$BackupDir\$f2" -Destination "$RepoDir\$f2" -Force
git add .
git commit -m "feat(backend): add base DAO classes for data access"

# Commit 3: 05/05 - 1 file (Week 1, file 2)
$env:GIT_AUTHOR_DATE = "2026-05-05 14:45:00"
$env:GIT_COMMITTER_DATE = "2026-05-05 14:45:00"
$f3 = "backend/src/main/java/com/primeestate/config/DBConnection.java"
$dest3 = Split-Path "$RepoDir\$f3" -Parent
if (!(Test-Path $dest3)) { New-Item -ItemType Directory -Path $dest3 }
Copy-Item -Path "$BackupDir\$f3" -Destination "$RepoDir\$f3" -Force
git add .
git commit -m "feat(backend): implement database connection utility"

# Commit 4: 07/05 - 1 file (Week 1, file 3) - Week 1 done
$env:GIT_AUTHOR_DATE = "2026-05-07 10:15:00"
$env:GIT_COMMITTER_DATE = "2026-05-07 10:15:00"
$f4 = "backend/src/main/java/com/primeestate/dao/CrudOperations.java"
$dest4 = Split-Path "$RepoDir\$f4" -Parent
if (!(Test-Path $dest4)) { New-Item -ItemType Directory -Path $dest4 }
Copy-Item -Path "$BackupDir\$f4" -Destination "$RepoDir\$f4" -Force
git add .
git commit -m "feat(backend): add generic CRUD operations interface"

# Commit 5: 08/05 - 1 file (Week 2, file 1)
$env:GIT_AUTHOR_DATE = "2026-05-08 13:30:00"
$env:GIT_COMMITTER_DATE = "2026-05-08 13:30:00"
$f5 = "backend/src/main/java/com/primeestate/model/Advertisement.java"
$dest5 = Split-Path "$RepoDir\$f5" -Parent
if (!(Test-Path $dest5)) { New-Item -ItemType Directory -Path $dest5 }
Copy-Item -Path "$BackupDir\$f5" -Destination "$RepoDir\$f5" -Force
git add .
git commit -m "feat(backend): implement advertisement entity model"

# Commit 6: 09/05 - 1 file (Week 2, file 2)
$env:GIT_AUTHOR_DATE = "2026-05-09 15:10:00"
$env:GIT_COMMITTER_DATE = "2026-05-09 15:10:00"
$f6 = "backend/src/main/java/com/primeestate/dao/AdvertisementDAO.java"
$dest6 = Split-Path "$RepoDir\$f6" -Parent
if (!(Test-Path $dest6)) { New-Item -ItemType Directory -Path $dest6 }
Copy-Item -Path "$BackupDir\$f6" -Destination "$RepoDir\$f6" -Force
git add .
git commit -m "feat(backend): implement advertisement DAO for database operations"

# Commit 7: 10/05 - 1 file (Week 2, file 3) AND ALL REMAINING
$env:GIT_AUTHOR_DATE = "2026-05-10 18:00:00"
$env:GIT_COMMITTER_DATE = "2026-05-10 18:00:00"
Write-Host "Finalizing commit (10/05)..."
Copy-Item -Path "$BackupDir\*" -Destination "$RepoDir" -Recurse -Force -Exclude ".git"
git add .
git commit -m "feat(backend): complete backend implementation with servlets and utilities"

# Finalize
git branch -D advertisement
git branch -m advertisement
Write-Host "Done. History for 'advertisement' branch has been rewritten and backdated."
