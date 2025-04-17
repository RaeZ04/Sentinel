# Sentinel Password Manager

![Sentinel Logo](Sentinel_PM/src/main/resources/com/sentinel/sentinel_pm/Media/sentinelPM-removebg-preview.png)

## 📋 Descripción

Sentinel Password Manager es una aplicación de escritorio desarrollada en Java con JavaFX que te permite almacenar, gestionar y recuperar contraseñas de manera segura. La aplicación utiliza encriptación para proteger tus credenciales y ofrece una interfaz amigable para administrar diferentes tipos de cuentas.

## 🔧 Requisitos del sistema

- Java 21 o superior
- Sistema operativo compatible: Windows, macOS, Linux

## 🚀 Instalación

### Opción 1: Ejecutar el archivo JAR

1. Descarga el archivo `Sentinel-PM-2.3.jar` de la carpeta `target`
2. Asegúrate de tener Java 21 o superior instalado en tu sistema
3. Ejecuta la aplicación con el comando:

```bash
java -jar Sentinel-PM-2.3.jar
```

### Opción 2: Compilación desde el código fuente

1. Clona el repositorio:

```bash
git clone https://github.com/RaeZ04/Sentinel-Password-Manager.git
```

2. Navega al directorio del proyecto:

```bash
cd Sentinel-main/V2.0/Sentinel_PM
```

3. Compila y empaqueta el proyecto usando Maven:

```bash
mvn clean package
```

4. El archivo JAR ejecutable estará disponible en la carpeta `target`

## 📱 Uso de la aplicación

### Primer inicio

1. Al iniciar la aplicación por primera vez, deberás crear una contraseña maestra
2. Esta contraseña se utilizará para acceder a todas tus credenciales almacenadas
3. También se te pedirá establecer la ruta donde se guardarán tus contraseñas

### Iniciar sesión

1. Introduce la contraseña maestra que estableciste durante la configuración
2. Presiona el botón "Iniciar sesión" o presiona Enter

### Gestión de contraseñas

#### Organización por clases

Las contraseñas se organizan en "clases" o categorías. Por ejemplo:
- Redes sociales
- Correos electrónicos
- Servicios bancarios
- Etc.

#### Añadir una nueva clase

1. Haz clic en la sección "Añadir clase"
2. Ingresa el nombre de la nueva categoría
3. Haz clic en "Añadir"

#### Añadir una nueva cuenta

1. Selecciona la clase/categoría donde deseas añadir la cuenta
2. Haz clic en el botón "Añadir cuenta"
3. Completa los campos:
   - Usuario: Nombre de usuario o correo electrónico
   - Contraseña: Puedes ingresar manualmente o usar el generador de contraseñas seguras
4. Haz clic en "Aceptar" para guardar la cuenta

#### Ver tus contraseñas

1. Selecciona una clase del menú desplegable
2. Haz clic en "Mostrar"
3. Se mostrará una lista de todas las cuentas en esa categoría
4. Para ver una contraseña, haz clic en el botón "Mostrar" junto a la cuenta

#### Funciones adicionales

- **Copiar contraseña**: Copia la contraseña al portapapeles sin necesidad de mostrarla
- **Eliminar cuenta**: Borra una cuenta específica de tu registro
- **Actualizar credenciales**: Modifica el nombre de usuario o contraseña de una cuenta existente
- **Generador de contraseñas**: Crea contraseñas seguras que incluyen letras mayúsculas, minúsculas, números y caracteres especiales

## 🔐 Seguridad

- Todas las contraseñas maestras se almacenan utilizando hash SHA-256
- Los datos se guardan en formato JSON en la ubicación especificada durante la configuración
- La aplicación incluye sistema de respaldo para evitar pérdida de datos

## ⚙️ Configuración

Para acceder a la configuración:
1. Haz clic en el icono de engranaje en la interfaz principal
2. Desde allí podrás:
   - Cambiar la contraseña maestra
   - Modificar la ubicación del archivo de almacenamiento

## 🛠️ Solución de problemas

### La aplicación no inicia

- Asegúrate de tener instalada la versión correcta de Java (21 o superior)
- Verifica los permisos de ejecución del archivo JAR
- Comprueba si hay mensajes de error en la consola

### No puedo acceder con mi contraseña

Si has olvidado tu contraseña maestra, lamentablemente no hay forma de recuperarla. Tendrás que:
1. Eliminar el archivo `config.json` ubicado en el directorio de la aplicación
2. Reiniciar la aplicación para crear una nueva configuración
