#!/usr/bin/env python3
import os

BASE_DIR = os.path.abspath(
    os.path.join(os.path.dirname(__file__), "../apps/api/src/main/java/com/enviora")
)

FEATURES = [
    "auth",
    "organization",
    "invitation",
    "project",
    "environment",
    "secret",
    "audit",
    "notification",
    "user",
]

SUBPACKAGES = [
    "controller",
    "service",
    "repository",
    "entity",
    "dto",
    "mapper",
    "validator",
]

def main():
    print(f"Creating feature package structure in {BASE_DIR}...")
    for feature in FEATURES:
        for subpkg in SUBPACKAGES:
            target_dir = os.path.join(BASE_DIR, feature, subpkg)
            os.makedirs(target_dir, exist_ok=True)
            package_info_path = os.path.join(target_dir, "package-info.java")
            package_name = f"com.enviora.{feature}.{subpkg}"
            content = f"/**\n * {feature.capitalize()} {subpkg} layer.\n */\npackage {package_name};\n"
            with open(package_info_path, "w") as f:
                f.write(content)
            print(f"Created: {package_name}")

if __name__ == "__main__":
    main()
