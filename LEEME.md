# Detox Your TV

**Less bloat. No ads. Just TV.**

Tu TV Hisense, sin publicidad y con cara de Apple TV.

Detox Your TV está pensado para los **Hisense con Google TV que se venden en Latinoamérica**. Sirve
también para cualquier Android TV o Google TV: hay listas probadas para 66 modelos de 25 marcas.

Sin root, sin cables y sin borrar nada: todo se desactiva por Wi-Fi o Ethernet y se deshace con un
solo comando.

## Qué trae

Una limpieza por modelo (listas en `examples/`) y un launcher propio (`com.detoxyourtv.home`) con:

- **Inicio negro con dock de vidrio** y un brillo que toma el color de la app enfocada.
- **Centro de control** estilo tvOS en el botón Ajustes: Asistente, Imagen, Sonido, Bluetooth, Red y
  Todos los ajustes.
- **Reordenar apps**: mantén presionado OK sobre una app y elige **Mover**. Llévala con las flechas
  (puede pasar del dock a la grilla y al revés) y suéltala con OK. El orden queda guardado.
- **Dock de vidrio** con seis apps, más grandes, y un menú que se abre sobre la app sin taparla.
- **Entradas** con TV en vivo primero: retoma el último canal de antena (ISDB-T, Ginga).
- **Sonido sin retraso** por HDMI ARC mientras estás en el inicio.
- **Modo seguro**: si el launcher se cae 3 veces en 5 minutos, apaga las animaciones solo.
- Fecha y textos en español, y un perfil completo de limpieza para el Hisense U75 de Latinoamérica.

## ¿Funciona en mi TV?

Si tu TV corre Android TV o Google TV, sí. Mira [docs/COMPATIBILIDAD.md](docs/COMPATIBILIDAD.md)
para saber cómo comprobarlo, qué Hisense ya están probados y cuáles probablemente funcionan.

## Empezar (Windows)

Necesitas Python 3 y, para compilar el launcher, JDK 17 y Android Studio (o el SDK 34).

1. En la TV: **Ajustes > Sistema > Información**, presiona 7 veces "Compilación del SO" para activar
   Opciones de desarrollador. Ahí activa **Depuración USB** / **ADB por red**.
2. Anota la IP de la TV (**Ajustes > Red**). En los comandos de abajo va donde dice `<ip>`.
3. En la PC, dentro de esta carpeta:

```powershell
python -m venv tools\venv; tools\venv\Scripts\python.exe -m pip install -r tools\requirements.txt
$env:TV_HOST = '<ip>'; tools\venv\Scripts\python.exe -I tools\tv.py --probe
```

La primera vez la TV muestra un aviso para aceptar la PC: acéptalo.

4. Limpia la TV y pon el launcher nuevo:

```powershell
powershell -ExecutionPolicy Bypass -File tools\disable.ps1 -TvHost <ip> profiles\hisense-sala\debloat.txt
powershell -ExecutionPolicy Bypass -File launcher\build-and-install.ps1 -TvHost <ip>
```

Si tu TV no es un Hisense U75, usa la lista de tu modelo en [`examples/`](examples/README.md).

5. Al terminar, apaga la depuración ADB en Opciones de desarrollador.

En Mac o Linux los mismos pasos están en el [README en inglés](README.md#doing-it-by-hand).

## Deshacer todo

```powershell
powershell -ExecutionPolicy Bypass -File tools\restore.ps1 -TvHost <ip> profiles\hisense-sala\debloat.txt com.google.android.apps.tv.launcherx
```

Más detalle del perfil Hisense, lo que se mantuvo a propósito y cómo volver a conectarse:
[profiles/hisense-sala/LEEME.md](profiles/hisense-sala/LEEME.md).

## Antes de empezar, lee esto

- En Hisense **nunca** desactives `com.vt.tv.smartsense*`, `ai_sense` ni `aiot*`: la TV entra en
  Android Recovery. Si pasa, elige **"Try again"**, nunca "Factory data reset".
- Esto no es un producto oficial de Hisense ni de Google. Lo usas bajo tu responsabilidad (ver
  [LICENSE](LICENSE)).

## Licencia

MIT, ver [LICENSE](LICENSE). La tipografía Outfit va bajo SIL Open Font License
(`launcher/FONT-LICENSE-OFL.txt`).
