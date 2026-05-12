# EggPdf Viewer (LWJGL + OpenGL + PDFBox)

A lightweight PDF viewer written in Java using OpenGL via LWJGL.  
Currently, this project is in its very early stage, it only renders a hardcoded PNG file as a placeholder while the rendering pipeline and window system is being built.

The goal is to build a fast, GPU-accelerated pdf viewer using Apache Pdfbox for PDF parsing and OpenGL for display.

---

## Current Status

### Implemented
- OpenGL renderer basic setup using LWJGL
- Basic window creation
- Texture loading and rendering
- Rendering a hardcoded PNG in a 2D viewport

### Planned
- PDF loading using PDFBox
- Rendering PDF pages to textures
- Zooming and panning
- Multi-page support
- Text selection
- Search functionality
- Smooth scrolling
- GPU-accelerated rendering pipeline
- Caching system for rendered pages

---

## Libraries used

- LWJGL (OpenGL)
- Apache PDFBox *(planned)*

---

## Getting Started

### Requirements

- Java 17+
- A build tool like Gradle or Maven
- OpenGL-compatible GPU

### Clone the Repository

```bash
git clone <this-repo>
cd <project-name>
```

### Run

```bash
./gradlew run
```

or run the main class from your IDE.

---

## Screenshots

*to be added*

---

## Goals

This project exists mainly for:
- learning OpenGL rendering in Java
- an experiment in GPU-accelerated PDF rendering
- an lightweight alternative to ui heavy pdf viewers

---

## Disaclaimer

This is just an initial commit and codebase is experimental

---

## License

MIT License
