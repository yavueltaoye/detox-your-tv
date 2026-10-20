# ¿Funciona en mi televisor?

La regla es simple: **si tu TV corre Android TV o Google TV, esto funciona.** No importa tanto la marca
ni el modelo, sino el sistema operativo. Lo que cambia de un modelo a otro es la lista de apps que se
pueden apagar sin romper nada (`debloat.txt`).

## Cómo saberlo en 30 segundos

1. En la TV: **Ajustes > Sistema > Información**. Si ves "Versión de Android TV OS" o el logo de
   Google TV, vas bien.
2. Si la pantalla de inicio dice **VIDAA**, **webOS** (LG), **Tizen** (Samsung) o **Roku**, no es
   compatible: esos sistemas no tienen depuración ADB.
3. Ojo con Hisense: el **mismo nombre de modelo** se vende con Google TV en un país y con VIDAA en
   otro (por ejemplo, la serie U7N es Google TV en EE. UU. y VIDAA en Europa). Fíjate en tu TV, no en
   la caja de otro país.

## Hisense: probados en una TV real

| Carpeta | Modelo | Sistema | Estado |
|---|---|---|---|
| `profiles/hisense-sala/` | Hisense U75 (hengshan, MediaTek), Latinoamérica | Google TV 12 | Probado de punta a punta: limpieza + launcher nuevo + reordenar apps |
| `examples/hisense-u75-hengshan/` | Hisense U75 (hengshan), Latinoamérica | Google TV 12 | Lista base de la que sale el perfil de arriba |
| `examples/hisense-smarttv-4k-ffm/` | Hisense SmartTV 4K FFM (huangshan, MediaTek) | Android TV 10 | Probado |
| `examples/hisense-hismarttv-a4/` | Hisense HiSmartTV A4 (xiaoyushan, MStar), India | Android TV 9 | Probado |
| `examples/vu-4k-lushan/` | Vu 4K (lushan, MediaTek), India | Google TV 12 | Probado; misma plataforma de software de Hisense |

## Hisense: muy probablemente compatibles (sin probar todavía)

Estas series se venden con Google TV en América (EE. UU., Canadá, México). Son de la misma
generación que la U75, así que lo más probable es que compartan sus paquetes `com.vt.*`, pero nadie
lo ha confirmado aún. Antes de aplicar, lista los paquetes de tu TV (`tools/tv.py --probe`). Empieza con `examples/hisense-u75-hengshan/debloat.txt`
y avanza con cuidado (ver más abajo).

| Serie | Años | Notas |
|---|---|---|
| A6 / A6N, A65 | 2023-2025 | Gama de entrada. Algunas fichas oficiales del A6N dicen VIDAA: revisa tu TV |
| A7 / A7N | 2023-2025 | |
| U6K / U6N / U65 | 2023-2025 | En EE. UU. hay versiones U6 con **Fire TV**: esas usan las listas de Fire TV |
| U7K / U7N / U75 / U78 | 2023-2025 | U78N es el nombre canadiense del U7N |
| U8K / U8N / U8 | 2023-2025 | |
| U9N | 2024 | |

Si lo pruebas en uno de estos, abre un issue con la plantilla "TV list" y lo pasamos a la tabla de
probados.

## Otras marcas

Hay 66 listas probadas en [`examples/`](../examples/README.md), de 25 marcas: TCL (14), Xiaomi (14),
Sony (9), Philips (4), Skyworth, CHiQ, NVIDIA SHIELD, Chromecast con Google TV, OnePlus, Panasonic,
Hitachi, Thomson, Sanyo, Amazon Fire TV y más. Si tu TV no aparece, elige la de tu misma marca y
versión de Android más cercana como punto de partida.

## Reglas de seguridad para un modelo nuevo

- Todo se **desactiva**, nada se borra. `tools/restore` lo deja como estaba.
- En Hisense y Vu, **nunca** apagues `com.vt.tv.smartsense*`, `com.vt.tv.ai_sense`, `com.vt.tv.aiot*`
  ni `com.hisense.tv.smartsense`: la TV entra en Android Recovery. Si pasa, elige **"Try again"**,
  nunca "Factory data reset".
- Apaga en grupos chicos y reinicia después de cada grupo. Espera unos 15 segundos antes de
  reiniciar: en algunos Hisense el cambio se pierde si reinicias de inmediato.
- Al terminar, apaga la depuración ADB en Opciones de desarrollador.
