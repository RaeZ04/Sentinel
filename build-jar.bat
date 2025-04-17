@echo off
echo Compilando y empaquetando Sentinel Password Manager para Windows, macOS y Linux...

cd Sentinel_PM

:: Limpiar y compilar con Maven
call mvn clean package

:: Crear estructura de distribución
mkdir ..\dist
mkdir ..\dist\lib

:: Copiar JAR principal
copy target\Sentinel-PM-2.3.jar ..\dist\Sentinel.jar

:: Copiar dependencias
xcopy /E /I /Y target\lib ..\dist\lib

echo.
echo ¡Empaquetado completado!
echo El JAR multiplataforma se encuentra en la carpeta dist
echo.
echo Para ejecutar en Windows: java -jar dist\Sentinel-PM.jar
echo Para ejecutar en macOS/Linux: java -jar dist/Sentinel-PM.jar
pause