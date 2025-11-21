#!/usr/bin/env bash
set -euo pipefail

# Build script pour Spawn_Keio_2025
# Compile tous les .java trouvés et place les .class dans bin

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
echo "Project root: $ROOT_DIR"
cd "$ROOT_DIR"

mkdir -p bin

# Trouver tous les fichiers .java (gère les nouveaux dossiers automatiquement)
JAVA_SOURCES=$(find . -name "*.java")

if [ -z "$JAVA_SOURCES" ]; then
	echo "Aucune source Java trouvée."
	exit 1
fi

echo "Compiling Java sources..."
# Utiliser -sourcepath . pour permettre la résolution des packages
javac -d bin -sourcepath . $JAVA_SOURCES

echo "Compilation terminée. Pour lancer :"
echo "  java -cp bin core.Game    # ou le nom qualifié du package de votre main"

exit 0