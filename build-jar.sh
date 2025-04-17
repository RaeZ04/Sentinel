#!/bin/bash
echo "Compilando y empaquetando Sentinel Password Manager para Windows, macOS y Linux..."

cd Sentinel_PM

# Limpiar y compilar con Maven
mvn clean package

# Crear estructura de distribución
mkdir -p ../dist/lib

# Copiar JAR principal
cp target/Sentinel-PM-2.3.jar ../dist/Sentinel.jar

# Copiar dependencias
cp -r target/lib/* ../dist/lib/

echo ""
echo "¡Empaquetado completado!"
echo "El JAR multiplataforma se encuentra en la carpeta dist"
echo ""
echo "Para ejecutar: java -jar dist/Sentinel-PM.jar"

# Crear un archivo launcher para Linux/macOS
cat > ../dist/sentinel-pm.sh << 'EOF'
#!/bin/bash
java -jar "$(dirname "$0")/Sentinel-PM.jar"
EOF

# Hacer ejecutable el script de lanzamiento
chmod +x ../dist/sentinel-pm.sh

echo "También se creó un script ejecutable: dist/sentinel-pm.sh"