# Hisense Sala: qué se hizo y cómo deshacerlo

TV: Hisense hengshan, Google TV / Android 12, conectada por Ethernet con IP fija.
Fecha: 2026-10-20.

## Qué cambió

1. **Limpieza (debloat).** Se desactivaron (no se borraron) las apps de `debloat.txt`:
   rastreo de pantalla (ACR), demo de tienda, modo hotel, navegador, HiAlexa, auto-instalador
   de apps de socios, uploader de feedback, etc. Se desactivó también el launcher de Google TV
   (`com.google.android.apps.tv.launcherx`) y el asistente de configuración que reclamaba Home.
2. **Launcher nuevo** (`com.detoxyourtv.home`), estilo Apple TV:
   - Saludo, reloj y fecha corta (día de la semana arriba, número abajo) a la izquierda;
     botones Entradas, Red y Ajustes a la derecha. La búsqueda queda en el botón del control.
   - Dock de vidrio: YouTube, DGO, Netflix, HBO Max, Apple TV, Prime Video.
   - Para reordenar: mantener presionado OK sobre una app → **Mover**, flechas para llevarla
     (puede pasar del dock a la grilla y al revés) y OK para soltar. El orden se guarda.
   - El fondo toma el color de la app enfocada.
   - **Ajustes** abre un Centro de control: Asistente, Imagen, Sonido, Bluetooth, Red, Todos los ajustes.
     Mantener presionado Ajustes abre las opciones del launcher.
   - **Entradas** lista HDMI 1-4 y TV en vivo.
   - Si el launcher se cae 3 veces en 5 minutos, apaga solo las animaciones (modo seguro).

## Qué se mantuvo a propósito

Asistente de Google, Chromecast, TV abierta / Ginga (interactividad), actualizaciones OTA,
reproductor USB. Nunca se tocan `com.vt.tv.smartsense*`, `ai_sense` ni `aiot*`
(si se desactivan, la TV entra en Android Recovery).

## Cierre de seguridad

- Se restauraron los tiempos de apagado originales (`power-defaults.txt`).
- Se apagó la depuración ADB (USB y red): el puerto 5555 queda cerrado.

## Para volver a conectarse desde la PC

En la TV: Ajustes > Sistema > Información > presionar 7 veces "Compilación del SO" para
activar Opciones de desarrollador; luego Opciones de desarrollador > Depuración USB / ADB por red.
La primera conexión pide aceptar la huella de la PC en la pantalla de la TV.

## Deshacer todo

```powershell
tools\restore.ps1 -TvHost <ip> profiles\hisense-sala\debloat.txt com.google.android.apps.tv.launcherx
```

Eso reactiva todas las apps y el launcher de Google TV. Para quitar el launcher nuevo:

```powershell
tools\venv\Scripts\python.exe -I tools\tv.py "pm uninstall com.detoxyourtv.home"
```

Si la TV todavía tiene el launcher de antes con otro nombre de paquete (`com.gathin.tvhome`),
quítalo igual: `tools\venv\Scripts\python.exe -I tools\tv.py "pm uninstall com.gathin.tvhome"`.

Como último recurso, un restablecimiento de fábrica deja la TV como salió de la caja.

## Reinstalar el launcher después de cambios

```powershell
$env:JAVA_HOME = 'C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot'
launcher\build-and-install.ps1 -TvHost <ip>
```

La configuración del dock y apps ocultas está en
`launcher/app/src/main/res/values/config.xml`.
