#!/bin/bash

echo "================================================"
echo "    MUSIQUE - APPLICATION DESKTOP"
echo "    Solution Hybride (Desktop + Web)"
echo "================================================"
echo

echo "[INFO] Compilation de l'application..."
mvn clean compile

if [ $? -ne 0 ]; then
    echo "[ERREUR] Echec de la compilation"
    echo "[INFO] Verifiez les erreurs ci-dessus"
    exit 1
fi

echo "[INFO] Compilation reussie"
echo

echo "[INFO] Lancement de la solution hybride..."
echo "[INFO] Interface desktop + Navigateur web complet"
echo

mvn exec:java -Dexec.mainClass=com.musique.DesktopApplicationHybrid

if [ $? -ne 0 ]; then
    echo "[ERREUR] Echec du lancement de l'application"
    echo "[INFO] Verifiez les erreurs ci-dessus"
    exit 1
fi

echo
echo "[INFO] Application fermee"
