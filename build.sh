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
cp rendering/pixel_art_background_by_isa_draws_d9s3e6d-fullview.jpg bin/rendering/

echo "Compilation terminée. Pour lancer :"
echo "  java -cp bin core.Game    or  java -Xcomp -XX:ReservedCodeCacheSize=256m -cp bin core.Game"

exit 0


