"""
tail_to_bmc.py
Tail a local log file and send new lines to BMC Helix Log Analytics via REST API.
Supports API key or JWT-based authentication.
Usage:
    python tail_to_bmc.py --portal <portal-host> --api-key <KEY> --file /var/log/myapp.log
    OR
    python tail_to_bmc.py --portal <portal-host> --user <username> --password <password> --file /var/log/myapp.log
"""
import argparse
import json
import time
import uuid
import logging
from typing import List, Dict, Any, Optional
import requests

# ---- Logging ----
logging.basicConfig(level=logging.INFO, format="%(asctime)s %(levelname)s %(message)s")
logger = logging.getLogger("bmc_log_tailer")
# ---- Helpers ----

def normalize_portal(portal: str) -> str:
    """Normalize portal base URL.

    Accepts either a host like "example.com" or a full URL like "https://example.com".
    Ensures a scheme is present and strips a trailing slash.
    """
    portal = (portal or "").strip()
    if not portal:
        return portal
    if not portal.startswith("http://") and not portal.startswith("https://"):
        portal = "https://" + portal
    return portal.rstrip("/")

# ---- Authentication ----
def get_jwt_token(portal: str, username: str, password: str, verify_ssl=True, timeout=30) -> str:
    """Obtain JWT token from BMC Helix portal."""
    url = f"{portal}/api/jwt/login"
    headers = {"Content-Type": "application/x-www-form-urlencoded"}
    data = {"username": username, "password": password}
    logger.info("Authenticating to %s as %s", portal, username)
    resp = requests.post(url, headers=headers, data=data, verify=verify_ssl, timeout=timeout)
    resp.raise_for_status()
    token = resp.text.strip()
    logger.info("JWT token obtained")
    return token
# ---- Log formatting ----

def make_log_record(message: str, host: str = "log-tailer") -> Dict[str, Any]:
    """Build a log record in the expected JSON format."""
    timestamp = time.strftime("%Y-%m-%dT%H:%M:%S.000Z", time.gmtime())

    return {
        "input": {"type": "log"},
        "@timestamp": timestamp,

        # agent no longer carries hostname
        "agent": {
            "type": "python-script",
            "id": str(uuid.uuid4())
        },

        # New field as requested
        "log_source_host": host,

        "host": {
            "name": host,
            "ip": ["127.0.0.1"]
        },

        "message": message.strip(),
        "log": {"file": {"path": "tailed-file"}},
        "tags": ["python", "tail"],
        "event_id": str(uuid.uuid4()),
    }

# ---- Sender ----
class BMCLogSender:
    def __init__(self, portal: str, jwt_token: Optional[str] = None, api_key: Optional[str] = None,
                 verify_ssl: bool = True, timeout: int = 30):
        self.portal = portal
        self.jwt_token = jwt_token
        self.api_key = api_key
        self.verify_ssl = verify_ssl
        self.timeout = timeout
        self.url = f"{portal}/log-service/api/v1.0/logs"
    def _headers(self) -> Dict[str, str]:
        if self.api_key:
            return {"Authorization": f"apiKey {self.api_key}", "Content-Type": "application/json"}
        elif self.jwt_token:
            return {"Authorization": f"Bearer {self.jwt_token}", "Content-Type": "application/json"}
        else:
            raise ValueError("Need either JWT token or API key")
    def send(self, records: List[Dict[str, Any]]):
        if not records:
            return
        # Use requests' JSON handling (sets content-type, handles encoding safely).
        resp = requests.post(
            self.url,
            headers=self._headers(),
            json=records,
            verify=self.verify_ssl,
            timeout=self.timeout,
        )
        if resp.status_code not in (200, 206):
            logger.error("Failed to send logs: %s %s", resp.status_code, resp.text[:300])
            resp.raise_for_status()
        logger.info("Sent %d log(s), response=%s", len(records), resp.status_code)
# ---- File tailing ----

def follow(file_path: str):
    """Generator that yields new lines as they are written to the file (continuous tail mode)."""
    # Windows default encodings (cp1252/cp1250/etc.) can fail on arbitrary bytes in log files.
    # Force a predictable decoding and replace undecodable bytes rather than crashing.
    with open(file_path, "r", encoding="utf-8-sig", errors="replace") as f:
        # Seek to end of file
        f.seek(0, 2)
        while True:
            line = f.readline()
            if not line:
                time.sleep(1)  # wait for new data
                continue
            yield line


def read_all_lines(file_path: str):
    """Generator that reads all lines from file once, then exits (send-once mode)."""
    logger.info("Reading all lines from file: %s", file_path)
    # Use the same robust decoding behavior as tail mode.
    with open(file_path, "r", encoding="utf-8-sig", errors="replace") as f:
        for line in f:
            if line.strip():  # Skip empty lines
                yield line
    logger.info("Finished reading file")
# ---- Main ----
def parse_args():
    p = argparse.ArgumentParser(description="Tail a log file and send to BMC Helix Log Analytics")
    p.add_argument(
        "--portal",
        required=True,
        help="BMC Helix portal base URL or host (e.g., 'https://example.com' or 'example.com')",
    )
    auth = p.add_mutually_exclusive_group(required=True)
    auth.add_argument("--api-key", help="API key")
    auth.add_argument("--user", help="Username for JWT login")
    p.add_argument("--password", help="Password (required if using --user)")
    p.add_argument("--file", required=True, help="Log file to tail")
    p.add_argument("--host", default="host-51", help="Host label for logs")
    p.add_argument("--batch-size", type=int, default=10, help="Batch size for sending logs")
    p.add_argument(
        "--mode",
        choices=["tail", "send-once"],
        default="send-once",
        help="Mode: 'tail' for continuous monitoring, 'send-once' to read all and exit (default: send-once)",
    )

    # Default should be SSL verification ON; allow opting out.
    p.add_argument(
        "--no-verify-ssl",
        dest="verify_ssl",
        action="store_false",
        help="Disable SSL certificate verification",
    )
    p.set_defaults(verify_ssl=True)

    return p.parse_args()


def main():
    args = parse_args()

    # Normalize portal so callers can pass either host or full URL.
    args.portal = normalize_portal(args.portal)

    jwt_token = None
    if args.user:
        if not args.password:
            raise SystemExit("If --user is used, --password is required")
        jwt_token = get_jwt_token(args.portal, args.user, args.password, verify_ssl=args.verify_ssl)

    sender = BMCLogSender(portal=args.portal, jwt_token=jwt_token, api_key=args.api_key,
                          verify_ssl=args.verify_ssl)
    buffer = []

    if args.mode == "send-once":
        # Send-once mode: Read all lines from file and exit
        logger.info("Mode: send-once - Reading all lines from file: %s", args.file)
        line_count = 0

        for line in read_all_lines(args.file):
            line_count += 1
            record = make_log_record(line, host=args.host)
            buffer.append(record)

            if len(buffer) >= args.batch_size:
                try:
                    sender.send(buffer)
                    buffer.clear()
                except Exception:
                    logger.exception("Error sending batch")
                    buffer.clear()

        # Send any remaining logs in buffer
        if buffer:
            try:
                logger.info("Sending final batch of %d log(s)", len(buffer))
                sender.send(buffer)
                buffer.clear()
            except Exception:
                logger.exception("Error sending final batch")

        logger.info("Completed: Sent %d log line(s) from file", line_count)
        logger.info("Script exiting normally")

    else:
        # Tail mode: Continuous monitoring (original behavior)
        logger.info("Mode: tail - Continuously monitoring file: %s", args.file)
        for line in follow(args.file):
            record = make_log_record(line, host=args.host)
            buffer.append(record)
            if len(buffer) >= args.batch_size:
                try:
                    sender.send(buffer)
                    buffer.clear()
                except Exception:
                    logger.exception("Error sending batch")
                    buffer.clear()
if __name__ == "__main__":
    main()
