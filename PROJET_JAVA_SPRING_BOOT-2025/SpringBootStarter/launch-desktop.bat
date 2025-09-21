@echo off
title Musique Desktop Application
color 0A

echo.
echo ================================================
echo    MUSIQUE - APPLICATION DESKTOP
echo    Location et Vente d'Equipements Musicaux
echo ================================================
echo.

echo [INFO] Verification de Java...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERREUR] Java n'est pas installe ou pas dans le PATH
    echo [INFO] Veuillez installer Java 17 ou superieur
    pause
    exit /b 1
)

echo [INFO] Java detecte avec succes
echo.

echo [INFO] Verification de Maven...
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERREUR] Maven n'est pas installe ou pas dans le PATH
    echo [INFO] Veuillez installer Maven
    pause
    exit /b 1
)

echo [INFO] Maven detecte avec succes
echo.

echo [INFO] Compilation de l'application...
call mvn clean compile -q

if %errorlevel% neq 0 (
    echo [ERREUR] Echec de la compilation
    echo [INFO] Verifiez les erreurs ci-dessus
    pause
    exit /b 1
)

echo [INFO] Compilation reussie
echo.

echo [INFO] Lancement de l'application desktop...
echo [INFO] L'application va s'ouvrir dans une nouvelle fenetre
echo [INFO] Vous pouvez fermer cette console une fois l'application lancee
echo.

call mvn exec:java -Dexec.mainClass="com.musique.DesktopApplication" -q

if %errorlevel% neq 0 (
    echo [ERREUR] Echec du lancement de l'application
    echo [INFO] Verifiez les erreurs ci-dessus
    pause
    exit /b 1
)

echo.
echo [INFO] Application fermee
pause
