#!/usr/bin/env python3
import os
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SKIP = {".git", "target", "node_modules", "__pycache__", "scripts"}

LABELS = re.compile(
    r'"Label"\s*:\s*"([^"]*)"\s*,\s*"Value"\s*:\s*"([^"]+)"',
    re.IGNORECASE,
)
SENSITIVE = ("password", "secret", "token", "apikey", "accesskey", "stackify")


def main() -> None:
    issues = []
    for dirpath, dirnames, filenames in os.walk(ROOT):
        dirnames[:] = [d for d in dirnames if d not in SKIP]
        for name in filenames:
            if not name.endswith(".json"):
                continue
            path = Path(dirpath) / name
            try:
                text = path.read_text(encoding="utf-8")
            except (OSError, UnicodeDecodeError):
                continue
            for label, value in LABELS.findall(text):
                ll = label.lower()
                if not any(s in ll for s in SENSITIVE):
                    continue
                if value.startswith("__") or value in {"Digital Workplace Password"}:
                    continue
                issues.append((path.relative_to(ROOT), label, value))

    for rel, label, value in sorted(issues):
        print(f"{rel}\t{label}\t{value}")
    print(f"TOTAL\t{len(issues)}")


if __name__ == "__main__":
    main()
