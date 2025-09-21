@echo off
title Musique Desktop
echo Lancement de Musique Desktop...
mvn exec:java -Dexec.mainClass="com.musique.DesktopApplication"
pause
