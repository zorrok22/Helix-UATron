#!/usr/bin/env python3
"""Replace known demo passwords in repo files with __USER_PASSWORD__."""

from __future__ import annotations

import os
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]

SKIP_DIRS = {
    ".git",
    "target",
    "node_modules",
    ".venv",
    "venv",
    "__pycache__",
    ".pytest_cache",
}

# Literals to replace live in scripts/password_literals.local.txt (gitignored),
# one per line, longer / more specific tokens first.
_LIST_FILE = Path(__file__).with_name("password_literals.local.txt")
REPLACEMENTS = [
    (line.strip(), "__USER_PASSWORD__")
    for line in (_LIST_FILE.read_text(encoding="utf-8").splitlines() if _LIST_FILE.exists() else [])
    if line.strip() and not line.lstrip().startswith("#")
]

TEXT_EXTENSIONS = {
    ".java",
    ".py",
    ".json",
    ".xml",
    ".properties",
    ".config",
    ".config.example",
    ".md",
    ".csv",
    ".txt",
    ".yml",
    ".yaml",
    ".sh",
    ".bat",
    ".Dockerfile",
}

SPECIAL_FILENAMES = {"Dockerfile", "dockerfile"}


def should_process(path: Path) -> bool:
    if path.name in SPECIAL_FILENAMES:
        return True
    if path.suffix.lower() in TEXT_EXTENSIONS:
        return True
    if path.suffix == ".example" and path.name.endswith(".config.example"):
        return True
    return False


def main() -> None:
    changed_files: list[tuple[Path, int]] = []

    for dirpath, dirnames, filenames in os.walk(ROOT):
        dirnames[:] = [d for d in dirnames if d not in SKIP_DIRS]
        for name in filenames:
            path = Path(dirpath) / name
            if path == Path(__file__).resolve():
                continue
            if not should_process(path):
                continue
            try:
                text = path.read_text(encoding="utf-8")
            except (UnicodeDecodeError, OSError):
                continue

            original = text
            for old, new in REPLACEMENTS:
                if old in text:
                    text = text.replace(old, new)

            if text != original:
                path.write_text(text, encoding="utf-8", newline="\n")
                count = sum(original.count(old) for old, _ in REPLACEMENTS)
                changed_files.append((path.relative_to(ROOT), count))

    print(f"Updated {len(changed_files)} files:")
    for rel, _ in sorted(changed_files, key=lambda x: str(x[0])):
        print(f"  {rel}")


if __name__ == "__main__":
    main()
