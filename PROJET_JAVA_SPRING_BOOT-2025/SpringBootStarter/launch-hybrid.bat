@echo off
title Musique Desktop - Solution Hybride
color 0A

echo.
echo ================================================
echo    MUSIQUE - APPLICATION DESKTOP
echo    Solution Hybride (Desktop + Web)
echo ================================================
echo.

echo [INFO] Compilation de l'application...
call mvn clean compile

if %errorlevel% neq 0 (
    echo [ERREUR] Echec de la compilation
    echo [INFO] Verifiez les erreurs ci-dessus
    pause
    exit /b 1
)

echo [INFO] Compilation reussie
echo.

echo [INFO] Lancement de la solution hybride...
echo [INFO] Interface desktop + Navigateur web complet
echo.

call mvn exec:java -Dexec.mainClass=com.musique.DesktopApplicationHybrid

if %errorlevel% neq 0 (
    echo [ERREUR] Echec du lancement de l'application
    echo [INFO] Verifiez les erreurs ci-dessus
    pause
    exit /b 1
)

echo.
echo [INFO] Application fermee
pause
